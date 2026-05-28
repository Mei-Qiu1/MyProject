package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.PurchaseOrder;
import com.example.hospital.mapper.PurchaseOrderMapper;
import com.example.hospital.service.PurchaseOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderMapper purchaseOrderMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    @Override
    public PurchaseOrder findById(Long id) {
        return purchaseOrderMapper.selectById(id);
    }

    @Override
    public IPage<PurchaseOrder> page(int page, int size, String keyword, Integer status) {
        Page<PurchaseOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PurchaseOrder::getOrderNo, keyword);
        }
        if (status != null) {
            wrapper.eq(PurchaseOrder::getStatus, status);
        }
        wrapper.orderByDesc(PurchaseOrder::getCreateTime);
        return purchaseOrderMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void save(PurchaseOrder order, Map<String, Object> details) {
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        purchaseOrderMapper.insert(order);
        // 明细表 purchase_order_detail 的处理可暂略
    }

    @Override
    public void update(PurchaseOrder order) {
        order.setUpdateTime(LocalDateTime.now());
        purchaseOrderMapper.updateById(order);
    }

    @Override
    public void delete(Long id) {
        purchaseOrderMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void receiveOrder(Long id, Map<String, Object> receiveData) {
        PurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order != null) {
            order.setStatus(3); // 已验收
            order.setUpdateTime(LocalDateTime.now());
            purchaseOrderMapper.updateById(order);
            // 更新库存等业务逻辑可后续补充
        }
    }

    @Override
    public void createFromRequest(Long requestId) {
        // 从采购申请生成订单，实际需查询申请单并插入订单，这里简化
    }
}