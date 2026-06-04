package com.exchange.service.impl;

import com.exchange.Utils.CurrentHolder;
import com.exchange.dto.GoodsDTO;
import com.exchange.mapper.GoodsMapper;
import com.exchange.pojo.Goods;
import com.exchange.service.GoodsService;
import com.exchange.vo.GoodsDetailsVO;
import com.exchange.vo.PageResult;
import com.exchange.vo.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.PageRanges;
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

    @Override
    public Result getNewGoodsPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<GoodsDTO> list = goodsMapper.selectNewGoodsPage();
        PageInfo<GoodsDTO> pageInfo = new PageInfo<>(list);
        PageResult<GoodsDTO> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return Result.success(pageResult);
    }

    @Override
    public Result favoriteToggle(Long id, Long type) {
        if (type == 1){
            goodsMapper.addFavoriteGoods(id, CurrentHolder.getCurrentUserInfo().getId(),LocalDateTime.now());
        }else if (type == 0){
            goodsMapper.deleteFavoriteGoods(id, CurrentHolder.getCurrentUserInfo().getId());
        }else {
            return Result.error("参数错误");
        }
        return Result.success();
    }

    @Override
    public GoodsDetailsVO getGoodsDetails(Long id) {
        return goodsMapper.getGoodsDetails(id, CurrentHolder.getCurrentUserInfo().getId());
    }
}
