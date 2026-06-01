package com.fangxiong.mapper;

import com.fangxiong.pojo.Goods;
import com.fangxiong.vo.RecentGoodsVO;
import com.fangxiong.vo.TodoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsMapper {
    Long selectCount();
    List<RecentGoodsVO> selectRecentGoods();
    List<TodoVO> selectWaitCheck();
    List<Goods> findAuditGoods();
    Goods findById(Long id);
    void updateAuditPass(@Param("id") Integer id);
    void updateAuditReject(@Param("id") Integer id, @Param("rejectReason") String rejectReason);
    // 已驳回
    List<Goods> findRejectedGoods();

    // 删除
    void deleteById(Long id);

    List<Goods> selectMyPublish(Long id);

    List<Goods> selectMyFavorite(Long id);

    void updateGoods(Goods goods);

    void updateSaleStatus(Long id, Integer saleStatus);

    void addGoods(Goods goods);
}