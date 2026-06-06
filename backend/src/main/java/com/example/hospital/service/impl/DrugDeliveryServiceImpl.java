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
        LambdaQueryWrapper<DrugDelivery> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(DrugDelivery::getDeliveryNo, keyword)
                    .or().like(DrugDelivery::getPatientName, keyword));
        }
        if (status != null) {
            wrapper.eq(DrugDelivery::getStatus, status);
        }
        if (department != null && !department.isEmpty()) {
            wrapper.eq(DrugDelivery::getDepartment, department);
        }
        
        wrapper.orderByDesc(DrugDelivery::getCreateTime);
        return drugDeliveryMapper.selectPage(pageParam, wrapper);
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
        // 从医嘱创建配送单
        MedicalOrder order = medicalOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("医嘱不存在");
        }
        
        // 创建配送单
        DrugDelivery delivery = new DrugDelivery();
        delivery.setDeliveryNo("DL" + System.currentTimeMillis());
        delivery.setOrderId(orderId);
        delivery.setPatientId(order.getPatientId());
        delivery.setPatientName(order.getPatientName());
        delivery.setDepartment(order.getDepartment());
        delivery.setBedNo(order.getBedNo());
        delivery.setStatus(1); // 待配送
        delivery.setCreateTime(LocalDateTime.now());
        delivery.setUpdateTime(LocalDateTime.now());
        drugDeliveryMapper.insert(delivery);
        
        // 从医嘱明细创建配送明细
        LambdaQueryWrapper<MedicalOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrderDetail::getOrderId, orderId);
        List<MedicalOrderDetail> orderDetails = medicalOrderDetailMapper.selectList(wrapper);
        
        for (MedicalOrderDetail detail : orderDetails) {
            DeliveryDetail deliveryDetail = new DeliveryDetail();
            deliveryDetail.setDeliveryId(delivery.getId());
            deliveryDetail.setDrugId(detail.getDrugId());
            deliveryDetail.setDrugName(detail.getDrugName());
            deliveryDetail.setSpec(detail.getSpec());
            deliveryDetail.setQuantity(detail.getQuantity());
            deliveryDetailMapper.insert(deliveryDetail);
        }
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