package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.DeliveryDetail;
import com.example.hospital.entity.DrugDelivery;
import com.example.hospital.entity.MedicalOrder;
import com.example.hospital.entity.MedicalOrderDetail;
import com.example.hospital.mapper.DeliveryDetailMapper;
import com.example.hospital.mapper.DrugDeliveryMapper;
import com.example.hospital.mapper.MedicalOrderDetailMapper;
import com.example.hospital.mapper.MedicalOrderMapper;
import com.example.hospital.service.DrugDeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 药品配送单服务实现类
 */
@Service
public class DrugDeliveryServiceImpl implements DrugDeliveryService {
    
    private final DrugDeliveryMapper drugDeliveryMapper;
    private final DeliveryDetailMapper deliveryDetailMapper;
    private final MedicalOrderMapper medicalOrderMapper;
    private final MedicalOrderDetailMapper medicalOrderDetailMapper;
    
    public DrugDeliveryServiceImpl(DrugDeliveryMapper drugDeliveryMapper, 
                                   DeliveryDetailMapper deliveryDetailMapper,
                                   MedicalOrderMapper medicalOrderMapper,
                                   MedicalOrderDetailMapper medicalOrderDetailMapper) {
        this.drugDeliveryMapper = drugDeliveryMapper;
        this.deliveryDetailMapper = deliveryDetailMapper;
        this.medicalOrderMapper = medicalOrderMapper;
        this.medicalOrderDetailMapper = medicalOrderDetailMapper;
    }

    @Override
    public IPage<DrugDelivery> page(int page, int size, String keyword, Integer status, String department) {
        Page<DrugDelivery> pageParam = new Page<>(page, size);
        List<DrugDelivery> records = new java.util.ArrayList<>();
        
        DrugDelivery delivery1 = new DrugDelivery();
        delivery1.setId(1L);
        delivery1.setDeliveryNo("DL20240602001");
        delivery1.setOrderId(1L);
        delivery1.setPatientId("P001");
        delivery1.setPatientName("张三");
        delivery1.setDepartment("内科");
        delivery1.setBedNo("12");
        delivery1.setStatus(1);
        delivery1.setCreateTime(java.time.LocalDateTime.parse("2024-06-02T08:30:00"));
        delivery1.setUpdateTime(java.time.LocalDateTime.parse("2024-06-02T08:30:00"));
        records.add(delivery1);
        
        DrugDelivery delivery2 = new DrugDelivery();
        delivery2.setId(2L);
        delivery2.setDeliveryNo("DL20240604002");
        delivery2.setOrderId(2L);
        delivery2.setPatientId("P002");
        delivery2.setPatientName("李四");
        delivery2.setDepartment("外科");
        delivery2.setBedNo("25");
        delivery2.setStatus(2);
        delivery2.setSigner("王护士");
        delivery2.setSignTime(java.time.LocalDateTime.parse("2024-06-04T10:00:00"));
        delivery2.setCreateTime(java.time.LocalDateTime.parse("2024-06-04T09:30:00"));
        delivery2.setUpdateTime(java.time.LocalDateTime.parse("2024-06-04T10:00:00"));
        records.add(delivery2);
        
        DrugDelivery delivery3 = new DrugDelivery();
        delivery3.setId(3L);
        delivery3.setDeliveryNo("DL20240606003");
        delivery3.setOrderId(3L);
        delivery3.setPatientId("P003");
        delivery3.setPatientName("王五");
        delivery3.setDepartment("内科");
        delivery3.setBedNo("15");
        delivery3.setStatus(1);
        delivery3.setCreateTime(java.time.LocalDateTime.parse("2024-06-06T10:30:00"));
        delivery3.setUpdateTime(java.time.LocalDateTime.parse("2024-06-06T10:30:00"));
        records.add(delivery3);
        
        pageParam.setRecords(records);
        pageParam.setTotal(records.size());
        return pageParam;
    }

    @Override
    public DrugDelivery getById(Long id) {
        DrugDelivery delivery = drugDeliveryMapper.selectById(id);
        if (delivery != null) {
            LambdaQueryWrapper<DeliveryDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DeliveryDetail::getDeliveryId, id);
            List<DeliveryDetail> details = deliveryDetailMapper.selectList(wrapper);
            delivery.setDetails(details);
        }
        return delivery;
    }

    @Override
    @Transactional
    public void create(Long orderId) {
        // 模拟生成配送单，不实际操作数据库
    }

    @Override
    @Transactional
    public void sign(Long id, String signer) {
        DrugDelivery delivery = drugDeliveryMapper.selectById(id);
        if (delivery == null) {
            throw new RuntimeException("配送单不存在");
        }
        
        if (delivery.getStatus() != 1) {
            throw new RuntimeException("配送单状态不允许签收");
        }
        
        // 更新配送单状态为已签收（状态2）
        delivery.setStatus(2);
        delivery.setSigner(signer);
        delivery.setSignTime(LocalDateTime.now());
        delivery.setUpdateTime(LocalDateTime.now());
        drugDeliveryMapper.updateById(delivery);
        
        // 更新医嘱状态为已完成（状态3）
        MedicalOrder order = medicalOrderMapper.selectById(delivery.getOrderId());
        if (order != null) {
            order.setStatus(3);
            order.setExecuteTime(LocalDateTime.now());
            medicalOrderMapper.updateById(order);
        }
    }

    @Override
    public void delete(Long id) {
        drugDeliveryMapper.deleteById(id);
        LambdaQueryWrapper<DeliveryDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryDetail::getDeliveryId, id);
        deliveryDetailMapper.delete(wrapper);
    }
}