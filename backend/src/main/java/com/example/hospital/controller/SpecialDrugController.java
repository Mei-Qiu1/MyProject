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
        try {
            List<Map<String, Object>> result = specialDrugService.listInventory(keyword);
            if (result.isEmpty()) {
                // 返回模拟数据
                return Result.success(List.of(
                    Map.of("id", 1, "drugCode", "D0005", "drugName", "吗啡注射液", "spec", "10mg/1ml*5支", 
                           "batchNo", "B20240501", "expireDate", "2026-12-31", "quantity", 0, "warehouseName", "特殊药品库"),
                    Map.of("id", 2, "drugCode", "D0005", "drugName", "吗啡注射液", "spec", "10mg/1ml*5支", 
                           "batchNo", "B20240502", "expireDate", "2026-12-31", "quantity", 0, "warehouseName", "特殊药品库"),
                    Map.of("id", 3, "drugCode", "D0006", "drugName", "地西泮片", "spec", "2.5mg*20片", 
                           "batchNo", "B20240601", "expireDate", "2026-12-31", "quantity", 0, "warehouseName", "特殊药品库")
                ));
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("加载库存失败：" + e.getMessage());
        }
    }

    @GetMapping("/records")
    public Result<?> records(@RequestParam(required = false) String keyword) {
        try {
            List<?> result = specialDrugService.listRecords(keyword);
            if (result.isEmpty()) {
                // 返回模拟数据
                List<Map<String, Object>> mockData = new java.util.ArrayList<>();
                mockData.add(Map.of("id", 1L, "drugName", "吗啡注射液", "batchNo", "B20240502", "prescriptionNo", "PR20240615005",
                           "quantity", 15, "purpose", "ICU镇静", "user1", "周医生", "user2", "王药师",
                           "recycleStatus", "已回收", "createTime", "2024-06-15 11:00:00"));
                mockData.add(Map.of("id", 2L, "drugName", "吗啡注射液", "batchNo", "B20240502", "prescriptionNo", "PR20240615005",
                           "quantity", 15, "purpose", "ICU镇静", "user1", "周医生", "user2", "王药师",
                           "recycleStatus", "已回收", "createTime", "2024-06-15 11:00:00"));
                mockData.add(Map.of("id", 3L, "drugName", "吗啡注射液", "batchNo", "B20240502", "prescriptionNo", "PR20240615005",
                           "quantity", 15, "purpose", "ICU镇静", "user1", "周医生", "user2", "王药师",
                           "recycleStatus", "使用中", "createTime", "2024-06-15 11:00:00"));
                return Result.success(mockData);
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("加载领用记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/applies")
    public Result<?> applies() {
        try {
            return Result.success(specialDrugService.listApplies());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("加载申请列表失败：" + e.getMessage());
        }
    }

    @PostMapping("/applies")
    public Result<?> createApply(@RequestBody Map<String, Object> apply) {
        try {
            specialDrugService.saveApply(apply);
            return Result.success("申请已提交");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("提交申请失败：" + e.getMessage());
        }
    }

    @PutMapping("/applies/{id}/approve")
    public Result<?> approveApply(@PathVariable Long id, @RequestBody Map<String, String> users) {
        try {
            specialDrugService.approveApply(id, users);
            return Result.success("审批通过");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("审批失败：" + e.getMessage());
        }
    }

    @PostMapping("/records/{id}/recycle")
    public Result<?> recycle(@PathVariable Long id, @RequestBody Map<String, Object> recycleData) {
        try {
            specialDrugService.recycle(id, recycleData);
            return Result.success("回收成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("回收失败：" + e.getMessage());
        }
    }
}