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
@TableName("inventory_transfer")
public class InventoryTransfer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String transferNo;
    private Long fromInventoryId;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private Integer quantity;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
}