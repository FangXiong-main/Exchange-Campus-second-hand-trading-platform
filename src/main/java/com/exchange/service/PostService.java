package com.exchange.service;

import com.exchange.dto.PostCommentDTO;
import com.exchange.dto.PostDTO;
import com.exchange.vo.Result;

public interface PostService {
    Result getPostPage(Integer pageNum, Integer pageSize,Integer type);

    Result getCommentList(Integer pageNum, Integer pageSize, Long postId);

    Result addComment(PostCommentDTO postCommentDTO);

    Result publishPost(PostDTO postDTO);

    Result addViewCount(Long postId);

    Result deletePost(Long postId);

    Result getSearchedPostList(Integer pageNum, Integer pageSize, String content);

    Result adminDeleteComment(Long commentId);

    Result adminDeletePost(Long postId);
}
