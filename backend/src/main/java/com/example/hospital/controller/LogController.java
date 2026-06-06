package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.SysLog;
import com.example.hospital.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 系统日志控制器
 */
@RestController
@RequestMapping("/system/logs")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * 分页查询日志
     */
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            // 将 LocalDate 转换为 LocalDateTime
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
            
            logger.info("查询日志列表 - page: {}, size: {}, keyword: {}, startDate: {}, endDate: {}", 
                    page, size, keyword, startDateTime, endDateTime);
            
            IPage<SysLog> logPage = logService.page(page, size, keyword, startDateTime, endDateTime);
            
            logger.info("查询成功 - 总记录数: {}", logPage.getTotal());
            
            return Result.success(PageResult.of(logPage.getRecords(), logPage.getTotal(),
                    (int) logPage.getCurrent(), (int) logPage.getSize()));
        } catch (Exception e) {
            logger.error("查询日志失败", e);
            return Result.fail("查询日志失败: " + e.getMessage());
        }
    }

    /**
     * 导出日志为 Excel
     */
    @GetMapping("/export")
    public void export(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                       HttpServletResponse response) {
        try {
            // 将 LocalDate 转换为 LocalDateTime
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;
            
            logService.export(keyword, startDateTime, endDateTime, response);
        } catch (Exception e) {
            logger.error("导出日志失败", e);
        }
    }
}