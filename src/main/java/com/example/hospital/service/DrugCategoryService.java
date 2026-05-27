package com.example.hospital.service;

import com.example.hospital.entity.DrugCategory;

import java.util.List;

public interface DrugCategoryService {
    DrugCategory findById(Long id);
    List<DrugCategory> list(Integer type);
    void save(DrugCategory category);
    void update(DrugCategory category);
    void delete(Long id);
}