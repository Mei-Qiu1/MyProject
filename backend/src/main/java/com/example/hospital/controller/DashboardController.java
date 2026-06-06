package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.*;
import com.example.hospital.mapper.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final PurchaseRequestMapper purchaseRequestMapper;
    private final PurchaseRequestDetailMapper purchaseRequestDetailMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final SupplierMapper supplierMapper;
    private final UserMapper userMapper;
    private final DrugMapper drugMapper;
    private final InventoryMapper inventoryMapper;
    private final PrescriptionMapper prescriptionMapper;
    private final DispensingMapper dispensingMapper;
    private final SpecialDrugApplyMapper specialDrugApplyMapper;
    private final SpecialDrugRecordMapper specialDrugRecordMapper;
    private final MedicalOrderMapper medicalOrderMapper;
    private final SysLogMapper sysLogMapper;
    private final WarehouseMapper warehouseMapper;

    public DashboardController(PurchaseRequestMapper purchaseRequestMapper,
                               PurchaseRequestDetailMapper purchaseRequestDetailMapper,
                               PurchaseOrderMapper purchaseOrderMapper,
                               SupplierMapper supplierMapper,
                               UserMapper userMapper,
                               DrugMapper drugMapper,
                               InventoryMapper inventoryMapper,
                               PrescriptionMapper prescriptionMapper,
                               DispensingMapper dispensingMapper,
                               SpecialDrugApplyMapper specialDrugApplyMapper,
                               SpecialDrugRecordMapper specialDrugRecordMapper,
                               MedicalOrderMapper medicalOrderMapper,
                               SysLogMapper sysLogMapper,
                               WarehouseMapper warehouseMapper) {
        this.purchaseRequestMapper = purchaseRequestMapper;
        this.purchaseRequestDetailMapper = purchaseRequestDetailMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.supplierMapper = supplierMapper;
        this.userMapper = userMapper;
        this.drugMapper = drugMapper;
        this.inventoryMapper = inventoryMapper;
        this.prescriptionMapper = prescriptionMapper;
        this.dispensingMapper = dispensingMapper;
        this.specialDrugApplyMapper = specialDrugApplyMapper;
        this.specialDrugRecordMapper = specialDrugRecordMapper;
        this.medicalOrderMapper = medicalOrderMapper;
        this.sysLogMapper = sysLogMapper;
        this.warehouseMapper = warehouseMapper;
    }

    @GetMapping("/admin")
    public Result<?> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();

        long userCount = userMapper.selectCount(null);
        data.put("userCount", userCount);

        long drugCount = drugMapper.selectCount(null);
        data.put("drugCount", drugCount);

        Long totalInventory = inventoryMapper.sumAllQuantity();
        data.put("inventoryCount", totalInventory != null ? totalInventory : 0);

        long orderCount = purchaseOrderMapper.selectCount(null);
        data.put("orderCount", orderCount);

        List<SysLog> logs = sysLogMapper.selectRecentLogs(5);
        List<Map<String, Object>> recentLogs = new ArrayList<>();
        for (SysLog log : logs) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", log.getId());
            item.put("action", log.getOperation());
            item.put("operator", log.getOperatorName() != null ? log.getOperatorName() : "系统");
            item.put("time", formatTimeAgo(log.getCreateTime()));
            recentLogs.add(item);
        }
        data.put("recentLogs", recentLogs);

        return Result.success(data);
    }

    @GetMapping("/purchaser")
    public Result<?> getPurchaserDashboard() {
        Map<String, Object> data = new HashMap<>();

        LambdaQueryWrapper<PurchaseRequest> requestWrapper = new LambdaQueryWrapper<>();
        requestWrapper.eq(PurchaseRequest::getStatus, 2);
        long pendingRequests = purchaseRequestMapper.selectCount(requestWrapper);
        data.put("pendingRequests", pendingRequests);

        LambdaQueryWrapper<PurchaseOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(PurchaseOrder::getStatus, 1);
        long pendingOrders = purchaseOrderMapper.selectCount(orderWrapper);
        data.put("pendingOrders", pendingOrders);

        long supplierCount = supplierMapper.selectCount(null);
        data.put("supplierCount", supplierCount);

        BigDecimal monthlyAmount = purchaseOrderMapper.sumAmountByMonth();
        data.put("monthlyAmount", monthlyAmount != null ? "¥" + formatNumber(monthlyAmount) : "¥0");

        List<PurchaseRequest> requestList = purchaseRequestMapper.selectApprovedRequests();
        List<Map<String, Object>> requests = new ArrayList<>();
        for (PurchaseRequest request : requestList) {
            LambdaQueryWrapper<PurchaseRequestDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(PurchaseRequestDetail::getRequestId, request.getId());
            List<PurchaseRequestDetail> details = purchaseRequestDetailMapper.selectList(detailWrapper);
            
            for (PurchaseRequestDetail detail : details) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", request.getId());
                item.put("detailId", detail.getId());
                Drug drug = drugMapper.selectById(detail.getDrugId());
                item.put("drugName", drug != null ? drug.getDrugName() : detail.getDrugName());
                item.put("quantity", detail.getQuantity());
                item.put("applicant", request.getCreateBy() != null ? request.getCreateBy() : "未知");
                item.put("createTime", request.getCreateTime() != null ?
                        request.getCreateTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "-");
                requests.add(item);
            }
        }
        data.put("requests", requests);

        List<Supplier> supplierList = supplierMapper.selectList(null);
        List<Map<String, Object>> suppliers = new ArrayList<>();
        for (Supplier supplier : supplierList) {
            Map<String, Object> item = new HashMap<>();
            item.put("supplierName", supplier.getSupplierName());
            item.put("contactName", supplier.getContactName());
            item.put("phone", supplier.getPhone());
            item.put("cooperationStatus", supplier.getCooperationStatus() == 1 ? "合作中" : "已终止");
            suppliers.add(item);
        }
        data.put("suppliers", suppliers);

        return Result.success(data);
    }

    @GetMapping("/stock-manager")
    public Result<?> getStockManagerDashboard() {
        Map<String, Object> data = new HashMap<>();

        Long totalInventory = inventoryMapper.sumAllQuantity();
        data.put("totalInventory", totalInventory != null ? totalInventory : 0);

        Long warningCount = inventoryMapper.countLowStock();
        data.put("warningCount", warningCount != null ? warningCount : 0);

        Long expireWarning = inventoryMapper.countExpiringStock();
        data.put("expireWarning", expireWarning != null ? expireWarning : 0);

        long warehouseCount = warehouseMapper.selectCount(null);
        data.put("warehouseCount", warehouseCount);

        List<Map<String, Object>> warningDrugs = inventoryMapper.selectLowStockDrugs(10);
        data.put("warningDrugs", warningDrugs);

        List<Map<String, Object>> expireDrugs = inventoryMapper.selectExpiringDrugs(10);
        data.put("expireDrugs", expireDrugs);

        return Result.success(data);
    }

    @GetMapping("/pharmacist")
    public Result<?> getPharmacistDashboard() {
        Map<String, Object> data = new HashMap<>();

        LambdaQueryWrapper<Prescription> presWrapper = new LambdaQueryWrapper<>();
        presWrapper.eq(Prescription::getStatus, 0);
        long pendingPrescriptions = prescriptionMapper.selectCount(presWrapper);
        data.put("pendingPrescriptions", pendingPrescriptions);

        long todayDispensing = dispensingMapper.countTodayDispensing();
        data.put("todayDispensing", todayDispensing);

        Long lowStockCount = inventoryMapper.countLowStock();
        data.put("lowStockCount", lowStockCount != null ? lowStockCount : 0);

        LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
        drugWrapper.eq(Drug::getIsSpecial, 1);
        long specialDrugsCount = drugMapper.selectCount(drugWrapper);
        data.put("specialDrugsCount", specialDrugsCount);

        List<Prescription> prescriptionList = prescriptionMapper.selectPendingPrescriptions(5);
        List<Map<String, Object>> prescriptions = new ArrayList<>();
        for (Prescription pres : prescriptionList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", pres.getId());
            item.put("patientName", pres.getPatientName());
            User doctor = userMapper.selectById(pres.getDoctorId());
            item.put("doctorName", doctor != null ? doctor.getUserName() : "未知");
            item.put("createTime", pres.getCreateTime() != null ?
                    pres.getCreateTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "-");
            prescriptions.add(item);
        }
        data.put("prescriptions", prescriptions);

        List<Map<String, Object>> lowStockDrugs = inventoryMapper.selectLowStockDrugs(10);
        data.put("lowStockDrugs", lowStockDrugs);

        return Result.success(data);
    }

    @GetMapping("/pharmacy-director")
    public Result<?> getPharmacyDirectorDashboard() {
        Map<String, Object> data = new HashMap<>();

        LambdaQueryWrapper<PurchaseRequest> requestWrapper = new LambdaQueryWrapper<>();
        requestWrapper.eq(PurchaseRequest::getStatus, 1);
        long pendingApprovals = purchaseRequestMapper.selectCount(requestWrapper);
        data.put("pendingApprovals", pendingApprovals);

        LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
        drugWrapper.eq(Drug::getIsSpecial, 1);
        long specialDrugs = drugMapper.selectCount(drugWrapper);
        data.put("specialDrugs", specialDrugs);

        BigDecimal monthlyAmount = purchaseOrderMapper.sumAmountByMonth();
        data.put("monthlyAmount", monthlyAmount != null ? "¥" + formatNumber(monthlyAmount) : "¥0");

        long drugCount = drugMapper.selectCount(null);
        data.put("drugCount", drugCount);

        List<PurchaseRequest> requestList = purchaseRequestMapper.selectPendingForApproval(5);
        List<Map<String, Object>> pendingRequests = new ArrayList<>();
        for (PurchaseRequest request : requestList) {
            LambdaQueryWrapper<PurchaseRequestDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(PurchaseRequestDetail::getRequestId, request.getId());
            List<PurchaseRequestDetail> details = purchaseRequestDetailMapper.selectList(detailWrapper);
            
            for (PurchaseRequestDetail detail : details) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", request.getId());
                Drug drug = drugMapper.selectById(detail.getDrugId());
                item.put("drugName", drug != null ? drug.getDrugName() : detail.getDrugName());
                item.put("quantity", detail.getQuantity());
                User user = userMapper.selectById(request.getCreateBy());
                item.put("applicant", user != null ? user.getUserName() : "未知");
                item.put("createTime", request.getCreateTime() != null ?
                        request.getCreateTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "-");
                pendingRequests.add(item);
            }
        }
        data.put("pendingRequests", pendingRequests);

        List<Map<String, Object>> specialDrugStock = inventoryMapper.selectSpecialDrugStock();
        data.put("specialDrugStock", specialDrugStock);

        return Result.success(data);
    }

    @GetMapping("/special-pharmacist")
    public Result<?> getSpecialPharmacistDashboard() {
        Map<String, Object> data = new HashMap<>();

        LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
        drugWrapper.eq(Drug::getIsSpecial, 1);
        long specialDrugCount = drugMapper.selectCount(drugWrapper);
        data.put("specialDrugCount", specialDrugCount);

        long todayRecords = specialDrugRecordMapper.countTodayRecords();
        data.put("todayRecords", todayRecords);

        Long specialInventory = inventoryMapper.sumSpecialDrugQuantity();
        data.put("specialInventory", specialInventory != null ? specialInventory : 0);

        Long lowStockCount = inventoryMapper.countSpecialDrugLowStock();
        data.put("lowStockCount", lowStockCount != null ? lowStockCount : 0);

        List<SpecialDrugRecord> recordList = specialDrugRecordMapper.selectTodayRecords(5);
        List<Map<String, Object>> records = new ArrayList<>();
        for (SpecialDrugRecord record : recordList) {
            Map<String, Object> item = new HashMap<>();
            Drug drug = drugMapper.selectById(record.getDrugId());
            item.put("drugName", drug != null ? drug.getDrugName() : "未知药品");
            item.put("patientName", record.getPatientName() != null ? record.getPatientName() : "");
            User doctor = userMapper.selectById(record.getDoctorId());
            item.put("doctorName", doctor != null ? doctor.getUserName() : "未知");
            item.put("quantity", record.getQuantity() + record.getUnit());
            item.put("createTime", record.getCreateTime() != null ?
                    record.getCreateTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "-");
            records.add(item);
        }
        data.put("records", records);

        List<Map<String, Object>> specialDrugs = inventoryMapper.selectSpecialDrugStock();
        data.put("specialDrugs", specialDrugs);

        return Result.success(data);
    }

    @GetMapping("/doctor")
    public Result<?> getDoctorDashboard() {
        Map<String, Object> data = new HashMap<>();

        long todayOrders = medicalOrderMapper.countTodayOrders();
        data.put("todayOrders", todayOrders);

        long pendingDispensing = dispensingMapper.countPendingDispensing();
        data.put("pendingDispensing", pendingDispensing);

        long patientCount = medicalOrderMapper.countTodayPatients();
        data.put("patientCount", patientCount);

        LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
        drugWrapper.eq(Drug::getIsSpecial, 0);
        long commonDrugs = drugMapper.selectCount(drugWrapper);
        data.put("commonDrugs", commonDrugs);

        List<MedicalOrder> orderList = medicalOrderMapper.selectTodayOrders(5);
        List<Map<String, Object>> orders = new ArrayList<>();
        for (MedicalOrder order : orderList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", order.getId());
            item.put("patientName", order.getPatientName());
            item.put("ward", order.getWard() != null ? order.getWard() : "-");
            item.put("orderType", order.getOrderType());
            item.put("createTime", order.getCreateTime() != null ?
                    order.getCreateTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "-");
            orders.add(item);
        }
        data.put("orders", orders);

        List<Drug> commonDrugList = drugMapper.selectCommonDrugs(10);
        List<Map<String, Object>> commonDrugsList = new ArrayList<>();
        for (Drug drug : commonDrugList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", drug.getId());
            item.put("name", drug.getDrugName());
            item.put("spec", drug.getSpec());
            commonDrugsList.add(item);
        }
        data.put("commonDrugsList", commonDrugsList);

        return Result.success(data);
    }

    private String formatNumber(BigDecimal number) {
        if (number == null) return "0";
        return String.format("%,.2f", number);
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "未知";
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();
        if (minutes < 1) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        long hours = minutes / 60;
        if (hours < 24) return hours + "小时前";
        long days = hours / 24;
        return days + "天前";
    }
}
