package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.PurchasePlan;
import com.example.hospital.entity.PurchaseRequest;
import com.example.hospital.entity.PurchaseRequestDetail;
import com.example.hospital.entity.Supplier;
import com.example.hospital.mapper.PurchasePlanMapper;
import com.example.hospital.mapper.SupplierMapper;
import com.example.hospital.service.PurchaseRequestService;
import com.example.hospital.mapper.PurchasePlanDetailMapper;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/purchase/requests")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;
    private final PurchasePlanDetailMapper purchasePlanDetailMapper;

    private final SupplierMapper supplierMapper;
    private final PurchasePlanMapper purchasePlanMapper;

    public PurchaseRequestController(PurchaseRequestService purchaseRequestService,
                                     PurchasePlanDetailMapper purchasePlanDetailMapper,
                                     SupplierMapper supplierMapper,
                                     PurchasePlanMapper purchasePlanMapper) {
        this.purchaseRequestService = purchaseRequestService;
        this.purchasePlanDetailMapper = purchasePlanDetailMapper;
        this.supplierMapper = supplierMapper;
        this.purchasePlanMapper = purchasePlanMapper;
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
        if (request == null) {
            return Result.fail("申请不存在");
        }
        List<PurchaseRequestDetail> details = purchaseRequestService.getDetailsByRequestId(id);

        // 查询供应商名称
        String supplierName = null;
        if (request.getSupplierId() != null) {
            Supplier supplier = supplierMapper.selectById(request.getSupplierId());
            supplierName = supplier != null ? supplier.getSupplierName() : null;
        }

        // 查询计划名称
        String planName = null;
        if (request.getPlanId() != null) {
            PurchasePlan plan = purchasePlanMapper.selectById(request.getPlanId());
            planName = plan != null ? plan.getPlanName() : null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", request.getId());
        result.put("requestNo", request.getRequestNo());
        result.put("planId", request.getPlanId());
        result.put("planName", planName);              // 新增
        result.put("supplierId", request.getSupplierId());
        result.put("supplierName", supplierName);      // 新增
        result.put("status", request.getStatus());
        result.put("auditComment", request.getAuditComment());
        result.put("createTime", request.getCreateTime());
        result.put("details", details);
        return Result.success(result);
    }

    @GetMapping("/plan-details/{planId}")
    public Result<?> getPlanDetails(@PathVariable Long planId) {
        List<Map<String, Object>> details = purchasePlanDetailMapper.selectPlanDetailsWithDrug(planId);
        return Result.success(details);
    }

    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        try {
            String requestNo = (String) payload.get("requestNo");
            Long planId = payload.get("planId") != null ? Long.valueOf(payload.get("planId").toString()) : null;
            Long supplierId = payload.get("supplierId") != null ? Long.valueOf(payload.get("supplierId").toString()) : null;
            String remark = (String) payload.get("remark");
            List<Map<String, Object>> detailList = (List<Map<String, Object>>) payload.get("details");

            if (detailList == null || detailList.isEmpty()) {
                return Result.fail("请至少添加一种药品");
            }
            if (supplierId == null) {
                return Result.fail("供应商不能为空");
            }

            PurchaseRequest request = new PurchaseRequest();
            request.setRequestNo(requestNo);
            request.setPlanId(planId);
            request.setSupplierId(supplierId);
            request.setRemark(remark);
            request.setStatus(1); // 待审批

            List<PurchaseRequestDetail> details = new ArrayList<>();
            for (Map<String, Object> d : detailList) {
                PurchaseRequestDetail detail = new PurchaseRequestDetail();
                detail.setDrugId(Long.valueOf(d.get("drugId").toString()));
                detail.setDrugName((String) d.get("drugName"));
                detail.setSpec((String) d.get("spec"));
                detail.setQuantity(Integer.valueOf(d.get("quantity").toString()));
                detail.setUnit((String) d.get("unit"));
                detail.setUnitPrice(new BigDecimal(d.get("unitPrice").toString()));
                detail.setAmount(new BigDecimal(d.get("amount").toString()));
                details.add(detail);
            }

            purchaseRequestService.save(request, details);
            return Result.success("申请提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("提交失败：" + e.getMessage());
        }
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