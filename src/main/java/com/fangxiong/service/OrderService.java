package com.fangxiong.service;

import com.fangxiong.vo.OrderDetailsVO;
import com.fangxiong.vo.OrderVO;

import java.util.List;

public interface OrderService {
    OrderDetailsVO getOrderDetails(Long orderId);
    void cancelOrder(Long orderId);
    List<OrderVO> getMyOrdersById(Long id);
}
