package com.example.hospital.service;

import com.example.hospital.entity.SpecialDrugApply;
import com.example.hospital.entity.SpecialDrugRecord;
import java.util.List;
import java.util.Map;

public interface SpecialDrugService {
    List<Map<String, Object>> listInventory(String keyword);
    List<SpecialDrugRecord> listRecords(String keyword);
    List<SpecialDrugApply> listApplies();
    void saveApply(Map<String, Object> apply);
    void approveApply(Long id, Map<String, String> users);
    void recycle(Long recordId, Map<String, Object> recycleData);
}
