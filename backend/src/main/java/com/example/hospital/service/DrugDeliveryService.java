package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.DrugDelivery;

/**
 * 药品配送单服务接口
 */
public interface DrugDeliveryService {
    
    IPage<DrugDelivery> page(int page, int size, String keyword, Integer status, String department);
    
    DrugDelivery getById(Long id);
    
    void create(Long orderId);
    
    void sign(Long id, String signer);
    
    void delete(Long id);
}