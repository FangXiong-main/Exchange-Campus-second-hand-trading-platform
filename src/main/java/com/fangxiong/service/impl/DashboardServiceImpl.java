package com.fangxiong.service.impl;

import com.fangxiong.mapper.GoodsMapper;
import com.fangxiong.mapper.OrdersMapper;
import com.fangxiong.mapper.UserMapper;
import com.fangxiong.service.DashboardService;
import com.fangxiong.vo.DashboardVO;
import com.fangxiong.vo.RecentGoodsVO;
import com.fangxiong.vo.TodoVO;

import java.util.List;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private OrdersMapper ordersMapper;

    // 统计
    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();
        vo.setUserCount(userMapper.selectCount());
        vo.setGoodsCount(goodsMapper.selectCount());
        vo.setOrderCount(ordersMapper.selectCount());
        vo.setTodayOrderCount(ordersMapper.countTodayOrder());
        return vo;
    }

    // 最近商品
    @Override
    public List<RecentGoodsVO> getRecentGoods() {
        return goodsMapper.selectRecentGoods();
    }

    // 待审核
    @Override
    public List<TodoVO> getTodoList() {
        return goodsMapper.selectWaitCheck();
    }
}