package com.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private String content;        // 帖子内容
    private Long userId;        // 发表用户
    private Integer viewCount;     // 浏览量
    private Integer commentCount;  // 评论数
    private String images;         // 图片
    private Integer type;          // INT 类型 1最新发布  2热门推荐 3校园闲聊 4生活互助
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
