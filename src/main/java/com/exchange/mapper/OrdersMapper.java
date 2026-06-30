package com.exchange.mapper;

import com.exchange.pojo.Goods;
import com.exchange.pojo.Orders;
import com.exchange.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrdersMapper {

    List<OrderVO> selectMyOrdersById(Long id);

    Orders getOrderDetailsById(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    Orders findById(Long id);

    int cancelOrder(
            @Param("orderId") Long orderId,
            @Param("finishTime") LocalDateTime finishTime
    );

    void createOrder(
            @Param("orders") Orders orders,
            @Param("generatedOrderId") Long generatedOrderId
    );

    int updateOrderStatus(
            @Param("id") Long id,
            @Param("originalStatus") Integer originalStatus,
            @Param("status") Integer status,
            @Param("updateTime") LocalDateTime updateTime,
            @Param("finishTime") LocalDateTime finishTime
    );

    Integer getUnresolvedOrdersCount(Long userId);

    void deleteById(Long id);

    Boolean selectByGoodsIdIfExist(Long id);

    void deleteUserInfoById(Long id);

    List<String> selectUserOrdersImages(Long id);

    Orders getOrderDetailsByOrderId(Long id);
}