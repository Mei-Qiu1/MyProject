package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.PurchaseRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PurchaseRequestMapper extends BaseMapper<PurchaseRequest> {
    
    @Select("SELECT COUNT(*) FROM purchase_request WHERE status = 0")
    Integer countPending();
    
    @Select("SELECT COUNT(*) FROM purchase_request WHERE status = 1")
    Integer countPendingApproval();
    
    @Select("SELECT * FROM purchase_request WHERE status = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<PurchaseRequest> selectPending(@Param("limit") Integer limit);
    
    @Select("SELECT * FROM purchase_request WHERE status = 1 ORDER BY create_time DESC LIMIT #{limit}")
    List<PurchaseRequest> selectPendingApproval(@Param("limit") Integer limit);
}