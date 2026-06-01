package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.SpecialDrugRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SpecialDrugRecordMapper extends BaseMapper<SpecialDrugRecord> {
    
    @Select("SELECT COUNT(*) FROM special_drug_record WHERE DATE(create_time) = CURDATE()")
    Integer countTodayRecords();
    
    @Select("SELECT * FROM special_drug_record WHERE DATE(create_time) = CURDATE() ORDER BY create_time DESC LIMIT #{limit}")
    List<SpecialDrugRecord> selectTodayRecords(Integer limit);
}