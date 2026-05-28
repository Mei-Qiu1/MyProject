
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 供应商实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("supplier")
public class Supplier {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String supplierName;
    private String supplierCode;
    private String contactName;
    private String phone;
    private String address;
    private String qualificationNo;
    private LocalDateTime qualificationExpireDate;
    private String bankAccount;
    private Integer cooperationStatus;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
