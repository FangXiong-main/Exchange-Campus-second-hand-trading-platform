package com.fangxiong.mapper;

import com.fangxiong.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrdersMapper {
    Orders selectById(Integer orderId);
    List<Orders> selectAll();
    int insert(Orders orders);
    int update(Orders orders);
    int deleteById(Integer orderId);
    Integer selectCount();
    Integer countTodayOrder();
}