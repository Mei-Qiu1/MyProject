package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.DispensingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DispensingRecordMapper extends BaseMapper<DispensingRecord> {
    
    @Select("SELECT pr.prescription_no as prescriptionNo, pr.patient_name as patientName, " +
            "u.real_name as dispenser, pr.update_time as createTime " +
            "FROM prescription pr " +
            "LEFT JOIN sys_user u ON pr.doctor_id = u.id " +
            "WHERE pr.status IN (3, 4) " +
            "ORDER BY pr.update_time DESC LIMIT 10")
    List<Map<String, Object>> selectRecentRecords();
}
