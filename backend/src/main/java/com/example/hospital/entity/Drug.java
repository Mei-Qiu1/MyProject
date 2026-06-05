
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
 * 药品实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("drug")
public class Drug {

    // 在 Drug.java 中添加
    private Integer minStock;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String drugCode;
    private String drugName;
    private String spec;
    private String dosageForm;
    private String manufacturer;
    private String approvalNumber;
    private Long categoryId;
    private Long manageCategoryId;
    private String unit;
    private Integer isSpecial;
    private BigDecimal purchasePrice;
    private BigDecimal retailPrice;
    private BigDecimal wholesalePrice;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
