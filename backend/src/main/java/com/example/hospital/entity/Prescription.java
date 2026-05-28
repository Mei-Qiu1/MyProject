
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 处方实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("prescription")
public class Prescription {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String prescriptionNo;
    private String patientName;
    private String patientId;
    private Integer patientAge;
    private String patientSex;
    private String department;
    private String doctorName;
    private Long doctorId;
    private Integer type;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
