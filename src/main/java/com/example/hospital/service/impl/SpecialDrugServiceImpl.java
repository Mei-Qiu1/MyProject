package com.example.hospital.service.impl;

import com.example.hospital.entity.SpecialDrugRecord;
import com.example.hospital.mapper.SpecialDrugRecordMapper;
import com.example.hospital.service.SpecialDrugService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SpecialDrugServiceImpl implements SpecialDrugService {

    private final SpecialDrugRecordMapper specialDrugRecordMapper;

    public SpecialDrugServiceImpl(SpecialDrugRecordMapper specialDrugRecordMapper) {
        this.specialDrugRecordMapper = specialDrugRecordMapper;
    }

    @Override
    public List<SpecialDrugRecord> listInventory(String keyword) {
        // 简化：直接查询库存中的特殊药品（实际需要关联 drug 表 is_special=1）
        return specialDrugRecordMapper.selectList(null);
    }

    @Override
    public List<SpecialDrugRecord> listRecords(String keyword) {
        return specialDrugRecordMapper.selectList(null);
    }

    @Override
    public List<SpecialDrugRecord> listApplies() {
        return specialDrugRecordMapper.selectList(null);
    }

    @Override
    public void saveApply(Map<String, Object> apply) {
        SpecialDrugRecord record = new SpecialDrugRecord();
        record.setDrugId(Long.valueOf(apply.get("drugId").toString()));
        record.setQuantity(Integer.valueOf(apply.get("quantity").toString()));
        record.setPrescriptionNo((String) apply.get("prescriptionNo"));
        record.setPurpose((String) apply.get("purpose"));
        record.setCreateTime(LocalDateTime.now());
        specialDrugRecordMapper.insert(record);
    }

    @Override
    public void approveApply(Long id, Map<String, String> users) {
        SpecialDrugRecord record = specialDrugRecordMapper.selectById(id);
        if (record != null) {
            record.setUser1(users.get("user1"));
            record.setUser2(users.get("user2"));
            specialDrugRecordMapper.updateById(record);
        }
    }

    @Override
    public void recycle(Long recordId, Map<String, Object> recycleData) {
        SpecialDrugRecord record = specialDrugRecordMapper.selectById(recordId);
        if (record != null) {
            record.setRecycleStatus("已回收");
            record.setRecycleNo((String) recycleData.get("recycleNo"));
            specialDrugRecordMapper.updateById(record);
        }
    }
}