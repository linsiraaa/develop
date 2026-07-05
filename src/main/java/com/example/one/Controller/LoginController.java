package com.example.one.Controller;

import com.example.one.pojo.SysUser;
import com.example.one.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private SysUserRepository sysUserRepository;

    /**
     * 登录接口
     */
    @PostMapping("/loginAction")
    @ResponseBody // 返回JSON数据给前端JS处理
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查询用户
        SysUser user = sysUserRepository.findByUsername(username);

        // 2. 判断用户是否存在
        if (user == null) {
            result.put("success", false);
            result.put("msg", "用户不存在");
            return result;
        }

        // 3. 判断密码 (实际项目中请使用 BCrypt 加密，这里演示明文比对)
        if (!user.getPassword().equals(password)) {
            result.put("success", false);
            result.put("msg", "密码错误");
            return result;
        }

        // 4. 判断状态 (0-待审核)
        if (user.getStatus() == 0) {
            result.put("success", false);
            result.put("msg", "账号审核中，请联系管理员");
            return result;
        }

        if (user.getStatus() == 2) {
            result.put("success", false);
            result.put("msg", "账号已被禁用");
            return result;
        }

        // 5. 登录成功
        result.put("success", true);
        result.put("msg", "登录成功");
        // 这里可以放入 token 或者 session
        return result;
    }

    /**
     * 注册接口
     */
    @PostMapping("/registerAction")
    @ResponseBody
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        // 1. 检查是否已存在
        if (sysUserRepository.findByUsername(username) != null) {
            result.put("success", false);
            result.put("msg", "用户名已存在");
            return result;
        }

        // 2. 创建新用户
        SysUser newUser = new SysUser();
        newUser.setUsername(username);
        newUser.setPassword(password); // 同样，建议后续改为加密存储
        newUser.setStatus(1); // 默认为0，待审核
        newUser.setCreateTime(new Date());

        // 3. 保存到数据库
        try {
            sysUserRepository.save(newUser);
            result.put("success", true);
            result.put("msg", "注册提交成功，请等待管理员审核");
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "注册失败: " + e.getMessage());
        }

        return result;
    }
}

