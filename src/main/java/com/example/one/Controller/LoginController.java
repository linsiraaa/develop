package com.example.one.Controller;

import com.example.one.pojo.SysUser;
import com.example.one.repository.SysUserRepository;
import com.example.one.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * 登录接口
     */
    @PostMapping("/loginAction")
    @ResponseBody // 返回JSON数据给前端JS处理
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        String normalizedUsername = username == null ? "" : username.trim();

        if (normalizedUsername.isEmpty() || password == null || password.isEmpty()) {
            return failure("请输入用户名和密码");
        }

        // 1. 查询用户
        SysUser user = sysUserRepository.findByUsername(normalizedUsername);
        if (user == null) {
            return failure("用户名或密码错误");
        }

        // 2. 使用 BCrypt 校验密码；旧版本的明文账号在首次成功登录时自动升级。
        if (!matchesPassword(password, user.getPassword())) {
            return failure("用户名或密码错误");
        }
        upgradeLegacyPasswordIfNeeded(user, password);

        // 3. 判断状态 (0-待审核, 1-正常, 2-禁用)
        if (Integer.valueOf(0).equals(user.getStatus())) {
            return failure("账号审核中，请联系管理员");
        }

        if (Integer.valueOf(2).equals(user.getStatus())) {
            return failure("账号已被禁用");
        }

        if (!Integer.valueOf(1).equals(user.getStatus())) {
            return failure("账号状态异常，请联系管理员");
        }

        // 4. 登录成功，返回短期 JWT；密码不会出现在响应中。
        result.put("success", true);
        result.put("msg", "登录成功");
        result.put("token", jwtService.generateToken(user));
        result.put("tokenType", "Bearer");
        result.put("expiresIn", jwtService.getExpirationSeconds());
        result.put("username", user.getUsername());
        return result;
    }

    /**
     * 注册接口
     */
    @PostMapping("/registerAction")
    @ResponseBody
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password) {
        String normalizedUsername = username == null ? "" : username.trim();

        if (normalizedUsername.isEmpty()) {
            return failure("用户名不能为空");
        }
        if (normalizedUsername.length() > 50) {
            return failure("用户名长度不能超过 50 个字符");
        }
        if (password == null || password.length() < 6 || password.length() > 100) {
            return failure("密码长度必须为 6 至 100 个字符");
        }

        // 1. 检查是否已存在
        if (sysUserRepository.findByUsername(normalizedUsername) != null) {
            return failure("用户名已存在");
        }

        // 2. 创建新用户
        SysUser newUser = new SysUser();
        newUser.setUsername(normalizedUsername);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setStatus(0); // 新用户必须审核后才能登录
        newUser.setCreateTime(new Date());

        // 3. 保存到数据库
        try {
            sysUserRepository.save(newUser);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("msg", "注册提交成功，请等待管理员审核");
            return result;
        } catch (DataIntegrityViolationException e) {
            // 防止并发注册绕过前置的重复检查。
            return failure("用户名已存在");
        } catch (Exception e) {
            return failure("注册失败，请稍后重试");
        }
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        if (isBcryptHash(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        // 仅用于兼容已存在的旧数据，成功登录后立即改写为 BCrypt。
        return MessageDigest.isEqual(
                rawPassword.getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8));
    }

    private void upgradeLegacyPasswordIfNeeded(SysUser user, String rawPassword) {
        if (!isBcryptHash(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            sysUserRepository.save(user);
        }
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }

    private Map<String, Object> failure(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", message);
        return result;
    }
}

