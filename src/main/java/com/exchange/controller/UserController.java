package com.exchange.controller;

import com.exchange.Utils.CurrentHolder;
import com.exchange.anno.RequiredAdmin;
import com.exchange.dto.LoginDTO;
import com.exchange.dto.UserInfoChangeAuditDTO;
import com.exchange.service.UserService;
import com.exchange.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.security.core.parameters.P;
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

    @RequiredAdmin
    @PostMapping("/info-change/audit")
    public Result auditInfoChange(@RequestBody UserInfoChangeAuditDTO userInfoChangeAuditDTO) {
        return userService.auditInfoChange(userInfoChangeAuditDTO);
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

    @RequiredAdmin
    @GetMapping("/info-change/page")
    public Result getInfoChangePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return userService.getInfoChangePage(pageNum, pageSize);
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

    @PostMapping("/setSchool")
    public Result setSchool(@RequestBody Map<String, String> map) {
        String school = map.get("school");
        if(school==null || school.isEmpty()){
            return Result.error("请选择学校");
        }
        Long schoolId = Long.parseLong(school);
        return userService.setSchool(CurrentHolder.getCurrentUserInfo().getId(), schoolId);
    }

    @PostMapping("/updateInfo")
    public Result updateInfo(@RequestBody Map<String, Object> map) {
        String username = (String) map.get("changedUsername");
        String avatarUrl = (String) map.get("avatarUrl");
        Long school = Long.parseLong(map.get("school").toString());
        return userService.updateInfo(CurrentHolder.getCurrentUserInfo().getId(), username, avatarUrl ,school);
    }

    @GetMapping("/infoIsChanged")
    public Result infoIsChanged() {
        return userService.infoIsChanged(CurrentHolder.getCurrentUserInfo().getId());
    }

    @GetMapping("/schoolList")
    public Result getSchoolList() {
        return userService.getSchoolList();
    }

    @GetMapping("/getExcWalletBalance")
    public Result getExcWalletBalance() {
        return userService.getExcWalletBalance();
    }

    @GetMapping("/getUnresolvedOrdersCount")
    public Result getUnresolvedOrdersCount() {
        return Result.success(userService.getUnresolvedOrdersCount());
    }

    @GetMapping("/getUserEXCWalletList")
    public Result getUserEXCWalletList(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return userService.getUserEXCWalletList(page, pageSize);
    }

    @PostMapping("/changeUserPwd")
    public Result changeUserPwd(@RequestBody LoginDTO loginDTO) {
        return userService.changeUserPwd(loginDTO);
    }

    @PostMapping("/deleteUserAccount")
    public Result deleteUserAccount(@RequestBody Map<String, String> map) {
        String email = map.get("email");
        String code = map.get("code");
        return userService.deleteUserAccount(email, code);
    }

}