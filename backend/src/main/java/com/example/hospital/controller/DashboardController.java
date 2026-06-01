package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.*;
import com.example.hospital.mapper.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private DrugMapper drugMapper;

    @Resource
    private InventoryMapper inventoryMapper;

    @Resource
    private PurchaseOrderMapper purchaseOrderMapper;

    @Resource
    private PrescriptionMapper prescriptionMapper;

    @Resource
    private MedicalOrderMapper medicalOrderMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private PurchaseRequestMapper purchaseRequestMapper;

    @Resource
    private SpecialDrugRecordMapper specialDrugRecordMapper;

    @GetMapping("/admin")
    public Result<Map<String, Object>> getAdminDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        // 统计数据
        int userCount = userMapper.selectCount(null).intValue();
        int drugCount = drugMapper.selectCount(null).intValue();
        int orderCount = purchaseOrderMapper.selectCount(null).intValue();
        
        // 计算库存总量
        List<Inventory> inventories = inventoryMapper.selectList(null);
        int totalInventory = inventories.stream().mapToInt(Inventory::getQuantity).sum();
        
        data.put("userCount", userCount);
        data.put("drugCount", drugCount);
        data.put("inventoryCount", totalInventory);
        data.put("orderCount", orderCount);
        
        // 最近操作日志
        List<SysLog> logs = sysLogMapper.selectRecentLogs(5);
        data.put("recentLogs", logs.stream().map(log -> {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("id", log.getId());
            logMap.put("action", log.getOperation());
            logMap.put("operator", log.getUsername());
            logMap.put("time", formatTime(log.getCreateTime()));
            return logMap;
        }).toList());
        
        return Result.success(data);
    }

    @GetMapping("/doctor")
    public Result<Map<String, Object>> getDoctorDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userMapper.findByUsername(username);
        
        // 今日医嘱数量
        LocalDate today = LocalDate.now();
        int todayOrders = 0;
        int pendingDispensing = 0;
        int patientCount = 0;
        
        if (user != null) {
            todayOrders = medicalOrderMapper.countByDateAndDoctor(today.toString(), user.getId()).intValue();
            pendingDispensing = medicalOrderMapper.countPendingDispensing(user.getId()).intValue();
            patientCount = medicalOrderMapper.countTodayPatients(user.getId()).intValue();
        }
        
        data.put("todayOrders", todayOrders);
        data.put("pendingDispensing", pendingDispensing);
        data.put("patientCount", patientCount);
        data.put("commonDrugs", 25);
        
        // 待处理医嘱列表（模拟数据）
        data.put("orders", List.of(
            Map.of("id", 1, "patientName", "张三", "ward", "内科-101", "orderType", "长期医嘱", "createTime", "08:30"),
            Map.of("id", 2, "patientName", "李四", "ward", "外科-205", "orderType", "临时医嘱", "createTime", "09:15"),
            Map.of("id", 3, "patientName", "王五", "ward", "内科-102", "orderType", "长期医嘱", "createTime", "10:00")
        ));
        
        // 常用药品（模拟数据）
        data.put("commonDrugsList", List.of(
            Map.of("id", 1, "name", "阿莫西林胶囊", "spec", "0.5g*20粒"),
            Map.of("id", 2, "name", "硝苯地平缓释片", "spec", "20mg*30片"),
            Map.of("id", 3, "name", "奥美拉唑肠溶胶囊", "spec", "20mg*14粒"),
            Map.of("id", 4, "name", "沙丁胺醇气雾剂", "spec", "100μg*200揿")
        ));
        
        return Result.success(data);
    }

    @GetMapping("/pharmacist")
    public Result<Map<String, Object>> getPharmacistDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        // 待审核处方
        int pendingPrescriptions = prescriptionMapper.countByStatus(1).intValue();
        
        // 今日调配数量
        LocalDate today = LocalDate.now();
        int todayDispensing = prescriptionMapper.countTodayDispensed().intValue();
        
        // 库存预警数量
        int lowStockCount = inventoryMapper.countLowStock().intValue();
        
        // 特殊药品数量
        int specialDrugsCount = drugMapper.countSpecialDrugs().intValue();
        
        data.put("pendingPrescriptions", pendingPrescriptions);
        data.put("todayDispensing", todayDispensing);
        data.put("lowStockCount", lowStockCount);
        data.put("specialDrugsCount", specialDrugsCount);
        
        // 待审核处方列表
        List<Prescription> prescriptions = prescriptionMapper.selectByStatus(1, 3);
        data.put("prescriptions", prescriptions.stream().map(p -> {
            Map<String, Object> pMap = new HashMap<>();
            pMap.put("id", p.getId());
            pMap.put("patientName", p.getPatientName());
            pMap.put("doctorName", p.getDoctorName());
            pMap.put("createTime", formatTime(p.getCreateTime()));
            return pMap;
        }).toList());
        
        // 库存预警药品
        List<Inventory> lowStockDrugs = inventoryMapper.selectLowStock(5);
        data.put("lowStockDrugs", lowStockDrugs.stream().map(iv -> {
            Map<String, Object> ivMap = new HashMap<>();
            Drug drug = drugMapper.selectById(iv.getDrugId());
            ivMap.put("drugName", drug != null ? drug.getDrugName() : "未知");
            ivMap.put("spec", drug != null ? drug.getSpec() : "");
            ivMap.put("warehouse", "门诊药房");
            ivMap.put("quantity", iv.getQuantity());
            ivMap.put("minStock", 30);
            return ivMap;
        }).toList());
        
        return Result.success(data);
    }

    @GetMapping("/purchaser")
    public Result<Map<String, Object>> getPurchaserDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        // 待处理申请
        int pendingRequests = purchaseRequestMapper.countPending().intValue();
        
        // 待收货订单
        int pendingOrders = purchaseOrderMapper.countPendingDelivery().intValue();
        
        data.put("pendingRequests", pendingRequests);
        data.put("pendingOrders", pendingOrders);
        data.put("supplierCount", 12);
        data.put("monthlyAmount", "¥128,500");
        
        // 待处理采购申请
        List<PurchaseRequest> requests = purchaseRequestMapper.selectPending(3);
        data.put("requests", requests.stream().map(req -> {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("id", req.getId());
            reqMap.put("drugName", "药品采购");
            reqMap.put("quantity", 100);
            reqMap.put("applicant", "系统");
            reqMap.put("createTime", formatTime(req.getCreateTime()));
            return reqMap;
        }).toList());
        
        // 供应商信息（模拟数据）
        data.put("suppliers", List.of(
            Map.of("supplierName", "华北制药集团", "contactName", "王经理", "phone", "0311-85962222", "cooperationStatus", "合作中"),
            Map.of("supplierName", "拜耳医药", "contactName", "李经理", "phone", "010-59218888", "cooperationStatus", "合作中"),
            Map.of("supplierName", "国药集团", "contactName", "张经理", "phone", "010-63365555", "cooperationStatus", "合作中")
        ));
        
        return Result.success(data);
    }

    @GetMapping("/stock-manager")
    public Result<Map<String, Object>> getStockManagerDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        // 库存总量
        List<Inventory> inventories = inventoryMapper.selectList(null);
        int totalInventory = inventories.stream().mapToInt(Inventory::getQuantity).sum();
        
        // 库存预警数量
        int warningCount = inventoryMapper.countLowStock().intValue();
        
        // 临期药品数量
        int expireWarning = inventoryMapper.countExpiring().intValue();
        
        data.put("totalInventory", totalInventory);
        data.put("warningCount", warningCount);
        data.put("expireWarning", expireWarning);
        data.put("warehouseCount", 4);
        
        // 库存预警药品
        List<Inventory> warningDrugs = inventoryMapper.selectLowStock(3);
        data.put("warningDrugs", warningDrugs.stream().map(iv -> {
            Map<String, Object> ivMap = new HashMap<>();
            Drug drug = drugMapper.selectById(iv.getDrugId());
            ivMap.put("id", iv.getId());
            ivMap.put("drugName", drug != null ? drug.getDrugName() : "未知");
            ivMap.put("warehouse", "门诊药房");
            ivMap.put("quantity", iv.getQuantity());
            return ivMap;
        }).toList());
        
        // 临期药品
        List<Inventory> expireDrugs = inventoryMapper.selectExpiring(3);
        data.put("expireDrugs", expireDrugs.stream().map(iv -> {
            Map<String, Object> ivMap = new HashMap<>();
            Drug drug = drugMapper.selectById(iv.getDrugId());
            ivMap.put("drugName", drug != null ? drug.getDrugName() : "未知");
            ivMap.put("spec", drug != null ? drug.getSpec() : "");
            ivMap.put("expireDate", iv.getExpireDate());
            ivMap.put("warehouse", "门诊药房");
            ivMap.put("quantity", iv.getQuantity());
            return ivMap;
        }).toList());
        
        return Result.success(data);
    }

    @GetMapping("/special-pharmacist")
    public Result<Map<String, Object>> getSpecialPharmacistDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        // 特殊药品种类
        int specialDrugCount = drugMapper.countSpecialDrugs().intValue();
        
        // 今日发放记录
        LocalDate today = LocalDate.now();
        int todayRecords = specialDrugRecordMapper.countTodayRecords().intValue();
        
        // 特殊药品库存总量
        List<Inventory> inventories = inventoryMapper.selectSpecialDrugInventory();
        int specialInventory = inventories.stream().mapToInt(Inventory::getQuantity).sum();
        
        // 库存预警数量
        int lowStockCount = inventoryMapper.countSpecialLowStock().intValue();
        
        data.put("specialDrugCount", specialDrugCount);
        data.put("todayRecords", todayRecords);
        data.put("specialInventory", specialInventory);
        data.put("lowStockCount", lowStockCount);
        
        // 今日发放记录（模拟数据）
        data.put("records", List.of(
            Map.of("drugName", "吗啡注射液", "patientName", "张三", "doctorName", "李医生", "quantity", 1, "createTime", "08:30"),
            Map.of("drugName", "地西泮片", "patientName", "李四", "doctorName", "王医生", "quantity", 2, "createTime", "09:15"),
            Map.of("drugName", "吗啡注射液", "patientName", "王五", "doctorName", "赵医生", "quantity", 1, "createTime", "10:00")
        ));
        
        // 特殊药品库存
        data.put("specialDrugs", inventories.stream().map(iv -> {
            Map<String, Object> ivMap = new HashMap<>();
            Drug drug = drugMapper.selectById(iv.getDrugId());
            ivMap.put("drugName", drug != null ? drug.getDrugName() : "未知");
            ivMap.put("spec", drug != null ? drug.getSpec() : "");
            ivMap.put("category", drug != null && drug.getIsSpecial() == 1 ? "麻醉药品" : "精神药品");
            ivMap.put("quantity", iv.getQuantity());
            ivMap.put("warehouse", "特殊药品库");
            return ivMap;
        }).toList());
        
        return Result.success(data);
    }

    @GetMapping("/pharmacy-director")
    public Result<Map<String, Object>> getPharmacyDirectorDashboard() {
        Map<String, Object> data = new HashMap<>();
        
        // 待审批采购申请
        int pendingApprovals = purchaseRequestMapper.countPendingApproval().intValue();
        
        // 特殊药品数量
        int specialDrugs = drugMapper.countSpecialDrugs().intValue();
        
        // 药品种类
        int drugCount = drugMapper.selectCount(null).intValue();
        
        data.put("pendingApprovals", pendingApprovals);
        data.put("specialDrugs", specialDrugs);
        data.put("monthlyAmount", "¥156,800");
        data.put("drugCount", drugCount);
        
        // 待审批采购申请
        List<PurchaseRequest> requests = purchaseRequestMapper.selectPendingApproval(3);
        data.put("pendingRequests", requests.stream().map(req -> {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("id", req.getId());
            reqMap.put("drugName", "药品采购申请");
            reqMap.put("quantity", 150);
            reqMap.put("applicant", "采购员");
            reqMap.put("createTime", formatTime(req.getCreateTime()));
            return reqMap;
        }).toList());
        
        // 特殊药品库存概览
        List<Inventory> inventories = inventoryMapper.selectSpecialDrugInventory();
        data.put("specialDrugStock", inventories.stream().map(iv -> {
            Map<String, Object> ivMap = new HashMap<>();
            Drug drug = drugMapper.selectById(iv.getDrugId());
            ivMap.put("drugName", drug != null ? drug.getDrugName() : "未知");
            ivMap.put("spec", drug != null ? drug.getSpec() : "");
            ivMap.put("category", drug != null && drug.getIsSpecial() == 1 ? "麻醉药品" : "精神药品");
            ivMap.put("quantity", iv.getQuantity());
            ivMap.put("warehouse", "特殊药品库");
            return ivMap;
        }).toList());
        
        return Result.success(data);
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
