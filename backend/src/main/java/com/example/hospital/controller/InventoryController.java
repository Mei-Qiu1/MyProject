package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Inventory;
import com.example.hospital.service.InventoryService;
import com.example.hospital.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final WarehouseService warehouseService;

    public InventoryController(InventoryService inventoryService, WarehouseService warehouseService) {
        this.inventoryService = inventoryService;
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Long drugId,
                          @RequestParam(required = false) String batchNo) {
        IPage<Inventory> inventoryPage = inventoryService.list(page, size, drugId, batchNo);
        return Result.success(PageResult.of(inventoryPage.getRecords(), inventoryPage.getTotal(),
                (int) inventoryPage.getCurrent(), (int) inventoryPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Inventory inventory = inventoryService.findById(id);
        if (inventory != null) {
            return Result.success(inventory);
        }
        return Result.fail("库存记录不存在");
    }

    @GetMapping("/expiring")
    public Result<?> getExpiringDrugs(@RequestParam(defaultValue = "30") Integer days,
                                      @RequestParam(required = false) String keyword) {
        List<Inventory> list = inventoryService.findExpiringDrugs(days);
        return Result.success(list);
    }

    @GetMapping("/low-stock")
    public Result<?> getLowStock(@RequestParam(defaultValue = "10") Integer threshold,
                                 @RequestParam(required = false) String keyword) {
        List<Inventory> list = inventoryService.findLowStockDrugs(threshold);
        return Result.success(list);
    }

    @GetMapping("/quantity/{drugId}")
    public Result<?> getTotalQuantity(@PathVariable Long drugId) {
        Integer quantity = inventoryService.getTotalQuantityByDrugId(drugId);
        return Result.success(quantity);
    }

    // 新增：获取仓库列表，供前端下拉选择
    @GetMapping("/warehouses")
    public Result<?> getWarehouses() {
        return Result.success(warehouseService.list());
    }

    @PostMapping
    public Result<?> create(@RequestBody Inventory inventory) {
        inventoryService.save(inventory);
        return Result.success("入库成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Inventory inventory) {
        inventory.setId(id);
        inventoryService.update(inventory);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/decrease")
    public Result<?> decreaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        inventoryService.decreaseStock(id, quantity);
        return Result.success("出库成功");
    }

    @PostMapping("/{id}/increase")
    public Result<?> increaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        inventoryService.increaseStock(id, quantity);
        return Result.success("库存增加成功");
    }
}