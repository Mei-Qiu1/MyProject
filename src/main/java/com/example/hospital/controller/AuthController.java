
package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.User;
import com.example.hospital.security.JwtTokenUtil;
import com.example.hospital.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    
    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        User user = userService.login(username, password);
        if (user != null) {
            String token = jwtTokenUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("role", user.getRole());
            userInfo.put("status", user.getStatus());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            result.put("user", userInfo);
            return Result.success(result);
        }
        return Result.fail("用户名或密码错误");
    }
    
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.fail("用户名已存在");
        }
        user.setStatus(1);
        userService.save(user);
        return Result.success("注册成功");
    }
}
