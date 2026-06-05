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
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("purchase_request")
public class PurchaseRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String requestNo;
    private Long planId;
    private Long supplierId;
    private Integer status;
    private String auditComment;
    private Long auditBy;
    private LocalDateTime auditTime;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 在 PurchaseRequest.java 中添加
    @TableField(exist = false)
    private BigDecimal totalAmount;

    // 非数据库字段，用于关联查询结果
    @TableField(exist = false)
    private String supplierName;

    @TableField(exist = false)
    private String planName;

}