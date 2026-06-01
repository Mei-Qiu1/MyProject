package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.User;
import com.example.hospital.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/system/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

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
    public Result<?> create(@RequestBody User user) {
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.fail("用户名已存在");
        }
        // 密码加密由 Service 层处理，但这里确保传入的密码不为空
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Result.fail("密码不能为空");
        }
        userService.save(user);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        // 如果密码字段为空，则不更新密码（保持原密码）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            User existing = userService.findById(id);
            if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        }
        userService.update(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 修改密码
     * @param id 用户ID
     * @param request 包含 oldPassword 和 newPassword
     */
    @PutMapping("/{id}/password")
    public Result<?> changePassword(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
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
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.fail("旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userService.update(user);
        
        return Result.success("密码修改成功");
    }

    /**
     * 批量导入用户（Excel文件）
     * 期望的Excel列顺序：用户名、姓名、角色、电话、邮箱
     * 必填列：用户名、姓名、角色（必须有值）
     * 选填列：电话、邮箱（可以为空）
     */
    @PostMapping("/import")
    public Result<?> importUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("上传文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            return Result.fail("请上传 Excel 文件（.xlsx 或 .xls）");
        }
        List<User> userList = new ArrayList<>();
        int skippedRows = 0;
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return Result.fail("文件中没有数据行");
            }
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 跳过标题行
                String username = getCellStringValue(row.getCell(0));
                String realName = getCellStringValue(row.getCell(1));
                String role = getCellStringValue(row.getCell(2));
                String phone = getCellStringValue(row.getCell(3));
                String email = getCellStringValue(row.getCell(4));
                
                // 必填字段校验：用户名、姓名、角色必须有值
                if (username == null || username.trim().isEmpty()) {
                    skippedRows++;
                    continue; // 用户名为空则跳过
                }
                if (realName == null || realName.trim().isEmpty()) {
                    skippedRows++;
                    continue; // 姓名为空则跳过
                }
                if (role == null || role.trim().isEmpty()) {
                    skippedRows++;
                    continue; // 角色为空则跳过
                }
                
                // 检查用户名是否已存在
                if (userService.findByUsername(username) != null) {
                    skippedRows++;
                    continue; // 已存在则跳过，不重复导入
                }
                
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode("123456")); // 默认密码
                user.setRealName(realName);
                user.setRole(role.toUpperCase()); // 角色转换为大写
                user.setPhone(phone);
                user.setEmail(email);
                user.setStatus(1);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                userList.add(user);
            }
            // 批量保存
            for (User user : userList) {
                userService.save(user);
            }
            String message = "导入成功，共导入 " + userList.size() + " 条记录";
            if (skippedRows > 0) {
                message += "，跳过 " + skippedRows + " 条无效记录";
            }
            return Result.success(message);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // 处理数字（如手机号）转换为字符串
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}