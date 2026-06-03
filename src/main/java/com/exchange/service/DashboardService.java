package com.exchange.service;

import com.exchange.vo.DashboardVO;
import com.exchange.vo.RecentGoodsVO;
import com.exchange.vo.TodoVO;

import java.util.List;

public interface DashboardService {

    DashboardVO getDashboardData();

    List<RecentGoodsVO> getRecentGoods();

    List<TodoVO> getTodoList();
}
