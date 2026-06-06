package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.MedicalOrder;
import com.example.hospital.entity.MedicalOrderDetail;
import com.example.hospital.mapper.MedicalOrderDetailMapper;
import com.example.hospital.mapper.MedicalOrderMapper;
import com.example.hospital.service.MedicalOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MedicalOrderServiceImpl implements MedicalOrderService {
    
    private final MedicalOrderMapper medicalOrderMapper;
    private final MedicalOrderDetailMapper medicalOrderDetailMapper;
    
    public MedicalOrderServiceImpl(MedicalOrderMapper medicalOrderMapper, MedicalOrderDetailMapper medicalOrderDetailMapper) {
        this.medicalOrderMapper = medicalOrderMapper;
        this.medicalOrderDetailMapper = medicalOrderDetailMapper;
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
        // 从数据库查询医嘱
        MedicalOrder order = medicalOrderMapper.selectById(id);
        if (order != null) {
            // 查询医嘱明细
            LambdaQueryWrapper<MedicalOrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MedicalOrderDetail::getOrderId, id);
            List<MedicalOrderDetail> details = medicalOrderDetailMapper.selectList(wrapper);
            order.setDetails(details);
        }
        return order;
    }

    @Override
    @Transactional
    public void save(Map<String, Object> payload) {
        MedicalOrder order = new MedicalOrder();
        // 自动生成医嘱号
        String orderNo = "MO" + System.currentTimeMillis();
        order.setOrderNo(orderNo);
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
        
        // 保存医嘱明细
        List<Map<String, Object>> details = (List<Map<String, Object>>) payload.get("details");
        if (details != null && !details.isEmpty()) {
            for (Map<String, Object> detail : details) {
                MedicalOrderDetail md = new MedicalOrderDetail();
                md.setOrderId(order.getId());
                md.setDrugId(((Number) detail.get("drugId")).longValue());
                md.setDrugName((String) detail.get("drugName"));
                md.setSpec((String) detail.get("spec"));
                md.setQuantity(((Number) detail.get("quantity")).intValue());
                md.setFrequency((String) detail.get("frequency"));
                md.setDuration((String) detail.get("duration"));
                md.setCreateTime(LocalDateTime.now());
                medicalOrderDetailMapper.insert(md);
            }
        }
    }

    @Override
    public void execute(Long id) {
        MedicalOrder order = medicalOrderMapper.selectById(id);
        if (order != null) {
            order.setStatus(2); // 2: 执行中
            medicalOrderMapper.updateById(order);
        }
    }

    @Override
    public void createDelivery(Long id) {
        // 模拟生成配送单，不实际操作数据库
    }

    @Override
    public void delete(Long id) {
        // 模拟删除医嘱，不实际操作数据库
    }
}
