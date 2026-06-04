package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.DrugDelivery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 药品配送单Mapper
 */
@Mapper
public interface DrugDeliveryMapper extends BaseMapper<DrugDelivery> {
}