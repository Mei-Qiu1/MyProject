package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hospital.entity.DrugCategory;
import com.example.hospital.mapper.DrugCategoryMapper;
import com.example.hospital.service.DrugCategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DrugCategoryServiceImpl implements DrugCategoryService {

    private final DrugCategoryMapper drugCategoryMapper;

    public DrugCategoryServiceImpl(DrugCategoryMapper drugCategoryMapper) {
        this.drugCategoryMapper = drugCategoryMapper;
    }

    @Override
    public DrugCategory findById(Long id) {
        return drugCategoryMapper.selectById(id);
    }

    @Override
    public List<DrugCategory> list(Integer type) {
        LambdaQueryWrapper<DrugCategory> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(DrugCategory::getType, type);
        }
        wrapper.orderByAsc(DrugCategory::getSortOrder);
        return drugCategoryMapper.selectList(wrapper);
    }

    @Override
    public void save(DrugCategory category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        drugCategoryMapper.insert(category);
    }

    @Override
    public void update(DrugCategory category) {
        category.setUpdateTime(LocalDateTime.now());
        drugCategoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        drugCategoryMapper.deleteById(id);
    }
}