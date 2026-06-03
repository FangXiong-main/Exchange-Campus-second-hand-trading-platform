package com.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoChangeAuditDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private Long school;
    private String originalUsername;
    private String email;
    private Integer changedStatus;
    private Integer auditStatus;  //0= 待审核 1=审核通过 2=审核未通过
    private LocalDateTime requestTime;
    private LocalDateTime serverTime;
    private String rejectReason;
}
