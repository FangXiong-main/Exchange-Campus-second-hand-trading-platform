package com.fangxiong.vo;

import lombok.Data;

@Data
public class DashboardVO {
    private Integer userCount;
    private Integer goodsCount;
    private Integer orderCount;
    private Integer todayOrderCount;
}
