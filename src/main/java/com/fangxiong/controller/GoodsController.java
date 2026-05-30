package com.fangxiong.controller;

import com.fangxiong.pojo.Goods;
import com.fangxiong.service.GoodsService;
import com.fangxiong.vo.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    // ====================== 分页查询待审核商品 ======================
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

    // ====================== 商品详情 ======================
    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Integer id) {
        Goods goods = goodsService.findById(id);
        return Result.success(goods);
    }

    // ====================== 审核通过 ======================
    @PostMapping("/pass")
    public Result pass(@RequestBody Map<String, Object> map) {
        Integer id = (Integer) map.get("id");
        goodsService.auditPass(id);
        return Result.success("审核通过");
    }

    // ====================== 审核驳回 ======================
    @PostMapping("/reject")
    public Result reject(@RequestBody Map<String, Object> map) {
        Integer id = (Integer) map.get("id");
        String rejectReason = (String) map.get("rejectReason");
        goodsService.auditReject(id, rejectReason);
        return Result.success("已驳回");
    }

    // 已驳回分页
    @GetMapping("/rejectedPage")
    public Result rejectedPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsService.findRejectedGoods();
        PageInfo<Goods> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    // 删除商品
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        goodsService.deleteById(id);
        return Result.success("删除成功");
    }
}