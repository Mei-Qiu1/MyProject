
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
 * 库存流水实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("inventory_record")
public class InventoryRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inventoryId;
    private Long drugId;
    private String batchNo;
    private Integer type;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String sourceNo;
    private Long warehouseId;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
}
