package com.example.hospital.config;

import com.example.hospital.entity.Role;
import com.example.hospital.entity.User;
import com.example.hospital.mapper.RoleMapper;
import com.example.hospital.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        // 初始化角色数据
        initRoles();
        
        // 检查管理员用户是否存在
        User admin = userMapper.findByUsername("admin");
        if (admin == null) {
            // 创建测试用户
            createUser("admin", "admin123", "系统管理员", "ADMIN");
            createUser("pharmacist", "admin123", "张药师", "PHARMACIST");
            createUser("purchaser", "admin123", "李采购", "PURCHASER");
            createUser("doctor", "admin123", "王医生", "DOCTOR");
            createUser("stock_manager", "admin123", "赵库管", "STOCK_MANAGER");
            createUser("special_pharmacist", "admin123", "孙药师", "SPECIAL_PHARMACIST");
            createUser("pharmacy_director", "admin123", "周主任", "PHARMACY_DIRECTOR");
        }
    }

    private void initRoles() {
        Map<String, String> roleDescriptions = new HashMap<>();
        roleDescriptions.put("ADMIN", "系统最高权限管理员");
        roleDescriptions.put("PHARMACIST", "药房药品管理人员");
        roleDescriptions.put("PURCHASER", "药品采购人员");
        roleDescriptions.put("DOCTOR", "临床医生");
        roleDescriptions.put("SPECIAL_PHARMACIST", "毒麻精放药品管理员");
        roleDescriptions.put("STOCK_MANAGER", "仓库库存管理人员");
        roleDescriptions.put("PHARMACY_DIRECTOR", "药剂科负责人，负责采购审批、特殊药品管理和统计报表");

        Map<String, String> roleNames = new HashMap<>();
        roleNames.put("ADMIN", "系统管理员");
        roleNames.put("PHARMACIST", "药剂师");
        roleNames.put("PURCHASER", "采购员");
        roleNames.put("DOCTOR", "医生");
        roleNames.put("SPECIAL_PHARMACIST", "特殊药品管理员");
        roleNames.put("STOCK_MANAGER", "库存管理员");
        roleNames.put("PHARMACY_DIRECTOR", "药剂科主任");

        for (Map.Entry<String, String> entry : roleDescriptions.entrySet()) {
            String roleCode = entry.getKey();
            Role existingRole = roleMapper.findByRoleCode(roleCode);
            if (existingRole == null) {
                Role role = new Role();
                role.setRoleName(roleNames.get(roleCode));
                role.setRoleCode(roleCode);
                role.setDescription(entry.getValue());
                role.setStatus(1);
                role.setCreateTime(LocalDateTime.now());
                role.setUpdateTime(LocalDateTime.now());
                roleMapper.insert(role);
            }
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
