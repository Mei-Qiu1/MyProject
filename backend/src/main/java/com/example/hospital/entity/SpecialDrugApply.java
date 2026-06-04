package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("special_drug_apply")
public class SpecialDrugApply {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String applyNo;
    
    private Long drugId;
    
    private String drugName;
    
    private Integer quantity;
    
    private String prescriptionNo;
    
    private String purpose;
    
    private Integer status;
    
    private String user1;
    
    private String user2;
    
    private Long createBy;
    
    private LocalDateTime createTime;
}
