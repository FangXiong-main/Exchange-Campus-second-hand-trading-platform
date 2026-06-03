package com.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentDTO {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private String avatarUrl;
    private LocalDateTime createTime;
}
