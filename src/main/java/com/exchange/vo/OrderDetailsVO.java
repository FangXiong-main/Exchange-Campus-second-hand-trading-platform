package com.exchange.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsVO {
    // 订单
    private Long id;
    private Long goodsId;
    private Long buyerId;
    private Integer status;

    private LocalDateTime remainedPayTime;
    private LocalDateTime payTime;
    private LocalDateTime finishTime;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 商品
    private String name;
    private String price;
    private String detailInfo;
    private String images;

    // 卖家
    private Long sellerId;
    private String sellerName;
    private String avatarUrl;
}
