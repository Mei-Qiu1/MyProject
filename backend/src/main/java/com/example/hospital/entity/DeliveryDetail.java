package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配送明细表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("delivery_detail")
public class DeliveryDetail {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long deliveryId;
    private Long drugId;
    private String drugName;
    private String spec;
    private Integer quantity;
    private String frequency;
    private String duration;
}