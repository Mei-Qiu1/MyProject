package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    // ========== 原有分页连表查询 ==========
    @Select("SELECT i.*, " +
            "d.drug_code AS drug_code, " +
            "d.drug_name AS drug_name, " +
            "d.spec AS spec, " +
            "w.warehouse_name AS warehouse_name " +
            "FROM inventory i " +
            "LEFT JOIN drug d ON i.drug_id = d.id " +
            "LEFT JOIN warehouse w ON i.warehouse_id = w.id " +
            "WHERE 1=1 " +
            "AND (#{warehouseId} IS NULL OR i.warehouse_id = #{warehouseId}) " +
            "AND (#{keyword} IS NULL OR d.drug_name LIKE CONCAT('%', #{keyword}, '%') OR i.batch_no LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY i.create_time DESC")
    IPage<Inventory> selectPageByCondition(Page<Inventory> page,
                                           @Param("keyword") String keyword,
                                           @Param("warehouseId") Long warehouseId);

    // ========== 低库存预警（聚合查询，不区分批次） ==========
    @Select("<script>" +
            "SELECT " +
            "   w.warehouse_name AS warehouseName, " +
            "   d.drug_code AS drugCode, " +
            "   d.drug_name AS drugName, " +
            "   d.spec, " +
            "   d.unit, " +
            "   COALESCE(SUM(i.quantity), 0) AS currentStock, " +
            "   d.min_stock AS minStock, " +
            "   d.id AS drugId " +
            "FROM drug d " +
            "LEFT JOIN inventory i ON d.id = i.drug_id AND i.expire_date > NOW() AND i.quantity > 0 " +
            "LEFT JOIN warehouse w ON i.warehouse_id = w.id " +
            "WHERE d.min_stock IS NOT NULL " +
            "<if test='warehouseId != null'> AND i.warehouse_id = #{warehouseId} </if>" +
            "GROUP BY d.id, i.warehouse_id, w.warehouse_name " +
            "HAVING currentStock &lt;= d.min_stock " +
            "ORDER BY w.warehouse_name, d.drug_name" +
            "</script>")
    List<Map<String, Object>> findLowStockByWarehouse(@Param("warehouseId") Long warehouseId);

    // ========== 效期预警（支持仓库筛选、已过期/即将过期） ==========
    // 修改：为 batch_no 和 expire_date 添加驼峰别名，便于前端直接使用
    @Select("<script>" +
            "SELECT i.id, i.drug_id, " +
            "   i.batch_no AS batchNo, " +              // 添加别名
            "   i.quantity, " +
            "   i.expire_date AS expireDate, " +        // 添加别名
            "   d.drug_code AS drugCode, " +
            "   d.drug_name AS drugName, " +
            "   d.spec, d.unit, " +
            "   w.warehouse_name AS warehouseName, " +
            "   w.id AS warehouseId " +
            "FROM inventory i " +
            "LEFT JOIN drug d ON i.drug_id = d.id " +
            "LEFT JOIN warehouse w ON i.warehouse_id = w.id " +
            "WHERE i.expire_date IS NOT NULL " +
            "AND i.quantity > 0 " +
            "<if test='includeExpired == true'>" +
            "   AND i.expire_date &lt;= DATE_ADD(NOW(), INTERVAL #{days} DAY) " +
            "</if>" +
            "<if test='includeExpired == false'>" +
            "   AND i.expire_date &gt; NOW() " +
            "   AND i.expire_date &lt;= DATE_ADD(NOW(), INTERVAL #{days} DAY) " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND (d.drug_name LIKE CONCAT('%', #{keyword}, '%') OR i.batch_no LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='warehouseId != null'> AND i.warehouse_id = #{warehouseId} </if>" +
            "ORDER BY i.expire_date ASC" +
            "</script>")
    List<Map<String, Object>> findExpiringDrugs(@Param("days") Integer days,
                                                @Param("keyword") String keyword,
                                                @Param("includeExpired") Boolean includeExpired,
                                                @Param("warehouseId") Long warehouseId);

    // ========== 其他原有方法 ==========
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM inventory WHERE drug_id = #{drugId} AND quantity > 0")
    Integer getTotalQuantityByDrugId(Long drugId);

    @Select("SELECT COUNT(*) FROM inventory WHERE quantity <= 10 AND quantity > 0")
    Integer countLowStock();

    @Select("SELECT COUNT(*) FROM inventory WHERE expire_date <= DATE_ADD(NOW(), INTERVAL 30 DAY) AND quantity > 0")
    Integer countExpiring();

    @Select("SELECT i.*, d.drug_name, d.spec FROM inventory i LEFT JOIN drug d ON i.drug_id = d.id WHERE i.quantity <= 10 AND i.quantity > 0 ORDER BY i.quantity ASC LIMIT #{limit}")
    List<Inventory> selectLowStock(@Param("limit") Integer limit);

    @Select("SELECT i.*, d.drug_name, d.spec FROM inventory i LEFT JOIN drug d ON i.drug_id = d.id WHERE i.expire_date <= DATE_ADD(NOW(), INTERVAL 30 DAY) AND i.quantity > 0 ORDER BY i.expire_date ASC LIMIT #{limit}")
    List<Inventory> selectExpiring(@Param("limit") Integer limit);

    @Select("SELECT i.*, d.drug_name, d.spec FROM inventory i LEFT JOIN drug d ON i.drug_id = d.id WHERE d.is_special = 1 AND i.quantity > 0")
    List<Inventory> selectSpecialDrugInventory();

    @Select("SELECT COUNT(*) FROM inventory i LEFT JOIN drug d ON i.drug_id = d.id WHERE d.is_special = 1 AND i.quantity <= 10 AND i.quantity > 0")
    Integer countSpecialLowStock();

    @Select("SELECT i.id, i.drug_id, i.batch_no, i.quantity, i.expire_date, " +
            "d.drug_code, d.drug_name, d.spec, d.unit " +
            "FROM inventory i " +
            "LEFT JOIN drug d ON i.drug_id = d.id " +
            "WHERE i.quantity <= #{threshold} AND i.quantity > 0 " +
            "ORDER BY i.quantity ASC")
    List<Inventory> findLowStockDrugs(@Param("threshold") Integer threshold);

    @Select("SELECT " +
            "d.id AS drugId, " +
            "d.drug_code AS drugCode, " +
            "d.drug_name AS drugName, " +
            "d.spec, " +
            "d.unit, " +
            "d.min_stock AS threshold, " +
            "COALESCE(SUM(i.quantity), 0) AS totalStock " +
            "FROM drug d " +
            "LEFT JOIN inventory i ON d.id = i.drug_id AND i.quantity > 0 " +
            "WHERE d.min_stock IS NOT NULL " +
            "GROUP BY d.id " +
            "HAVING totalStock <= d.min_stock " +
            "ORDER BY totalStock ASC")
    List<Map<String, Object>> findLowStockSummary();
}