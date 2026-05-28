package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.PurchaseRequest;
import com.example.hospital.service.PurchaseRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/purchase/requests")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status) {
        IPage<PurchaseRequest> requestPage = purchaseRequestService.page(page, size, keyword, status);
        return Result.success(PageResult.of(requestPage.getRecords(), requestPage.getTotal(),
                (int) requestPage.getCurrent(), (int) requestPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        PurchaseRequest request = purchaseRequestService.findById(id);
        return request != null ? Result.success(request) : Result.fail("申请不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        PurchaseRequest request = new PurchaseRequest();
        request.setRequestNo((String) payload.get("requestNo"));
        request.setPlanId(payload.get("planId") != null ? Long.valueOf(payload.get("planId").toString()) : null);
        request.setSupplierId(payload.get("supplierId") != null ? Long.valueOf(payload.get("supplierId").toString()) : null);
        request.setRemark((String) payload.get("remark"));
        request.setStatus(1); // 待审批
        purchaseRequestService.save(request, payload);
        return Result.success("提交成功");
    }

    @PutMapping("/{id}/audit")
    public Result<?> audit(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Integer status = (Integer) payload.get("status");
        String comment = (String) payload.get("comment");
        purchaseRequestService.audit(id, status, comment);
        return Result.success("审批完成");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        purchaseRequestService.delete(id);
        return Result.success("删除成功");
    }
}