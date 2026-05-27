package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hospital.entity.PurchasePlan;
import com.example.hospital.mapper.PurchasePlanMapper;
import com.example.hospital.service.PurchasePlanService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchasePlanServiceImpl implements PurchasePlanService {

    private final PurchasePlanMapper purchasePlanMapper;

    public PurchasePlanServiceImpl(PurchasePlanMapper purchasePlanMapper) {
        this.purchasePlanMapper = purchasePlanMapper;
    }

    @Override
    public List<PurchasePlan> list(String keyword) {
        LambdaQueryWrapper<PurchasePlan> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PurchasePlan::getPlanName, keyword).or().like(PurchasePlan::getPlanNo, keyword);
        }
        wrapper.orderByDesc(PurchasePlan::getCreateTime);
        return purchasePlanMapper.selectList(wrapper);
    }

    @Override
    public PurchasePlan getById(Long id) {
        return purchasePlanMapper.selectById(id);
    }

    @Override
    public void save(PurchasePlan plan) {
        plan.setCreateTime(LocalDateTime.now());
        plan.setUpdateTime(LocalDateTime.now());
        purchasePlanMapper.insert(plan);
    }

    @Override
    public void update(PurchasePlan plan) {
        plan.setUpdateTime(LocalDateTime.now());
        purchasePlanMapper.updateById(plan);
    }

    @Override
    public void delete(Long id) {
        purchasePlanMapper.deleteById(id);
    }
}