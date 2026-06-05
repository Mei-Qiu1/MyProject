package com.example.hospital.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.Drug;

import java.util.List;
import java.util.Set;

public interface DrugService {

    Drug findById(Long id);

    Drug findByCode(String drugCode);

    Drug save(Drug drug);

    void update(Drug drug);

    void delete(Long id);

    IPage<Drug> list(int page, int size, String keyword, Long categoryId, Long manageCategoryId);

    void updateStatus(Long id, Integer status);

    List<Drug> listAll(LambdaQueryWrapper<Drug> wrapper);

    Set<String> getAllDrugCodes();
}