package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.DeliveryDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配送明细表Mapper
 */
@Mapper
public interface DeliveryDetailMapper extends BaseMapper<DeliveryDetail> {
}