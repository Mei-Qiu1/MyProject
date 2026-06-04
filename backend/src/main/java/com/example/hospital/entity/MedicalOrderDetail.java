package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("medical_order_detail")
public class MedicalOrderDetail {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long drugId;
    
    private String drugName;
    
    private String spec;
    
    private Integer quantity;
    
    private String frequency;
    
    private String duration;
    
    private LocalDateTime createTime;
}
