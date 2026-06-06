package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.Supplier;
import com.example.hospital.mapper.SupplierMapper;
import com.example.hospital.service.SupplierService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;

    public SupplierServiceImpl(SupplierMapper supplierMapper) {
        this.supplierMapper = supplierMapper;
    }

    @Override
    public Supplier findById(Long id) {
        return supplierMapper.selectById(id);
    }

    @Override
    public IPage<Supplier> page(int page, int size, String keyword) {
        Page<Supplier> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Supplier::getSupplierName, keyword).or().like(Supplier::getSupplierCode, keyword);
        }
        return supplierMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void save(Supplier supplier) {
        supplier.setCreateTime(LocalDateTime.now());
        supplier.setUpdateTime(LocalDateTime.now());
        // 确保状态和合作状态保持一致
        if (supplier.getStatus() == null) {
            supplier.setStatus(1);
        }
        if (supplier.getCooperationStatus() == null || supplier.getCooperationStatus() != supplier.getStatus()) {
            supplier.setCooperationStatus(supplier.getStatus());
        }
        supplierMapper.insert(supplier);
    }

    @Override
    public void update(Supplier supplier) {
        supplier.setUpdateTime(LocalDateTime.now());
        // 确保状态和合作状态保持一致
        if (supplier.getStatus() != null && supplier.getCooperationStatus() != supplier.getStatus()) {
            supplier.setCooperationStatus(supplier.getStatus());
        }
        supplierMapper.updateById(supplier);
    }

    @Override
    public void delete(Long id) {
        supplierMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier != null) {
            supplier.setStatus(status);
            // 状态和合作状态保持一致：开启→合作中，关闭→暂停
            supplier.setCooperationStatus(status);
            supplier.setUpdateTime(LocalDateTime.now());
            supplierMapper.updateById(supplier);
        }
    }
}