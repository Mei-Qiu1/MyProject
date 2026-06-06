package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Role;
import com.example.hospital.entity.SysLog;
import com.example.hospital.entity.User;
import com.example.hospital.mapper.RoleMapper;
import com.example.hospital.mapper.UserMapper;
import com.example.hospital.service.LogService;
import com.example.hospital.service.UserService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;
    private final LogService logService;
    private final UserMapper userMapper;

    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          RoleMapper roleMapper,
                          LogService logService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleMapper = roleMapper;
        this.logService = logService;
        this.userMapper = userMapper;
    }

    // ==================== 用户修改自己的密码 ====================
    @PostMapping("/change-password")
    public Result<?> changeOwnPassword(@RequestBody Map<String, String> request,
                                       HttpServletRequest httpRequest) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || oldPassword.isEmpty()) {
            return Result.fail("旧密码不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.fail("新密码不能为空");
        }

        // 获取当前登录用户
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        
        if (user == null) {
            return Result.fail("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.fail("旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userService.update(user);

        logService.save(buildLog("修改密码", "POST",
                "用户名: " + user.getUsername(),
                getIpAddress(httpRequest), 1, null));

        return Result.success("密码修改成功");
    }

    // ==================== 用户CRUD ====================

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword) {
        IPage<User> userPage = userService.list(page, size, keyword);
        return Result.success(PageResult.of(userPage.getRecords(), userPage.getTotal(),
                (int) userPage.getCurrent(), (int) userPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.fail("用户不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody User user, HttpServletRequest request) {
        try {
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                return Result.fail("用户名已存在");
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                return Result.fail("密码不能为空");
            }
            userService.save(user);
            logService.save(buildLog("新增用户", "POST",
                    "用户名: " + user.getUsername() + ", 姓名: " + user.getRealName(),
                    getIpAddress(request), 1, null));
            return Result.success("创建成功");
        } catch (Exception e) {
            logService.save(buildLog("新增用户", "POST",
                    getParamsAsString(user), getIpAddress(request), 0, e.getMessage()));
            throw e;
        }
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        try {
            user.setId(id);
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                User existing = userService.findById(id);
                if (existing != null) {
                    user.setPassword(existing.getPassword());
                }
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userService.update(user);
            logService.save(buildLog("编辑用户", "PUT",
                    "用户ID: " + id + ", 用户名: " + user.getUsername(),
                    getIpAddress(request), 1, null));
            return Result.success("更新成功");
        } catch (Exception e) {
            logService.save(buildLog("编辑用户", "PUT",
                    getParamsAsString(user), getIpAddress(request), 0, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            userService.delete(id);
            logService.save(buildLog("删除用户", "DELETE",
                    "用户ID: " + id + ", 用户名: " + user.getUsername(),
                    getIpAddress(request), 1, null));
            return Result.success("删除成功");
        } catch (Exception e) {
            logService.save(buildLog("删除用户", "DELETE",
                    "用户ID: " + id, getIpAddress(request), 0, e.getMessage()));
            throw e;
        }
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        try {
            userService.updateStatus(id, status);
            logService.save(buildLog("修改用户状态", "PUT",
                    "用户ID: " + id + ", 状态: " + (status == 1 ? "启用" : "禁用"),
                    getIpAddress(request), 1, null));
            return Result.success("状态更新成功");
        } catch (Exception e) {
            logService.save(buildLog("修改用户状态", "PUT",
                    "用户ID: " + id, getIpAddress(request), 0, e.getMessage()));
            throw e;
        }
    }

    @PutMapping("/{id}/password")
    public Result<?> changePassword(@PathVariable Long id,
                                    @RequestBody Map<String, String> request,
                                    HttpServletRequest httpRequest) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (oldPassword == null || oldPassword.isEmpty()) {
            return Result.fail("旧密码不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.fail("新密码不能为空");
        }

        User user = userService.findById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.fail("旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userService.update(user);

        logService.save(buildLog("修改密码", "PUT",
                "用户ID: " + id + ", 用户名: " + user.getUsername(),
                getIpAddress(httpRequest), 1, null));

        return Result.success("密码修改成功");
    }

    // ==================== 批量导入（原子性，动态加密，直接使用 Mapper） ====================

    @PostMapping("/import")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importUsers(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.fail("上传文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            return Result.fail("Excel格式错误，请上传 .xlsx 或 .xls 文件");
        }

        // 预加载所有角色
        List<Role> allRoles = roleMapper.selectList(null);
        Set<String> validRoleCodes = allRoles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());

        // 中文角色名映射
        Map<String, String> roleNameToCode = new HashMap<>();
        roleNameToCode.put("系统管理员", "ADMIN");
        roleNameToCode.put("管理员", "ADMIN");
        roleNameToCode.put("药剂师", "PHARMACIST");
        roleNameToCode.put("采购员", "PURCHASER");
        roleNameToCode.put("医生", "DOCTOR");
        roleNameToCode.put("特殊药品管理员", "SPECIAL_PHARMACIST");
        roleNameToCode.put("库存管理员", "STOCK_MANAGER");
        roleNameToCode.put("药剂科主任", "PHARMACY_DIRECTOR");

        List<User> userList = new ArrayList<>();
        StringBuilder errorLog = new StringBuilder();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook;
            if (originalFilename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(is);
            } else {
                workbook = new HSSFWorkbook(is);
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return Result.fail("文件中没有数据行");
            }

            // 校验标题行
            Row headerRow = sheet.getRow(0);
            String[] expectedHeaders = {"用户名", "姓名", "电话", "邮箱", "角色"};
            for (int i = 0; i < expectedHeaders.length; i++) {
                String cellValue = getCellStringValue(headerRow.getCell(i));
                if (cellValue == null || !expectedHeaders[i].equals(cellValue.trim())) {
                    return Result.fail("Excel格式错误，请确保列顺序为：用户名、姓名、电话、邮箱、角色");
                }
            }

            // 逐行校验
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String username = getCellStringValue(row.getCell(0));
                String realName = getCellStringValue(row.getCell(1));
                String phone = getCellStringValue(row.getCell(2));
                String email = getCellStringValue(row.getCell(3));
                String roleInput = getCellStringValue(row.getCell(4));

                int rowNum = row.getRowNum() + 1;

                if (isBlank(username)) {
                    errorLog.append("第").append(rowNum).append("行用户名缺失；");
                    continue;
                }
                if (isBlank(realName)) {
                    errorLog.append("第").append(rowNum).append("行姓名缺失；");
                    continue;
                }
                if (isBlank(roleInput)) {
                    errorLog.append("第").append(rowNum).append("行角色缺失；");
                    continue;
                }
                if (isBlank(phone)) {
                    errorLog.append("第").append(rowNum).append("行电话为空；");
                    continue;
                }
                String phoneTrim = phone.trim();
                if (!phoneTrim.matches("1\\d{10}")) {
                    errorLog.append("第").append(rowNum).append("行电话格式错误；");
                    continue;
                }

                // 邮箱格式校验（非必填，但如果填写了必须格式正确）
                if (!isBlank(email)) {
                    String emailTrim = email.trim();
                    if (!emailTrim.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
                        errorLog.append("第").append(rowNum).append("行邮箱格式错误；");
                        continue;
                    }
                }

                String roleCode = null;
                String trimmedRole = roleInput.trim();
                String upperRole = trimmedRole.toUpperCase();
                if (validRoleCodes.contains(upperRole)) {
                    roleCode = upperRole;
                } else {
                    roleCode = roleNameToCode.get(trimmedRole);
                    if (roleCode == null || !validRoleCodes.contains(roleCode)) {
                        errorLog.append("第").append(rowNum).append("行角色【").append(trimmedRole).append("】不存在；");
                        continue;
                    }
                }

                // 用户名唯一性校验
                if (userService.findByUsername(username) != null) {
                    errorLog.append("第").append(rowNum).append("行用户名【").append(username).append("】已存在；");
                    continue;
                }

                // ★ 关键：动态生成 "123456" 的 BCrypt 哈希
                String encodedPassword = passwordEncoder.encode("123456");
                System.out.println("用户 " + username + " 的密码哈希: " + encodedPassword);

                User user = new User();
                user.setUsername(username);
                user.setPassword(encodedPassword);
                user.setRealName(realName);
                user.setRole(roleCode);
                user.setPhone(phoneTrim);
                user.setEmail(email);
                user.setStatus(1);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());

                userList.add(user);
            }

            if (errorLog.length() > 0) {
                return Result.fail("导入失败：存在无效数据，未导入任何用户。错误详情：" + errorLog.toString());
            }
            if (userList.isEmpty()) {
                return Result.fail("导入失败：没有有效数据行。");
            }

            // 直接使用 Mapper 插入，确保没有任何额外逻辑干扰
            for (User user : userList) {
                userMapper.insert(user);
                // 验证插入后的密码是否正确
                User inserted = userMapper.selectById(user.getId());
                if (!user.getPassword().equals(inserted.getPassword())) {
                    throw new RuntimeException("用户 " + user.getUsername() + " 的密码被篡改，期望: " + user.getPassword() + "，实际: " + inserted.getPassword());
                }
            }

            // 记录日志
            logService.save(buildLog("批量导入用户", "POST",
                    "文件名: " + originalFilename + ", 导入条数: " + userList.size(),
                    getIpAddress(request), 1, null));

            return Result.success("导入成功，共导入 " + userList.size() + " 条记录");
        } catch (Exception e) {
            e.printStackTrace();
            logService.save(buildLog("批量导入用户", "POST",
                    "文件名: " + originalFilename, getIpAddress(request), 0, e.getMessage()));
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

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

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}