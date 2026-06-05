package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.hospital.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    // 分页查询（带供应商名称）
    @Select("SELECT po.*, s.supplier_name AS supplier_name " +
            "FROM purchase_order po " +
            "LEFT JOIN supplier s ON po.supplier_id = s.id " +
            "${ew.customSqlSegment}")
    IPage<PurchaseOrder> selectPageWithNames(IPage<PurchaseOrder> page,
                                             @Param(Constants.WRAPPER) Wrapper<PurchaseOrder> wrapper);

    // 根据ID查询单个订单（带供应商名称）
    @Select("SELECT po.*, s.supplier_name AS supplier_name " +
            "FROM purchase_order po " +
            "LEFT JOIN supplier s ON po.supplier_id = s.id " +
            "WHERE po.id = #{id}")
    PurchaseOrder selectByIdWithNames(@Param("id") Long id);
}