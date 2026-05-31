package com.fangxiong.service;

import com.fangxiong.dto.LoginResult;
import com.fangxiong.pojo.Goods;

import java.util.List;

public interface GoodsService {
    List<Goods> findAuditGoods();
    Goods findById(Long id);
    void auditPass(Integer id);
    void auditReject(Integer id, String rejectReason);
    // 已驳回商品分页
    List<Goods> findRejectedGoods();

    // 删除商品
    void deleteById(Long id);

    List<Goods> myPublish(Long id);

    List<Goods> myFavorite(Long id);

    void updateGoods(Goods goods);

    void updateSaleStatus(Long id, Integer saleStatus);

    void addGoods(Goods goods);
}
