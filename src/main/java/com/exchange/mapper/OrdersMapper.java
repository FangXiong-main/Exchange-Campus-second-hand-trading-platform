package com.exchange.mapper;

import com.exchange.pojo.Orders;
import com.exchange.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrdersMapper {
    Long selectCount();

    Long countTodayOrder();

    List<OrderVO> selectMyOrdersById(Long id);

    Orders getOrderDetailsById(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    Orders findById(Long id);

    void cancelOrder(
            @Param("orderId") Long orderId,
            @Param("finishTime") LocalDateTime finishTime
    );

    void createOrder(
            @Param("orders") Orders orders,
            @Param("generatedOrderId") Long generatedOrderId
    );

    void updateOrderStatus(
            @Param("id") Long id,
            @Param("status") Integer status,
            @Param("updateTime") LocalDateTime updateTime,
            @Param("finishTime") LocalDateTime finishTime
    );

    Integer getUnresolvedOrdersCount(Long userId);

    void deleteById(Long id);
}