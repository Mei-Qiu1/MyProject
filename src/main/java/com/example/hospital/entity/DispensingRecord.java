
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 调配记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("dispensing_record")
public class DispensingRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long prescriptionId;
    private Long detailId;
    private String batchNo;
    private Integer quantity;
    private Long pharmacyId;
    private Long createBy;
    private LocalDateTime createTime;
}
