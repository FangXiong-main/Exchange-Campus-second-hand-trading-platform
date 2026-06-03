package com.exchange.mapper;

import com.exchange.vo.OrderDetailsVO;
import com.exchange.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrdersMapper {
    Long selectCount();
    Long countTodayOrder();

    List<OrderVO> selectMyOrdersById(Long id);

    OrderDetailsVO getOrderDetailsById(Long orderId);

    void cancelOrder(Long orderId);
}