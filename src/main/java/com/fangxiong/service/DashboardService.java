package com.fangxiong.service;

import com.fangxiong.vo.DashboardVO;
import com.fangxiong.vo.RecentGoodsVO;
import com.fangxiong.vo.TodoVO;

import java.util.List;

public interface DashboardService {

    DashboardVO getDashboardData();

    List<RecentGoodsVO> getRecentGoods();

    List<TodoVO> getTodoList();
}
