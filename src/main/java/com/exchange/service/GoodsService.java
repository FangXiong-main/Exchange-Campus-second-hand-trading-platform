package com.exchange.service;

import com.exchange.dto.GoodsDTO;
import com.exchange.pojo.Goods;
import com.exchange.vo.GoodsDetailsVO;
import com.exchange.vo.Result;

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

    Result getNewGoodsPage(Integer pageNum, Integer pageSize);

    Result favoriteToggle(Long id, Long type);

    GoodsDetailsVO getGoodsDetails(Long id);
}
