package com.exchange.mapper;

import com.exchange.dto.GoodsDTO;
import com.exchange.pojo.Goods;
import com.exchange.vo.GoodsDetailsVO;
import com.exchange.vo.RecentGoodsVO;
import com.exchange.vo.TodoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface GoodsMapper {
    Long selectCount();
    List<RecentGoodsVO> selectRecentGoods();
    List<TodoVO> selectWaitCheck();
    List<Goods> findAuditGoods();
    Goods findById(Long id);

    void updateAuditPass(@Param("id") Integer id);

    void updateAuditReject(
            @Param("id") Integer id,
            @Param("rejectReason") String rejectReason
    );

    List<Goods> findRejectedGoods();
    void deleteById(Long id);
    List<Goods> selectMyPublish(Long id);
    List<Goods> selectMyFavorite(Long id);

    void updateGoods(@Param("goods") Goods goods);

    void updateSaleStatus(
            @Param("id") Long id,
            @Param("saleStatus") Integer saleStatus
    );

    void addGoods(@Param("goods") Goods goods);

    List<GoodsDTO> selectNewGoodsPage();

    void addFavoriteGoods(
            @Param("id") Long id,
            @Param("userId") Long userId,
            @Param("createTime") LocalDateTime createTime
    );

    void deleteFavoriteGoods(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    GoodsDetailsVO getGoodsDetails(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    BigDecimal selectGoodsPriceById(Long id);
    Long selectSellerId(Long goodsId);
}