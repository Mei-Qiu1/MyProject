package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.hospital.entity.Role;
import com.example.hospital.mapper.RoleMapper;
import com.example.hospital.service.RoleMenuService;
import com.example.hospital.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleMenuService roleMenuService;

    public RoleServiceImpl(RoleMapper roleMapper, RoleMenuService roleMenuService) {
        this.roleMapper = roleMapper;
        this.roleMenuService = roleMenuService;
    }

    @Override
    public List<Role> list(String keyword) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Role::getRoleName, keyword).or().like(Role::getRoleCode, keyword);
        }
        wrapper.last("ORDER BY role_code = 'ADMIN' DESC, create_time DESC");
        return roleMapper.selectList(wrapper);
    }

    @Override
    public void save(Role role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleMapper.deleteById(id);
        roleMenuService.saveRoleMenus(id, null);
    }

    @Override
    public void setPermissions(Long roleId, List<Long> menuIds) {
        roleMenuService.saveRoleMenus(roleId, menuIds);
    }

    @Override
    public List<Long> getPermissions(Long roleId) {
        return roleMenuService.getMenuIdsByRoleId(roleId);
    }
}