package com.exchange.controller;

import com.exchange.anno.Log;
import com.exchange.anno.RequiredAdmin;
import com.exchange.dto.PostCommentDTO;
import com.exchange.dto.PostDTO;
import com.exchange.service.PostService;
import com.exchange.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    //删除评论
    @Log
    @RequiredAdmin
    @PostMapping("/adminDeleteComment")
    public Result adminDeleteComment(@RequestBody Map<String,String> params){
        Long commentId = Long.parseLong(params.get("id"));
        return postService.adminDeleteComment(commentId);
    }

    //删除帖子
    @Log
    @RequiredAdmin
    @PostMapping("/adminDeletePost")
    public Result adminDeletePost(@RequestBody Map<String,String> params){
        Long postId = Long.parseLong(params.get("id"));
        return postService.adminDeletePost(postId);
    }

    @GetMapping("/pages")
    public Result getPostPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam Integer type){
        return postService.getPostPage(pageNum,pageSize,type);
    }

    @GetMapping("/commentList")
    public Result getCommentList(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam Long postId){
        return postService.getCommentList(pageNum,pageSize,postId);
    }

    @PostMapping("/addComment")
    public Result addComment(@RequestBody PostCommentDTO postCommentDTO){
        return postService.addComment(postCommentDTO);
    }

    @PostMapping("/publishPost")
    public Result publishPost(@RequestBody PostDTO postDTO){
        return postService.publishPost(postDTO);
    }

    @GetMapping("/addViewCount")
    public Result addViewCount(@RequestParam Long postId){
        return postService.addViewCount(postId);
    }

    @PostMapping("/deletePost")
    public Result deletePost(@RequestBody Map<String,String>  params){
        Long postId = Long.parseLong(params.get("id"));
        return postService.deletePost(postId);
    }

    @PostMapping("/getSearchedPostList")
    public Result getSearchedPostList(@RequestBody Map<String,String> params){
        Integer pageNum = Integer.parseInt(params.get("pageNum"));
        Integer pageSize = Integer.parseInt(params.get("pageSize"));
        String content = params.get("content");
        return postService.getSearchedPostList(pageNum,pageSize,content);
    }
}
