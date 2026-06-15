package com.exchange.service;

import com.exchange.pojo.Orders;
import com.exchange.vo.OrderVO;
import com.exchange.vo.Result;

import java.util.List;

public interface OrderService {
    Orders getOrderDetails(Long orderId);
    Result cancelOrder(Long orderId);
    List<OrderVO> getMyOrdersById(Long id);

    Result createOrder(Long goodsId, Integer payType);

    Result confirmOrder(Long id);

    Integer getUnresolvedOrdersCount();

    Result deleteOrder(Long id);

    Result getOrderDetailsById(Long id);

    Result operateDrawback(Long id);
}
