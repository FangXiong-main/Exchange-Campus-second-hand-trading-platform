package com.exchange.service.impl;

import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.DeleteFileUtil;
import com.exchange.mapper.GoodsMapper;
import com.exchange.mapper.OrdersMapper;
import com.exchange.mapper.UserMapper;
import com.exchange.pojo.Goods;
import com.exchange.pojo.Orders;
import com.exchange.pojo.User;
import com.exchange.service.OrderService;
import com.exchange.vo.OrderVO;
import com.exchange.vo.Result;
import com.fangxiong.utils.redis.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.exchange.constants.SystemConstants.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private DeleteFileUtil deleteFileUtil;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrdersMapper ordersMapper;
    @Override
    public List<OrderVO> getMyOrdersById(Long id) {
        return ordersMapper.selectMyOrdersById(id);
    }

    @Transactional
    @Override
    public Result createOrder(Long goodsId, Integer payType) {
        try {
            Boolean enableLock = redisUtils.enableLock(USER_PURCHASE_GOODS_LOCK_KEY + goodsId);
            if (!enableLock) {
                return Result.error("商品正在被其他用户购买,购买失败");
            }
            BigDecimal goodsPrice = goodsMapper.selectGoodsPriceById(goodsId);
            BigDecimal userBalance = userMapper.selectBalanceById(CurrentHolder.getCurrentUserInfo().getId());
            if (userBalance.compareTo(goodsPrice) < 0){
                return Result.error("余额不足");
            }
            Goods goodsDetails = goodsMapper.findById(goodsId);
            if(goodsDetails ==  null){
                return Result.error("商品不存在");
            } else if (goodsDetails.getAuditStatus() == 0) {
                return Result.error("商品审核未通过(在购买时修改了商品信息)");
            } else if (goodsDetails.getSaleStatus() == 2) {
                return Result.error("商品已售出");
            }
            User seller = userMapper.selectById(goodsDetails.getUserId());
            if (seller.getRole()==-1){
                return Result.error("卖家已被封禁");
            }
            goodsMapper.updateSaleStatus(goodsId, 2);
            userMapper.updateBalance(CurrentHolder.getCurrentUserInfo().getId(), userBalance.subtract(goodsPrice));
            userMapper.addNewWalletUseLog(CurrentHolder.getCurrentUserInfo().getId(),1, goodsPrice, LocalDateTime.now());
            Orders newOrder = new Orders();
            newOrder.setGoodsId(goodsId);
            newOrder.setBuyerId(CurrentHolder.getCurrentUserInfo().getId());
            newOrder.setSellerId(goodsDetails.getUserId());
            newOrder.setGoodsName(goodsDetails.getName());
            newOrder.setGoodsPrice(goodsPrice);
            newOrder.setGoodsImage(goodsDetails.getImages());
            newOrder.setStatus(1);
            newOrder.setCreateTime(LocalDateTime.now());
            newOrder.setPayTime(LocalDateTime.now());
            newOrder.setPayType(payType);
            newOrder.setSellerId(seller.getId());
            newOrder.setSellerName(seller.getUsername());
            newOrder.setGoodsDetail(goodsDetails.getDetailInfo());
            Long generatedOrderId = redisUtils.uniqueIdGenerator(EXCHANGE_ORDER_INCR_ID_KEY_PREFIX, EXCHANGE_UUID_TIME_KEY_FORMAT, EXCHANGE_ORDER_START_TIME, EXCHANGE_ORDER_ID_TIMESTAMP_LENGTH, EXCHANGE_ORDER_ID_MACHINE_CODE_LENGTH, EXCHANGE_ORDER_ID_SEQUENCE_LENGTH, EXCHANGE_MACHINE_CODE);
            ordersMapper.createOrder(newOrder,generatedOrderId);
            return Result.success(generatedOrderId.toString());
        } catch (Exception e){
            log.info("购买商品出现错误:{}",e.getMessage());
            //回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("购买失败，请重试");
        } finally {
            redisUtils.disableLock(USER_PURCHASE_GOODS_LOCK_KEY + goodsId);
        }
    }

    @Transactional
    @Override
    public Result confirmOrder(Long id) {
        Orders order = ordersMapper.findById(id);
        if (order == null){
            return Result.error("订单不存在");
        } else if (order.getStatus()==3||order.getStatus()==4) {
            return Result.error("当前订单状态不支持该操作");
        }
        try {
            Boolean enableLock = redisUtils.enableLock(USER_CONFIRM_ORDER_LOCK_KEY+id);
            if (!enableLock){
                return Result.error("订单正在被（卖家/买家）确认,请勿重复点击");
            }
            if (order.getBuyerId().equals(CurrentHolder.getCurrentUserInfo().getId())){
                if (order.getStatus() == 2){
                    ordersMapper.updateOrderStatus(id,4,LocalDateTime.now(),LocalDateTime.now());
                    BigDecimal goodsPrice = order.getGoodsPrice();
                    BigDecimal exchangeIncome = goodsPrice.multiply(EXCHANGE_DEDUCTION_RATE);
                    BigDecimal sellerIncome = goodsPrice.subtract(exchangeIncome);
                    BigDecimal sellerOriginalBalance = userMapper.selectBalanceById(order.getSellerId());
                    userMapper.updateBalance(order.getSellerId(),sellerOriginalBalance.add(sellerIncome));
                    userMapper.addNewWalletUseLog(order.getSellerId(),2,goodsPrice,LocalDateTime.now());
                    userMapper.addNewWalletUseLog(order.getSellerId(),5,exchangeIncome,LocalDateTime.now());
                    Long schoolAdminId = userMapper.findSchoolAdminId(CurrentHolder.getCurrentUserInfo().getSchool());
                    userMapper.updateBalance(schoolAdminId,userMapper.selectBalanceById(schoolAdminId).add(exchangeIncome));
                    userMapper.addNewWalletUseLog(schoolAdminId,2,exchangeIncome,LocalDateTime.now());
                    return Result.success("确认成功");
                } else {
                    return Result.error("请等待卖家确认面交");
                }
            } else if (order.getSellerId().equals(CurrentHolder.getCurrentUserInfo().getId())) {
                if (order.getStatus() == 1){
                    ordersMapper.updateOrderStatus(id,2,LocalDateTime.now(),null);
                    return Result.success("确认成功");
                } else {
                    return Result.error("未知状态");
                }
            } else {
                return Result.error("请勿非法操作");
            }
        } catch (Exception e) {
            log.info("确认订单出现错误:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("确认失败，请重试");
        } finally {
            redisUtils.disableLock(USER_CONFIRM_ORDER_LOCK_KEY+id);
        }
    }

    @Override
    public Integer getUnresolvedOrdersCount() {
        return ordersMapper.getUnresolvedOrdersCount(CurrentHolder.getCurrentUserInfo().getId());
    }

    @Override
    public Result deleteOrder(Long id) {
        Orders order = ordersMapper.findById(id);
        if (order == null){
            return Result.error("订单不存在");
        } else if (!order.getBuyerId().equals(CurrentHolder.getCurrentUserInfo().getId())) {
            return Result.error("只有买家才能删除订单");
        }
        try {
            if (!redisUtils.enableLock(USER_DELETE_ORDER_OR_GOODS_LOCK_KEY+ order.getGoodsId())) {
                return Result.error("删除失败，请稍后重试");
            }
            Boolean isExist = goodsMapper.selectGoodsIsExist(order.getGoodsId());
            if (!isExist){
                if (!deleteFileUtil.deleteFile(order.getGoodsImage())) {
                    return Result.error("删除失败");
                }
            }
            ordersMapper.deleteById(id);
        } catch (Exception e) {
            log.info("删除订单出现错误:{}",e.getMessage());
            throw new RuntimeException(e);
        } finally {
            redisUtils.disableLock(USER_DELETE_ORDER_OR_GOODS_LOCK_KEY+ id);
        }
        return Result.success();
    }

    @Override
    public Orders getOrderDetails(Long orderId) {
        return ordersMapper.getOrderDetailsById(orderId, CurrentHolder.getCurrentUserInfo().getId());
    }

    @Transactional
    @Override
    public Result cancelOrder(Long orderId) {
        try {
            Boolean enableLock = redisUtils.enableLock(USER_CANCEL_ORDER_LOCK_KEY + orderId);
            if (!enableLock){
                return Result.error("订单正在被取消，请勿重复点击");
            }
            Orders order = ordersMapper.findById(orderId);
            if (order == null){
                return Result.error("订单不存在");
            }
            if (!order.getBuyerId().equals(CurrentHolder.getCurrentUserInfo().getId())&&!order.getSellerId().equals(CurrentHolder.getCurrentUserInfo().getId())){
                return Result.error("请勿非法操作");
            }
            if (order.getStatus()==3||order.getStatus()==4){
                return Result.error("当前订单状态不支持该操作");
            }
            BigDecimal goodsPrice = order.getGoodsPrice();
            BigDecimal buyerOriginalBalance = userMapper.selectBalanceById(order.getBuyerId());
            userMapper.updateBalance(order.getBuyerId(),buyerOriginalBalance.add(goodsPrice));
            userMapper.addNewWalletUseLog(order.getBuyerId(),3,goodsPrice,LocalDateTime.now());
            ordersMapper.cancelOrder(orderId,LocalDateTime.now());
            return Result.success("取消成功");
        } catch (Exception e) {
            log.info("取消订单出现错误:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("取消失败，请重试");
        } finally {
            redisUtils.disableLock(USER_CANCEL_ORDER_LOCK_KEY + orderId);
        }
    }
}
