package com.exchange.service.impl;

import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.DeleteFileUtil;
import com.exchange.Utils.MoveFileUtil;
import com.exchange.dto.GoodsDTO;
import com.exchange.dto.SearchGoodsDTO;
import com.exchange.mapper.GoodsMapper;
import com.exchange.mapper.OrdersMapper;
import com.exchange.pojo.Goods;
import com.exchange.service.GoodsService;
import com.exchange.vo.GoodsDetailsVO;
import com.exchange.vo.PageResult;
import com.exchange.vo.Result;
import com.fangxiong.utils.redis.RedisUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.PageRanges;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.exchange.constants.SystemConstants.USER_DELETE_ORDER_OR_GOODS_LOCK_KEY;

@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private MoveFileUtil moveFileUtil;

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
    public Result deleteById(Long id) {
        String imageUrl = goodsMapper.findById(id).getImages();
        String tempImageUrl = null;
        boolean needToMoveBackFile = false;
        try {
            if (!redisUtils.enableLock(USER_DELETE_ORDER_OR_GOODS_LOCK_KEY+ id)) {
                return Result.error("删除失败，请稍后重试");
            }
            if (!ordersMapper.selectByGoodsIdIfExist(id)) {
                tempImageUrl = moveFileUtil.moveRealToTemp(imageUrl);
                needToMoveBackFile = true;
            }
            goodsMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除商品失败", e);
            if (needToMoveBackFile) {
                moveFileUtil.moveTempToReal(tempImageUrl);
            }
            return Result.error("删除失败");
        } finally {
            redisUtils.disableLock(USER_DELETE_ORDER_OR_GOODS_LOCK_KEY+ id);
        }
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
    public Result addGoods(Goods goods) {
        String realImageUrl = moveFileUtil.moveTempToReal(goods.getImages());
        if (realImageUrl == null){
            return Result.error("图片保存失败，保存发布商品失败");
        }
        goods.setImages(realImageUrl);
        if (CurrentHolder.getCurrentUserInfo().getSchool()==0) {
            return Result.error("请先完善学校信息");
        }
        goods.setSchool(CurrentHolder.getCurrentUserInfo().getSchool());
        goodsMapper.addGoods(goods);
        return Result.success();
    }

    @Override
    public Result getNewGoodsPage(Integer pageNum, Integer pageSize) {
        if (CurrentHolder.getCurrentUserInfo().getSchool()==0){
            return Result.error("请先完善学校信息");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<GoodsDTO> list = goodsMapper.selectNewGoodsPage(CurrentHolder.getCurrentUserInfo().getSchool());
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

    @Override
    public Result getGoodsListByTypeOrSearchApi(SearchGoodsDTO searchGoodsDTO) {
        if (CurrentHolder.getCurrentUserInfo().getSchool()==0){
            return Result.error("请先完善学校信息");
        }
        searchGoodsDTO.setSchool(CurrentHolder.getCurrentUserInfo().getSchool());
        PageHelper.startPage(searchGoodsDTO.getPageNum(), searchGoodsDTO.getPageSize());
        List<GoodsDTO> list = goodsMapper.selectGoodsListByTypeOrSearch(searchGoodsDTO);
        PageInfo<GoodsDTO> pageInfo = new PageInfo<>(list);
        PageResult<GoodsDTO> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return Result.success(pageResult);
    }
}
