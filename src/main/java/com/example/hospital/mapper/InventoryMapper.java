
package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 库存Mapper接口
 */
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {
    
    @Select("SELECT * FROM inventory WHERE expire_date IS NOT NULL AND expire_date <= DATE_ADD(NOW(), INTERVAL #{days} DAY) AND quantity > 0 ORDER BY expire_date ASC")
    List<Inventory> findExpiringDrugs(@Param("days") Integer days);
    
    @Select("SELECT * FROM inventory WHERE quantity <= #{threshold} AND quantity > 0 ORDER BY quantity ASC")
    List<Inventory> findLowStockDrugs(@Param("threshold") Integer threshold);
    
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory WHERE drug_id = #{drugId} AND quantity > 0")
    Integer getTotalQuantityByDrugId(Long drugId);
}
