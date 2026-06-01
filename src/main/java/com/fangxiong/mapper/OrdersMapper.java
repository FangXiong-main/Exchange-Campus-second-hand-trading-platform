package com.fangxiong.mapper;

import com.fangxiong.pojo.Orders;
import com.fangxiong.vo.OrderDetailsVO;
import com.fangxiong.vo.OrderVO;
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