package com.exchange.controller;

import com.exchange.service.DashboardService;
import com.exchange.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/data")
    public Result getData() {
        return Result.success(dashboardService.getDashboardData());
    }

    @GetMapping("/recent-goods")
    public Result getRecentGoods() {
        return Result.success(dashboardService.getRecentGoods());
    }

    @GetMapping("/todo")
    public Result getTodo() {
        return Result.success(dashboardService.getTodoList());
    }
}