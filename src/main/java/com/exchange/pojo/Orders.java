package com.exchange.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    private Long id;
    private Long goodsId;
    private Long buyerId;
    private Integer status;

    private LocalDateTime payTime;     // 支付时间
    private LocalDateTime finishTime;  // 完成时间

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
