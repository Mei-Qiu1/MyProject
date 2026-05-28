package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.MedicalOrder;
import java.util.Map;

public interface MedicalOrderService {
    IPage<MedicalOrder> page(int page, int size, String keyword, Integer status);
    MedicalOrder getById(Long id);
    void save(Map<String, Object> payload);
    void execute(Long id);
    void createDelivery(Long id);
    void delete(Long id);
}