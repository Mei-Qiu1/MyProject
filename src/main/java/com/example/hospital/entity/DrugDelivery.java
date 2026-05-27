
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 药品配送实体类
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
    private String department;
    private Integer status;
    private LocalDateTime deliveryTime;
    private String signer;
    private Long createBy;
    private LocalDateTime createTime;
}
