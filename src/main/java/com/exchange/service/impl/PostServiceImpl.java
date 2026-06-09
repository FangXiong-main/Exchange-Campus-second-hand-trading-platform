package com.exchange.service.impl;

import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.DeleteFileUtil;
import com.exchange.Utils.MoveFileUtil;
import com.exchange.dto.PostCommentDTO;
import com.exchange.dto.PostDTO;
import com.exchange.mapper.PostMapper;
import com.exchange.pojo.Post;
import com.exchange.pojo.User;
import com.exchange.service.PostService;
import com.exchange.vo.PageResult;
import com.exchange.vo.Result;
import com.fangxiong.jsonUtilsCore.coreUtil.CustomizeGenericTypes;
import com.fangxiong.utils.redis.RedisUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class PostServiceImpl implements PostService {

    @Resource
    private DeleteFileUtil deleteFileUtil;

    @Resource
    private MoveFileUtil moveFileUtil;

    @Resource
    private PostMapper postMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public Result getPostPage(Integer pageNum, Integer pageSize,Integer type) {
        if (CurrentHolder.getCurrentUserInfo().getSchool()==0){
            return Result.error("请先绑定学校");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<PostDTO> postList = postMapper.getPostList(type, CurrentHolder.getCurrentUserInfo().getSchool());
        PageInfo<PostDTO> pageInfo = new PageInfo<>(postList);
        PageResult<PostDTO> pageResult = new PageResult<>();
        pageResult.setRows(pageInfo.getList());
        pageResult.setTotal(pageInfo.getTotal());
        return Result.success(pageResult);
    }

    @Override
    public Result getCommentList(Integer pageNum, Integer pageSize, Long postId) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostCommentDTO> commentList = postMapper.getCommentList(postId);
        PageInfo<PostCommentDTO> pageInfo = new PageInfo<>(commentList);
        PageResult<PostCommentDTO> pageResult = new PageResult<>();
        pageResult.setRows(pageInfo.getList());
        pageResult.setTotal(pageInfo.getTotal());
        return Result.success(pageResult);
    }

    @Override
    public Result addComment(PostCommentDTO postCommentDTO) {
        postCommentDTO.setUserId(CurrentHolder.getCurrentUserInfo().getId());
        postCommentDTO.setCreateTime(LocalDateTime.now());
        postMapper.addComment(postCommentDTO);
        return Result.success();
    }

    @Override
    public Result publishPost(PostDTO postDTO) {
        if (CurrentHolder.getCurrentUserInfo().getSchool()==0) {
            return Result.error("请先绑定学校");
        }
        postDTO.setSchool(CurrentHolder.getCurrentUserInfo().getSchool());
        postDTO.setUserId(CurrentHolder.getCurrentUserInfo().getId());
        postDTO.setCreateTime(LocalDateTime.now());
        String realUrl = moveFileUtil.moveTempToReal(postDTO.getImages());
        postDTO.setImages(realUrl);
        postMapper.publishPost(postDTO);
        return Result.success();
    }

    @Override
    public Result addViewCount(Long postId) {
        postMapper.addViewCount(postId);
        return Result.success();
    }

    @Transactional
    @Override
    public Result deletePost(Long postId) {
        String postImagesUrl = postMapper.selectPostImagesUrl(postId);
        if (postImagesUrl != null && !postImagesUrl.isEmpty()){
            if (!deleteFileUtil.deleteFile(postImagesUrl)) {
                return Result.error("删除失败");
            }
        }
        postMapper.deletePost(postId, CurrentHolder.getCurrentUserInfo().getId());
        postMapper.deletePostComment(postId, CurrentHolder.getCurrentUserInfo().getId());
        return Result.success();
    }
}
