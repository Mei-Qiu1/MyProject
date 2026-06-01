package com.example.hospital.service.impl;

import com.example.hospital.entity.RoleMenu;
import com.example.hospital.mapper.RoleMenuMapper;
import com.example.hospital.service.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    private final RoleMenuMapper roleMenuMapper;

    public RoleMenuServiceImpl(RoleMenuMapper roleMenuMapper) {
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        // 先删除旧的权限
        roleMenuMapper.deleteByRoleId(roleId);
        
        // 保存新的权限
        if (menuIds != null && !menuIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenu.setCreateTime(now);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }
}
