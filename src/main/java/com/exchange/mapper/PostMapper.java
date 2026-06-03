package com.exchange.mapper;

import com.exchange.dto.PostCommentDTO;
import com.exchange.dto.PostDTO;
import com.exchange.pojo.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostDTO> getPostList(Integer type);

    List<PostCommentDTO> getCommentList(Long postId);

    void addComment(PostCommentDTO postCommentDTO);

    void publishPost(PostDTO postDTO);

    void addViewCount(Long postId);

    void deletePost(Long postId, Long userId);

    void deletePostComment(Long postId, Long userId);
}
