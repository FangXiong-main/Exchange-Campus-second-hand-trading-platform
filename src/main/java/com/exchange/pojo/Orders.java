package com.exchange.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    private String id;
    private Long goodsId;
    private Long buyerId;
    private Long sellerId;
    private Integer payType;

    private String goodsName;
    private BigDecimal goodsPrice;
    private String goodsImage;
    private String goodsDetail; // 新增
    private String sellerName;  // 新增

    private Integer status;

    private LocalDateTime payTime;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
