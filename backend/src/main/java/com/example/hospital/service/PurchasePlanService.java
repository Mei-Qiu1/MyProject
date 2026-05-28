package com.example.hospital.service;

import com.example.hospital.entity.PurchasePlan;
import java.util.List;

public interface PurchasePlanService {
    List<PurchasePlan> list(String keyword);
    PurchasePlan getById(Long id);
    void save(PurchasePlan plan);
    void update(PurchasePlan plan);
    void delete(Long id);
}