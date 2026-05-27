package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.service.SpecialDrugService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/special/drugs")
public class SpecialDrugController {

    private final SpecialDrugService specialDrugService;

    public SpecialDrugController(SpecialDrugService specialDrugService) {
        this.specialDrugService = specialDrugService;
    }

    @GetMapping("/inventory")
    public Result<?> inventory(@RequestParam(required = false) String keyword) {
        return Result.success(specialDrugService.listInventory(keyword));
    }

    @GetMapping("/records")
    public Result<?> records(@RequestParam(required = false) String keyword) {
        return Result.success(specialDrugService.listRecords(keyword));
    }

    @GetMapping("/applies")
    public Result<?> applies() {
        return Result.success(specialDrugService.listApplies());
    }

    @PostMapping("/applies")
    public Result<?> createApply(@RequestBody Map<String, Object> apply) {
        specialDrugService.saveApply(apply);
        return Result.success("申请已提交");
    }

    @PutMapping("/applies/{id}/approve")
    public Result<?> approveApply(@PathVariable Long id, @RequestBody Map<String, String> users) {
        specialDrugService.approveApply(id, users);
        return Result.success("审批通过");
    }

    @PostMapping("/records/{id}/recycle")
    public Result<?> recycle(@PathVariable Long id, @RequestBody Map<String, Object> recycleData) {
        specialDrugService.recycle(id, recycleData);
        return Result.success("回收成功");
    }
}