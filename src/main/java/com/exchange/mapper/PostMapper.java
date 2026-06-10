package com.exchange.mapper;

import com.exchange.dto.PostCommentDTO;
import com.exchange.dto.PostDTO;
import com.exchange.pojo.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostDTO> getPostList(@Param("type") Integer type,@Param("school")Long school);

    List<PostCommentDTO> getCommentList(Long postId);

    void addComment(PostCommentDTO postCommentDTO);

    void publishPost(@Param("postDTO") PostDTO postDTO);

    void addViewCount(Long postId);

    void deletePost(@Param("postId") Long postId,@Param("userId") Long userId);

    void deletePostComment(@Param("postId") Long postId, @Param("userId") Long userId);

    String selectPostImagesUrl(Long postId);

    void deleteUserInfoById(Long id);
    void deleteUserCommentInfoById(Long id);

    List<String> selectUserPostImages(Long id);

    List<PostDTO> selectPostListByContent(@Param("content") String content,@Param("school") Long school);
}
