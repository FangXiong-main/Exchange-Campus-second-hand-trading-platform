package com.fangxiong.service.impl;

import com.fangxiong.mapper.GoodsMapper;
import com.fangxiong.pojo.Goods;
import com.fangxiong.service.GoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
    public Goods findById(Integer id) {
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
    public void deleteById(Integer id) {
        goodsMapper.deleteById(id);
    }
}
