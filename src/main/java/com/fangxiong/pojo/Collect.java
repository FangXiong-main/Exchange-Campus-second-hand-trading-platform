package com.fangxiong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Collect {
    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private LocalDateTime createTime;
}