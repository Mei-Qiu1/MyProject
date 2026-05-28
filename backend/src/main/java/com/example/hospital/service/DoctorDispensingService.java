package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.DoctorDispensing;
import java.util.List;

public interface DoctorDispensingService {
    IPage<DoctorDispensing> page(int page, int size, String keyword);
    void save(DoctorDispensing record);
    void update(DoctorDispensing record);
    void delete(Long id);
    void batchSave(List<DoctorDispensing> records);
}