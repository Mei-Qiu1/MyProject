package com.example.hospital.service;

import java.util.List;
import java.util.Map;

public interface ReportService {
    // 库存报表
    Map<String, Object> getInventorySummary();
    List<Map<String, Object>> getInventoryDetail(int page, int size);
    long getInventoryTotal();
    List<Map<String, Object>> getTurnoverData();
    List<Map<String, Object>> getABCData();

    // 采购报表
    Map<String, Object> getPurchaseSummary(Long supplierId, String startDate, String endDate);
    List<Map<String, Object>> getPurchaseDetail(int page, int size, Long supplierId);
    long getPurchaseTotal(Long supplierId);
    List<Map<String, Object>> getSupplierStats();
    List<Map<String, Object>> getPurchaseTrend();

    // 消耗报表
    Map<String, Object> getConsumptionSummary(Long departmentId, String startDate, String endDate);
    List<Map<String, Object>> getDrugRanking(int limit);
    List<Map<String, Object>> getDepartmentStats();
    List<Map<String, Object>> getDoctorStats();
    List<Map<String, Object>> getConsumptionTrend();
}
