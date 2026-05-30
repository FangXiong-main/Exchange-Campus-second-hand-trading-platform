package com.fangxiong.mapper;

import com.fangxiong.pojo.Collect;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CollectMapper {
    Collect selectById(Integer collectId);
    List<Collect> selectAll();
    int insert(Collect collect);
    int deleteById(Integer collectId);
}