package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.Prescription;
import com.example.hospital.entity.PrescriptionDetail;
import com.example.hospital.mapper.PrescriptionDetailMapper;
import com.example.hospital.mapper.PrescriptionMapper;
import com.example.hospital.service.PrescriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionDetailMapper prescriptionDetailMapper;

    public PrescriptionServiceImpl(PrescriptionMapper prescriptionMapper, PrescriptionDetailMapper prescriptionDetailMapper) {
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionDetailMapper = prescriptionDetailMapper;
    }

    @Override
    public Prescription findById(Long id) {
        Prescription prescription = prescriptionMapper.selectById(id);
        if (prescription != null) {
            // 查询处方明细
            List<PrescriptionDetail> details = prescriptionDetailMapper.selectByPrescriptionId(id);
            prescription.setDetails(details);
        }
        return prescription;
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
        IPage<Prescription> result = prescriptionMapper.selectPage(pageParam, wrapper);
        
        // 为每个处方加载明细
        for (Prescription prescription : result.getRecords()) {
            List<PrescriptionDetail> details = prescriptionDetailMapper.selectByPrescriptionId(prescription.getId());
            prescription.setDetails(details);
        }
        
        return result;
    }

    @Override
    @Transactional
    public void save(Prescription prescription, Map<String, Object> payload) {
        // 生成处方号
        String prescriptionNo = "PR" + System.currentTimeMillis();
        prescription.setPrescriptionNo(prescriptionNo);
        prescription.setCreateTime(LocalDateTime.now());
        prescription.setUpdateTime(LocalDateTime.now());
        prescriptionMapper.insert(prescription);
        
        // 保存处方明细
        List<Map<String, Object>> details = (List<Map<String, Object>>) payload.get("details");
        if (details != null && !details.isEmpty()) {
            for (Map<String, Object> detail : details) {
                PrescriptionDetail pd = new PrescriptionDetail();
                pd.setPrescriptionId(prescription.getId());
                pd.setDrugId(((Number) detail.get("drugId")).longValue());
                pd.setDrugName((String) detail.get("drugName"));
                pd.setSpec((String) detail.get("spec"));
                pd.setQuantity(((Number) detail.get("quantity")).intValue());
                pd.setUsage((String) detail.get("usage"));
                pd.setPrice(BigDecimal.valueOf(((Number) detail.get("price")).doubleValue()));
                pd.setAmount(BigDecimal.valueOf(((Number) detail.get("amount")).doubleValue()));
                prescriptionDetailMapper.insert(pd);
            }
        }
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
        if (prescription != null) {
            if (prescription.getStatus() == 2) {
                prescription.setStatus(3);
            } else if (prescription.getStatus() == 3) {
                prescription.setStatus(4);
            }
            prescription.setUpdateTime(LocalDateTime.now());
            prescriptionMapper.updateById(prescription);
        }
    }

    @Override
    public void returnDrug(Long id) {
        Prescription prescription = prescriptionMapper.selectById(id);
        if (prescription != null && prescription.getStatus() == 4) {
            prescription.setStatus(5);
            prescription.setUpdateTime(LocalDateTime.now());
            prescriptionMapper.updateById(prescription);
        }
    }
}
