package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.DoctorDispensing;
import com.example.hospital.service.DoctorDispensingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor/dispensing")
public class DoctorDispensingController {

    private final DoctorDispensingService doctorDispensingService;

    public DoctorDispensingController(DoctorDispensingService doctorDispensingService) {
        this.doctorDispensingService = doctorDispensingService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword) {
        IPage<DoctorDispensing> recordPage = doctorDispensingService.page(page, size, keyword);
        return Result.success(PageResult.of(recordPage.getRecords(), recordPage.getTotal(),
                (int) recordPage.getCurrent(), (int) recordPage.getSize()));
    }

    @PostMapping
    public Result<?> create(@RequestBody DoctorDispensing record) {
        doctorDispensingService.save(record);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody DoctorDispensing record) {
        record.setId(id);
        doctorDispensingService.update(record);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        doctorDispensingService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/batch")
    public Result<?> batchSave(@RequestBody List<DoctorDispensing> records) {
        doctorDispensingService.batchSave(records);
        return Result.success("批量保存成功");
    }
}