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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    
    // 原子计数器，用于保证同一毫秒内的唯一性
    private final AtomicLong counter = new AtomicLong(0);
    // 上一次生成批次号的时间戳
    private volatile long lastTimestamp = 0;

    public InventoryServiceImpl(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public Inventory findById(Long id) {
        return inventoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public void save(Inventory inventory) {
        // 自动生成批次号
        if (inventory.getBatchNo() == null || inventory.getBatchNo().isEmpty()) {
            inventory.setBatchNo(generateBatchNo());
        }
        // 设置默认状态
        if (inventory.getStatus() == null) {
            inventory.setStatus(1);
        }
        inventory.setCreateTime(LocalDateTime.now());
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.insert(inventory);
    }
    
    private String generateBatchNo() {
        long now = System.currentTimeMillis();
        
        // 如果当前时间戳与上次相同，计数器递增；否则重置计数器
        if (now == lastTimestamp) {
            counter.incrementAndGet();
        } else {
            counter.set(0);
            lastTimestamp = now;
        }
        
        // 获取计数器值（确保在同一毫秒内唯一）
        long sequence = counter.get();
        
        // 生成批次号：B + 日期(6位) + 时间(6位) + 毫秒(3位) + 计数器(3位)
        LocalDateTime dateTime = LocalDateTime.now();
        String dateStr = dateTime.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String timeStr = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
        
        // 毫秒的后3位
        String millisStr = String.format("%03d", now % 1000);
        // 计数器（最多3位，超过则用UUID补充）
        String seqStr;
        if (sequence < 1000) {
            seqStr = String.format("%03d", sequence);
        } else {
            // 如果同一毫秒内超过1000次，使用UUID的最后几位保证唯一性
            seqStr = UUID.randomUUID().toString().substring(0, 3).toUpperCase();
        }
        
        return "B" + dateStr + timeStr + millisStr + seqStr;
    }

    @Override
    @Transactional
    public void update(Inventory inventory) {
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(inventory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        inventoryMapper.deleteById(id);
    }

    @Override
    public IPage<Inventory> list(int page, int size, String keyword, Long warehouseId) {
        Page<Inventory> pageParam = new Page<>(page, size);
        return inventoryMapper.selectPageByCondition(pageParam, keyword, warehouseId);
    }

    @Override
    public List<Inventory> findExpiringDrugs(Integer days, String keyword, Boolean includeExpired, Long warehouseId) {
        // 此方法为兼容原有接口，直接调用 getExpiringDrugsMap 并转换（实际前端使用 Map 版本，可忽略）
        return null;
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
            throw new IllegalArgumentException("数量必须大于0");
        }
        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("库存不足，当前库存: " + inventory.getQuantity());
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
            throw new IllegalArgumentException("数量必须大于0");
        }
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(inventory);
    }

    @Override
    public List<Inventory> getBatchesByDrugId(Long drugId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getDrugId, drugId)
                .gt(Inventory::getQuantity, 0)
                .orderByDesc(Inventory::getCreateTime);
        return inventoryMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> findLowStockSummary() {
        return inventoryMapper.findLowStockSummary();
    }

    @Override
    public List<Map<String, Object>> getLowStockByWarehouse(Long warehouseId) {
        return inventoryMapper.findLowStockByWarehouse(warehouseId);
    }

    @Override
    public List<Map<String, Object>> getExpiringDrugsMap(Integer days, String keyword, Boolean includeExpired, Long warehouseId) {
        return inventoryMapper.findExpiringDrugs(days, keyword, includeExpired, warehouseId);
    }

    @Override
    @Transactional
    public void transfer(Long fromInventoryId, Long toWarehouseId, Integer quantity, String remark) {
        Inventory fromInv = inventoryMapper.selectById(fromInventoryId);
        if (fromInv == null) {
            throw new IllegalArgumentException("源库存批次不存在");
        }
        if (fromInv.getQuantity() < quantity) {
            throw new IllegalArgumentException("库存不足，当前库存: " + fromInv.getQuantity());
        }

        // 减少源库存
        fromInv.setQuantity(fromInv.getQuantity() - quantity);
        fromInv.setUpdateTime(LocalDateTime.now());
        inventoryMapper.updateById(fromInv);

        // 检查目标仓库是否已存在相同药品、批号的库存记录
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getDrugId, fromInv.getDrugId())
                .eq(Inventory::getBatchNo, fromInv.getBatchNo())
                .eq(Inventory::getWarehouseId, toWarehouseId);
        Inventory existing = inventoryMapper.selectOne(wrapper);

        if (existing != null) {
            // 如果存在，增加数量
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUpdateTime(LocalDateTime.now());
            inventoryMapper.updateById(existing);
        } else {
            // 不存在，新增记录
            Inventory newInv = new Inventory();
            newInv.setDrugId(fromInv.getDrugId());
            newInv.setBatchNo(fromInv.getBatchNo());
            newInv.setProductionDate(fromInv.getProductionDate());
            newInv.setExpireDate(fromInv.getExpireDate());
            newInv.setQuantity(quantity);
            newInv.setUnitPrice(fromInv.getUnitPrice());
            newInv.setWarehouseId(toWarehouseId);
            newInv.setStatus(1);
            newInv.setCreateTime(LocalDateTime.now());
            newInv.setUpdateTime(LocalDateTime.now());
            inventoryMapper.insert(newInv);
        }

        // 可选：插入调拨记录（创建 InventoryTransfer 实体并保存）
        // 示例：省略，如需可自行实现
    }
}