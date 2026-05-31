package com.fangxiong.service.impl;

import com.fangxiong.mapper.OrdersMapper;
import com.fangxiong.service.OrderService;
import com.fangxiong.vo.OrderDetailsVO;
import com.fangxiong.vo.OrderVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrdersMapper ordersMapper;
    @Override
    public List<OrderVO> getMyOrdersById(Long id) {
        return ordersMapper.selectMyOrdersById(id);
    }

    @Override
    public OrderDetailsVO getOrderDetails(Long orderId) {
        return ordersMapper.getOrderDetailsById(orderId);
    }

    @Override
    public void cancelOrder(Long orderId) {
        ordersMapper.cancelOrder(orderId);
    }
}
