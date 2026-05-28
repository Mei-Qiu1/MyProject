package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.DoctorDispensing;
import com.example.hospital.mapper.DoctorDispensingMapper;
import com.example.hospital.service.DoctorDispensingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoctorDispensingServiceImpl implements DoctorDispensingService {

    private final DoctorDispensingMapper doctorDispensingMapper;

    public DoctorDispensingServiceImpl(DoctorDispensingMapper doctorDispensingMapper) {
        this.doctorDispensingMapper = doctorDispensingMapper;
    }

    @Override
    public IPage<DoctorDispensing> page(int page, int size, String keyword) {
        Page<DoctorDispensing> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DoctorDispensing> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(DoctorDispensing::getPrescriptionNo, keyword)
                    .or().like(DoctorDispensing::getPatientName, keyword);
        }
        wrapper.orderByDesc(DoctorDispensing::getCreateTime);
        return doctorDispensingMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void save(DoctorDispensing record) {
        record.setCreateTime(LocalDateTime.now());
        doctorDispensingMapper.insert(record);
    }

    @Override
    public void update(DoctorDispensing record) {
        doctorDispensingMapper.updateById(record);
    }

    @Override
    public void delete(Long id) {
        doctorDispensingMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchSave(List<DoctorDispensing> records) {
        for (DoctorDispensing record : records) {
            record.setCreateTime(LocalDateTime.now());
            doctorDispensingMapper.insert(record);
        }
    }
}