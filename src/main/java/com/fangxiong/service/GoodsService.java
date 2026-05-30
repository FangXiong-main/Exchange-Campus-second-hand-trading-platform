package com.fangxiong.service;

import com.fangxiong.pojo.Goods;

import java.util.List;

public interface GoodsService {
    List<Goods> findAuditGoods();
    Goods findById(Integer id);
    void auditPass(Integer id);
    void auditReject(Integer id, String rejectReason);
    // 已驳回商品分页
    List<Goods> findRejectedGoods();

    // 删除商品
    void deleteById(Integer id);
}
