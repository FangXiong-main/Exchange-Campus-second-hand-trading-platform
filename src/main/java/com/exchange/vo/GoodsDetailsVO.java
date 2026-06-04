package com.exchange.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDetailsVO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String images;
    private String detailInfo;
    private String username;
    private Long userId;
    private String avatarUrl;
    private LocalDateTime createTime;
    private Integer isLiked; //0 not 1 yes
    private String rejectReason;
    private Integer saleStatus;
    private Integer auditStatus;
}
