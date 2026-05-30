package com.fangxiong.vo;

import lombok.Data;

@Data
public class TodoVO {
    private Integer id;
    private String content;
    private String time;
    private Integer status; // 0待审核 1已通过
}
