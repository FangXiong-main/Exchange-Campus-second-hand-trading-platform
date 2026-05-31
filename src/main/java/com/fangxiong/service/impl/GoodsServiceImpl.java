package com.fangxiong.service.impl;

import com.fangxiong.mapper.GoodsMapper;
import com.fangxiong.pojo.Goods;
import com.fangxiong.service.GoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    // ====================== 待审核列表 ======================
    @Override
    public List<Goods> findAuditGoods() {
        return goodsMapper.findAuditGoods();
    }

    // ====================== 已驳回列表 ======================
    @Override
    public List<Goods> findRejectedGoods() {
        return goodsMapper.findRejectedGoods();
    }

    // ====================== 查询详情 ======================
    @Override
    public Goods findById(Long id) {
        return goodsMapper.findById(id);
    }

    // ====================== 审核通过 ======================
    @Override
    public void auditPass(Integer id) {
        goodsMapper.updateAuditPass(id);
    }

    // ====================== 驳回 ======================
    @Override
    public void auditReject(Integer id, String rejectReason) {
        goodsMapper.updateAuditReject(id, rejectReason);
    }

    // ====================== 删除 ======================
    @Override
    public void deleteById(Long id) {
        goodsMapper.deleteById(id);
    }

    @Override
    public List<Goods> myPublish(Long id) {
        return goodsMapper.selectMyPublish(id);
    }

    @Override
    public List<Goods> myFavorite(Long id) {
        return goodsMapper.selectMyFavorite(id);
    }

    @Override
    public void updateGoods(Goods goods) {
        goods.setUpdateTime(LocalDateTime.now());
        goods.setAuditStatus(0);
        goods.setSaleStatus(0);
        goodsMapper.updateGoods(goods);
    }

    @Override
    public void updateSaleStatus(Long id, Integer saleStatus) {
        goodsMapper.updateSaleStatus(id, saleStatus);
    }

    @Override
    public void addGoods(Goods goods) {
        goodsMapper.addGoods(goods);
    }
}
