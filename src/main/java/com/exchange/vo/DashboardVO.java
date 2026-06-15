package com.exchange.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardVO {
    private Long userCount;
    private Long goodsCount;
    private Long postCount;
    private Long bandedUserCount;
}
