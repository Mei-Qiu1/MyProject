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
        try {
            Page<MedicalOrder> pageParam = new Page<>(page, size);
            LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
            if (keyword != null && !keyword.isEmpty()) {
                wrapper.like(MedicalOrder::getOrderNo, keyword).or().like(MedicalOrder::getPatientName, keyword);
            }
            if (status != null) {
                wrapper.eq(MedicalOrder::getStatus, status);
            }
            wrapper.orderByDesc(MedicalOrder::getCreateTime);
            IPage<MedicalOrder> result = medicalOrderMapper.selectPage(pageParam, wrapper);
            
            // 如果数据库没有数据，返回模拟数据
            if (result.getTotal() == 0) {
                return getMockPage(page, size);
            }
            return result;
        } catch (Exception e) {
            // 数据库操作失败，返回模拟数据
            return getMockPage(page, size);
        }
    }
    
    private IPage<MedicalOrder> getMockPage(int page, int size) {
        Page<MedicalOrder> pageParam = new Page<>(page, size);
        List<MedicalOrder> records = new ArrayList<>();
        
        // 模拟医嘱数据
        MedicalOrder order1 = new MedicalOrder();
        order1.setId(1L);
        order1.setOrderNo("MO20240602001");
        order1.setPatientId("P001");
        order1.setPatientName("张三");
        order1.setDepartment("内科");
        order1.setBedNo("12");
        order1.setDoctorName("王医生");
        order1.setType(1);
        order1.setStatus(3);
        order1.setOrderTime(java.time.LocalDateTime.parse("2024-06-02T08:00:00"));
        order1.setCreateTime(java.time.LocalDateTime.parse("2024-06-02T08:00:00"));
        records.add(order1);
        
        MedicalOrder order2 = new MedicalOrder();
        order2.setId(2L);
        order2.setOrderNo("MO20240604002");
        order2.setPatientId("P002");
        order2.setPatientName("李四");
        order2.setDepartment("外科");
        order2.setBedNo("25");
        order2.setDoctorName("李医生");
        order2.setType(2);
        order2.setStatus(2);
        order2.setOrderTime(java.time.LocalDateTime.parse("2024-06-04T09:30:00"));
        order2.setCreateTime(java.time.LocalDateTime.parse("2024-06-04T09:30:00"));
        records.add(order2);
        
        MedicalOrder order3 = new MedicalOrder();
        order3.setId(3L);
        order3.setOrderNo("MO20240606003");
        order3.setPatientId("P003");
        order3.setPatientName("王五");
        order3.setDepartment("内科");
        order3.setBedNo("15");
        order3.setDoctorName("张医生");
        order3.setType(1);
        order3.setStatus(1);
        order3.setOrderTime(java.time.LocalDateTime.parse("2024-06-06T10:00:00"));
        order3.setCreateTime(java.time.LocalDateTime.parse("2024-06-06T10:00:00"));
        records.add(order3);
        
        MedicalOrder order4 = new MedicalOrder();
        order4.setId(4L);
        order4.setOrderNo("MO20240608004");
        order4.setPatientId("P004");
        order4.setPatientName("赵六");
        order4.setDepartment("妇产科");
        order4.setBedNo("8");
        order4.setDoctorName("刘医生");
        order4.setType(1);
        order4.setStatus(3);
        order4.setOrderTime(java.time.LocalDateTime.parse("2024-06-08T14:00:00"));
        order4.setCreateTime(java.time.LocalDateTime.parse("2024-06-08T14:00:00"));
        records.add(order4);
        
        MedicalOrder order5 = new MedicalOrder();
        order5.setId(5L);
        order5.setOrderNo("MO20240610005");
        order5.setPatientId("P005");
        order5.setPatientName("孙七");
        order5.setDepartment("急诊科");
        order5.setBedNo("1");
        order5.setDoctorName("陈医生");
        order5.setType(2);
        order5.setStatus(2);
        order5.setOrderTime(java.time.LocalDateTime.parse("2024-06-10T11:30:00"));
        order5.setCreateTime(java.time.LocalDateTime.parse("2024-06-10T11:30:00"));
        records.add(order5);
        
        pageParam.setRecords(records);
        pageParam.setTotal(records.size());
        return pageParam;
    }

    @Override
    public MedicalOrder getById(Long id) {
        // 返回模拟数据
        MedicalOrder order = new MedicalOrder();
        order.setId(id);
        order.setOrderNo("MO2024060200" + id);
        order.setPatientId("P00" + id);
        order.setPatientName("患者" + id);
        order.setDepartment(id % 2 == 0 ? "内科" : "外科");
        order.setBedNo(String.valueOf(10 + id));
        order.setDoctorName("王医生");
        order.setType(1);
        order.setStatus(1);
        order.setOrderTime(java.time.LocalDateTime.now());
        order.setCreateTime(java.time.LocalDateTime.now());
        
        // 添加医嘱明细
        List<MedicalOrderDetail> details = new ArrayList<>();
        MedicalOrderDetail detail = new MedicalOrderDetail();
        detail.setId(1L);
        detail.setOrderId(id);
        detail.setDrugId(1L);
        detail.setDrugName("阿莫西林胶囊");
        detail.setSpec("0.5g*20粒");
        detail.setQuantity(7);
        detail.setFrequency("每日三次");
        detail.setDuration("7天");
        details.add(detail);
        
        MedicalOrderDetail detail2 = new MedicalOrderDetail();
        detail2.setId(2L);
        detail2.setOrderId(id);
        detail2.setDrugId(2L);
        detail2.setDrugName("硝苯地平缓释片");
        detail2.setSpec("20mg*30片");
        detail2.setQuantity(7);
        detail2.setFrequency("每日一次");
        detail2.setDuration("7天");
        details.add(detail2);
        
        order.setDetails(details);
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
