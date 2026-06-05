package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.PurchaseRequest;
import com.example.hospital.entity.PurchaseRequestDetail;

import java.util.List;

public interface PurchaseRequestService {
    PurchaseRequest findById(Long id);
    IPage<PurchaseRequest> page(int page, int size, String keyword, Integer status);
    void save(PurchaseRequest request, List<PurchaseRequestDetail> details);
    void audit(Long id, Integer status, String comment);
    void delete(Long id);
    List<PurchaseRequestDetail> getDetailsByRequestId(Long requestId);
}