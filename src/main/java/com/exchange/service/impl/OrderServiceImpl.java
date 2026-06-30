package com.exchange.service.impl;

import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.MoveFileUtil;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.exchange.constants.SystemConstants.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private MoveFileUtil moveFileUtil;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrdersMapper ordersMapper;

    private String orderRequestUUIDGenerator(){
         return redisUtils.uniqueIdGenerator(
                EXCHANGE_ORDER_REQUEST_INCR_ID_KEY_PREFIX,
                EXCHANGE_UUID_TIME_KEY_FORMAT,
                EXCHANGE_UUID_START_TIME,
                EXCHANGE_ORDER_REQUEST_ID_TIMESTAMP_LENGTH,
                EXCHANGE_ORDER_REQUEST_ID_MACHINE_CODE_LENGTH,
                EXCHANGE_ORDER_REQUEST_ID_SEQUENCE_LENGTH,
                EXCHANGE_MACHINE_CODE
        ).toString();
    }

    private String financeRequestUUIDGenerator(){
        return redisUtils.uniqueIdGenerator(
                EXCHANGE_FINANCE_REQUEST_INCR_ID_KEY_PREFIX,
                EXCHANGE_UUID_TIME_KEY_FORMAT,
                EXCHANGE_UUID_START_TIME,
                EXCHANGE_FINANCE_REQUEST_ID_TIMESTAMP_LENGTH,
                EXCHANGE_FINANCE_REQUEST_ID_MACHINE_CODE_LENGTH,
                EXCHANGE_FINANCE_REQUEST_ID_SEQUENCE_LENGTH,
                EXCHANGE_MACHINE_CODE
        ).toString();
    }

    private boolean requestIsExpired(Long startTime){
        return startTime - System.nanoTime() >= EXCHANGE_ORDER_PROCESS_TIME_LIMIT;
    }

    @Override
    public List<OrderVO> getMyOrdersById(Long id) {
        return ordersMapper.selectMyOrdersById(id);
    }

    @Transactional
    @Override
    public Result createOrder(Long goodsId, Integer payType) {
        String orderRequestUUID = orderRequestUUIDGenerator();
        String financeRequestUUID = financeRequestUUIDGenerator();
        Boolean orderRequestLocked = false;Boolean financeRequestLocked = false;
        try {
            Long processStartTime = System.nanoTime();
            orderRequestLocked = redisUtils.enableLock(USER_PURCHASE_GOODS_LOCK_KEY + goodsId, orderRequestUUID);
            financeRequestLocked = redisUtils.enableLock(USER_FIANCE_OPERATION_LOCK_KEY + CurrentHolder.getCurrentUserInfo().getId(), financeRequestUUID);
            if (!orderRequestLocked) {
                return Result.error("商品正在被其他用户购买,购买失败");
            }
            if (!financeRequestLocked) {
                redisUtils.disableLock(USER_PURCHASE_GOODS_LOCK_KEY + goodsId, orderRequestUUID);
                return Result.error("正在执行操作,请勿进行钱款操作");
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
            if (!seller.getSchool().equals(CurrentHolder.getCurrentUserInfo().getSchool())) {
                return Result.error("商品不属于您所在的学校");
            }
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtils.disableLock(USER_PURCHASE_GOODS_LOCK_KEY + goodsId,orderRequestUUID);
                            redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + CurrentHolder.getCurrentUserInfo().getId(), financeRequestUUID);
                        }
                    }
            );
            int updated = goodsMapper.updateSaleStatus(goodsId, 2);
            if (updated == 0){
                return Result.error("手太慢啦，商品已经被抢走啦");
            }
            if (requestIsExpired(processStartTime)){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                redisUtils.disableLock(USER_PURCHASE_GOODS_LOCK_KEY+goodsId,orderRequestUUID);
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY+CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
                return Result.error("订单处理超时");
            }
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
            Long generatedOrderId = redisUtils.uniqueIdGenerator(EXCHANGE_ORDER_INCR_ID_KEY_PREFIX, EXCHANGE_UUID_TIME_KEY_FORMAT, EXCHANGE_UUID_START_TIME, EXCHANGE_ORDER_ID_TIMESTAMP_LENGTH, EXCHANGE_ORDER_ID_MACHINE_CODE_LENGTH, EXCHANGE_ORDER_ID_SEQUENCE_LENGTH, EXCHANGE_MACHINE_CODE);
            ordersMapper.createOrder(newOrder,generatedOrderId);
            return Result.success(generatedOrderId.toString());
        } catch (Exception e){
            log.info("购买商品出现错误:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (orderRequestLocked){
                redisUtils.disableLock(USER_PURCHASE_GOODS_LOCK_KEY+goodsId,orderRequestUUID);
            }
            if (financeRequestLocked){
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY+CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
            }
            return Result.error("购买失败，请重试");
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
        String orderRequestUUID = orderRequestUUIDGenerator();
        String financeRequestUUID = financeRequestUUIDGenerator();
        Boolean orderRequestLocked = false;Boolean financeRequestLocked = false;
        try {
            Long processStartTime = System.nanoTime();
            orderRequestLocked = redisUtils.enableLock(USER_CONFIRM_ORDER_LOCK_KEY+id,orderRequestUUID);
            financeRequestLocked = redisUtils.enableLock(USER_FIANCE_OPERATION_LOCK_KEY+CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
            if (!orderRequestLocked){
                return Result.error("订单正在被（卖家/买家）确认,请勿重复点击");
            }
            if (!financeRequestLocked) {
                redisUtils.disableLock(USER_CONFIRM_ORDER_LOCK_KEY+id,orderRequestUUID);
                return Result.error("正在执行其他钱款操作,请稍后重试");
            }
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtils.disableLock(USER_CONFIRM_ORDER_LOCK_KEY + id,orderRequestUUID);
                            redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + CurrentHolder.getCurrentUserInfo().getId(), financeRequestUUID);
                        }
                    }
            );
            if (order.getBuyerId().equals(CurrentHolder.getCurrentUserInfo().getId())){
                if (order.getStatus() == 2){
                    int updated = ordersMapper.updateOrderStatus(id, 2, 4, LocalDateTime.now(), LocalDateTime.now());
                    if (updated == 0){
                        return Result.error("请勿重复确认订单");
                    }
                    if (requestIsExpired(processStartTime)){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        redisUtils.disableLock(USER_CONFIRM_ORDER_LOCK_KEY+id,orderRequestUUID);
                        redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY+CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
                        return Result.error("处理超时,请重试");
                    }
                    BigDecimal goodsPrice = order.getGoodsPrice();
                    BigDecimal exchangeIncome = goodsPrice.multiply(EXCHANGE_DEDUCTION_RATE);
                    BigDecimal sellerIncome = goodsPrice.subtract(exchangeIncome);
                    BigDecimal sellerOriginalBalance = userMapper.selectBalanceById(order.getSellerId());
                    userMapper.updateBalance(order.getSellerId(),sellerOriginalBalance.add(sellerIncome));
                    userMapper.addNewWalletUseLog(order.getSellerId(),2,goodsPrice,LocalDateTime.now());
                    userMapper.addNewWalletUseLog(order.getSellerId(),5,exchangeIncome,LocalDateTime.now());
                    //Long schoolAdminId = userMapper.findSchoolAdminId(CurrentHolder.getCurrentUserInfo().getSchool());
                    Long schoolAdminId = EXCHANGE_OFFICIAL_ID;
                    userMapper.updateBalance(schoolAdminId,userMapper.selectBalanceById(schoolAdminId).add(exchangeIncome));
                    userMapper.addNewWalletUseLog(schoolAdminId,2,exchangeIncome,LocalDateTime.now());
                    return Result.success("确认成功");
                } else if(order.getStatus() == 1){
                    return Result.error("请等待卖家确认面交");
                } else {
                    return Result.error("当前订单不支持该操作");
                }
            } else if (order.getSellerId().equals(CurrentHolder.getCurrentUserInfo().getId())) {
                if (order.getStatus() == 1){
                    int updated = ordersMapper.updateOrderStatus(id, 1, 2, LocalDateTime.now(), null);
                    if (updated == 0){
                        return Result.error("请稍后重试");
                    }
                    if (requestIsExpired(processStartTime)){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        redisUtils.disableLock(USER_CONFIRM_ORDER_LOCK_KEY+id,orderRequestUUID);
                        redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY+CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
                        return Result.error("处理超时,请重试");
                    }
                    return Result.success("确认成功");
                } else {
                    return Result.error("未知状态");
                }
            } else {
                return Result.error("请勿非法操作");
            }
        } catch (Exception e) {
            log.info("确认订单出现错误:{}",e.toString());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (orderRequestLocked){
                redisUtils.disableLock(USER_CONFIRM_ORDER_LOCK_KEY+id,orderRequestUUID);
            }
            if (financeRequestLocked){
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY+CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
            }
            return Result.error("确认失败，请重试");
        }
    }

    @Override
    public Integer getUnresolvedOrdersCount() {
        return ordersMapper.getUnresolvedOrdersCount(CurrentHolder.getCurrentUserInfo().getId());
    }

    @Transactional
    @Override
    public Result deleteOrder(Long id) {
        Orders order = ordersMapper.findById(id);
        String tempImageUrl = null;
        boolean needToMoveBackFile = false;
        if (order == null){
            return Result.error("订单不存在");
        } else if (!order.getBuyerId().equals(CurrentHolder.getCurrentUserInfo().getId())) {
            return Result.error("只有买家才能删除订单");
        }
        try {
            Boolean isExist = goodsMapper.selectGoodsIsExist(order.getGoodsId());
            if (!isExist){
               tempImageUrl = moveFileUtil.moveRealToTemp(order.getGoodsImage());
               needToMoveBackFile = true;
            }
            ordersMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            log.info("删除订单出现错误:{}",e.getMessage());
            if (needToMoveBackFile){
                moveFileUtil.moveTempToReal(tempImageUrl);
            }
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error("删除订单出现错误");
        }
    }

    @Override
    public Result getOrderDetailsById(Long id) {
        Orders orderDetailsByOrderId = ordersMapper.getOrderDetailsByOrderId(id);
        if (orderDetailsByOrderId == null){
            return Result.error("订单不存在或未完成");
        }
        Long buyerSchool = userMapper.selectUserSchoolId(orderDetailsByOrderId.getBuyerId());
        if (!buyerSchool.equals(CurrentHolder.getCurrentUserInfo().getSchool())){
            return Result.error("请勿非法操作,该订单不属于您的管辖范围内");
        }
        return Result.success(orderDetailsByOrderId);
    }

    @Transactional
    @Override
    public Result operateDrawback(Long id) {
        Orders orderDetailsByOrderId = ordersMapper.getOrderDetailsByOrderId(id);
        if (orderDetailsByOrderId == null){
            return Result.error("订单不存在或未完成");
        }
        if (orderDetailsByOrderId.getStatus()!=4){
            return Result.error("当前订单状态不支持该操作");
        }
        Long buyerSchool = userMapper.selectUserSchoolId(orderDetailsByOrderId.getBuyerId());
        if (!buyerSchool.equals(CurrentHolder.getCurrentUserInfo().getSchool())){
            return Result.error("请勿非法操作,该订单不属于您的管辖范围内");
        }
        String orderRequestUUID = orderRequestUUIDGenerator();
        String financeRequestUUID = financeRequestUUIDGenerator();
        boolean orderRequestLocked = false;
        boolean financeRequestLocked = false;
        try {
            Long processStartTime = System.nanoTime();
            orderRequestLocked = redisUtils.enableLock(ADMIN_ORDER_DRAWBACK_LOCK_KEY+ id,orderRequestUUID);
            financeRequestLocked = redisUtils.enableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getSellerId(),financeRequestUUID)&&redisUtils.enableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getBuyerId(),financeRequestUUID);
            if (!orderRequestLocked) {
                return Result.error("正在处理退款中，请勿重复操作");
            }
            if (!financeRequestLocked) {
                redisUtils.enableLock(ADMIN_ORDER_DRAWBACK_LOCK_KEY+ id,orderRequestUUID);
                return Result.error("监测到卖家或买家正在进行其他钱款操作，请稍后再试");
            }
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtils.disableLock(ADMIN_ORDER_DRAWBACK_LOCK_KEY + id,orderRequestUUID);
                            redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getSellerId(),financeRequestUUID);
                            redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getBuyerId(),financeRequestUUID);
                        }
                    }
            );
            BigDecimal sellerBalance = userMapper.selectBalanceById(orderDetailsByOrderId.getSellerId());
            if (sellerBalance.compareTo(orderDetailsByOrderId.getGoodsPrice())<0){
                return Result.error("卖家余额不足,请参照管理员手册处理");
            }
            int updated = ordersMapper.updateOrderStatus(id, 4, 3, LocalDateTime.now(), LocalDateTime.now());
            if (updated == 0){
                return Result.error("操作失败，请重试");
            }
            if (requestIsExpired(processStartTime)){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                redisUtils.disableLock(ADMIN_ORDER_DRAWBACK_LOCK_KEY + id,orderRequestUUID);
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getSellerId(),financeRequestUUID);
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getBuyerId(),financeRequestUUID);
                return Result.error("操作已超时，请重试");
            }
            userMapper.updateBalance(orderDetailsByOrderId.getSellerId(),sellerBalance.subtract(orderDetailsByOrderId.getGoodsPrice()));
            userMapper.addNewWalletUseLog(orderDetailsByOrderId.getSellerId(),5,orderDetailsByOrderId.getGoodsPrice(),LocalDateTime.now());
            userMapper.updateBalance(orderDetailsByOrderId.getBuyerId(),userMapper.selectBalanceById(orderDetailsByOrderId.getBuyerId()).add(orderDetailsByOrderId.getGoodsPrice()));
            userMapper.addNewWalletUseLog(orderDetailsByOrderId.getBuyerId(),3,orderDetailsByOrderId.getGoodsPrice(),LocalDateTime.now());
            return Result.success("操作退款成功");
        }catch (Exception e){
            log.info("管理员处理订单退款出现错误:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (orderRequestLocked){
                redisUtils.disableLock(ADMIN_ORDER_DRAWBACK_LOCK_KEY+ id,orderRequestUUID);
            }
            if (financeRequestLocked){
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getSellerId(),financeRequestUUID);
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + orderDetailsByOrderId.getBuyerId(),financeRequestUUID);
            }
            return Result.error("处理失败，请稍后重试");
        }
    }

    @Override
    public Orders getOrderDetails(Long orderId) {
        return ordersMapper.getOrderDetailsById(orderId, CurrentHolder.getCurrentUserInfo().getId());
    }

    @Transactional
    @Override
    public Result cancelOrder(Long orderId) {
        String orderRequestUUID = orderRequestUUIDGenerator();
        String financeRequestUUID = financeRequestUUIDGenerator();
        boolean orderRequestLocked = false;
        boolean financeRequestLocked = false;
        try {
            Long processStartTime = System.nanoTime();
            orderRequestLocked = redisUtils.enableLock(USER_CANCEL_ORDER_LOCK_KEY + orderId,orderRequestUUID);
            financeRequestLocked = redisUtils.enableLock(USER_FIANCE_OPERATION_LOCK_KEY+ CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
            if (!orderRequestLocked){
                return Result.error("订单正在被取消，请勿重复点击");
            }
            if (!financeRequestLocked) {
                redisUtils.disableLock(USER_CANCEL_ORDER_LOCK_KEY + orderId,orderRequestUUID);
                return Result.error("正在处理中订单中，请勿执行其他钱款操作");
            }
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtils.disableLock(USER_CANCEL_ORDER_LOCK_KEY + orderId,orderRequestUUID);
                            redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
                        }
                    }
            );
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
            int cancelled = ordersMapper.cancelOrder(orderId, LocalDateTime.now());
            if (cancelled == 0){
                return Result.error("操作失败,请重试");
            }
            if (requestIsExpired(processStartTime)){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                redisUtils.disableLock(USER_CANCEL_ORDER_LOCK_KEY + orderId,orderRequestUUID);
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
                return Result.error("操作已超时，请重试");
            }
            BigDecimal buyerOriginalBalance = userMapper.selectBalanceById(order.getBuyerId());
            userMapper.updateBalance(order.getBuyerId(),buyerOriginalBalance.add(goodsPrice));
            userMapper.addNewWalletUseLog(order.getBuyerId(),3,goodsPrice,LocalDateTime.now());
            return Result.success("取消成功");
        } catch (Exception e) {
            log.info("取消订单出现错误:{}",e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (orderRequestLocked){
                redisUtils.disableLock(USER_CANCEL_ORDER_LOCK_KEY + orderId,orderRequestUUID);
            }
            if (financeRequestLocked){
                redisUtils.disableLock(USER_FIANCE_OPERATION_LOCK_KEY + CurrentHolder.getCurrentUserInfo().getId(),financeRequestUUID);
            }
            return Result.error("取消失败，请重试");
        }
    }
}
