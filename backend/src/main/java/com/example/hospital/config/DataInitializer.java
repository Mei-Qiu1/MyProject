package com.example.hospital.config;

import com.example.hospital.entity.User;
import com.example.hospital.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

@Component
public class DataInitializer {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        // 检查管理员用户是否存在
        User admin = userMapper.findByUsername("admin");
        if (admin == null) {
            // 创建测试用户
            createUser("admin", "admin123", "系统管理员", "ADMIN");
            createUser("pharmacist", "admin123", "张药师", "PHARMACIST");
            createUser("purchaser", "admin123", "李采购", "PURCHASER");
        }
    }

    private void createUser(String username, String password, String realName, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setRole(role);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }
}
