package com.exchange.mapper;

import com.exchange.dto.LoginDTO;
import com.exchange.dto.WalletDetailDTO;
import com.exchange.pojo.User;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    User selectById(Long userId);
    List<User> selectAll();
    int insert(User user);
    int update(User user);
    void deleteById(Long userId);
    User selectByName(LoginDTO loginDTO);
    User selectByEmail(LoginDTO loginDTO);

    Long selectCount(Long school);

    List<User> getUserList(@Param("email") String email, @Param("status") Integer status);

    // 封禁用户
    void banUser(@Param("id") Long id, @Param("banReason") String banReason);

    // 解封
    int unBanUser(@Param("id") Long id);

    void setSchool(@Param("id") Long id,@Param("school") Long school);

    void updateInfo(@Param("id") Long id,@Param("username") String username, @Param("avatarUrl") String avatarUrl,@Param("school") Long school);

    @MapKey("id")
    Map<Integer, Map<String, Object>> selectSchoolList();

    Long selectSchoolId(String school);

    BigDecimal getUserBalance(Long id);

    Integer getUnresolvedOrdersCount();

    BigDecimal selectBalanceById(Long id);

    Long findSchoolAdminId(Long schoolId);

    void updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);

    void addNewWalletUseLog(@Param("userId") Long userId, @Param("type") Integer type,@Param("price") BigDecimal price, @Param("createTime") LocalDateTime createTime);

    List<WalletDetailDTO> getUserEXCWalletList(Long id);

    User findByEmail(String email);

    void updatePassword(@Param("password") String password,@Param("id") Long  id);

    void deleteUserWalletUseLog(Long id);
}
