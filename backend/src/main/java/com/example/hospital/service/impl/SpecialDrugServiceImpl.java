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
        
        // 查询特殊药品（从药品表查询 is_special=1 的药品）
        LambdaQueryWrapper<Drug> drugWrapper = new LambdaQueryWrapper<>();
        drugWrapper.eq(Drug::getIsSpecial, 1);
        if (keyword != null && !keyword.isEmpty()) {
            drugWrapper.and(w -> w.like(Drug::getDrugName, keyword).or().like(Drug::getDrugCode, keyword));
        }
        List<Drug> specialDrugs = drugMapper.selectList(drugWrapper);
        
        for (Drug drug : specialDrugs) {
            // 查询该药品在特殊药品库的库存
            LambdaQueryWrapper<Inventory> invWrapper = new LambdaQueryWrapper<>();
            invWrapper.eq(Inventory::getDrugId, drug.getId());
            List<Inventory> inventories = inventoryMapper.selectList(invWrapper);
            
            if (inventories.isEmpty()) {
                // 如果没有库存记录，返回药品基本信息
                Map<String, Object> item = new HashMap<>();
                item.put("id", drug.getId());
                item.put("drugCode", drug.getDrugCode());
                item.put("drugName", drug.getDrugName());
                item.put("spec", drug.getSpec());
                item.put("batchNo", "");
                item.put("expireDate", "");
                item.put("quantity", 0);
                item.put("warehouseName", "特殊药品库");
                result.add(item);
            } else {
                // 如果有库存记录，返回每条库存的详细信息
                for (Inventory inv : inventories) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", inv.getId());
                    item.put("drugCode", drug.getDrugCode());
                    item.put("drugName", drug.getDrugName());
                    item.put("spec", drug.getSpec());
                    item.put("batchNo", inv.getBatchNo() != null ? inv.getBatchNo() : "");
                    item.put("expireDate", inv.getExpireDate() != null ? inv.getExpireDate().toString() : "");
                    item.put("quantity", inv.getQuantity() != null ? inv.getQuantity() : 0);
                    item.put("warehouseName", "特殊药品库");
                    result.add(item);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> listRecords(String keyword) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        
        LambdaQueryWrapper<SpecialDrugRecord> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SpecialDrugRecord::getPrescriptionNo, keyword)
                    .or().like(SpecialDrugRecord::getPurpose, keyword));
        }
        wrapper.orderByDesc(SpecialDrugRecord::getCreateTime);
        List<SpecialDrugRecord> records = specialDrugRecordMapper.selectList(wrapper);
        
        // 为每条记录补充药品名称
        for (SpecialDrugRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            
            // 查询药品名称
            if (record.getDrugId() != null) {
                Drug drug = drugMapper.selectById(record.getDrugId());
                if (drug != null) {
                    item.put("drugName", drug.getDrugName());
                } else {
                    item.put("drugName", "未知药品");
                }
            } else {
                item.put("drugName", "未知药品");
            }
            
            item.put("batchNo", record.getBatchNo() != null ? record.getBatchNo() : "");
            item.put("prescriptionNo", record.getPrescriptionNo() != null ? record.getPrescriptionNo() : "");
            item.put("quantity", record.getQuantity() != null ? record.getQuantity() : 0);
            item.put("purpose", record.getPurpose() != null ? record.getPurpose() : "");
            item.put("user1", record.getUser1() != null ? record.getUser1() : "");
            item.put("user2", record.getUser2() != null ? record.getUser2() : "");
            item.put("recycleStatus", record.getRecycleStatus() != null ? record.getRecycleStatus() : "");
            item.put("createTime", record.getCreateTime() != null ? record.getCreateTime().toString() : "");
            
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<SpecialDrugApply> listApplies() {
        LambdaQueryWrapper<SpecialDrugApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SpecialDrugApply::getCreateTime);
        return specialDrugApplyMapper.selectList(wrapper);
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
        // 从数据库更新审批状态
        SpecialDrugApply apply = specialDrugApplyMapper.selectById(id);
        if (apply != null) {
            apply.setStatus(2); // 已审核
            if (users != null && users.containsKey("user2")) {
                apply.setUser2(users.get("user2"));
            }
            specialDrugApplyMapper.updateById(apply);
        }
    }

    @Override
    @Transactional
    public void recycle(Long recordId, Map<String, Object> recycleData) {
        // 从数据库更新回收状态
        SpecialDrugRecord record = specialDrugRecordMapper.selectById(recordId);
        if (record != null) {
            record.setRecycleStatus("已回收");
            if (recycleData != null && recycleData.containsKey("user1")) {
                record.setUser1((String) recycleData.get("user1"));
            }
            if (recycleData != null && recycleData.containsKey("user2")) {
                record.setUser2((String) recycleData.get("user2"));
            }
            specialDrugRecordMapper.updateById(record);
        }
    }
}
