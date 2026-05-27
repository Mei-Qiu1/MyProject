package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.SysLog;
import java.time.LocalDateTime;

public interface LogService {
    IPage<SysLog> page(int page, int size, String keyword, LocalDateTime startDate, LocalDateTime endDate);
    void save(SysLog log);
    void export(String keyword, LocalDateTime startDate, LocalDateTime endDate);
}