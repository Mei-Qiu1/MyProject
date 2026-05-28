
package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.Inventory;
import com.example.hospital.mapper.InventoryMapper;
import com.example.hospital.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存服务实现类
 */
@Service
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryMapper inventoryMapper;
    
    public InventoryServiceImpl(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }
    
    @Override
    public Inventory findById(Long id) {
        return inventoryMapper.selectById(id);
    }
    
    @Override
    public void save(Inventory inventory) {
        inventory.setCreateTime(LocalDateTime.now());
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.insert(inventory);
    }
    
    @Override
    public void update(Inventory inventory) {
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(inventory);
    }
    
    @Override
    public void delete(Long id) {
        inventoryMapper.deleteById(id);
    }
    
    @Override
    public IPage<Inventory> list(int page, int size, Long drugId, String batchNo) {
        Page<Inventory> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        if (drugId != null) {
            wrapper.eq(Inventory::getDrugId, drugId);
        }
        if (batchNo != null && !batchNo.isEmpty()) {
            wrapper.like(Inventory::getBatchNo, batchNo);
        }
        return inventoryMapper.selectPage(pageParam, wrapper);
    }
    
    @Override
    public List<Inventory> findExpiringDrugs(Integer days) {
        return inventoryMapper.findExpiringDrugs(days);
    }
    
    @Override
    public List<Inventory> findLowStockDrugs(Integer threshold) {
        return inventoryMapper.findLowStockDrugs(threshold);
    }
    
    @Override
    public Integer getTotalQuantityByDrugId(Long drugId) {
        return inventoryMapper.getTotalQuantityByDrugId(drugId);
    }
    
    @Override
    @Transactional
    public void decreaseStock(Long inventoryId, Integer quantity) {
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new IllegalArgumentException("库存记录不存在");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("出库数量必须大于0");
        }
        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("库存不足，当前库存: " + inventory.getQuantity() + "，需要: " + quantity);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(inventory);
    }
    
    @Override
    @Transactional
    public void increaseStock(Long inventoryId, Integer quantity) {
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new IllegalArgumentException("库存记录不存在");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("入库数量必须大于0");
        }
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(inventory);
    }
}
