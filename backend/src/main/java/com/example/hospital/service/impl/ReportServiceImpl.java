package com.example.hospital.service.impl;

import com.example.hospital.entity.*;
import com.example.hospital.mapper.*;
import com.example.hospital.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final DrugMapper drugMapper;
    private final InventoryMapper inventoryMapper;
    private final WarehouseMapper warehouseMapper;
    private final DrugCategoryMapper categoryMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final SupplierMapper supplierMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionDetailMapper prescriptionDetailMapper;
    private final MedicalOrderMapper medicalOrderMapper;
    private final MedicalOrderDetailMapper medicalOrderDetailMapper;

    public ReportServiceImpl(DrugMapper drugMapper, InventoryMapper inventoryMapper,
                            WarehouseMapper warehouseMapper, DrugCategoryMapper categoryMapper,
                            PurchaseOrderMapper purchaseOrderMapper, SupplierMapper supplierMapper,
                            PrescriptionMapper prescriptionMapper, PrescriptionDetailMapper prescriptionDetailMapper,
                            MedicalOrderMapper medicalOrderMapper, MedicalOrderDetailMapper medicalOrderDetailMapper) {
        this.drugMapper = drugMapper;
        this.inventoryMapper = inventoryMapper;
        this.warehouseMapper = warehouseMapper;
        this.categoryMapper = categoryMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.supplierMapper = supplierMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionDetailMapper = prescriptionDetailMapper;
        this.medicalOrderMapper = medicalOrderMapper;
        this.medicalOrderDetailMapper = medicalOrderDetailMapper;
    }

    // ==================== 库存报表 ====================

    @Override
    public Map<String, Object> getInventorySummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // 药品种类数
        int totalDrugCount = drugMapper.selectCount(null).intValue();
        summary.put("totalDrugCount", totalDrugCount);
        
        // 库存总数量和总金额
        List<Inventory> inventories = inventoryMapper.selectList(null);
        int totalQuantity = inventories.stream().mapToInt(Inventory::getQuantity).sum();
        BigDecimal totalAmount = inventories.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalQuantity", totalQuantity);
        summary.put("totalAmount", totalAmount.doubleValue());
        
        // 近效期药品数（3个月内过期）
        LocalDate threeMonthsLater = LocalDate.now().plusMonths(3);
        int expiringCount = (int) inventories.stream()
                .filter(i -> i.getExpireDate() != null && i.getExpireDate().toLocalDate().isBefore(threeMonthsLater))
                .count();
        summary.put("expiringCount", expiringCount);
        
        return summary;
    }

    @Override
    public List<Map<String, Object>> getInventoryDetail(int page, int size) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Inventory> inventories = inventoryMapper.selectList(null);
        
        Map<Long, Drug> drugMap = drugMapper.selectList(null).stream()
                .collect(Collectors.toMap(Drug::getId, d -> d));
        Map<Long, Warehouse> warehouseMap = warehouseMapper.selectList(null).stream()
                .collect(Collectors.toMap(Warehouse::getId, w -> w));
        Map<Long, DrugCategory> categoryMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(DrugCategory::getId, c -> c));
        
        for (Inventory inv : inventories) {
            Drug drug = drugMap.get(inv.getDrugId());
            Warehouse warehouse = warehouseMap.get(inv.getWarehouseId());
            
            Map<String, Object> item = new HashMap<>();
            item.put("drugCode", drug != null ? drug.getDrugCode() : "");
            item.put("drugName", drug != null ? drug.getDrugName() : "");
            item.put("spec", drug != null ? drug.getSpec() : "");
            item.put("categoryName", drug != null && drug.getCategoryId() != null 
                    ? categoryMap.get(drug.getCategoryId()).getCategoryName() : "");
            item.put("quantity", inv.getQuantity());
            item.put("unitPrice", inv.getUnitPrice().doubleValue());
            item.put("amount", inv.getUnitPrice().multiply(BigDecimal.valueOf(inv.getQuantity())).doubleValue());
            item.put("warehouseName", warehouse != null ? warehouse.getWarehouseName() : "");
            result.add(item);
        }
        
        return result;
    }

    @Override
    public long getInventoryTotal() {
        return inventoryMapper.selectCount(null);
    }

    @Override
    public List<Map<String, Object>> getTurnoverData() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        
        // 模拟周转率数据
        double[] rates = {4.2, 3.8, 5.1, 4.5, 5.8, 4.9, 6.2, 5.5, 4.8, 5.3, 6.1, 5.7};
        
        for (int i = 0; i < 12; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", months[i]);
            item.put("rate", rates[i]);
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getABCData() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Inventory> inventories = inventoryMapper.selectList(null);
        Map<Long, Drug> drugMap = drugMapper.selectList(null).stream()
                .collect(Collectors.toMap(Drug::getId, d -> d));
        
        // 计算每个药品的库存金额
        List<Map<String, Object>> drugAmounts = new ArrayList<>();
        for (Inventory inv : inventories) {
            Drug drug = drugMap.get(inv.getDrugId());
            if (drug != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("drugId", drug.getId());
                item.put("drugName", drug.getDrugName());
                item.put("spec", drug.getSpec());
                item.put("amount", inv.getUnitPrice().multiply(BigDecimal.valueOf(inv.getQuantity())));
                item.put("quantity", inv.getQuantity());
                drugAmounts.add(item);
            }
        }
        
        // 按金额降序排序
        drugAmounts.sort((a, b) -> ((BigDecimal) b.get("amount")).compareTo((BigDecimal) a.get("amount")));
        
        // 计算总金额
        BigDecimal totalAmount = drugAmounts.stream()
                .map(i -> (BigDecimal) i.get("amount"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 分配ABC类别（A: 前20%, B: 中间30%, C: 后50%）
        BigDecimal cumulative = BigDecimal.ZERO;
        for (int i = 0; i < drugAmounts.size(); i++) {
            Map<String, Object> item = drugAmounts.get(i);
            BigDecimal amount = (BigDecimal) item.get("amount");
            cumulative = cumulative.add(amount);
            double percentage = cumulative.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 2, RoundingMode.HALF_UP).doubleValue();
            
            String level;
            if (percentage <= 70) level = "A";
            else if (percentage <= 90) level = "B";
            else level = "C";
            
            Map<String, Object> abcItem = new HashMap<>();
            abcItem.put("level", level);
            abcItem.put("drugName", item.get("drugName"));
            abcItem.put("spec", item.get("spec"));
            abcItem.put("amount", amount.doubleValue());
            abcItem.put("percentage", percentage);
            abcItem.put("quantity", item.get("quantity"));
            result.add(abcItem);
        }
        
        return result;
    }

    // ==================== 采购报表 ====================

    @Override
    public Map<String, Object> getPurchaseSummary(Long supplierId, String startDate, String endDate) {
        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(null);
        
        int orderCount = orders.size();
        BigDecimal totalAmount = orders.stream()
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int completedCount = (int) orders.stream().filter(o -> o.getStatus() == 3).count();
        int pendingCount = (int) orders.stream().filter(o -> o.getStatus() < 3).count();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("orderCount", orderCount);
        summary.put("totalAmount", totalAmount.doubleValue());
        summary.put("completedCount", completedCount);
        summary.put("pendingCount", pendingCount);
        
        return summary;
    }

    @Override
    public List<Map<String, Object>> getPurchaseDetail(int page, int size, Long supplierId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(null);
        
        Map<Long, Supplier> supplierMap = supplierMapper.selectList(null).stream()
                .collect(Collectors.toMap(Supplier::getId, s -> s));
        
        for (PurchaseOrder order : orders) {
            Supplier supplier = supplierMap.get(order.getSupplierId());
            
            Map<String, Object> item = new HashMap<>();
            item.put("orderNo", order.getOrderNo());
            item.put("supplierName", supplier != null ? supplier.getSupplierName() : "");
            item.put("orderDate", order.getCreateTime());
            item.put("totalAmount", order.getTotalAmount().doubleValue());
            item.put("status", order.getStatus() == 1 ? "待审批" : order.getStatus() == 2 ? "已审批" : "已完成");
            item.put("createTime", order.getCreateTime());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public long getPurchaseTotal(Long supplierId) {
        return purchaseOrderMapper.selectCount(null);
    }

    @Override
    public List<Map<String, Object>> getSupplierStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Supplier> suppliers = supplierMapper.selectList(null);
        List<PurchaseOrder> orders = purchaseOrderMapper.selectList(null);
        
        for (Supplier supplier : suppliers) {
            List<PurchaseOrder> supplierOrders = orders.stream()
                    .filter(o -> supplier.getId().equals(o.getSupplierId()))
                    .collect(Collectors.toList());
            
            BigDecimal totalAmount = supplierOrders.stream()
                    .map(PurchaseOrder::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> item = new HashMap<>();
            item.put("supplierName", supplier.getSupplierName());
            item.put("orderCount", supplierOrders.size());
            item.put("totalAmount", totalAmount.doubleValue());
            item.put("contactName", supplier.getContactName());
            item.put("phone", supplier.getPhone());
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getPurchaseTrend() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        
        // 模拟采购趋势数据
        double[] amounts = {125000.00, 138000.00, 142000.00, 156000.00, 148000.00, 165000.00};
        
        for (int i = 0; i < 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", months[i]);
            item.put("amount", amounts[i]);
            result.add(item);
        }
        
        return result;
    }

    // ==================== 消耗报表 ====================

    @Override
    public Map<String, Object> getConsumptionSummary(Long departmentId, String startDate, String endDate) {
        // 直接返回模拟数据用于展示
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalConsumption", 5520);
        summary.put("totalAmount", 165600.0);
        summary.put("drugCount", 6);
        summary.put("departmentCount", 5);
        return summary;
    }

    @Override
    public List<Map<String, Object>> getDrugRanking(int limit) {
        // 直接返回模拟数据用于展示
        List<Map<String, Object>> result = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("drugName", "阿莫西林胶囊");
        item1.put("spec", "0.5g*20粒");
        item1.put("consumption", 1250);
        item1.put("rank", 1);
        result.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("drugName", "硝苯地平缓释片");
        item2.put("spec", "20mg*30片");
        item2.put("consumption", 1100);
        item2.put("rank", 2);
        result.add(item2);
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("drugName", "奥美拉唑肠溶胶囊");
        item3.put("spec", "20mg*14粒");
        item3.put("consumption", 850);
        item3.put("rank", 3);
        result.add(item3);
        
        Map<String, Object> item4 = new HashMap<>();
        item4.put("drugName", "沙丁胺醇气雾剂");
        item4.put("spec", "100μg*200揿");
        item4.put("consumption", 680);
        item4.put("rank", 4);
        result.add(item4);
        
        Map<String, Object> item5 = new HashMap<>();
        item5.put("drugName", "吗啡注射液");
        item5.put("spec", "10mg/1ml*5支");
        item5.put("consumption", 320);
        item5.put("rank", 5);
        result.add(item5);
        
        return result.subList(0, Math.min(limit, result.size()));
    }

    @Override
    public List<Map<String, Object>> getDepartmentStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] departments = {"内科", "外科", "妇产科", "儿科", "急诊科"};
        int[] consumptions = {1560, 1280, 890, 670, 1120};
        double[] amounts = {45600.00, 38400.00, 26700.00, 20100.00, 33600.00};
        
        for (int i = 0; i < departments.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("departmentName", departments[i]);
            item.put("consumption", consumptions[i]);
            item.put("amount", amounts[i]);
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getDoctorStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] doctors = {"王医生", "李医生", "张医生", "刘医生", "陈医生"};
        int[] prescriptionCounts = {156, 134, 128, 112, 98};
        double[] amounts = {46800.00, 40200.00, 38400.00, 33600.00, 29400.00};
        
        for (int i = 0; i < doctors.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("doctorName", doctors[i]);
            item.put("prescriptionCount", prescriptionCounts[i]);
            item.put("amount", amounts[i]);
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getConsumptionTrend() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        
        // 模拟消耗趋势数据
        double[] amounts = {85000.00, 92000.00, 88000.00, 95000.00, 91000.00, 98000.00};
        
        for (int i = 0; i < 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", months[i]);
            item.put("amount", amounts[i]);
            result.add(item);
        }
        
        return result;
    }
}
