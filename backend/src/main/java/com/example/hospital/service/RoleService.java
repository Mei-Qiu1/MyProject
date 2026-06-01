package com.example.hospital.service;

import com.example.hospital.entity.Role;
import java.util.List;

public interface RoleService {
    List<Role> list(String keyword);
    void save(Role role);
    void update(Role role);
    void delete(Long id);
    void setPermissions(Long roleId, List<Long> menuIds);
    List<Long> getPermissions(Long roleId);
}