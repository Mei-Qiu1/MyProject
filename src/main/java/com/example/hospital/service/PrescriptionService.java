package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.Prescription;
import java.util.Map;

public interface PrescriptionService {
    Prescription findById(Long id);
    IPage<Prescription> page(int page, int size, String keyword, Integer status);
    void save(Prescription prescription, Map<String, Object> details);
    void update(Prescription prescription);
    void delete(Long id);
    void audit(Long id, Integer status, String comment);
    void dispense(Long id);
}