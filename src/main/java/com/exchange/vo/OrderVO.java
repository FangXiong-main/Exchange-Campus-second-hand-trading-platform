package com.exchange.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private String id;
    private String goodsName;  // 商品名称
    private String images;     // 商品图片（逗号分隔）
    private BigDecimal price;  // 商品价格
    private Long buyerId;    // 买家ID
    private Long sellerId;   // 卖家ID（商品发布者ID）
    private Integer status;
    private LocalDateTime createTime; // 下单时间
}
