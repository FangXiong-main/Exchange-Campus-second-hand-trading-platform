package com.fangxiong.controller;

import com.fangxiong.Utils.CurrentHolder;
import com.fangxiong.service.OrderService;
import com.fangxiong.vo.OrderDetailsVO;
import com.fangxiong.vo.OrderVO;
import com.fangxiong.vo.PageResult;
import com.fangxiong.vo.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/my-orders")
    public Result myOrders(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<OrderVO> myOrdersById = orderService.getMyOrdersById(CurrentHolder.getCurrentUserInfo().getId());
        PageInfo<OrderVO> pageInfo = new PageInfo<>(myOrdersById);
        PageResult<OrderVO> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return Result.success(pageResult);
    }
    @GetMapping("/orderDetail")
    public Result getDetail(@RequestParam Long id) {
        OrderDetailsVO vo = orderService.getOrderDetails(id);
        return Result.success(vo);
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestParam Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

}
