package com.example.hospital.service;

import com.example.hospital.entity.SpecialDrugRecord;
import java.util.List;
import java.util.Map;

public interface SpecialDrugService {
    List<SpecialDrugRecord> listInventory(String keyword);
    List<SpecialDrugRecord> listRecords(String keyword);
    List<SpecialDrugRecord> listApplies();
    void saveApply(Map<String, Object> apply);
    void approveApply(Long id, Map<String, String> users);
    void recycle(Long recordId, Map<String, Object> recycleData);
}