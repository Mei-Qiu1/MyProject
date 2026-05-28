package com.example.hospital.controller;

import com.example.hospital.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    // 库存报表
    @GetMapping("/inventory/summary")
    public Result<?> inventorySummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDrugCount", 0);
        summary.put("totalQuantity", 0);
        summary.put("totalAmount", 0);
        summary.put("expiringCount", 0);
        return Result.success(summary);
    }

    @GetMapping("/inventory/detail")
    public Result<?> inventoryDetail(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return Result.success(Map.of("records", List.of(), "total", 0L, "current", page, "size", size));
    }

    @GetMapping("/inventory/turnover")
    public Result<?> turnover() {
        return Result.success(List.of());
    }

    @GetMapping("/inventory/abc")
    public Result<?> abc() {
        return Result.success(List.of());
    }

    @GetMapping("/inventory/export")
    public void exportInventory() {
        // 空实现
    }

    // 采购报表
    @GetMapping("/purchase/summary")
    public Result<?> purchaseSummary(@RequestParam(required = false) Long supplierId,
                                     @RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate) {
        return Result.success(Map.of("orderCount", 0, "totalAmount", 0, "completedCount", 0, "pendingCount", 0));
    }

    @GetMapping("/purchase/detail")
    public Result<?> purchaseDetail(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) Long supplierId) {
        return Result.success(Map.of("records", List.of(), "total", 0L));
    }

    @GetMapping("/purchase/supplier-stats")
    public Result<?> supplierStats() {
        return Result.success(List.of());
    }

    @GetMapping("/purchase/trend")
    public Result<?> purchaseTrend() {
        return Result.success(List.of());
    }

    @GetMapping("/purchase/export")
    public void exportPurchase() {}

    // 消耗报表
    @GetMapping("/consumption/summary")
    public Result<?> consumptionSummary(@RequestParam(required = false) Long departmentId,
                                        @RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate) {
        return Result.success(Map.of("totalConsumption", 0, "totalAmount", 0, "drugCount", 0, "departmentCount", 0));
    }

    @GetMapping("/consumption/drug-ranking")
    public Result<?> drugRanking(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(List.of());
    }

    @GetMapping("/consumption/department-stats")
    public Result<?> departmentStats() {
        return Result.success(List.of());
    }

    @GetMapping("/consumption/doctor-stats")
    public Result<?> doctorStats() {
        return Result.success(List.of());
    }

    @GetMapping("/consumption/trend")
    public Result<?> consumptionTrend() {
        return Result.success(List.of());
    }

    @GetMapping("/consumption/export")
    public void exportConsumption() {}
}