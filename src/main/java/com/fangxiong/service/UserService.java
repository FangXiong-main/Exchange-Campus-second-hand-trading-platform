package com.fangxiong.service;

import com.fangxiong.dto.LoginResult;
import com.fangxiong.dto.UserInfoChangeAuditDTO;
import com.fangxiong.vo.Result;

public interface UserService {
    Result getUserPage(Integer pageNum, Integer pageSize, String email, Integer status);
    Result banUser(Long id, String banReason);
    Result unBanUser(Long id);

    Result getCurrentUser(LoginResult currentUserInfo);

    Result setSchool(Long id, Long school);

    Result updateInfo(Long id, String username, String avatarUrl, Long school);

    Result infoIsChanged(Long id);

    Result getSchoolList();

    Result getInfoChangePage(Integer pageNum, Integer pageSize);

    Result auditInfoChange(UserInfoChangeAuditDTO userInfoChangeAuditDTO);
}
