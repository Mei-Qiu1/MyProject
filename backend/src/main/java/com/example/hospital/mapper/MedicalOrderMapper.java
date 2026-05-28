package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.MedicalOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MedicalOrderMapper extends BaseMapper<MedicalOrder> {
}