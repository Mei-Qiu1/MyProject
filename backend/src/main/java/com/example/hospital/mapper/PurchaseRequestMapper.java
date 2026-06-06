package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.PurchaseRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PurchaseRequestMapper extends BaseMapper<PurchaseRequest> {

    // 统计待处理申请（status=0？根据您的业务，可能 status=1 为待审批，这里需要确认）
    @Select("SELECT COUNT(*) FROM purchase_request WHERE status = 0")
    Integer countPending();

    @Select("SELECT COUNT(*) FROM purchase_request WHERE status = 1")
    Integer countPendingApproval();

    @Select("SELECT * FROM purchase_request WHERE status = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<PurchaseRequest> selectPending(@Param("limit") Integer limit);

    @Select("SELECT * FROM purchase_request WHERE status = 1 ORDER BY create_time DESC LIMIT #{limit}")
    List<PurchaseRequest> selectPendingApproval(@Param("limit") Integer limit);


    // 根据ID查询单个申请，并连表获取供应商和计划名称
    @Select("SELECT pr.*, s.supplier_name AS supplier_name, pp.plan_name AS plan_name " +
            "FROM purchase_request pr " +
            "LEFT JOIN supplier s ON pr.supplier_id = s.id " +
            "LEFT JOIN purchase_plan pp ON pr.plan_id = pp.id " +
            "WHERE pr.id = #{id}")
    PurchaseRequest selectByIdWithNames(@Param("id") Long id);

    @Select("SELECT pr.*, " +
            "s.supplier_name AS supplier_name, " +
            "pp.plan_name AS plan_name, " +
            "COALESCE((SELECT SUM(d.amount) FROM purchase_request_detail d WHERE d.request_id = pr.id), 0) AS total_amount " +
            "FROM purchase_request pr " +
            "LEFT JOIN supplier s ON pr.supplier_id = s.id " +
            "LEFT JOIN purchase_plan pp ON pr.plan_id = pp.id " +
            "${ew.customSqlSegment}")
    IPage<PurchaseRequest> selectPageWithNames(IPage<PurchaseRequest> page,
                                               @Param(Constants.WRAPPER) Wrapper<PurchaseRequest> wrapper);

    IPage<PurchaseRequest> selectPageWithConditions(Page<PurchaseRequest> page,
                                                    @Param("keyword") String keyword,
                                                    @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM purchase_request WHERE status = #{status}")
    long countByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM purchase_request WHERE status IN (0, 1) ORDER BY create_time DESC LIMIT 10")
    List<PurchaseRequest> selectPendingRequests();

    @Select("SELECT * FROM purchase_request WHERE status = 2 ORDER BY create_time DESC LIMIT 10")
    List<PurchaseRequest> selectApprovedRequests();

    @Select("SELECT * FROM purchase_request WHERE status = 1 ORDER BY create_time DESC LIMIT #{limit}")
    List<PurchaseRequest> selectPendingForApproval(@Param("limit") Integer limit);

}