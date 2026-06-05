package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Inventory;
import com.example.hospital.service.InventoryService;
import com.example.hospital.service.WarehouseService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final WarehouseService warehouseService;

    public InventoryController(InventoryService inventoryService, WarehouseService warehouseService) {
        this.inventoryService = inventoryService;
        this.warehouseService = warehouseService;
    }

    // 库存查询列表（分页）
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Long warehouseId) {
        IPage<Inventory> inventoryPage = inventoryService.list(page, size, keyword, warehouseId);
        return Result.success(PageResult.of(inventoryPage.getRecords(), inventoryPage.getTotal(),
                (int) inventoryPage.getCurrent(), (int) inventoryPage.getSize()));
    }

    // 低库存预警（按仓库聚合，不区分批次，仅未过期）
    @GetMapping("/low-stock")
    public Result<?> getLowStock(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Long warehouseId) {
        List<Map<String, Object>> list = inventoryService.getLowStockByWarehouse(warehouseId);
        if (StringUtils.hasText(keyword)) {
            list = list.stream()
                    .filter(item -> item.get("drugName").toString().contains(keyword) ||
                            item.get("drugCode").toString().contains(keyword))
                    .collect(Collectors.toList());
        }
        return Result.success(list);
    }

    // 效期预警
    @GetMapping("/expiring")
    public Result<?> getExpiringDrugs(@RequestParam(defaultValue = "180") Integer days,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "true") Boolean includeExpired,
                                      @RequestParam(required = false) Long warehouseId) {
        List<Map<String, Object>> list = inventoryService.getExpiringDrugsMap(days, keyword, includeExpired, warehouseId);
        return Result.success(list);
    }

    // 获取仓库列表
    @GetMapping("/warehouses")
    public Result<?> getWarehouses() {
        return Result.success(warehouseService.list());
    }

    // 报损（减少库存）
    @PostMapping("/{id}/decrease")
    public Result<?> decreaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            inventoryService.decreaseStock(id, quantity);
            return Result.success("报损成功");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    // 调拨
    @PostMapping("/transfer")
    public Result<?> transfer(@RequestBody Map<String, Object> params) {
        try {
            Long fromInventoryId = Long.valueOf(params.get("fromInventoryId").toString());
            Long toWarehouseId = Long.valueOf(params.get("toWarehouseId").toString());
            Integer quantity = Integer.valueOf(params.get("quantity").toString());
            String remark = (String) params.get("remark");
            inventoryService.transfer(fromInventoryId, toWarehouseId, quantity, remark);
            return Result.success("调拨成功");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("调拨失败：" + e.getMessage());
        }
    }
}