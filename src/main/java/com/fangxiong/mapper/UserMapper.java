package com.fangxiong.mapper;

import com.fangxiong.dto.LoginDTO;
import com.fangxiong.pojo.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(Long userId);
    List<User> selectAll();
    int insert(User user);
    int update(User user);
    int deleteById(Long userId);
    User selectByName(LoginDTO loginDTO);
    User selectByEmail(LoginDTO loginDTO);

    Integer selectCount();

    List<User> getUserList(@Param("email") String email, @Param("status") Integer status);

    // 封禁用户
    void banUser(@Param("id") Long id, @Param("banReason") String banReason);

    // 解封
    int unBanUser(@Param("id") Long id);
}
