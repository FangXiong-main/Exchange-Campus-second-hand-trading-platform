package com.exchange.service;

import com.exchange.vo.OrderDetailsVO;
import com.exchange.vo.OrderVO;

import java.util.List;

public interface OrderService {
    OrderDetailsVO getOrderDetails(Long orderId);
    void cancelOrder(Long orderId);
    List<OrderVO> getMyOrdersById(Long id);
}
