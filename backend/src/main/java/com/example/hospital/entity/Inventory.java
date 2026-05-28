
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
 * 库存实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("inventory")
public class Inventory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long drugId;
    private String batchNo;
    private LocalDateTime productionDate;
    private LocalDateTime expireDate;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Long warehouseId;
    private Long locationId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
