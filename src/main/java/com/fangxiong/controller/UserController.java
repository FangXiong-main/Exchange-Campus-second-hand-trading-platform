package com.fangxiong.controller;

import com.fangxiong.Utils.CurrentHolder;
import com.fangxiong.Utils.DetectIsAdmin;
import com.fangxiong.anno.RequiredAdmin;
import com.fangxiong.dto.LoginDTO;
import com.fangxiong.service.UserService;
import com.fangxiong.vo.Result;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/current")
    public Result getCurrentUser() {
        return userService.getCurrentUser(CurrentHolder.getCurrentUserInfo());
    }


    // 全部用户（分页 + 邮箱搜索）
    @RequiredAdmin
    @GetMapping("/page")
    public Result getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status  // 状态筛选：0正常 -1封禁
    ) {
        return userService.getUserPage(pageNum, pageSize, email, status);
    }

    // 封禁用户
    @RequiredAdmin
    @PostMapping("/ban")
    public Result banUser(@RequestBody Map<String, Object> map) {
        Integer id = (Integer) map.get("id");
        String banReason = (String) map.get("banReason");
        return userService.banUser((long)id, banReason);
    }

    // 解封用户
    @RequiredAdmin
    @PostMapping("/unban")
    public Result unBanUser(@RequestBody Map<String, Long> map) {
        return userService.unBanUser(map.get("id"));
    }

}