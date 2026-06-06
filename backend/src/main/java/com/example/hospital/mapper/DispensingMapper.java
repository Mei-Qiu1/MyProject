package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Dispensing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DispensingMapper extends BaseMapper<Dispensing> {

    @Select("SELECT COUNT(*) FROM drug_delivery WHERE DATE(create_time) = CURDATE()")
    long countTodayDispensing();

    @Select("SELECT COUNT(*) FROM drug_delivery WHERE status = 1")
    long countPendingDispensing();
}
