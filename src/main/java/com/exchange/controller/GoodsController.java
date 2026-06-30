package com.exchange.controller;

import com.exchange.Utils.CurrentHolder;
import com.exchange.anno.Log;
import com.exchange.anno.RequiredAdmin;
import com.exchange.dto.LoginResult;
import com.exchange.dto.SearchGoodsDTO;
import com.exchange.pojo.Goods;
import com.exchange.service.GoodsService;
import com.exchange.vo.PageResult;
import com.exchange.vo.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    // 分页查询待审核商品
    @RequiredAdmin
    @GetMapping("/auditPage")
    public Result auditPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "8") Integer pageSize
    ) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsService.findAuditGoods();
        PageInfo<Goods> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    //商品详情
    @RequiredAdmin
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Goods goods = goodsService.findById(id);
        return Result.success(goods);
    }

    //审核通过
    @Log
    @RequiredAdmin
    @PostMapping("/pass")
    public Result pass(@RequestBody Map<String, Object> map) {
        Integer id = (Integer) map.get("id");
        goodsService.auditPass(id);
        return Result.success("审核通过");
    }

    //审核驳回
    @Log
    @RequiredAdmin
    @PostMapping("/reject")
    public Result reject(@RequestBody Map<String, Object> map) {
        Integer id = (Integer) map.get("id");
        String rejectReason = (String) map.get("rejectReason");
        goodsService.auditReject(id, rejectReason);
        return Result.success("已驳回");
    }

    // 已驳回分页
    @RequiredAdmin
    @GetMapping("/rejectedPage")
    public Result rejectedPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsService.findRejectedGoods();
        PageInfo<Goods> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    // 删除商品
    @Log
    @RequiredAdmin
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        goodsService.deleteById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/my-publish")
    public Result myPublish(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize){
        PageHelper.startPage(page, pageSize);
        List<Goods> goods = goodsService.myPublish(CurrentHolder.getCurrentUserInfo().getId());
        PageInfo<Goods> pageInfo = new PageInfo<>(goods);
        PageResult<Goods> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return Result.success(pageResult);
    }

    @GetMapping("/favorite")
    public Result favorite(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize){
        PageHelper.startPage(page, pageSize);
        List<Goods> goods = goodsService.myFavorite(CurrentHolder.getCurrentUserInfo().getId());
        PageInfo<Goods> pageInfo = new PageInfo<>(goods);
        PageResult<Goods> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return Result.success(pageResult);
    }

    @GetMapping("/goodsDetail")
    public Result getGoodsDetail(@RequestParam Long id){
        return Result.success(goodsService.getGoodsDetails(id));
    }

    @PostMapping("/updateGoods")
    public Result updateGoods(@RequestBody Goods goods){
        goodsService.updateGoods(goods);
        return Result.success("修改成功");
    }

    @PostMapping("/change-sale-status")
    public Result changeSaleStatus(
            @RequestBody Map<String, Object> params
    ) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer saleStatus = (Integer) params.get("saleStatus");
        goodsService.updateSaleStatus(id, saleStatus);
        return Result.success();
    }

    @PostMapping("/deleteGoods")
    public Result deleteGoods(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        goodsService.deleteById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/addGoods")
    public Result addGoods(@RequestBody Goods goods) {
        LoginResult loginResult = CurrentHolder.getCurrentUserInfo();
        goods.setUserId(loginResult.getId());
        goods.setUsername(loginResult.getUsername());
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        goods.setAuditStatus(0);
        goods.setSaleStatus(0);
        goodsService.addGoods(goods);
        return Result.success();
    }

    @GetMapping("/newGoodsPage")
    public Result newGoodsPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return goodsService.getNewGoodsPage(pageNum, pageSize);
    }

    @PostMapping("/favorite/toggle")
    public Result favoriteToggle(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Long type = Long.valueOf(params.get("type").toString()); // 0 取消收藏 1 收藏
        return goodsService.favoriteToggle(id, type);
    }

    @PostMapping("/getGoodsListByTypeOrSearchApi")
    public Result getGoodsListByTypeOrSearchApi(
            @RequestBody SearchGoodsDTO searchGoodsDTO
            ){
        return goodsService.getGoodsListByTypeOrSearchApi(searchGoodsDTO);
    }

}