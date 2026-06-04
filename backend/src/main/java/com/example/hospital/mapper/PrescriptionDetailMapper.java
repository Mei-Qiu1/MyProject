package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.PrescriptionDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PrescriptionDetailMapper extends BaseMapper<PrescriptionDetail> {
    
    List<PrescriptionDetail> selectByPrescriptionId(Long prescriptionId);
}