package com.exchange.mapper;

import com.exchange.dto.PostCommentDTO;
import com.exchange.dto.PostDTO;
import com.exchange.pojo.Post;
import com.exchange.pojo.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostDTO> getPostList(@Param("type") Integer type,@Param("school")Long school);

    List<PostCommentDTO> getCommentList(Long postId);

    void addComment(PostCommentDTO postCommentDTO);

    void publishPost(PostDTO postDTO);

    void addViewCount(Long postId);

    void deletePost(@Param("postId") Long postId,@Param("userId") Long userId);

    void deletePostComment(@Param("postId") Long postId, @Param("userId") Long userId);

    String selectPostImagesUrl(Long postId);

    void deleteUserInfoById(Long id);
    void deleteUserCommentInfoById(Long id);

    List<String> selectUserPostImages(Long id);

    List<PostDTO> selectPostListByContent(@Param("content") String content,@Param("school") Long school);

    Post selectCommentPostInfoById(Long commentId);

    void deleteCommentById(Long commentId);

    Post selectPostInfoById(Long postId);

    void deletePostByPostId(Long postId);

    void deletePostCommentByPostId(Long postId);

    List<String> selectPostImagesUrlByPostId(Long postId);
}
