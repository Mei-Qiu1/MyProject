package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.InventoryRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryRecordMapper extends BaseMapper<InventoryRecord> {

    Long sumOutQuantity(@Param("startDate") String startDate, @Param("endDate") String endDate);

    Long countDistinctDrugs(@Param("startDate") String startDate, @Param("endDate") String endDate);

    Long countDistinctDepartments(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT d.drug_name AS drugName, COALESCE(SUM(r.quantity), 0) AS quantity FROM inventory_record r " +
            "LEFT JOIN drug d ON r.drug_id = d.id WHERE r.type = 2 " +
            "GROUP BY r.drug_id ORDER BY quantity DESC LIMIT #{limit}")
    List<Map<String, Object>> getDrugConsumptionRanking(@Param("limit") Integer limit);

    @Select("SELECT r.department AS departmentName, COALESCE(SUM(r.quantity), 0) AS quantity FROM inventory_record r " +
            "WHERE r.type = 2 AND r.department IS NOT NULL " +
            "GROUP BY r.department ORDER BY quantity DESC")
    List<Map<String, Object>> getDepartmentConsumptionStats();

    @Select("SELECT DATE_FORMAT(r.create_time, '%Y-%m') AS month, COALESCE(SUM(r.quantity), 0) AS quantity " +
            "FROM inventory_record r WHERE r.type = 2 " +
            "GROUP BY DATE_FORMAT(r.create_time, '%Y-%m') ORDER BY month DESC LIMIT 6")
    List<Map<String, Object>> getConsumptionTrend();
}
