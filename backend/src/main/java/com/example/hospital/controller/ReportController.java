package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ==================== 库存报表 ====================

    @GetMapping("/inventory/summary")
    public Result<?> inventorySummary() {
        return Result.success(reportService.getInventorySummary());
    }

    @GetMapping("/inventory/detail")
    public Result<?> inventoryDetail(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        List<Map<String, Object>> records = reportService.getInventoryDetail(page, size);
        long total = reportService.getInventoryTotal();
        return Result.success(Map.of("records", records, "total", total, "current", page, "size", size));
    }

    @GetMapping("/inventory/turnover")
    public Result<?> turnover() {
        return Result.success(reportService.getTurnoverData());
    }

    @GetMapping("/inventory/abc")
    public Result<?> abc() {
        return Result.success(reportService.getABCData());
    }

    @GetMapping("/inventory/export")
    public void exportInventory() {
        // 空实现，可后续扩展
    }

    // ==================== 采购报表 ====================

    @GetMapping("/purchase/summary")
    public Result<?> purchaseSummary(@RequestParam(required = false) Long supplierId,
                                     @RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate) {
        return Result.success(reportService.getPurchaseSummary(supplierId, startDate, endDate));
    }

    @GetMapping("/purchase/detail")
    public Result<?> purchaseDetail(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) Long supplierId) {
        List<Map<String, Object>> records = reportService.getPurchaseDetail(page, size, supplierId);
        long total = reportService.getPurchaseTotal(supplierId);
        return Result.success(Map.of("records", records, "total", total));
    }

    @GetMapping("/purchase/supplier-stats")
    public Result<?> supplierStats() {
        return Result.success(reportService.getSupplierStats());
    }

    @GetMapping("/purchase/trend")
    public Result<?> purchaseTrend() {
        return Result.success(reportService.getPurchaseTrend());
    }

    @GetMapping("/purchase/export")
    public void exportPurchase() {}

    // ==================== 消耗报表 ====================

    @GetMapping("/consumption/summary")
    public Result<?> consumptionSummary(@RequestParam(required = false) Long departmentId,
                                        @RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate) {
        return Result.success(reportService.getConsumptionSummary(departmentId, startDate, endDate));
    }

    @GetMapping("/consumption/drug-ranking")
    public Result<?> drugRanking(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(reportService.getDrugRanking(limit));
    }

    @GetMapping("/consumption/department-stats")
    public Result<?> departmentStats() {
        return Result.success(reportService.getDepartmentStats());
    }

    @GetMapping("/consumption/doctor-stats")
    public Result<?> doctorStats() {
        return Result.success(reportService.getDoctorStats());
    }

    @GetMapping("/consumption/trend")
    public Result<?> consumptionTrend() {
        return Result.success(reportService.getConsumptionTrend());
    }

    @GetMapping("/consumption/export")
    public void exportConsumption() {}
}
