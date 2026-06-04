package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.MedicalOrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MedicalOrderDetailMapper extends BaseMapper<MedicalOrderDetail> {
    
    List<MedicalOrderDetail> selectByOrderId(Long orderId);
}
