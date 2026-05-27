
package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_menu")
public class Menu {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String menuName;
    private String path;
    private String component;
    private Long parentId;
    private String icon;
    private Integer sortOrder;
    private Integer type;
    private String permission;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
