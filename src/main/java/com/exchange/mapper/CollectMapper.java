package com.exchange.mapper;

import com.exchange.pojo.Collect;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CollectMapper {
    void deleteUserInfoById(Long id);
}