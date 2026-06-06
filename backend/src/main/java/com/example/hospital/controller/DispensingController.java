package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.mapper.DispensingRecordMapper;
import com.example.hospital.service.PrescriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pharmacy/dispensing")
public class DispensingController {

    private final PrescriptionService prescriptionService;
    private final DispensingRecordMapper dispensingRecordMapper;

    public DispensingController(PrescriptionService prescriptionService, DispensingRecordMapper dispensingRecordMapper) {
        this.prescriptionService = prescriptionService;
        this.dispensingRecordMapper = dispensingRecordMapper;
    }

    @GetMapping("/recent")
    public Result<?> recent() {
        List<Map<String, Object>> records = dispensingRecordMapper.selectRecentRecords();
        return Result.success(records);
    }

    @PostMapping("/{id}/dispense")
    public Result<?> dispense(@PathVariable Long id) {
        prescriptionService.dispense(id);
        return Result.success("发药完成");
    }
}