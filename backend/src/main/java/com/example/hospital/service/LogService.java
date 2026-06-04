package com.example.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.entity.SysLog;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * 系统日志服务接口
 */
public interface LogService {

    /**
     * 分页查询系统日志
     */
    IPage<SysLog> page(int page, int size, String keyword, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 保存日志
     */
    void save(SysLog log);

    /**
     * 导出日志到 Excel
     */
    void export(String keyword, LocalDateTime startDate, LocalDateTime endDate, HttpServletResponse response);
}