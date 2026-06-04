
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 医嘱实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("medical_order")
public class MedicalOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private String patientId;
    private String patientName;
    private String department;
    private String bedNo;
    private Long doctorId;
    private String doctorName;
    private Integer type;
    private Integer status;
    private LocalDateTime orderTime;
    private LocalDateTime executeTime;
    private String remark;
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private List<MedicalOrderDetail> details;
}
