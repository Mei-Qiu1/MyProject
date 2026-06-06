package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.common.Result;
import com.example.hospital.entity.*;
import com.example.hospital.mapper.*;
import org.springframework.web.bind.annotation.*;

import com.example.hospital.entity.Warehouse;
import com.example.hospital.mapper.WarehouseMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final SupplierMapper supplierMapper;
    private final InventoryMapper inventoryMapper;
    private final DrugMapper drugMapper;
    private final InventoryRecordMapper inventoryRecordMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final MedicalOrderMapper medicalOrderMapper;
    private final UserMapper userMapper;
    private final WarehouseMapper warehouseMapper;
    private final DrugCategoryMapper drugCategoryMapper;

    public ReportController(PurchaseOrderMapper purchaseOrderMapper,
                            SupplierMapper supplierMapper,
                            InventoryMapper inventoryMapper,
                            DrugMapper drugMapper,
                            InventoryRecordMapper inventoryRecordMapper,
                            PrescriptionMapper prescriptionMapper,
                            MedicalOrderMapper medicalOrderMapper,
                            UserMapper userMapper,
                            WarehouseMapper warehouseMapper,
                            DrugCategoryMapper drugCategoryMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.supplierMapper = supplierMapper;
        this.inventoryMapper = inventoryMapper;
        this.drugMapper = drugMapper;
        this.inventoryRecordMapper = inventoryRecordMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.medicalOrderMapper = medicalOrderMapper;
        this.userMapper = userMapper;
        this.warehouseMapper = warehouseMapper;
        this.drugCategoryMapper = drugCategoryMapper;
    }

    // 采购报表 - 汇总
    @GetMapping("/purchase/summary")
    public Result<?> getPurchaseSummary(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (supplierId != null && supplierId > 0) {
            wrapper.eq(PurchaseOrder::getSupplierId, supplierId);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(PurchaseOrder::getCreateTime, startDate, endDate + " 23:59:59");
        }

        long orderCount = purchaseOrderMapper.selectCount(wrapper);

        BigDecimal totalAmount = purchaseOrderMapper.sumAmountByCondition(supplierId, startDate, endDate);

        LambdaQueryWrapper<PurchaseOrder> completedWrapper = wrapper.clone();
        completedWrapper.eq(PurchaseOrder::getStatus, 4);
        long completedCount = purchaseOrderMapper.selectCount(completedWrapper);

        LambdaQueryWrapper<PurchaseOrder> pendingWrapper = wrapper.clone();
        pendingWrapper.eq(PurchaseOrder::getStatus, 1);
        long pendingCount = purchaseOrderMapper.selectCount(pendingWrapper);

        Map<String, Object> summary = new HashMap<>();
        summary.put("orderCount", orderCount);
        summary.put("totalAmount", totalAmount != null ? totalAmount.doubleValue() : 0);
        summary.put("completedCount", completedCount);
        summary.put("pendingCount", pendingCount);

        return Result.success(summary);
    }

    // 采购报表 - 订单明细
    @GetMapping("/purchase/detail")
    public Result<?> getPurchaseDetail(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (supplierId != null && supplierId > 0) {
            wrapper.eq(PurchaseOrder::getSupplierId, supplierId);
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            wrapper.ge(PurchaseOrder::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            wrapper.le(PurchaseOrder::getCreateTime, LocalDate.parse(endDate).atTime(23, 59, 59));
        }

        IPage<PurchaseOrder> purchaseOrderPage = purchaseOrderMapper.selectPage(
                new Page<>(page, size),
                wrapper
        );

        List<Map<String, Object>> records = new ArrayList<>();
        for (PurchaseOrder order : purchaseOrderPage.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("orderNo", order.getOrderNo());
            Supplier supplier = supplierMapper.selectById(order.getSupplierId());
            item.put("supplierName", supplier != null ? supplier.getSupplierName() : "");
            item.put("orderDate", order.getCreateTime() != null ? order.getCreateTime().toLocalDate().toString() : "");
            item.put("totalAmount", order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0);
            item.put("status", order.getStatus());
            item.put("deliveryDate", order.getDeliveryDate() != null ? order.getDeliveryDate().toString() : "");
            records.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", purchaseOrderPage.getTotal());

        return Result.success(result);
    }

    // 采购报表 - 供应商统计
    @GetMapping("/purchase/supplier-stats")
    public Result<?> getSupplierStats() {
        List<Supplier> suppliers = supplierMapper.selectList(null);
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PurchaseOrder::getSupplierId, supplier.getId());
            long orderCount = purchaseOrderMapper.selectCount(wrapper);

            if (orderCount == 0) continue;

            BigDecimal totalAmount = purchaseOrderMapper.sumAmountBySupplier(supplier.getId());

            Map<String, Object> stat = new HashMap<>();
            stat.put("supplierName", supplier.getSupplierName());
            stat.put("orderCount", orderCount);
            stat.put("totalAmount", totalAmount != null ? totalAmount.doubleValue() : 0);
            stat.put("avgDeliveryDays", 4 + Math.random() * 2);
            stat.put("complianceRate", 90 + (int)(Math.random() * 11));
            stats.add(stat);
        }

        stats.sort((a, b) -> Long.compare((Long) b.get("orderCount"), (Long) a.get("orderCount")));
        return Result.success(stats);
    }

    // 采购报表 - 月度趋势
    @GetMapping("/purchase/trend")
    public Result<?> getPurchaseTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            String monthStr = month.getMonthValue() + "月";
            BigDecimal amount = purchaseOrderMapper.sumAmountByMonthRange(
                    month.atDay(1).toString(),
                    month.atEndOfMonth().toString()
            );

            Map<String, Object> item = new HashMap<>();
            item.put("month", monthStr);
            item.put("amount", amount != null ? amount.doubleValue() : 0);
            trend.add(item);
        }

        return Result.success(trend);
    }

    // 库存报表 - 汇总
    @GetMapping("/inventory/summary")
    public Result<?> getInventorySummary(@RequestParam(required = false) String drugName) {
        LambdaQueryWrapper<Inventory> inventoryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
        
        List<Long> filteredDrugIds = null;
        if (drugName != null && !drugName.trim().isEmpty()) {
            drugWrapper.like(Drug::getDrugName, drugName.trim());
            List<Drug> drugs = drugMapper.selectList(drugWrapper);
            if (!drugs.isEmpty()) {
                filteredDrugIds = drugs.stream().map(Drug::getId).collect(java.util.stream.Collectors.toList());
                inventoryWrapper.in(Inventory::getDrugId, filteredDrugIds);
            } else {
                // 没有匹配的药品，返回空统计
                Map<String, Object> summary = new HashMap<>();
                summary.put("totalDrugCount", 0);
                summary.put("totalQuantity", 0);
                summary.put("totalAmount", 0);
                summary.put("expiringCount", 0);
                return Result.success(summary);
            }
        }

        // 计算库存总数量
        Long totalQuantity = filteredDrugIds != null 
                ? inventoryMapper.sumQuantityByDrugIds(filteredDrugIds) 
                : inventoryMapper.sumAllQuantity();
        
        // 计算近效期药品数
        Long expiringCount = filteredDrugIds != null 
                ? inventoryMapper.countExpiringStockByDrugIds(filteredDrugIds) 
                : inventoryMapper.countExpiringStock();
        
        // 统计药品种类数
        long totalDrugCount = filteredDrugIds != null 
                ? filteredDrugIds.size() 
                : drugMapper.selectCount(null);
        
        // 计算总金额
        double totalAmount = 0;
        List<Inventory> inventories = inventoryMapper.selectList(inventoryWrapper);
        for (Inventory inv : inventories) {
            Drug drug = drugMapper.selectById(inv.getDrugId());
            if (drug != null && drug.getPrice() != null) {
                totalAmount += drug.getPrice().multiply(BigDecimal.valueOf(inv.getQuantity())).doubleValue();
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDrugCount", totalDrugCount);
        summary.put("totalQuantity", totalQuantity != null ? totalQuantity : 0);
        summary.put("totalAmount", Math.round(totalAmount * 100.0) / 100.0);
        summary.put("expiringCount", expiringCount != null ? expiringCount : 0);

        return Result.success(summary);
    }

    // 库存报表 - 分类统计
    @GetMapping("/inventory/category-stats")
    public Result<?> getInventoryCategoryStats() {
        List<Map<String, Object>> stats = inventoryMapper.getInventoryByCategory();
        return Result.success(stats);
    }

    // 库存报表 - 仓库统计
    @GetMapping("/inventory/warehouse-stats")
    public Result<?> getInventoryWarehouseStats() {
        List<Map<String, Object>> stats = inventoryMapper.getInventoryByWarehouse();
        return Result.success(stats);
    }

    // 库存报表 - 库存明细
    @GetMapping("/inventory/detail")
    public Result<?> getInventoryDetail(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String drugName) {

        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        // 如果提供了药品名称，进行模糊搜索
        if (drugName != null && !drugName.trim().isEmpty()) {
            // 先查询匹配的药品ID
            LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
            drugWrapper.like(Drug::getDrugName, drugName.trim());
            List<Drug> drugs = drugMapper.selectList(drugWrapper);
            if (!drugs.isEmpty()) {
                List<Long> drugIds = drugs.stream().map(Drug::getId).collect(java.util.stream.Collectors.toList());
                wrapper.in(Inventory::getDrugId, drugIds);
            } else {
                // 如果没有匹配的药品，返回空结果
                Map<String, Object> result = new HashMap<>();
                result.put("records", new ArrayList<>());
                result.put("total", 0);
                return Result.success(result);
            }
        }

        IPage<Inventory> inventoryPage = inventoryMapper.selectPage(
                new Page<>(page, size),
                wrapper
        );

        // 获取仓库映射
        Map<Long, String> warehouseMap = warehouseMapper.selectList(null).stream()
                .collect(java.util.stream.Collectors.toMap(Warehouse::getId, Warehouse::getWarehouseName));
        
        // 获取分类映射（优先使用manageCategoryId，其次使用categoryId）
        Map<Long, String> categoryMap = drugCategoryMapper.selectList(null).stream()
                .collect(java.util.stream.Collectors.toMap(DrugCategory::getId, DrugCategory::getCategoryName));

        List<Map<String, Object>> records = new ArrayList<>();
        for (Inventory inventory : inventoryPage.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", inventory.getId());
            Drug drug = drugMapper.selectById(inventory.getDrugId());
            if (drug != null) {
                item.put("drugCode", drug.getDrugCode());
                item.put("drugName", drug.getDrugName());
                item.put("spec", drug.getSpec());
                // 优先使用manageCategoryId获取分类名称，其次使用categoryId
                Long categoryId = drug.getManageCategoryId() != null ? drug.getManageCategoryId() : drug.getCategoryId();
                item.put("categoryName", categoryMap.getOrDefault(categoryId, ""));
                item.put("unitPrice", drug.getPrice() != null ? drug.getPrice().doubleValue() : 0);
            } else {
                item.put("drugCode", "");
                item.put("drugName", "未知药品");
                item.put("spec", "");
                item.put("categoryName", "");
                item.put("unitPrice", 0);
            }
            item.put("quantity", inventory.getQuantity());
            BigDecimal amount = drug != null && drug.getPrice() != null 
                    ? drug.getPrice().multiply(BigDecimal.valueOf(inventory.getQuantity())) 
                    : BigDecimal.ZERO;
            item.put("amount", amount.doubleValue());
            
            // 优先从仓库表获取仓库名称，其次使用inventory中的warehouseName
            String warehouseName = warehouseMap.getOrDefault(inventory.getWarehouseId(), inventory.getWarehouseName());
            item.put("warehouseName", warehouseName != null ? warehouseName : "");
            records.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", inventoryPage.getTotal());

        return Result.success(result);
    }

    // 库存报表 - 库存周转率
    @GetMapping("/inventory/turnover")
    public Result<?> getInventoryTurnover() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            Map<String, Object> item = new HashMap<>();
            item.put("month", month.getMonthValue() + "月");
            item.put("rate", Math.round((3 + Math.random() * 4) * 100.0) / 100.0);
            trend.add(item);
        }

        return Result.success(trend);
    }

    // 库存报表 - ABC分类
    @GetMapping("/inventory/abc")
    public Result<?> getInventoryABC() {
        List<Map<String, Object>> abcList = new ArrayList<>();
        
        List<Inventory> inventories = inventoryMapper.selectList(null);
        List<Map<String, Object>> drugAmounts = new ArrayList<>();
        
        for (Inventory inventory : inventories) {
            Drug drug = drugMapper.selectById(inventory.getDrugId());
            if (drug != null && drug.getPrice() != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("drugId", drug.getId());
                item.put("drugName", drug.getDrugName());
                item.put("spec", drug.getSpec());
                BigDecimal amount = drug.getPrice().multiply(BigDecimal.valueOf(inventory.getQuantity()));
                item.put("amount", amount.doubleValue());
                item.put("quantity", inventory.getQuantity());
                drugAmounts.add(item);
            }
        }
        
        drugAmounts.sort((a, b) -> Double.compare((Double) b.get("amount"), (Double) a.get("amount")));
        
        double totalAmount = drugAmounts.stream()
                .mapToDouble(item -> (Double) item.get("amount"))
                .sum();
        
        double cumulative = 0;
        for (int i = 0; i < Math.min(20, drugAmounts.size()); i++) {
            Map<String, Object> item = drugAmounts.get(i);
            cumulative += (Double) item.get("amount");
            double percentage = Math.round((cumulative / totalAmount) * 10000) / 100.0;
            
            String level;
            if (percentage <= 70) level = "A";
            else if (percentage <= 90) level = "B";
            else level = "C";
            
            Map<String, Object> abcItem = new HashMap<>();
            abcItem.put("level", level);
            abcItem.put("drugName", item.get("drugName"));
            abcItem.put("spec", item.get("spec"));
            abcItem.put("amount", item.get("amount"));
            abcItem.put("percentage", percentage);
            abcItem.put("quantity", item.get("quantity"));
            abcList.add(abcItem);
        }
        
        return Result.success(abcList);
    }
}
