package com.exchange.service.impl;

import com.exchange.mapper.OrdersMapper;
import com.exchange.service.OrderService;
import com.exchange.vo.OrderDetailsVO;
import com.exchange.vo.OrderVO;
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
