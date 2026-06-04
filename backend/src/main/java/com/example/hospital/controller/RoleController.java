package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Role;
import com.example.hospital.service.LogService;
import com.example.hospital.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;import jakarta.servlet.http.HttpServletRequest;           // 获取请求IP
import org.springframework.security.core.Authentication;   // 获取当前认证信息
import org.springframework.security.core.context.SecurityContextHolder; // 获取安全上下文
import java.time.LocalDateTime;                            // 记录日志时间
import java.util.Arrays;                                   // 将参数转为字符串
import java.util.Map;                                      // 权限设置参数
import java.util.List;                                     // 权限设置参数
import com.example.hospital.entity.SysLog;                 // 日志实体
import com.example.hospital.service.LogService;            // 日志服务

@RestController
@RequestMapping("/system/roles")
public class RoleController {

    private final LogService logService;

    private final RoleService roleService;

    public RoleController(RoleService roleService, LogService logService) {
        this.roleService = roleService;
        this.logService = logService;
    }

    @GetMapping
    public Result<?> list(@RequestParam(required = false) String keyword) {
        List<Role> list = roleService.list(keyword);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> create(@RequestBody Role role, HttpServletRequest request) {
        try {
            roleService.save(role);
            // 记录日志
            logService.save(buildLog("新增角色", "POST", "角色编码: " + role.getRoleCode() + ", 角色名称: " + role.getRoleName(),
                    getIpAddress(request), 1, null));
            return Result.success("创建成功");
        } catch (Exception e) {
            logService.save(buildLog("新增角色", "POST", getParamsAsString(role),
                    getIpAddress(request), 0, e.getMessage()));
            throw e; // 或返回错误
        }
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Role role, HttpServletRequest request) {
        try {
            role.setId(id);
            roleService.update(role);
            logService.save(buildLog("编辑角色", "PUT", "角色ID: " + id + ", 名称: " + role.getRoleName(),
                    getIpAddress(request), 1, null));
            return Result.success("更新成功");
        } catch (Exception e) {
            logService.save(buildLog("编辑角色", "PUT", getParamsAsString(role),
                    getIpAddress(request), 0, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        try {
            roleService.delete(id);
            logService.save(buildLog("删除角色", "DELETE", "角色ID: " + id,
                    getIpAddress(request), 1, null));
            return Result.success("删除成功");
        } catch (Exception e) {
            logService.save(buildLog("删除角色", "DELETE", "角色ID: " + id,
                    getIpAddress(request), 0, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{id}/permissions")
    public Result<?> getPermissions(@PathVariable Long id) {
        List<Long> permissions = roleService.getPermissions(id);
        return Result.success(permissions);
    }

    @PostMapping("/{id}/permissions")
    public Result<?> setPermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> request, HttpServletRequest httpRequest) {
        try {
            List<Long> menuIds = request.get("menuIds");
            roleService.setPermissions(id, menuIds);
            logService.save(buildLog("权限设置", "POST", "角色ID: " + id + ", 菜单ID列表: " + menuIds,
                    getIpAddress(httpRequest), 1, null));
            return Result.success("权限设置成功");
        } catch (Exception e) {
            logService.save(buildLog("权限设置", "POST", "角色ID: " + id,
                    getIpAddress(httpRequest), 0, e.getMessage()));
            throw e;
        }
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    /**
     * 获取请求IP（需要从 HttpServletRequest 获取，在控制器方法参数中添加）
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 将操作参数转为字符串（避免过长，可截取）
     */
    private String getParamsAsString(Object... params) {
        if (params == null || params.length == 0) return "";
        return Arrays.toString(params);
    }

    private SysLog buildLog(String operation, String method, String params, String ip, int status, String errorMsg) {
        SysLog log = new SysLog();
        log.setUsername(getCurrentUsername());
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params != null && params.length() > 500 ? params.substring(0, 500) : params);
        log.setIpAddress(ip);
        log.setStatus(status);
        log.setErrorMessage(errorMsg);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

}