package com.exchange.vo;

import com.exchange.pojo.Post;
import com.exchange.pojo.PostComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAndCommentVO {
    private Post post;
    private List<PostComment> postComments;
}
