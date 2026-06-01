package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {
    
    @Select("SELECT COUNT(*) FROM prescription WHERE status = #{status}")
    Integer countByStatus(@Param("status") Integer status);
    
    @Select("SELECT * FROM prescription WHERE status = #{status} ORDER BY create_time DESC LIMIT #{limit}")
    List<Prescription> selectByStatus(@Param("status") Integer status, @Param("limit") Integer limit);
    
    @Select("SELECT COUNT(*) FROM prescription WHERE status = 3 AND DATE(create_time) = CURDATE()")
    Integer countTodayDispensed();
}