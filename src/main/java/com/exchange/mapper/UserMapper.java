package com.exchange.mapper;

import com.exchange.dto.LoginDTO;
import com.exchange.pojo.User;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    User selectById(Long userId);
    List<User> selectAll();
    int insert(User user);
    int update(User user);
    int deleteById(Long userId);
    User selectByName(LoginDTO loginDTO);
    User selectByEmail(LoginDTO loginDTO);

    Long selectCount();

    List<User> getUserList(@Param("email") String email, @Param("status") Integer status);

    // 封禁用户
    void banUser(@Param("id") Long id, @Param("banReason") String banReason);

    // 解封
    int unBanUser(@Param("id") Long id);

    void setSchool(Long id, Long school);

    void updateInfo(Long id, String username, String avatarUrl, Long school);

    @MapKey("id")
    Map<Integer, Map<String, Object>> selectSchoolList();

    Long selectSchoolId(String school);
}
