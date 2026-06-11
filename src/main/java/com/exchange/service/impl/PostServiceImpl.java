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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
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

    @Override
    public Result getSearchedPostList(Integer pageNum, Integer pageSize, String content) {
        if (content==null||content.isEmpty()){
            return Result.error("请输入搜索内容");
        }
        PageHelper.startPage(pageNum, pageSize);
        if (CurrentHolder.getCurrentUserInfo().getSchool()==0){
            return Result.error("请先绑定学校");
        }
        List<PostDTO> postDTOS = postMapper.selectPostListByContent(content, CurrentHolder.getCurrentUserInfo().getSchool());
        PageInfo<PostDTO> pageInfo = new PageInfo<>(postDTOS);
        PageResult<PostDTO> pageResult = new PageResult<>();
        pageResult.setRows(pageInfo.getList());
        pageResult.setTotal(pageInfo.getTotal());
        return Result.success(pageResult);
    }

    @Transactional
    @Override
    public Result adminDeleteComment(Long commentId) {
        Post post = postMapper.selectCommentPostInfoById(commentId);
        if (post==null){
            return Result.error("该评论不存在");
        }
        if (!post.getSchool().equals(CurrentHolder.getCurrentUserInfo().getSchool())){
            return Result.error("无权限");
        }
        postMapper.deleteCommentById(commentId);
        return Result.success();
    }

    @Transactional
    @Override
    public Result adminDeletePost(Long postId) {
        List<String> imagesUrl = null;
        try {
            Post post = postMapper.selectPostInfoById(postId);
            if (post==null){
                return Result.error("该帖子不存在");
            }
            if (!post.getSchool().equals(CurrentHolder.getCurrentUserInfo().getSchool())){
                return Result.error("无权限");
            }
            imagesUrl = postMapper.selectPostImagesUrlByPostId(postId);
            if (imagesUrl!=null){
                imagesUrl.forEach(moveFileUtil::moveRealToTemp);
            }
            postMapper.deletePostByPostId(postId);
            postMapper.deletePostCommentByPostId(postId);
        } catch (Exception e) {
            log.info("删除失败：{}", e.getMessage());
            if (imagesUrl!=null){
                imagesUrl.forEach(moveFileUtil::moveTempToReal);
            }
            return Result.error("删除失败");
        }
        return Result.success();
    }
}
