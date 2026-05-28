
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 采购申请实体类
 */
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
}
