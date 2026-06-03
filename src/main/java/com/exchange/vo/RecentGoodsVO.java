package com.exchange.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecentGoodsVO {
    private String name;
    private String username;
    private BigDecimal price;
    private String time;
}
