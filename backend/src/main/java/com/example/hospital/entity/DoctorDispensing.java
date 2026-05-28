package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("doctor_dispensing")
public class DoctorDispensing {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String prescriptionNo;    // 处方号
    private String patientName;       // 患者姓名
    private String department;        // 科室
    private String doctorName;        // 医生姓名
    private LocalDateTime createTime; // 调配时间
}