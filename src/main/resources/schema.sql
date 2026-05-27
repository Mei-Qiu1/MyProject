-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    path VARCHAR(100),
    component VARCHAR(200),
    parent_id BIGINT DEFAULT 0,
    icon VARCHAR(100),
    sort_order INT DEFAULT 0,
    type INT NOT NULL DEFAULT 1,
    permission VARCHAR(100),
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统日志表
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    operation VARCHAR(100),
    method VARCHAR(100),
    params TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    status INT DEFAULT 1,
    error_message TEXT,
    create_time DATETIME NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 数据字典表
CREATE TABLE IF NOT EXISTS sys_dict (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_type VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    dict_name VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 药品分类表
CREATE TABLE IF NOT EXISTS drug_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    parent_id BIGINT DEFAULT 0,
    type INT NOT NULL DEFAULT 1,
    sort_order INT DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_category_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 药品表
CREATE TABLE IF NOT EXISTS drug (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_code VARCHAR(50) NOT NULL UNIQUE,
    drug_name VARCHAR(100) NOT NULL,
    spec VARCHAR(100),
    dosage_form VARCHAR(50),
    manufacturer VARCHAR(200),
    approval_number VARCHAR(100),
    category_id BIGINT,
    manage_category_id BIGINT,
    unit VARCHAR(20),
    is_special INT DEFAULT 0,
    purchase_price DECIMAL(10,2),
    retail_price DECIMAL(10,2),
    wholesale_price DECIMAL(10,2),
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_drug_code (drug_code),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(200) NOT NULL,
    supplier_code VARCHAR(50) NOT NULL UNIQUE,
    contact_name VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(500),
    qualification_no VARCHAR(100),
    qualification_expire_date DATETIME,
    bank_account VARCHAR(100),
    cooperation_status INT DEFAULT 1,
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_supplier_code (supplier_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 采购计划表
CREATE TABLE IF NOT EXISTS purchase_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_no VARCHAR(50) NOT NULL UNIQUE,
    plan_name VARCHAR(200) NOT NULL,
    plan_type INT DEFAULT 1,
    plan_date DATETIME NOT NULL,
    execute_date DATETIME,
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_plan_no (plan_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 采购申请表
CREATE TABLE IF NOT EXISTS purchase_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_no VARCHAR(50) NOT NULL UNIQUE,
    plan_id BIGINT,
    supplier_id BIGINT,
    status INT NOT NULL DEFAULT 1,
    audit_comment VARCHAR(500),
    audit_by BIGINT,
    audit_time DATETIME,
    remark VARCHAR(500),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_request_no (request_no),
    INDEX idx_supplier_id (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 采购申请表明细表
CREATE TABLE IF NOT EXISTS purchase_request_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    drug_name VARCHAR(100),
    spec VARCHAR(100),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    amount DECIMAL(12,2),
    INDEX idx_request_id (request_id),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 采购订单表
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    request_id BIGINT,
    supplier_id BIGINT,
    total_amount DECIMAL(12,2),
    status INT NOT NULL DEFAULT 1,
    delivery_date DATETIME,
    remark VARCHAR(500),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_order_no (order_no),
    INDEX idx_supplier_id (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 采购订单明细表
CREATE TABLE IF NOT EXISTS purchase_order_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    drug_name VARCHAR(100),
    spec VARCHAR(100),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    amount DECIMAL(12,2),
    received_quantity INT DEFAULT 0,
    INDEX idx_order_id (order_id),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 到货验收表
CREATE TABLE IF NOT EXISTS goods_receipt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_no VARCHAR(50) NOT NULL UNIQUE,
    order_id BIGINT NOT NULL,
    supplier_id BIGINT,
    status INT NOT NULL DEFAULT 1,
    receipt_date DATETIME NOT NULL,
    inspector VARCHAR(50),
    remark VARCHAR(500),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    INDEX idx_receipt_no (receipt_no),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_name VARCHAR(100) NOT NULL,
    warehouse_code VARCHAR(50) NOT NULL UNIQUE,
    type INT DEFAULT 1,
    location VARCHAR(200),
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_warehouse_code (warehouse_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存表
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id BIGINT NOT NULL,
    batch_no VARCHAR(50) NOT NULL,
    production_date DATETIME,
    expire_date DATETIME NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10,2),
    warehouse_id BIGINT,
    location_id BIGINT,
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_drug_id (drug_id),
    INDEX idx_batch_no (batch_no),
    INDEX idx_expire_date (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存流水表
CREATE TABLE IF NOT EXISTS inventory_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_id BIGINT,
    drug_id BIGINT NOT NULL,
    batch_no VARCHAR(50),
    type INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    amount DECIMAL(12,2),
    source_no VARCHAR(50),
    warehouse_id BIGINT,
    remark VARCHAR(500),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    INDEX idx_drug_id (drug_id),
    INDEX idx_source_no (source_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存调拨表
CREATE TABLE IF NOT EXISTS inventory_transfer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transfer_no VARCHAR(50) NOT NULL UNIQUE,
    from_warehouse_id BIGINT NOT NULL,
    to_warehouse_id BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    INDEX idx_transfer_no (transfer_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存调拨明细表
CREATE TABLE IF NOT EXISTS inventory_transfer_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transfer_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    batch_no VARCHAR(50),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    INDEX idx_transfer_id (transfer_id),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存盘点表
CREATE TABLE IF NOT EXISTS inventory_check (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_no VARCHAR(50) NOT NULL UNIQUE,
    warehouse_id BIGINT,
    status INT NOT NULL DEFAULT 1,
    check_date DATETIME NOT NULL,
    checker VARCHAR(50),
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    INDEX idx_check_no (check_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存盘点明细表
CREATE TABLE IF NOT EXISTS inventory_check_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    check_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    batch_no VARCHAR(50),
    book_quantity INT NOT NULL,
    actual_quantity INT NOT NULL,
    difference INT,
    reason VARCHAR(500),
    INDEX idx_check_id (check_id),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 处方表
CREATE TABLE IF NOT EXISTS prescription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_no VARCHAR(50) NOT NULL UNIQUE,
    patient_name VARCHAR(50) NOT NULL,
    patient_id VARCHAR(50),
    patient_age INT,
    patient_sex VARCHAR(10),
    department VARCHAR(100),
    doctor_name VARCHAR(50),
    doctor_id BIGINT,
    type INT NOT NULL DEFAULT 1,
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    INDEX idx_prescription_no (prescription_no),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 处方明细表
CREATE TABLE IF NOT EXISTS prescription_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,
    drug_name VARCHAR(100),
    spec VARCHAR(100),
    quantity INT NOT NULL,
    usage_info VARCHAR(500),
    price DECIMAL(10,2),
    amount DECIMAL(12,2),
    INDEX idx_prescription_id (prescription_id),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 医嘱表
CREATE TABLE IF NOT EXISTS medical_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    patient_id VARCHAR(50) NOT NULL,
    patient_name VARCHAR(50) NOT NULL,
    department VARCHAR(100),
    bed_no VARCHAR(20),
    doctor_id BIGINT,
    doctor_name VARCHAR(50),
    type INT DEFAULT 1,
    status INT NOT NULL DEFAULT 1,
    order_time DATETIME NOT NULL,
    execute_time DATETIME,
    remark VARCHAR(500),
    create_time DATETIME NOT NULL,
    INDEX idx_order_no (order_no),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 药品配送表
CREATE TABLE IF NOT EXISTS drug_delivery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_no VARCHAR(50) NOT NULL UNIQUE,
    order_id BIGINT,
    department VARCHAR(100),
    status INT NOT NULL DEFAULT 1,
    delivery_time DATETIME,
    signer VARCHAR(50),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    INDEX idx_delivery_no (delivery_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用药记录表
CREATE TABLE IF NOT EXISTS medication_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id VARCHAR(50) NOT NULL,
    patient_name VARCHAR(50),
    drug_id BIGINT NOT NULL,
    drug_name VARCHAR(100),
    spec VARCHAR(100),
    quantity INT NOT NULL,
    usage_info VARCHAR(500),
    doctor_id BIGINT,
    doctor_name VARCHAR(50),
    record_time DATETIME NOT NULL,
    INDEX idx_patient_id (patient_id),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 特殊药品领用申请表
CREATE TABLE IF NOT EXISTS special_drug_apply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    apply_no VARCHAR(50) NOT NULL UNIQUE,
    drug_id BIGINT NOT NULL,
    drug_name VARCHAR(100),
    quantity INT NOT NULL,
    prescription_no VARCHAR(50),
    purpose VARCHAR(500),
    status INT NOT NULL DEFAULT 1,
    user1 VARCHAR(50),
    user2 VARCHAR(50),
    create_by BIGINT,
    create_time DATETIME NOT NULL,
    INDEX idx_apply_no (apply_no),
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 特殊药品使用记录表
CREATE TABLE IF NOT EXISTS special_drug_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id BIGINT NOT NULL,
    batch_no VARCHAR(50),
    prescription_no VARCHAR(50),
    quantity INT NOT NULL,
    amount DECIMAL(12,2),
    purpose VARCHAR(500),
    user1 VARCHAR(50),
    user2 VARCHAR(50),
    recycle_status VARCHAR(20),
    recycle_no VARCHAR(50),
    warehouse_id BIGINT,
    create_time DATETIME NOT NULL,
    INDEX idx_drug_id (drug_id),
    INDEX idx_prescription_no (prescription_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 价格调整记录表
CREATE TABLE IF NOT EXISTS price_adjustment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id BIGINT NOT NULL,
    drug_code VARCHAR(50),
    drug_name VARCHAR(100),
    original_price DECIMAL(10,2),
    new_price DECIMAL(10,2),
    price_type INT NOT NULL,
    adjust_reason VARCHAR(500),
    adjust_by BIGINT,
    adjust_time DATETIME NOT NULL,
    INDEX idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
