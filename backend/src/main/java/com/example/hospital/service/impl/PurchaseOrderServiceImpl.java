package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.*;
import com.example.hospital.mapper.*;
import com.example.hospital.service.PurchaseOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private final PurchaseRequestMapper purchaseRequestMapper;
    private final PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderMapper purchaseOrderMapper,
                                    PurchaseOrderDetailMapper purchaseOrderDetailMapper,
                                    PurchaseRequestMapper purchaseRequestMapper,
                                    PurchaseRequestDetailMapper purchaseRequestDetailMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderDetailMapper = purchaseOrderDetailMapper;
        this.purchaseRequestMapper = purchaseRequestMapper;
        this.purchaseRequestDetailMapper = purchaseRequestDetailMapper;
    }

    @Override
    public PurchaseOrder findById(Long id) {
        return purchaseOrderMapper.selectById(id);
    }

    @Override
    public PurchaseOrder findByIdWithNames(Long id) {
        return purchaseOrderMapper.selectByIdWithNames(id);
    }

    @Override
    public IPage<PurchaseOrder> page(int page, int size, String keyword, Integer status) {
        Page<PurchaseOrder> pageParam = new Page<>(page, size);
        return purchaseOrderMapper.selectPageWithNames(pageParam, 
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null, 
                (status != null && status > 0) ? status : null);
    }

    @Override
    @Transactional
    public void save(PurchaseOrder order, Map<String, Object> details) {
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        purchaseOrderMapper.insert(order);
        // 插入明细等逻辑略（可从请求中解析）
    }

    @Override
    @Transactional
    public void update(PurchaseOrder order) {
        order.setUpdateTime(LocalDateTime.now());
        purchaseOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 先删除明细
        LambdaQueryWrapper<PurchaseOrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(PurchaseOrderDetail::getOrderId, id);
        purchaseOrderDetailMapper.delete(detailWrapper);
        // 再删除主表
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
            // 更新库存等逻辑可补充
        }
    }

    @Override
    @Transactional
    public void createFromRequest(Long requestId) {
        // 1. 查询采购申请
        PurchaseRequest request = purchaseRequestMapper.selectById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("采购申请不存在");
        }
        if (request.getStatus() != 2) {
            throw new IllegalArgumentException("只有已批准的申请才能生成订单");
        }

        // 2. 查询申请明细
        LambdaQueryWrapper<PurchaseRequestDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(PurchaseRequestDetail::getRequestId, requestId);
        List<PurchaseRequestDetail> requestDetails = purchaseRequestDetailMapper.selectList(detailWrapper);
        if (requestDetails.isEmpty()) {
            throw new IllegalArgumentException("申请明细为空，无法生成订单");
        }

        // 3. 生成订单号
        String orderNo = "PO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + (int)(Math.random() * 1000);

        // 4. 计算总金额
        BigDecimal totalAmount = requestDetails.stream()
                .map(d -> d.getAmount() == null ? BigDecimal.ZERO : d.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 创建订单主表
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(orderNo);
        order.setRequestId(requestId);
        order.setSupplierId(request.getSupplierId());
        order.setTotalAmount(totalAmount);
        order.setStatus(1); // 待发货
        order.setRemark(request.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        purchaseOrderMapper.insert(order);

        // 6. 创建订单明细
        for (PurchaseRequestDetail rd : requestDetails) {
            PurchaseOrderDetail od = new PurchaseOrderDetail();
            od.setOrderId(order.getId());
            od.setDrugId(rd.getDrugId());
            od.setDrugName(rd.getDrugName());
            od.setSpec(rd.getSpec());
            od.setQuantity(rd.getQuantity());
            od.setUnitPrice(rd.getUnitPrice());
            od.setAmount(rd.getAmount());
            od.setReceivedQuantity(0);
            purchaseOrderDetailMapper.insert(od);
        }

        // 7. 更新采购申请状态（可选）
        request.setStatus(4); // 自定义：已生成订单
        purchaseRequestMapper.updateById(request);
    }

    @Override
    public List<PurchaseOrderDetail> getDetailsByOrderId(Long orderId) {
        LambdaQueryWrapper<PurchaseOrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrderDetail::getOrderId, orderId);
        return purchaseOrderDetailMapper.selectList(wrapper);
    }
}