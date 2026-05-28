package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.PurchaseRequest;
import com.example.hospital.mapper.PurchaseRequestMapper;
import com.example.hospital.service.PurchaseRequestService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestMapper purchaseRequestMapper;

    public PurchaseRequestServiceImpl(PurchaseRequestMapper purchaseRequestMapper) {
        this.purchaseRequestMapper = purchaseRequestMapper;
    }

    @Override
    public PurchaseRequest findById(Long id) {
        return purchaseRequestMapper.selectById(id);
    }

    @Override
    public IPage<PurchaseRequest> page(int page, int size, String keyword, Integer status) {
        Page<PurchaseRequest> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PurchaseRequest> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PurchaseRequest::getRequestNo, keyword);
        }
        if (status != null) {
            wrapper.eq(PurchaseRequest::getStatus, status);
        }
        wrapper.orderByDesc(PurchaseRequest::getCreateTime);
        return purchaseRequestMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void save(PurchaseRequest request, Map<String, Object> details) {
        request.setCreateTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        purchaseRequestMapper.insert(request);
        // 保存明细需要 purchase_request_detail 表，暂略
    }

    @Override
    public void audit(Long id, Integer status, String comment) {
        PurchaseRequest request = purchaseRequestMapper.selectById(id);
        if (request != null) {
            request.setStatus(status);
            request.setAuditComment(comment);
            request.setAuditTime(LocalDateTime.now());
            purchaseRequestMapper.updateById(request);
        }
    }

    @Override
    public void delete(Long id) {
        purchaseRequestMapper.deleteById(id);
    }
}