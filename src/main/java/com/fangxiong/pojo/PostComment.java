package com.fangxiong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostComment {
    private Integer id;
    private Integer postId;       // 关联帖子ID
    private Integer userId;        // 评论人ID
    private String content;        // 评论内容
    private LocalDateTime createTime;
}
