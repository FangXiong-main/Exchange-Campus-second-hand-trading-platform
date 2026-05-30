package com.fangxiong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Integer id;
    private String content;        // 帖子内容
    private Integer userId;        // 发表用户
    private Integer likeCount;     // 收藏数
    private Integer commentCount;  // 评论数
    private String images;         // 图片，多张逗号分隔
    private Integer type;          // INT 类型
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
