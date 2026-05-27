package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Supplier;
import com.example.hospital.service.SupplierService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drugs/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword) {
        IPage<Supplier> supplierPage = supplierService.page(page, size, keyword);
        return Result.success(PageResult.of(supplierPage.getRecords(), supplierPage.getTotal(),
                (int) supplierPage.getCurrent(), (int) supplierPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Supplier supplier = supplierService.findById(id);
        return supplier != null ? Result.success(supplier) : Result.fail("供应商不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody Supplier supplier) {
        supplierService.save(supplier);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierService.update(supplier);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        supplierService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
}