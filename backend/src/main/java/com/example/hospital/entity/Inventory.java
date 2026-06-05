package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // 非数据库字段，用于连表查询
    @TableField(exist = false)
    private String drugCode;

    @TableField(exist = false)
    private String drugName;

    @TableField(exist = false)
    private String spec;

    @TableField(exist = false)
    private String warehouseName;

    // 在 Inventory.java 中添加
    @TableField(exist = false)
    private String unit;      // 药品单位

    // 前端使用 currentStock，实际对应 quantity
    public Integer getCurrentStock() {
        return getQuantity();
    }

}