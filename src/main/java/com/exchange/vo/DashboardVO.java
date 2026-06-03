package com.exchange.vo;

import lombok.Data;

@Data
public class DashboardVO {
    private Long userCount;
    private Long goodsCount;
    private Long orderCount;
    private Long todayOrderCount;
}
