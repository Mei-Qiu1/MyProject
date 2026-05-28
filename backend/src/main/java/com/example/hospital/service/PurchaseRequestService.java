package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.PurchaseRequest;
import java.util.Map;

public interface PurchaseRequestService {
    PurchaseRequest findById(Long id);
    IPage<PurchaseRequest> page(int page, int size, String keyword, Integer status);
    void save(PurchaseRequest request, Map<String, Object> details);
    void audit(Long id, Integer status, String comment);
    void delete(Long id);
}