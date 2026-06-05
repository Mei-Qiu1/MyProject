package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.PurchasePlanDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PurchasePlanDetailMapper extends BaseMapper<PurchasePlanDetail> {

    @Select("SELECT pd.drug_id AS drugId, pd.drug_name AS drugName, " +
            "d.spec, pd.quantity, d.unit, d.purchase_price AS unitPrice, " +
            "(d.purchase_price * pd.quantity) AS amount " +
            "FROM purchase_plan_detail pd " +
            "LEFT JOIN drug d ON pd.drug_id = d.id " +
            "WHERE pd.plan_id = #{planId}")
    List<Map<String, Object>> selectPlanDetailsWithDrug(@Param("planId") Long planId);
}