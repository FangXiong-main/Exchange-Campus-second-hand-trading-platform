package com.fangxiong.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String detailInfo;
    private String images;
    private Integer type;
    private String rejectReason;
    private Integer saleStatus;
    private Integer auditStatus;
    private Integer userId;
    private String username;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}