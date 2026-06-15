package com.exchange.service.impl;

import com.exchange.Utils.BCryptPasswordUtil;
import com.exchange.Utils.CurrentHolder;
import com.exchange.Utils.DeleteFileUtil;
import com.exchange.Utils.MoveFileUtil;
import com.exchange.dto.LoginDTO;
import com.exchange.dto.LoginResult;
import com.exchange.dto.UserInfoChangeAuditDTO;
import com.exchange.dto.WalletDetailDTO;
import com.exchange.mapper.*;
import com.exchange.pojo.User;
import com.exchange.service.UserService;
import com.fangxiong.utils.json.JsonUtils;
import com.fangxiong.utils.redis.RedisUtils;
import com.exchange.vo.PageResult;
import com.exchange.vo.Result;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.exchange.constants.SystemConstants.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private PostMapper postMapper;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private CollectMapper collectMapper;

    @Resource
    private MoveFileUtil moveFileUtil;

    @Resource
    private DeleteFileUtil deleteFileUtil;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getUserPage(Integer pageNum, Integer pageSize, String email, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.getUserList(email, status);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        PageResult<User> pageResult = new PageResult<>();
        pageResult.setRows(pageInfo.getList());
        pageResult.setTotal(pageInfo.getTotal());
        return Result.success(pageResult);
    }

    @Override
    public Result banUser(Long id, String banReason) {
        User user = new User();
        user.setId(id);
        user.setRole(-1);
        user.setBanReason(banReason);
        userMapper.banUser(user.getId(), user.getBanReason());
        redisUtils.setStringValue(ACCOUNT_BANDED_KEY+id,"1");
        return Result.success("封禁成功");
    }

    @Override
    public Result unBanUser(Long id) {
        userMapper.unBanUser(id);
        redisUtils.remove(ACCOUNT_BANDED_KEY+id);
        return Result.success();
    }

    @Override
    public Result getCurrentUser(LoginResult currentUserInfo) {
        User user = userMapper.selectById(currentUserInfo.getId());
        currentUserInfo.setUsername(user.getUsername());
        currentUserInfo.setSchool(user.getSchool());
        currentUserInfo.setAvatarUrl(user.getAvatarUrl());
        return Result.success(currentUserInfo);
    }

    @Override
    public Result setSchool(Long id, Long school) {
        Long schoolId = redisUtils.getStringValue(SCHOOL_CHANGE_KEY + id, Long.class);
        if(schoolId!=null&&!schoolId.equals(school))
        {
            return Result.error("30天内只允许修改一次学校信息");
        }
        redisUtils.setStringValue(SCHOOL_CHANGE_KEY+id, school, CHANGE_SCHOOL_LIMIT_TIME);
        userMapper.setSchool(id, school);
        return Result.success();
    }

    @Override
    public Result updateInfo(Long id,String username,String avatarUrl, Long school) {
        Long schoolId = redisUtils.getStringValue(SCHOOL_CHANGE_KEY + id, Long.class);
        if(schoolId!=null&&!schoolId.equals(school))
        {
            return Result.error("30天内只允许修改一次学校信息");
        }
        if (username.length()>10){
            return Result.error("用户名长度不能超过10个字符");
        }
        UserInfoChangeAuditDTO userInfoChangeAuditDTO = redisUtils.getStringValue(REQUEST_INFO_CHANGE_KEY + id, UserInfoChangeAuditDTO.class);
        if(userInfoChangeAuditDTO!=null){
            return Result.error("请勿重复提交修改");
        }
        redisUtils.setStringValue(REQUEST_INFO_CHANGE_IGNORED_KEY+id, "1");
        redisUtils.setStringValue(SCHOOL_CHANGE_KEY+id, "1", CHANGE_SCHOOL_LIMIT_TIME);
        if (avatarUrl.isEmpty()){
            avatarUrl = null;
        }
        redisUtils.setStringValue(REQUEST_INFO_CHANGE_KEY+id, new UserInfoChangeAuditDTO(CurrentHolder.getCurrentUserInfo().getId(),username,avatarUrl,CurrentHolder.getCurrentUserInfo().getSchool(),school, null, 1, 0,LocalDateTime.now(),null, null),CHANGE_INFO_ADMIN_AUDIT_LIMIT_TIME);
        return Result.success();
    }

    @Override
    public Result infoIsChanged(Long id) {
        UserInfoChangeAuditDTO userInfoChangeAuditDTO = redisUtils.getStringValue(REQUEST_INFO_CHANGE_KEY + id, UserInfoChangeAuditDTO.class);
        String requestIsRejected = redisUtils.getStringValue(REQUEST_INFO_CHANGE_REJECTED_KEY + id, String.class);
        String requestIsIgnored = redisUtils.getStringValue(REQUEST_INFO_CHANGE_IGNORED_KEY + id, String.class);
        String requestIsSuccess = redisUtils.getStringValue(REQUEST_INFO_CHANGE_SUCCESS_KEY + id,String.class);
        if (userInfoChangeAuditDTO==null&&requestIsRejected!=null){
            redisUtils.remove(REQUEST_INFO_CHANGE_REJECTED_KEY+id);
            UserInfoChangeAuditDTO isRejected = new UserInfoChangeAuditDTO();
            isRejected.setChangedStatus(1);
            isRejected.setAuditStatus(2);
            isRejected.setRejectReason(requestIsRejected);
            return Result.success(isRejected);
        } else if (userInfoChangeAuditDTO== null&&requestIsIgnored!=null) {
            redisUtils.remove(REQUEST_INFO_CHANGE_IGNORED_KEY+id);
            UserInfoChangeAuditDTO isIgnored = new UserInfoChangeAuditDTO();
            isIgnored.setChangedStatus(1);
            isIgnored.setAuditStatus(-1);
            return Result.success(isIgnored);
        } else if (userInfoChangeAuditDTO == null &&requestIsSuccess!= null) {
            redisUtils.remove(REQUEST_INFO_CHANGE_SUCCESS_KEY+id);
            UserInfoChangeAuditDTO isSuccess = new UserInfoChangeAuditDTO();
            isSuccess.setChangedStatus(1);
            isSuccess.setAuditStatus(1);
            return Result.success(isSuccess);
        }
        return Result.success(userInfoChangeAuditDTO);
    }

    @Override
    public Result getSchoolList() {
        return Result.success(userMapper.selectSchoolList());
    }

    @Override
    public Result getInfoChangePage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        String prefix = "exchange:change:info:id:*";

        Set<String> keySet = stringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions()
                            .match(prefix)
                            .count(100)
                            .build()
            )) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            }
            return keys;
        });

        if (keySet == null || keySet.isEmpty()) {
            return Result.success(new PageResult<>(0, Collections.emptyList()));
        }

        List<UserInfoChangeAuditDTO> list = new ArrayList<>();
        for (String key : keySet) {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) continue;

            UserInfoChangeAuditDTO dto = JsonUtils.jsonToBean(json, UserInfoChangeAuditDTO.class);
            dto.setServerTime(LocalDateTime.now());
            if (dto.getOriginalSchool().equals(CurrentHolder.getCurrentUserInfo().getSchool())){
                list.add(dto);
            }
        }

        list.sort((a, b) -> b.getRequestTime().compareTo(a.getRequestTime()));

        int total = list.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<UserInfoChangeAuditDTO> pageList = Collections.emptyList();
        if (start < end) {
            pageList = list.subList(start, end);
        }
        return Result.success(new PageResult<>(total, pageList));
    }

    @Transactional
    @Override
    public Result auditInfoChange(UserInfoChangeAuditDTO userInfoChangeAuditDTO) {
        boolean needToMoveBackFile = false;
        try {
            if(userInfoChangeAuditDTO.getAuditStatus()==2){
                redisUtils.setStringValue(REQUEST_INFO_CHANGE_REJECTED_KEY+userInfoChangeAuditDTO.getId(), userInfoChangeAuditDTO.getRejectReason());
                redisUtils.remove(REQUEST_INFO_CHANGE_IGNORED_KEY+userInfoChangeAuditDTO.getId());
                redisUtils.remove(REQUEST_INFO_CHANGE_KEY+userInfoChangeAuditDTO.getId());
                return Result.success();
            }
            String realAvatarUrl = null;
            if (userInfoChangeAuditDTO.getAvatarUrl()!=null&&!userInfoChangeAuditDTO.getAvatarUrl().isEmpty()&&!userInfoChangeAuditDTO.getAvatarUrl().equals(EXCHANGE_DEFAULT_AVATAR_URL)){
                realAvatarUrl  = moveFileUtil.moveTempToReal(userInfoChangeAuditDTO.getAvatarUrl());
                moveFileUtil.moveRealToTemp(userMapper.selectAvatarUrlById(userInfoChangeAuditDTO.getId()));
                needToMoveBackFile = true;
            }
            if (!userInfoChangeAuditDTO.getSchool().equals(userInfoChangeAuditDTO.getOriginalSchool())){
                goodsMapper.changeUserGoodsSchool(userInfoChangeAuditDTO.getId(), userInfoChangeAuditDTO.getSchool());
                collectMapper.deleteUserInfoById(userInfoChangeAuditDTO.getId());
            }
            userMapper.updateInfo(userInfoChangeAuditDTO.getId(), userInfoChangeAuditDTO.getUsername(), realAvatarUrl, userInfoChangeAuditDTO.getSchool());
            redisUtils.setStringValue(REQUEST_INFO_CHANGE_SUCCESS_KEY+userInfoChangeAuditDTO.getId(), "1");
            redisUtils.remove(REQUEST_INFO_CHANGE_IGNORED_KEY+userInfoChangeAuditDTO.getId());
            redisUtils.remove(REQUEST_INFO_CHANGE_KEY+userInfoChangeAuditDTO.getId());
        } catch (Exception e) {
            log.info("审核信息修改失败");
            if (needToMoveBackFile){
                moveFileUtil.moveTempToReal(userInfoChangeAuditDTO.getAvatarUrl());
            }
            return Result.error("审核信息修改失败");
        }
        return Result.success();
    }

    @Override
    public Result getExcWalletBalance() {
        BigDecimal userBalance = userMapper.getUserBalance(CurrentHolder.getCurrentUserInfo().getId());
        return Result.success(userBalance);
    }

    @Override
    public Integer getUnresolvedOrdersCount() {
        return userMapper.getUnresolvedOrdersCount();
    }

    @Override
    public Result getUserEXCWalletList(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<WalletDetailDTO> walletDetailDTOS = userMapper.getUserEXCWalletList(CurrentHolder.getCurrentUserInfo().getId());
        PageInfo<WalletDetailDTO> pageInfo = new PageInfo<>(walletDetailDTOS);
        PageResult<WalletDetailDTO> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return Result.success(pageResult);
    }

    @Override
    public Result changeUserPwd(LoginDTO loginDTO) {
        User user = userMapper.findByEmail(loginDTO.getEmail());
        if (user==null){
            return Result.error("用户不存在");
        }else if (!user.getId().equals(CurrentHolder.getCurrentUserInfo().getId())){
            return Result.error("请勿进行非法操作！");
        }

        if (redisUtils.getStringValue(EMAIL_KEY+loginDTO.getEmail(), String.class)==null){
            return Result.error("验证码无效");
        }else {
            if (!redisUtils.getStringValue(EMAIL_KEY+loginDTO.getEmail(), String.class).equals(loginDTO.getCode())){
                return Result.error("验证码错误");
            }
            userMapper.updatePassword(BCryptPasswordUtil.encode(loginDTO.getPassword()),CurrentHolder.getCurrentUserInfo().getId());
        }

        return Result.success();
    }

    @Transactional
    @Override
    public Result deleteUserAccount(String email, String code) {
        User user = userMapper.findByEmail(email);
        if (user==null){
            return Result.error("用户不存在");
        }else if (!user.getId().equals(CurrentHolder.getCurrentUserInfo().getId())){
            return Result.error("请勿进行非法操作！");
        }
        if (redisUtils.getStringValue(EMAIL_KEY+email, String.class)==null){
            return Result.error("验证码无效");
        }else {
            if (!redisUtils.getStringValue(EMAIL_KEY+email, String.class).equals(code)){
                return Result.error("验证码错误");
            }
            BigDecimal balance = userMapper.selectBalanceById(CurrentHolder.getCurrentUserInfo().getId());
            if (balance.compareTo(BigDecimal.ZERO)>0){
                return Result.error("账户余额不为0，请先清空余额");
            }
            if (!user.getAvatarUrl().equals(EXCHANGE_DEFAULT_AVATAR_URL)){
                deleteFileUtil.deleteFile(user.getAvatarUrl());
            }
            goodsMapper.selectUserGoodsImages(CurrentHolder.getCurrentUserInfo().getId()).forEach(deleteFileUtil::deleteFile);
            postMapper.selectUserPostImages(CurrentHolder.getCurrentUserInfo().getId()).forEach(deleteFileUtil::deleteFile);
            ordersMapper.selectUserOrdersImages(CurrentHolder.getCurrentUserInfo().getId()).forEach(deleteFileUtil::deleteFile);
            collectMapper.deleteUserInfoById(CurrentHolder.getCurrentUserInfo().getId());
            goodsMapper.deleteUserInfoById(CurrentHolder.getCurrentUserInfo().getId());
            ordersMapper.deleteUserInfoById(CurrentHolder.getCurrentUserInfo().getId());
            postMapper.deleteUserInfoById(CurrentHolder.getCurrentUserInfo().getId());
            postMapper.deleteUserCommentInfoById(CurrentHolder.getCurrentUserInfo().getId());
            userMapper.deleteUserWalletUseLog(CurrentHolder.getCurrentUserInfo().getId());
            userMapper.deleteById(CurrentHolder.getCurrentUserInfo().getId());
            return Result.success();
        }
    }
}
