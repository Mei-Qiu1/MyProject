package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.PurchaseRequest;
import com.example.hospital.entity.PurchaseRequestDetail;
import com.example.hospital.mapper.PurchaseRequestDetailMapper;
import com.example.hospital.mapper.PurchaseRequestMapper;
import com.example.hospital.service.PurchaseRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestMapper purchaseRequestMapper;
    private final PurchaseRequestDetailMapper detailMapper;

    public PurchaseRequestServiceImpl(PurchaseRequestMapper purchaseRequestMapper,
                                      PurchaseRequestDetailMapper detailMapper) {
        this.purchaseRequestMapper = purchaseRequestMapper;
        this.detailMapper = detailMapper;
    }

    @Override
    public PurchaseRequest findById(Long id) {
        return purchaseRequestMapper.selectById(id);
    }

    // 修改后（正确）
    @Override
    public IPage<PurchaseRequest> page(int page, int size, String keyword, Integer status) {
        Page<PurchaseRequest> pageParam = new Page<>(page, size);
        // 直接调用新方法，传入 keyword 和 status
        return purchaseRequestMapper.selectPageWithConditions(pageParam, keyword, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(PurchaseRequest request, List<PurchaseRequestDetail> details) {
        request.setCreateTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        purchaseRequestMapper.insert(request);
        if (details != null && !details.isEmpty()) {
            for (PurchaseRequestDetail detail : details) {
                detail.setRequestId(request.getId());
                detailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional
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
    @Transactional
    public void delete(Long id) {
        LambdaQueryWrapper<PurchaseRequestDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(PurchaseRequestDetail::getRequestId, id);
        detailMapper.delete(detailWrapper);
        purchaseRequestMapper.deleteById(id);
    }

    @Override
    public List<PurchaseRequestDetail> getDetailsByRequestId(Long requestId) {
        LambdaQueryWrapper<PurchaseRequestDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseRequestDetail::getRequestId, requestId);
        return detailMapper.selectList(wrapper);
    }
}