package com.example.hospital.service;

import java.util.List;

public interface RoleMenuService {
    List<Long> getMenuIdsByRoleId(Long roleId);
    void saveRoleMenus(Long roleId, List<Long> menuIds);
}
