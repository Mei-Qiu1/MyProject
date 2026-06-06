
package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 药品Mapper接口
 */
@Mapper
public interface DrugMapper extends BaseMapper<Drug> {
    
    @Select("SELECT COUNT(*) FROM drug WHERE is_special = 1")
    Integer countSpecialDrugs();

    @Select("SELECT * FROM drug WHERE is_special = 0 ORDER BY id DESC LIMIT #{limit}")
    List<Drug> selectCommonDrugs(@Param("limit") Integer limit);
}
