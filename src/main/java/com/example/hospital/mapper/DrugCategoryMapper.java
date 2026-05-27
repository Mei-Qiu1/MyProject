package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.DrugCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DrugCategoryMapper extends BaseMapper<DrugCategory> {
}