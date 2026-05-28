package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.SysLog;
import com.example.hospital.mapper.SysLogMapper;
import com.example.hospital.service.LogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class LogServiceImpl implements LogService {

    private final SysLogMapper sysLogMapper;

    public LogServiceImpl(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    @Override
    public IPage<SysLog> page(int page, int size, String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        Page<SysLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysLog::getUsername, keyword).or().like(SysLog::getOperation, keyword);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(SysLog::getCreateTime, startDate, endDate);
        }
        wrapper.orderByDesc(SysLog::getCreateTime);
        return sysLogMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void save(SysLog log) {
        sysLogMapper.insert(log);
    }

    @Override
    public void export(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        // 导出 Excel，暂留空
    }
}