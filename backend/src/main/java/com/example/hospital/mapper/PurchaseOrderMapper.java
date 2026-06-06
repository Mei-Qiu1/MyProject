package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    IPage<PurchaseOrder> selectPageWithNames(IPage<PurchaseOrder> page,
                                             @Param("keyword") String keyword,
                                             @Param("status") Integer status);

    @Select("SELECT po.*, s.supplier_name AS supplier_name " +
            "FROM purchase_order po " +
            "LEFT JOIN supplier s ON po.supplier_id = s.id " +
            "WHERE po.id = #{id}")
    PurchaseOrder selectByIdWithNames(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM purchase_order WHERE status = #{status}")
    long countByStatus(@Param("status") Integer status);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM purchase_order WHERE DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    java.math.BigDecimal sumAmountByMonth();

    java.math.BigDecimal sumAmountByCondition(
            @Param("supplierId") Long supplierId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM purchase_order WHERE supplier_id = #{supplierId}")
    java.math.BigDecimal sumAmountBySupplier(@Param("supplierId") Long supplierId);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM purchase_order WHERE create_time BETWEEN #{startDate} AND #{endDate}")
    java.math.BigDecimal sumAmountByMonthRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}