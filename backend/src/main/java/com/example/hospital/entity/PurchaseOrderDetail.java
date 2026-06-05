package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("purchase_order_detail")
public class PurchaseOrderDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long drugId;
    private String drugName;
    private String spec;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private Integer receivedQuantity;
}