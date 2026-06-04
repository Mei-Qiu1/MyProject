package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hospital.entity.Drug;
import com.example.hospital.entity.Inventory;
import com.example.hospital.entity.SpecialDrugApply;
import com.example.hospital.entity.SpecialDrugRecord;
import com.example.hospital.mapper.DrugMapper;
import com.example.hospital.mapper.InventoryMapper;
import com.example.hospital.mapper.SpecialDrugApplyMapper;
import com.example.hospital.mapper.SpecialDrugRecordMapper;
import com.example.hospital.service.SpecialDrugService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecialDrugServiceImpl implements SpecialDrugService {

    private final SpecialDrugRecordMapper specialDrugRecordMapper;
    private final SpecialDrugApplyMapper specialDrugApplyMapper;
    private final InventoryMapper inventoryMapper;
    private final DrugMapper drugMapper;

    public SpecialDrugServiceImpl(SpecialDrugRecordMapper specialDrugRecordMapper, 
                                  SpecialDrugApplyMapper specialDrugApplyMapper,
                                  InventoryMapper inventoryMapper,
                                  DrugMapper drugMapper) {
        this.specialDrugRecordMapper = specialDrugRecordMapper;
        this.specialDrugApplyMapper = specialDrugApplyMapper;
        this.inventoryMapper = inventoryMapper;
        this.drugMapper = drugMapper;
    }

    @Override
    public List<Map<String, Object>> listInventory(String keyword) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("drugCode", "D0005");
        item1.put("drugName", "吗啡注射液");
        item1.put("spec", "10mg/1ml*5支");
        item1.put("batchNo", "B20240501");
        item1.put("expireDate", "2025-05-09");
        item1.put("quantity", 30);
        item1.put("warehouse", "特殊药品库");
        result.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("drugCode", "D0006");
        item2.put("drugName", "地西泮片");
        item2.put("spec", "2.5mg*20片");
        item2.put("batchNo", "B20240502");
        item2.put("expireDate", "2025-05-14");
        item2.put("quantity", 50);
        item2.put("warehouse", "特殊药品库");
        result.add(item2);
        
        return result;
    }

    @Override
    public List<SpecialDrugRecord> listRecords(String keyword) {
        List<SpecialDrugRecord> result = new java.util.ArrayList<>();
        
        SpecialDrugRecord record1 = new SpecialDrugRecord();
        record1.setId(1L);
        record1.setDrugId(5L);
        record1.setBatchNo("B20240501");
        record1.setPrescriptionNo("PR20240601001");
        record1.setQuantity(5);
        record1.setAmount(java.math.BigDecimal.valueOf(75.00));
        record1.setPurpose("术后镇痛");
        record1.setUser1("王医生");
        record1.setUser2("张药师");
        record1.setRecycleStatus("已回收");
        record1.setWarehouseId(4L);
        record1.setCreateTime(java.time.LocalDateTime.parse("2024-06-01T10:30:00"));
        result.add(record1);
        
        SpecialDrugRecord record2 = new SpecialDrugRecord();
        record2.setId(2L);
        record2.setDrugId(5L);
        record2.setBatchNo("B20240501");
        record2.setPrescriptionNo("PR20240605002");
        record2.setQuantity(3);
        record2.setAmount(java.math.BigDecimal.valueOf(45.00));
        record2.setPurpose("癌症晚期止痛");
        record2.setUser1("李医生");
        record2.setUser2("张药师");
        record2.setRecycleStatus("已回收");
        record2.setWarehouseId(4L);
        record2.setCreateTime(java.time.LocalDateTime.parse("2024-06-05T14:20:00"));
        result.add(record2);
        
        SpecialDrugRecord record3 = new SpecialDrugRecord();
        record3.setId(3L);
        record3.setDrugId(6L);
        record3.setBatchNo("B20240502");
        record3.setPrescriptionNo("PR20240608003");
        record3.setQuantity(10);
        record3.setAmount(java.math.BigDecimal.valueOf(100.00));
        record3.setPurpose("术前镇静");
        record3.setUser1("赵医生");
        record3.setUser2("王药师");
        record3.setRecycleStatus("使用中");
        record3.setWarehouseId(4L);
        record3.setCreateTime(java.time.LocalDateTime.parse("2024-06-08T09:15:00"));
        result.add(record3);
        
        return result;
    }

    @Override
    public List<SpecialDrugApply> listApplies() {
        List<SpecialDrugApply> result = new java.util.ArrayList<>();
        
        SpecialDrugApply apply1 = new SpecialDrugApply();
        apply1.setId(1L);
        apply1.setApplyNo("SA20240620001");
        apply1.setDrugId(5L);
        apply1.setDrugName("吗啡注射液");
        apply1.setQuantity(10);
        apply1.setPrescriptionNo("PR20240620006");
        apply1.setPurpose("晚期癌症患者止痛");
        apply1.setStatus(2);
        apply1.setUser1("刘医生");
        apply1.setUser2("陈主任");
        apply1.setCreateTime(java.time.LocalDateTime.parse("2024-06-20T09:30:00"));
        result.add(apply1);
        
        SpecialDrugApply apply2 = new SpecialDrugApply();
        apply2.setId(2L);
        apply2.setApplyNo("SA20240621002");
        apply2.setDrugId(6L);
        apply2.setDrugName("地西泮片");
        apply2.setQuantity(20);
        apply2.setPrescriptionNo("PR20240621007");
        apply2.setPurpose("术前患者镇静");
        apply2.setStatus(2);
        apply2.setUser1("马医生");
        apply2.setUser2("陈主任");
        apply2.setCreateTime(java.time.LocalDateTime.parse("2024-06-21T14:00:00"));
        result.add(apply2);
        
        SpecialDrugApply apply3 = new SpecialDrugApply();
        apply3.setId(3L);
        apply3.setApplyNo("SA20240622003");
        apply3.setDrugId(5L);
        apply3.setDrugName("吗啡注射液");
        apply3.setQuantity(5);
        apply3.setPrescriptionNo("PR20240622008");
        apply3.setPurpose("术后镇痛");
        apply3.setStatus(1);
        apply3.setUser1("周医生");
        apply3.setUser2(null);
        apply3.setCreateTime(java.time.LocalDateTime.parse("2024-06-22T10:15:00"));
        result.add(apply3);
        
        return result;
    }

    @Override
    @Transactional
    public void saveApply(Map<String, Object> apply) {
        SpecialDrugApply record = new SpecialDrugApply();
        String applyNo = "SA" + System.currentTimeMillis();
        record.setApplyNo(applyNo);
        record.setDrugId(Long.valueOf(apply.get("drugId").toString()));
        record.setDrugName((String) apply.get("drugName"));
        record.setQuantity(Integer.valueOf(apply.get("quantity").toString()));
        record.setPrescriptionNo((String) apply.get("prescriptionNo"));
        record.setPurpose((String) apply.get("purpose"));
        record.setStatus(1);
        record.setCreateTime(LocalDateTime.now());
        specialDrugApplyMapper.insert(record);
    }

    @Override
    @Transactional
    public void approveApply(Long id, Map<String, String> users) {
        // 模拟审批，不实际操作数据库
    }

    @Override
    @Transactional
    public void recycle(Long recordId, Map<String, Object> recycleData) {
        // 模拟回收，不实际操作数据库
    }
}
