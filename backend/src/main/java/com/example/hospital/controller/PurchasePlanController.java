package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.PurchasePlan;
import com.example.hospital.entity.PurchasePlanDetail;
import com.example.hospital.mapper.PurchasePlanDetailMapper;
import com.example.hospital.service.PurchasePlanService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase/plans")
public class PurchasePlanController {

    private final PurchasePlanDetailMapper purchasePlanDetailMapper;
    private final PurchasePlanService purchasePlanService;

    public PurchasePlanController(PurchasePlanService purchasePlanService,
                                  PurchasePlanDetailMapper purchasePlanDetailMapper) {
        this.purchasePlanService = purchasePlanService;
        this.purchasePlanDetailMapper = purchasePlanDetailMapper;
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
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        String planName = (String) payload.get("planName");
        Integer planType = (Integer) payload.get("planType");
        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");

        if (items == null || items.isEmpty()) {
            return Result.fail("请至少添加一种药品");
        }

        // 生成计划编号
        String planNo = "PL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        PurchasePlan plan = new PurchasePlan();
        plan.setPlanNo(planNo);
        plan.setPlanName(planName);
        plan.setPlanType(planType);
        plan.setPlanDate(LocalDateTime.now());
        plan.setStatus(1);
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        purchasePlanService.save(plan);

        // 保存明细（仅保存必要字段，不存储单位、价格等）
        for (Map<String, Object> item : items) {
            PurchasePlanDetail detail = new PurchasePlanDetail();
            detail.setPlanId(plan.getId());
            detail.setDrugId(Long.valueOf(item.get("drugId").toString()));
            detail.setDrugName((String) item.get("drugName"));
            detail.setQuantity(Integer.valueOf(item.get("quantity").toString()));
            // 注意：spec, unit 等字段不存储，查询时关联 drug 表获取
            purchasePlanDetailMapper.insert(detail);
        }
        return Result.success("采购计划生成成功");
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