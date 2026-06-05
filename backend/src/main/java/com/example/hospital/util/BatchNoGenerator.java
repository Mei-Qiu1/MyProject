package com.example.hospital.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BatchNoGenerator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 生成入库批号：B + yyyyMM + 两位序号（从00开始，按年月递增）
     */
    public synchronized String generateBatchNo() {
        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String prefix = "B" + yearMonth;
        // 查询当前年月下最大的序号
        String sql = "SELECT MAX(CAST(SUBSTRING(batch_no, 8, 2) AS UNSIGNED)) FROM inventory WHERE batch_no LIKE ?";
        String maxSeq = jdbcTemplate.queryForObject(sql, String.class, prefix + "%");
        int nextSeq = (maxSeq == null ? -1 : Integer.parseInt(maxSeq)) + 1;
        if (nextSeq > 99) {
            throw new RuntimeException("批次号序号超出范围（最大99）");
        }
        return prefix + String.format("%02d", nextSeq);
    }
}