
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 特殊药品使用记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("special_drug_record")
public class SpecialDrugRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long drugId;
    private String batchNo;
    private String prescriptionNo;
    private Integer quantity;
    private BigDecimal amount;
    private String purpose;
    private String user1;
    private String user2;
    private String recycleStatus;
    private String recycleNo;
    private Long warehouseId;
    private LocalDateTime createTime;
}
