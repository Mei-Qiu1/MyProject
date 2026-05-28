
package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.Drug;

/**
 * 药品服务接口
 */
public interface DrugService {
    
    Drug findById(Long id);
    
    Drug findByCode(String drugCode);
    
    Drug save(Drug drug);
    
    void update(Drug drug);
    
    void delete(Long id);
    
    IPage<Drug> list(int page, int size, String keyword, Long categoryId);
    
    void updateStatus(Long id, Integer status);
}
