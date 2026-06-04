package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.DrugDelivery;
import com.example.hospital.service.DrugDeliveryService;
import org.springframework.web.bind.annotation.*;

/**
 * 药品配送单控制器
 */
@RestController
@RequestMapping("/pharmacy/delivery")
public class DrugDeliveryController {
    
    private final DrugDeliveryService drugDeliveryService;
    
    public DrugDeliveryController(DrugDeliveryService drugDeliveryService) {
        this.drugDeliveryService = drugDeliveryService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status,
                          @RequestParam(required = false) String department) {
        IPage<DrugDelivery> deliveryPage = drugDeliveryService.page(page, size, keyword, status, department);
        return Result.success(PageResult.of(deliveryPage.getRecords(), deliveryPage.getTotal(),
                (int) deliveryPage.getCurrent(), (int) deliveryPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        DrugDelivery delivery = drugDeliveryService.getById(id);
        return delivery != null ? Result.success(delivery) : Result.fail("配送单不存在");
    }

    @PostMapping("/order/{orderId}")
    public Result<?> createFromOrder(@PathVariable Long orderId) {
        try {
            drugDeliveryService.create(orderId);
            return Result.success("配送单生成成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/sign")
    public Result<?> sign(@PathVariable Long id, @RequestParam String signer) {
        try {
            drugDeliveryService.sign(id, signer);
            return Result.success("签收成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        drugDeliveryService.delete(id);
        return Result.success("删除成功");
    }
}