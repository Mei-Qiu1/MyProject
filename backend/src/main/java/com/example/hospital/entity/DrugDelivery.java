package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 药品配送单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("drug_delivery")
public class DrugDelivery {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deliveryNo;
    private Long orderId;
    private String patientId;
    private String patientName;
    private String department;
    private String bedNo;
    private Integer status;
    private LocalDateTime deliveryTime;
    private String signer;
    private LocalDateTime signTime;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    private List<DeliveryDetail> details;
}