package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.Prescription;
import com.example.hospital.mapper.PrescriptionMapper;
import com.example.hospital.service.PrescriptionService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionServiceImpl(PrescriptionMapper prescriptionMapper) {
        this.prescriptionMapper = prescriptionMapper;
    }

    @Override
    public Prescription findById(Long id) {
        return prescriptionMapper.selectById(id);
    }

    @Override
    public IPage<Prescription> page(int page, int size, String keyword, Integer status) {
        Page<Prescription> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Prescription> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Prescription::getPrescriptionNo, keyword).or().like(Prescription::getPatientName, keyword);
        }
        if (status != null) {
            wrapper.eq(Prescription::getStatus, status);
        }
        wrapper.orderByDesc(Prescription::getCreateTime);
        return prescriptionMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void save(Prescription prescription, Map<String, Object> details) {
        prescription.setCreateTime(LocalDateTime.now());
        prescription.setUpdateTime(LocalDateTime.now());
        prescriptionMapper.insert(prescription);
        // 保存处方明细，需 prescription_detail 表
    }

    @Override
    public void update(Prescription prescription) {
        prescription.setUpdateTime(LocalDateTime.now());
        prescriptionMapper.updateById(prescription);
    }

    @Override
    public void delete(Long id) {
        prescriptionMapper.deleteById(id);
    }

    @Override
    public void audit(Long id, Integer status, String comment) {
        Prescription prescription = prescriptionMapper.selectById(id);
        if (prescription != null) {
            prescription.setStatus(status);
            prescription.setUpdateTime(LocalDateTime.now());
            prescriptionMapper.updateById(prescription);
        }
    }

    @Override
    public void dispense(Long id) {
        Prescription prescription = prescriptionMapper.selectById(id);
        if (prescription != null && prescription.getStatus() == 2) {
            prescription.setStatus(3); // 已调配
            prescription.setUpdateTime(LocalDateTime.now());
            prescriptionMapper.updateById(prescription);
        }
    }
}