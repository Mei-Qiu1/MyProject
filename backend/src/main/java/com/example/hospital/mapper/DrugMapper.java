
package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 药品Mapper接口
 */
@Mapper
public interface DrugMapper extends BaseMapper<Drug> {
    
    @Select("SELECT COUNT(*) FROM drug WHERE is_special = 1")
    Integer countSpecialDrugs();
}
