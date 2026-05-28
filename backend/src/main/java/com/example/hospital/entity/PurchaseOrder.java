
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
 * 采购订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("purchase_order")
public class PurchaseOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long requestId;
    private Long supplierId;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDateTime deliveryDate;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
