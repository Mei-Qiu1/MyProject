
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统日志实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_log")
public class SysLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private String ipAddress;
    private String userAgent;
    private Integer status;
    private String errorMessage;
    private LocalDateTime createTime;
}
