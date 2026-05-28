package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.MedicalOrder;
import com.example.hospital.mapper.MedicalOrderMapper;
import com.example.hospital.service.MedicalOrderService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MedicalOrderServiceImpl implements MedicalOrderService {
    private final MedicalOrderMapper medicalOrderMapper;
    public MedicalOrderServiceImpl(MedicalOrderMapper medicalOrderMapper) {
        this.medicalOrderMapper = medicalOrderMapper;
    }

    @Override
    public IPage<MedicalOrder> page(int page, int size, String keyword, Integer status) {
        Page<MedicalOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(MedicalOrder::getOrderNo, keyword).or().like(MedicalOrder::getPatientName, keyword);
        }
        if (status != null) {
            wrapper.eq(MedicalOrder::getStatus, status);
        }
        wrapper.orderByDesc(MedicalOrder::getCreateTime);
        return medicalOrderMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public MedicalOrder getById(Long id) {
        return medicalOrderMapper.selectById(id);
    }

    @Override
    public void save(Map<String, Object> payload) {
        MedicalOrder order = new MedicalOrder();
        order.setOrderNo((String) payload.get("orderNo"));
        order.setPatientId((String) payload.get("patientId"));
        order.setPatientName((String) payload.get("patientName"));
        order.setDepartment((String) payload.get("department"));
        order.setBedNo((String) payload.get("bedNo"));
        order.setDoctorName((String) payload.get("doctorName"));
        order.setType(Integer.valueOf(payload.get("type").toString()));
        order.setStatus(1);
        order.setOrderTime(LocalDateTime.now());
        order.setCreateTime(LocalDateTime.now());
        medicalOrderMapper.insert(order);
    }

    @Override
    public void execute(Long id) {
        MedicalOrder order = medicalOrderMapper.selectById(id);
        if (order != null && order.getStatus() == 1) {
            order.setStatus(2);
            order.setExecuteTime(LocalDateTime.now());
            medicalOrderMapper.updateById(order);
        }
    }

    @Override
    public void createDelivery(Long id) {
        // 暂不实现，可后续扩展
    }

    @Override
    public void delete(Long id) {
        medicalOrderMapper.deleteById(id);
    }
}