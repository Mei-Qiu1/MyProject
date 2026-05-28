
package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    User findById(Long id);
    
    User findByUsername(String username);
    
    User login(String username, String password);
    
    User save(User user);
    
    void update(User user);
    
    void delete(Long id);
    
    IPage<User> list(int page, int size, String keyword);
    
    void updateStatus(Long id, Integer status);
}
