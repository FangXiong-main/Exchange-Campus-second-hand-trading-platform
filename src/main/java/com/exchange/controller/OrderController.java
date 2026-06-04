package com.exchange.controller;

import com.exchange.Utils.CurrentHolder;
import com.exchange.service.OrderService;
import com.exchange.vo.OrderVO;
import com.exchange.vo.PageResult;
import com.exchange.vo.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/my-orders")
    public Result myOrders(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<OrderVO> myOrdersById = orderService.getMyOrdersById(CurrentHolder.getCurrentUserInfo().getId());
        PageInfo<OrderVO> pageInfo = new PageInfo<>(myOrdersById);
        PageResult<OrderVO> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return Result.success(pageResult);
    }
    @GetMapping("/orderDetail")
    public Result getDetail(@RequestParam Long id) {
        return Result.success(orderService.getOrderDetails(id));
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestParam Long id) {
        return orderService.cancelOrder(id);
    }

    @PostMapping("/createOrder")
    public Result createOrder(@RequestBody Map<String, Object> params) {
        Long goodsId = Long.parseLong(params.get("goodsId").toString());
        Integer payType = Integer.parseInt(params.get("payType").toString());
        return orderService.createOrder(goodsId, payType);
    }

    @PostMapping("/confirm")
    public Result confirm(@RequestParam Long id) {
        return orderService.confirmOrder(id);
    }

    @GetMapping("/getUnresolvedOrdersCount")
    public Result getUnresolvedOrdersCount() {
        return Result.success(orderService.getUnresolvedOrdersCount());
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Long id) {
        return orderService.deleteOrder(id);
    }

}
