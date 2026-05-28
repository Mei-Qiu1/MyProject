
package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.Inventory;

import java.util.List;

/**
 * 库存服务接口
 */
public interface InventoryService {
    
    Inventory findById(Long id);
    
    void save(Inventory inventory);
    
    void update(Inventory inventory);
    
    void delete(Long id);
    
    IPage<Inventory> list(int page, int size, Long drugId, String batchNo);
    
    List<Inventory> findExpiringDrugs(Integer days);
    
    List<Inventory> findLowStockDrugs(Integer threshold);
    
    Integer getTotalQuantityByDrugId(Long drugId);
    
    void decreaseStock(Long inventoryId, Integer quantity);
    
    void increaseStock(Long inventoryId, Integer quantity);
}
