-- 处方统计报表相关表
-- 如果需要更详细的处方统计功能，可以创建以下表

-- 处方统计汇总表（按日/月/年汇总）
CREATE TABLE IF NOT EXISTS prescription_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    stat_type INT NOT NULL DEFAULT 1 COMMENT '统计类型：1-按日，2-按月，3-按年',
    department VARCHAR(100) COMMENT '科室',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    prescription_count INT DEFAULT 0 COMMENT '处方数量',
    patient_count INT DEFAULT 0 COMMENT '患者数量',
    drug_count INT DEFAULT 0 COMMENT '药品项次',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '处方总金额',
    special_count INT DEFAULT 0 COMMENT '特殊药品处方数',
    create_time DATETIME NOT NULL,
    INDEX idx_stat_date (stat_date),
    INDEX idx_stat_type (stat_type),
    INDEX idx_department (department),
    INDEX idx_doctor_id (doctor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方统计汇总表';

-- 药品使用统计表
CREATE TABLE IF NOT EXISTS drug_usage_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    drug_id BIGINT NOT NULL COMMENT '药品ID',
    drug_code VARCHAR(50) COMMENT '药品编码',
    drug_name VARCHAR(100) COMMENT '药品名称',
    spec VARCHAR(100) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    prescription_count INT DEFAULT 0 COMMENT '出处方数',
    total_quantity INT DEFAULT 0 COMMENT '总用量',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    patient_count INT DEFAULT 0 COMMENT '患者数',
    department VARCHAR(100) COMMENT '科室',
    create_time DATETIME NOT NULL,
    INDEX idx_stat_date (stat_date),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品使用统计表';

-- 科室处方统计表
CREATE TABLE IF NOT EXISTS department_prescription_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    department VARCHAR(100) NOT NULL COMMENT '科室名称',
    prescription_count INT DEFAULT 0 COMMENT '处方数量',
    patient_count INT DEFAULT 0 COMMENT '患者数量',
    avg_drugs_per_prescription DECIMAL(5,2) DEFAULT 0 COMMENT '平均每处方药品数',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '处方总金额',
    special_prescription_count INT DEFAULT 0 COMMENT '特殊药品处方数',
    create_time DATETIME NOT NULL,
    INDEX idx_stat_date (stat_date),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室处方统计表';

-- 医生处方统计表
CREATE TABLE IF NOT EXISTS doctor_prescription_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date DATE NOT NULL COMMENT '统计日期',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    doctor_name VARCHAR(50) COMMENT '医生姓名',
    department VARCHAR(100) COMMENT '科室',
    prescription_count INT DEFAULT 0 COMMENT '处方数量',
    patient_count INT DEFAULT 0 COMMENT '患者数量',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '处方总金额',
    avg_drugs_per_prescription DECIMAL(5,2) DEFAULT 0 COMMENT '平均每处方药品数',
    special_prescription_count INT DEFAULT 0 COMMENT '特殊处方数',
    create_time DATETIME NOT NULL,
    INDEX idx_stat_date (stat_date),
    INDEX idx_doctor_id (doctor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生处方统计表';

-- 处方月度对比表
CREATE TABLE IF NOT EXISTS prescription_monthly_comparison (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL COMMENT '年份',
    month INT NOT NULL COMMENT '月份',
    prescription_count INT DEFAULT 0 COMMENT '处方数量',
    patient_count INT DEFAULT 0 COMMENT '患者数量',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    avg_amount_per_prescription DECIMAL(10,2) DEFAULT 0 COMMENT '每处方平均金额',
    special_count INT DEFAULT 0 COMMENT '特殊药品处方数',
    create_time DATETIME NOT NULL,
    UNIQUE KEY uk_year_month (year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方月度对比表';
