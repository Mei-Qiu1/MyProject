package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.Drug;
import com.example.hospital.mapper.DrugMapper;
import com.example.hospital.service.DrugService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DrugServiceImpl implements DrugService {

    private final DrugMapper drugMapper;

    public DrugServiceImpl(DrugMapper drugMapper) {
        this.drugMapper = drugMapper;
    }

    @Override
    public Drug findById(Long id) {
        return drugMapper.selectById(id);
    }

    @Override
    public Drug findByCode(String drugCode) {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Drug::getDrugCode, drugCode);
        return drugMapper.selectOne(wrapper);
    }

    @Override
    public Drug save(Drug drug) {
        drug.setCreateTime(LocalDateTime.now());
        drug.setUpdateTime(LocalDateTime.now());
        drugMapper.insert(drug);
        return drug;
    }

    @Override
    public void update(Drug drug) {
        drug.setUpdateTime(LocalDateTime.now());
        drugMapper.updateById(drug);
    }

    @Override
    public void delete(Long id) {
        drugMapper.deleteById(id);
    }

    @Override
    public IPage<Drug> list(int page, int size, String keyword, Long categoryId, Long manageCategoryId) {
        Page<Drug> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Drug::getDrugName, keyword).or().like(Drug::getDrugCode, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Drug::getCategoryId, categoryId);
        }
        if (manageCategoryId != null) {
            wrapper.eq(Drug::getManageCategoryId, manageCategoryId);
        }
        return drugMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Drug drug = drugMapper.selectById(id);
        if (drug != null) {
            drug.setStatus(status);
            drug.setUpdateTime(LocalDateTime.now());
            drugMapper.updateById(drug);
        }
    }

    @Override
    public List<Drug> listAll(LambdaQueryWrapper<Drug> wrapper) {
        return drugMapper.selectList(wrapper);
    }

    @Override
    public Set<String> getAllDrugCodes() {
        List<Drug> drugs = drugMapper.selectList(null);
        return drugs.stream().map(Drug::getDrugCode).collect(Collectors.toSet());
    }
}