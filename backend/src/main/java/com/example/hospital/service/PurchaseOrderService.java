package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.PurchaseOrder;
import com.example.hospital.entity.PurchaseOrderDetail;

import java.util.List;
import java.util.Map;

public interface PurchaseOrderService {
    PurchaseOrder findById(Long id);
    PurchaseOrder findByIdWithNames(Long id);
    IPage<PurchaseOrder> page(int page, int size, String keyword, Integer status);
    void save(PurchaseOrder order, Map<String, Object> details);
    void update(PurchaseOrder order);
    void delete(Long id);
    void receiveOrder(Long id, Map<String, Object> receiveData);
    void createFromRequest(Long requestId);
    List<PurchaseOrderDetail> getDetailsByOrderId(Long orderId);
}