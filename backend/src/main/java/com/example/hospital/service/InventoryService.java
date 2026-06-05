package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.Inventory;

import java.util.List;
import java.util.Map;

public interface InventoryService {

    Inventory findById(Long id);
    void save(Inventory inventory);
    void update(Inventory inventory);
    void delete(Long id);
    IPage<Inventory> list(int page, int size, String keyword, Long warehouseId);
    List<Inventory> findExpiringDrugs(Integer days, String keyword, Boolean includeExpired, Long warehouseId);
    List<Inventory> findLowStockDrugs(Integer threshold);
    Integer getTotalQuantityByDrugId(Long drugId);
    void decreaseStock(Long inventoryId, Integer quantity);
    void increaseStock(Long inventoryId, Integer quantity);
    List<Inventory> getBatchesByDrugId(Long drugId);
    List<Map<String, Object>> findLowStockSummary();

    // 新增：按仓库聚合的低库存预警
    List<Map<String, Object>> getLowStockByWarehouse(Long warehouseId);
    // 新增：效期预警返回 Map 列表（避免转换）
    List<Map<String, Object>> getExpiringDrugsMap(Integer days, String keyword, Boolean includeExpired, Long warehouseId);
    // 新增：调拨
    void transfer(Long fromInventoryId, Long toWarehouseId, Integer quantity, String remark);
}