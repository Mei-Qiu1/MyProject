package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.PurchaseOrder;
import com.example.hospital.service.PurchaseOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/purchase/orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status) {
        IPage<PurchaseOrder> orderPage = purchaseOrderService.page(page, size, keyword, status);
        return Result.success(PageResult.of(orderPage.getRecords(), orderPage.getTotal(),
                (int) orderPage.getCurrent(), (int) orderPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        PurchaseOrder order = purchaseOrderService.findById(id);
        return order != null ? Result.success(order) : Result.fail("订单不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo((String) payload.get("orderNo"));
        order.setSupplierId(payload.get("supplierId") != null ? Long.valueOf(payload.get("supplierId").toString()) : null);
        order.setTotalAmount(payload.get("totalAmount") != null ? new java.math.BigDecimal(payload.get("totalAmount").toString()) : null);
        order.setStatus(1);
        purchaseOrderService.save(order, payload);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PurchaseOrder order) {
        order.setId(id);
        purchaseOrderService.update(order);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/receive")
    public Result<?> receive(@PathVariable Long id, @RequestBody Map<String, Object> receiveData) {
        purchaseOrderService.receiveOrder(id, receiveData);
        return Result.success("验收成功");
    }

    @PostMapping("/from-request/{requestId}")
    public Result<?> createFromRequest(@PathVariable Long requestId) {
        purchaseOrderService.createFromRequest(requestId);
        return Result.success("订单已生成");
    }
}