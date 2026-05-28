
package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Drug;
import com.example.hospital.service.DrugService;
import org.springframework.web.bind.annotation.*;

/**
 * 药品管理控制器
 */
@RestController
@RequestMapping("/drugs")
public class DrugController {
    
    private final DrugService drugService;
    
    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }
    
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) Long categoryId) {
        IPage<Drug> drugPage = drugService.list(page, size, keyword, categoryId);
        return Result.success(PageResult.of(drugPage.getRecords(), drugPage.getTotal(),
                (int) drugPage.getCurrent(), (int) drugPage.getSize())); }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Drug drug = drugService.findById(id);
        if (drug != null) {
            return Result.success(drug);
        }
        return Result.fail("药品不存在");
    }
    
    @PostMapping
    public Result<?> create(@RequestBody Drug drug) {
        Drug existingDrug = drugService.findByCode(drug.getDrugCode());
        if (existingDrug != null) {
            return Result.fail("药品编码已存在");
        }
        drugService.save(drug);
        return Result.success("创建成功");
    }
    
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Drug drug) {
        drug.setId(id);
        drugService.update(drug);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        drugService.delete(id);
        return Result.success("删除成功");
    }
    
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        drugService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
}
