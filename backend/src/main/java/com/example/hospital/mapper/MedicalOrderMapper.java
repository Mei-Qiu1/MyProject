package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.MedicalOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicalOrderMapper extends BaseMapper<MedicalOrder> {
    
    @Select("SELECT COUNT(*) FROM medical_order WHERE DATE(order_time) = #{date} AND doctor_id = #{doctorId}")
    Integer countByDateAndDoctor(@Param("date") String date, @Param("doctorId") Long doctorId);
    
    @Select("SELECT COUNT(*) FROM medical_order WHERE status = 0 AND doctor_id = #{doctorId}")
    Integer countPendingDispensing(@Param("doctorId") Long doctorId);
    
    @Select("SELECT COUNT(DISTINCT patient_id) FROM medical_order WHERE DATE(order_time) = CURDATE() AND doctor_id = #{doctorId}")
    Integer countTodayPatientsByDoctor(@Param("doctorId") Long doctorId);
    
    @Select("SELECT * FROM medical_order WHERE status = 0 AND doctor_id = #{doctorId} ORDER BY order_time DESC LIMIT #{limit}")
    List<MedicalOrder> selectPendingByDoctor(@Param("doctorId") Long doctorId, @Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM medical_order WHERE DATE(order_time) = CURDATE()")
    long countTodayOrders();

    @Select("SELECT COUNT(DISTINCT patient_id) FROM medical_order WHERE DATE(order_time) = CURDATE()")
    long countTodayPatients();

    @Select("SELECT * FROM medical_order WHERE DATE(order_time) = CURDATE() ORDER BY order_time DESC LIMIT #{limit}")
    List<MedicalOrder> selectTodayOrders(@Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM medical_order WHERE DATE(order_time) = CURDATE() AND doctor_id = #{doctorId}")
    long countTodayOrdersByDoctor(@Param("doctorId") Long doctorId);
}