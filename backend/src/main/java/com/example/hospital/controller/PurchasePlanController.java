package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.PurchasePlan;
import com.example.hospital.service.PurchasePlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase/plans")
public class PurchasePlanController {

    private final PurchasePlanService purchasePlanService;

    public PurchasePlanController(PurchasePlanService purchasePlanService) {
        this.purchasePlanService = purchasePlanService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(required = false) String keyword) {
        List<PurchasePlan> list = purchasePlanService.list(keyword);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        PurchasePlan plan = purchasePlanService.getById(id);
        return plan != null ? Result.success(plan) : Result.fail("计划不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody PurchasePlan plan) {
        purchasePlanService.save(plan);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PurchasePlan plan) {
        plan.setId(id);
        purchasePlanService.update(plan);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        purchasePlanService.delete(id);
        return Result.success("删除成功");
    }
}