package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.service.PrescriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pharmacy/dispensing")
public class DispensingController {

    private final PrescriptionService prescriptionService;

    public DispensingController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/recent")
    public Result<?> recent() {
        // 返回最近调配记录（模拟数据）
        return Result.success(List.of());
    }

    @PostMapping("/{id}/dispense")
    public Result<?> dispense(@PathVariable Long id) {
        prescriptionService.dispense(id);
        return Result.success("发药完成");
    }
}