
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 仓库实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("warehouse")
public class Warehouse {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String warehouseName;
    private String warehouseCode;
    private Integer type;
    private String location;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
