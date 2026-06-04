package com.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String images;
    private String username;
    private String avatarUrl;
    private LocalDateTime createTime;
}
