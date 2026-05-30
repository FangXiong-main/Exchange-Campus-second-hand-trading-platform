package com.fangxiong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    private Integer id;
    private Integer goodsId;
    private Integer buyerId;
    private Integer status; // 0待处理 1已完成 2已取消
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
