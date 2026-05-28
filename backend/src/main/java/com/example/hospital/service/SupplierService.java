package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.Supplier;

public interface SupplierService {
    Supplier findById(Long id);
    IPage<Supplier> page(int page, int size, String keyword);
    void save(Supplier supplier);
    void update(Supplier supplier);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}