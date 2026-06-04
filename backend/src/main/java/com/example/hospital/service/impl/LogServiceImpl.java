package com.example.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.entity.SysLog;
import com.example.hospital.mapper.SysLogMapper;
import com.example.hospital.service.LogService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统日志服务实现类
 */
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
    public void export(String keyword, LocalDateTime startDate, LocalDateTime endDate, HttpServletResponse response) {
        // 1. 查询日志数据
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysLog::getUsername, keyword).or().like(SysLog::getOperation, keyword);
        }
        if (startDate != null && endDate != null) {
            wrapper.between(SysLog::getCreateTime, startDate, endDate);
        }
        wrapper.orderByDesc(SysLog::getCreateTime);
        List<SysLog> logs = sysLogMapper.selectList(wrapper);

        // 2. 创建工作簿和样式
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("系统日志");

            // 标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "操作人", "操作描述", "请求方法", "请求参数", "IP地址", "状态", "错误信息", "操作时间"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            // 填充数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (SysLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(log.getId());
                row.createCell(1).setCellValue(log.getUsername() == null ? "" : log.getUsername());
                row.createCell(2).setCellValue(log.getOperation() == null ? "" : log.getOperation());
                row.createCell(3).setCellValue(log.getMethod() == null ? "" : log.getMethod());
                row.createCell(4).setCellValue(log.getParams() == null ? "" : log.getParams());
                row.createCell(5).setCellValue(log.getIpAddress() == null ? "" : log.getIpAddress());
                row.createCell(6).setCellValue(log.getStatus() == 1 ? "成功" : "失败");
                row.createCell(7).setCellValue(log.getErrorMessage() == null ? "" : log.getErrorMessage());
                row.createCell(8).setCellValue(log.getCreateTime() == null ? "" : log.getCreateTime().format(formatter));
            }

            // 3. 设置响应头并输出
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("系统日志_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("导出日志失败", e);
        }
    }
}