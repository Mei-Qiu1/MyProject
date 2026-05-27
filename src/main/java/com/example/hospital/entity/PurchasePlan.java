
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 采购计划实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("purchase_plan")
public class PurchasePlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String planNo;
    private String planName;
    private Integer planType;
    private LocalDateTime planDate;
    private LocalDateTime executeDate;
    private Integer status;
    private String remark;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
