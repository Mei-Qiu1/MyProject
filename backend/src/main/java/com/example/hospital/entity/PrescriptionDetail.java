
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 处方明细实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("prescription_detail")
public class PrescriptionDetail {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long prescriptionId;
    private Long drugId;
    private String drugName;
    private String spec;
    private Integer quantity;
    @TableField("usage_info")
    private String usage;
    private BigDecimal price;
    private BigDecimal amount;
}
