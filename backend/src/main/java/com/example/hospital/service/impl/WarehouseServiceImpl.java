package com.example.hospital.service.impl;

import com.example.hospital.entity.Warehouse;
import com.example.hospital.mapper.WarehouseMapper;
import com.example.hospital.service.WarehouseService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseMapper warehouseMapper;
    public WarehouseServiceImpl(WarehouseMapper warehouseMapper) {
        this.warehouseMapper = warehouseMapper;
    }
    @Override
    public List<Warehouse> list() {
        return warehouseMapper.selectList(null);
    }
}