package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.DrugCategory;
import com.example.hospital.service.DrugCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drugs/categories")
public class DrugCategoryController {

    private final DrugCategoryService drugCategoryService;

    public DrugCategoryController(DrugCategoryService drugCategoryService) {
        this.drugCategoryService = drugCategoryService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(required = false) Integer type) {
        List<DrugCategory> categories = drugCategoryService.list(type);
        return Result.success(categories);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        DrugCategory category = drugCategoryService.findById(id);
        return category != null ? Result.success(category) : Result.fail("分类不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody DrugCategory category) {
        drugCategoryService.save(category);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody DrugCategory category) {
        category.setId(id);
        drugCategoryService.update(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        drugCategoryService.delete(id);
        return Result.success("删除成功");
    }
}