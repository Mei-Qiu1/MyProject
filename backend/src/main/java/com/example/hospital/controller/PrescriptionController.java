package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Prescription;
import com.example.hospital.service.PrescriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pharmacy/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Integer status) {
        IPage<Prescription> prescriptionPage = prescriptionService.page(page, size, keyword, status);
        return Result.success(PageResult.of(prescriptionPage.getRecords(), prescriptionPage.getTotal(),
                (int) prescriptionPage.getCurrent(), (int) prescriptionPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Prescription prescription = prescriptionService.findById(id);
        return prescription != null ? Result.success(prescription) : Result.fail("处方不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        Prescription prescription = new Prescription();
        prescription.setPatientName((String) payload.get("patientName"));
        prescription.setPatientId((String) payload.get("patientId"));
        prescription.setPatientAge((Integer) payload.get("patientAge"));
        prescription.setPatientSex((String) payload.get("patientSex"));
        prescription.setDepartment((String) payload.get("department"));
        prescription.setDoctorName((String) payload.get("doctorName"));
        prescription.setType((Integer) payload.get("type"));
        prescription.setStatus(1);
        prescriptionService.save(prescription, payload);
        return Result.success("处方创建成功");
    }

    @PutMapping("/{id}/audit")
    public Result<?> audit(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Integer status = (Integer) payload.get("status");
        String comment = (String) payload.get("comment");
        prescriptionService.audit(id, status, comment);
        return Result.success("审核完成");
    }

    @PostMapping("/{id}/dispense")
    public Result<?> dispense(@PathVariable Long id) {
        prescriptionService.dispense(id);
        return Result.success("调配完成");
    }

    @GetMapping("/{id}/audit-check")
    public Result<?> auditCheck(@PathVariable Long id) {
        Prescription prescription = prescriptionService.findById(id);
        if (prescription == null) {
            return Result.fail("处方不存在");
        }
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("warnings", new java.util.ArrayList<>());
        return Result.success(result);
    }

    @PostMapping("/{id}/return")
    public Result<?> returnDrug(@PathVariable Long id) {
        prescriptionService.returnDrug(id);
        return Result.success("退药成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        prescriptionService.delete(id);
        return Result.success("删除成功");
    }
}