package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Role;
import com.example.hospital.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(required = false) String keyword) {
        List<Role> list = roleService.list(keyword);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> create(@RequestBody Role role) {
        roleService.save(role);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        roleService.update(role);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/permissions")
    public Result<?> setPermissions(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.setPermissions(id, menuIds);
        return Result.success("权限设置成功");
    }
}