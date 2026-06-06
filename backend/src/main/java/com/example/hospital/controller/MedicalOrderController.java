package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.MedicalOrder;
import com.example.hospital.service.MedicalOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clinical/orders")
public class MedicalOrderController {
    private final MedicalOrderService medicalOrderService;
    public MedicalOrderController(MedicalOrderService medicalOrderService) {
        this.medicalOrderService = medicalOrderService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status) {
        try {
            IPage<MedicalOrder> orderPage = medicalOrderService.page(page, size, keyword, status);
            return Result.success(PageResult.of(orderPage.getRecords(), orderPage.getTotal(),
                    (int) orderPage.getCurrent(), (int) orderPage.getSize()));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("加载医嘱列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        try {
            MedicalOrder order = medicalOrderService.getById(id);
            return order != null ? Result.success(order) : Result.fail("医嘱不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取医嘱详情失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        try {
            medicalOrderService.save(payload);
            return Result.success("创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("创建医嘱失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/execute")
    public Result<?> execute(@PathVariable Long id) {
        try {
            medicalOrderService.execute(id);
            return Result.success("执行成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("执行医嘱失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/delivery")
    public Result<?> createDelivery(@PathVariable Long id) {
        try {
            medicalOrderService.createDelivery(id);
            return Result.success("配送单已生成");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("生成配送单失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            medicalOrderService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除医嘱失败：" + e.getMessage());
        }
    }
}