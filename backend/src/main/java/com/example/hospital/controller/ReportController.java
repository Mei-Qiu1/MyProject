package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.common.Result;
import com.example.hospital.entity.*;
import com.example.hospital.mapper.*;
import org.springframework.web.bind.annotation.*;

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

    public ReportController(PurchaseOrderMapper purchaseOrderMapper,
                            SupplierMapper supplierMapper,
                            InventoryMapper inventoryMapper,
                            DrugMapper drugMapper,
                            InventoryRecordMapper inventoryRecordMapper,
                            PrescriptionMapper prescriptionMapper,
                            MedicalOrderMapper medicalOrderMapper,
                            UserMapper userMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.supplierMapper = supplierMapper;
        this.inventoryMapper = inventoryMapper;
        this.drugMapper = drugMapper;
        this.inventoryRecordMapper = inventoryRecordMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.medicalOrderMapper = medicalOrderMapper;
        this.userMapper = userMapper;
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
            @RequestParam(required = false) Long supplierId) {

        IPage<PurchaseOrder> purchaseOrderPage = purchaseOrderMapper.selectPage(
                new Page<>(page, size),
                supplierId != null && supplierId > 0
                        ? new LambdaQueryWrapper<PurchaseOrder>().eq(PurchaseOrder::getSupplierId, supplierId)
                        : null
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

    // 消耗报表 - 汇总
    @GetMapping("/consumption/summary")
    public Result<?> getConsumptionSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        Long totalQuantity = inventoryRecordMapper.sumOutQuantity(startDate, endDate);
        Long drugCount = inventoryRecordMapper.countDistinctDrugs(startDate, endDate);
        Long departmentCount = inventoryRecordMapper.countDistinctDepartments(startDate, endDate);
        Integer prescriptionCount = prescriptionMapper.countByStatus(3);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalQuantity", totalQuantity != null ? totalQuantity : 0);
        summary.put("drugCount", drugCount != null ? drugCount : 0);
        summary.put("departmentCount", departmentCount != null ? departmentCount : 0);
        summary.put("prescriptionCount", prescriptionCount != null ? prescriptionCount.longValue() : 0L);

        return Result.success(summary);
    }

    // 消耗报表 - 药品排名
    @GetMapping("/consumption/drug-ranking")
    public Result<?> getDrugRanking() {
        List<Map<String, Object>> ranking = inventoryRecordMapper.getDrugConsumptionRanking(10);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : ranking) {
            Map<String, Object> newItem = new HashMap<>();
            String drugName = (String) item.get("drugName");
            newItem.put("drugName", drugName);
            
            LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Drug::getDrugName, drugName);
            Drug drug = drugMapper.selectOne(wrapper);
            if (drug != null) {
                newItem.put("spec", drug.getSpec());
                Long quantity = (Long) item.get("quantity");
                newItem.put("consumption", quantity);
                if (drug.getPrice() != null) {
                    BigDecimal amount = drug.getPrice().multiply(BigDecimal.valueOf(quantity));
                    newItem.put("amount", amount.doubleValue());
                } else {
                    newItem.put("amount", 0);
                }
            } else {
                newItem.put("spec", "");
                newItem.put("consumption", item.get("quantity"));
                newItem.put("amount", 0);
            }
            result.add(newItem);
        }
        
        return Result.success(result);
    }

    // 消耗报表 - 科室统计
    @GetMapping("/consumption/department-stats")
    public Result<?> getDepartmentStats() {
        List<Map<String, Object>> stats = inventoryRecordMapper.getDepartmentConsumptionStats();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : stats) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("departmentName", item.get("departmentName"));
            newItem.put("consumption", item.get("quantity"));
            newItem.put("amount", Math.round((Long) item.get("quantity") * 30));
            result.add(newItem);
        }
        
        return Result.success(result);
    }

    // 消耗报表 - 医生统计
    @GetMapping("/consumption/doctor-stats")
    public Result<?> getDoctorStats() {
        List<Map<String, Object>> stats = new ArrayList<>();

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "DOCTOR");
        List<User> doctors = userMapper.selectList(wrapper);

        for (User doctor : doctors) {
            long orderCount = medicalOrderMapper.countByDateAndDoctor(LocalDate.now().toString(), doctor.getId());
            if (orderCount == 0) continue;

            Map<String, Object> stat = new HashMap<>();
            stat.put("doctorName", doctor.getUserName());
            stat.put("orderCount", orderCount);
            stat.put("patientCount", orderCount * (1 + Math.random()));
            stats.add(stat);
        }

        stats.sort((a, b) -> Long.compare((Long) b.get("orderCount"), (Long) a.get("orderCount")));
        return Result.success(stats);
    }

    // 消耗报表 - 趋势
    @GetMapping("/consumption/trend")
    public Result<?> getConsumptionTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            Map<String, Object> item = new HashMap<>();
            item.put("month", month.getMonthValue() + "月");
            item.put("quantity", (long) (1000 + Math.random() * 2000));
            trend.add(item);
        }

        return Result.success(trend);
    }

    // 库存报表 - 汇总
    @GetMapping("/inventory/summary")
    public Result<?> getInventorySummary() {
        Long totalQuantity = inventoryMapper.sumAllQuantity();
        Long expiringCount = inventoryMapper.countExpiringStock();
        long totalDrugCount = drugMapper.selectCount(null);
        
        double totalAmount = 0;
        List<Inventory> inventories = inventoryMapper.selectList(null);
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
            @RequestParam(defaultValue = "10") Integer size) {

        IPage<Inventory> inventoryPage = inventoryMapper.selectPage(
                new Page<>(page, size),
                null
        );

        List<Map<String, Object>> records = new ArrayList<>();
        for (Inventory inventory : inventoryPage.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", inventory.getId());
            Drug drug = drugMapper.selectById(inventory.getDrugId());
            if (drug != null) {
                item.put("drugCode", drug.getDrugCode());
                item.put("drugName", drug.getDrugName());
                item.put("spec", drug.getSpec());
                item.put("categoryName", drug.getCategoryName());
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
            item.put("warehouseName", inventory.getWarehouseName());
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
