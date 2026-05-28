
package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    
    @Select("SELECT DISTINCT m.* FROM sys_menu m LEFT JOIN sys_role_menu rm ON m.id = rm.menu_id LEFT JOIN sys_role r ON rm.role_id = r.id WHERE r.role_code = #{roleCode} ORDER BY m.sort_order ASC")
    List<Menu> findMenusByRole(String roleCode);
}
