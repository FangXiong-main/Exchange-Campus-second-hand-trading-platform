package com.exchange.service.impl;

import com.exchange.Utils.CurrentHolder;
import com.exchange.mapper.GoodsMapper;
import com.exchange.mapper.OrdersMapper;
import com.exchange.mapper.PostMapper;
import com.exchange.mapper.UserMapper;
import com.exchange.service.DashboardService;
import com.exchange.vo.DashboardVO;
import com.exchange.vo.RecentGoodsVO;
import com.exchange.vo.TodoVO;

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
    @Resource
    private PostMapper postMapper;

    // 统计
    @Override
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();
        vo.setUserCount(userMapper.selectCount(CurrentHolder.getCurrentUserInfo().getSchool()));
        vo.setGoodsCount(goodsMapper.selectCount(CurrentHolder.getCurrentUserInfo().getSchool()));
        vo.setPostCount(postMapper.selectPostCount(CurrentHolder.getCurrentUserInfo().getSchool()));
        vo.setBandedUserCount(userMapper.selectBandedUserCount(CurrentHolder.getCurrentUserInfo().getSchool()));
        return vo;
    }

    // 最近商品
    @Override
    public List<RecentGoodsVO> getRecentGoods() {
        return goodsMapper.selectRecentGoods(CurrentHolder.getCurrentUserInfo().getSchool());
    }

    // 待审核
    @Override
    public List<TodoVO> getTodoList() {
        return goodsMapper.selectWaitCheck(CurrentHolder.getCurrentUserInfo().getSchool());
    }
}