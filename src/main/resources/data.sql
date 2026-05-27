
-- 初始化角色数据
INSERT INTO sys_role (role_name, role_code, description, status, create_time, update_time) VALUES
('系统管理员', 'ADMIN', '系统最高权限管理员', 1, NOW(), NOW()),
('药剂师', 'PHARMACIST', '药房药品管理人员', 1, NOW(), NOW()),
('采购员', 'PURCHASER', '药品采购人员', 1, NOW(), NOW()),
('医生', 'DOCTOR', '临床医生', 1, NOW(), NOW()),
('特殊药品管理员', 'SPECIAL_PHARMACIST', '毒麻精放药品管理员', 1, NOW(), NOW()),
('库存管理员', 'STOCK_MANAGER', '仓库库存管理人员', 1, NOW(), NOW());

-- 初始化管理员用户（密码：admin123）
--INSERT INTO sys_user (username, password, real_name, phone, email, role, status, create_time, update_time) VALUES
--('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '系统管理员', '13800138000', 'admin@hospital.com', 'ADMIN', 1, NOW(), NOW()),
--('pharmacist', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '张药师', '13800138001', 'pharmacist@hospital.com', 'PHARMACIST', 1, NOW(), NOW()),
--('purchaser', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '李采购', '13800138002', 'purchaser@hospital.com', 'PURCHASER', 1, NOW(), NOW());

-- 初始化药品分类
INSERT INTO drug_category (category_name, category_code, parent_id, type, sort_order, status, remark, create_time, update_time) VALUES
('抗感染药物', 'ANTI_INFECTION', 0, 1, 1, 1, '用于治疗感染性疾病', NOW(), NOW()),
('心血管系统药物', 'CARDIOVASCULAR', 0, 1, 2, 1, '用于治疗心血管疾病', NOW(), NOW()),
('消化系统药物', 'DIGESTIVE', 0, 1, 3, 1, '用于治疗消化系统疾病', NOW(), NOW()),
('呼吸系统药物', 'RESPIRATORY', 0, 1, 4, 1, '用于治疗呼吸系统疾病', NOW(), NOW()),
('普通药品', 'NORMAL', 0, 2, 1, 1, '普通管理药品', NOW(), NOW()),
('毒性药品', 'TOXIC', 0, 2, 2, 1, '毒性药品', NOW(), NOW()),
('麻醉药品', 'NARCOTIC', 0, 2, 3, 1, '麻醉药品', NOW(), NOW()),
('精神药品', 'PSYCHOTROPIC', 0, 2, 4, 1, '精神药品', NOW(), NOW()),
('放射性药品', 'RADIOACTIVE', 0, 2, 5, 1, '放射性药品', NOW(), NOW());

-- 初始化仓库
INSERT INTO warehouse (warehouse_name, warehouse_code, type, location, status, remark, create_time, update_time) VALUES
('中心药库', 'WH001', 1, '门诊楼B1层', 1, '医院中心药品仓库', NOW(), NOW()),
('门诊药房', 'WH002', 2, '门诊楼1层', 1, '门诊患者取药药房', NOW(), NOW()),
('住院药房', 'WH003', 2, '住院部1层', 1, '住院患者用药药房', NOW(), NOW()),
('特殊药品库', 'WH004', 3, '门诊楼B2层', 1, '毒麻精放药品专用仓库', NOW(), NOW());

-- 初始化示例药品数据
INSERT INTO drug (drug_code, drug_name, spec, dosage_form, manufacturer, approval_number, category_id, manage_category_id, unit, is_special, purchase_price, retail_price, wholesale_price, status, remark, create_time, update_time) VALUES
('D0001', '阿莫西林胶囊', '0.5g*20粒', '胶囊剂', '华北制药集团', '国药准字H13024138', 1, 5, '盒', 0, 15.00, 25.00, 20.00, 1, '广谱抗生素', NOW(), NOW()),
('D0002', '硝苯地平缓释片', '20mg*30片', '片剂', '拜耳医药', '国药准字J20180025', 2, 5, '盒', 0, 35.00, 58.00, 45.00, 1, '降压药', NOW(), NOW()),
('D0003', '奥美拉唑肠溶胶囊', '20mg*14粒', '胶囊剂', '阿斯利康', '国药准字H20030412', 3, 5, '盒', 0, 42.00, 68.00, 55.00, 1, '胃药', NOW(), NOW()),
('D0004', '沙丁胺醇气雾剂', '100μg*200揿', '气雾剂', '葛兰素史克', '国药准字H10940033', 4, 5, '瓶', 0, 28.00, 45.00, 35.00, 1, '哮喘用药', NOW(), NOW()),
('D0005', '吗啡注射液', '10mg/1ml*5支', '注射剂', '东北制药', '国药准字H21022436', 1, 7, '盒', 1, 8.50, 15.00, 12.00, 1, '麻醉药品', NOW(), NOW()),
('D0006', '地西泮片', '2.5mg*20片', '片剂', '天津药业', '国药准字H12020247', 4, 8, '瓶', 1, 5.00, 10.00, 8.00, 1, '精神药品', NOW(), NOW());

-- 初始化供应商数据
INSERT INTO supplier (supplier_name, supplier_code, contact_name, phone, address, qualification_no, qualification_expire_date, bank_account, cooperation_status, status, remark, create_time, update_time) VALUES
('华北制药集团有限公司', 'S0001', '王经理', '0311-85962222', '河北省石家庄市长安区和平东路388号', 'SC10613010200012', '2028-12-31', '12345678901234567890', 1, 1, '主要抗生素供应商', NOW(), NOW()),
('拜耳医药保健有限公司', 'S0002', '李经理', '010-59218888', '北京市朝阳区望京利泽东二路1号', 'SC10611010500015', '2029-06-30', '23456789012345678901', 1, 1, '外资药企', NOW(), NOW()),
('国药集团药业股份有限公司', 'S0003', '张经理', '010-63365555', '北京市西城区宣武门西大街28号', 'SC10611010200022', '2030-03-15', '34567890123456789012', 1, 1, '大型药品批发商', NOW(), NOW());

-- 初始化库存数据
INSERT INTO inventory (drug_id, batch_no, production_date, expire_date, quantity, unit_price, warehouse_id, location_id, status, create_time, update_time) VALUES
(1, 'B20240101', '2024-01-15', '2026-01-14', 100, 15.00, 1, 1, 1, NOW(), NOW()),
(1, 'B20240601', '2024-06-20', '2026-06-19', 150, 14.80, 1, 1, 1, NOW(), NOW()),
(2, 'B20240201', '2024-02-10', '2026-02-09', 80, 35.00, 1, 1, 1, NOW(), NOW()),
(3, 'B20240301', '2024-03-05', '2025-09-04', 60, 42.00, 1, 1, 1, NOW(), NOW()),
(4, 'B20240401', '2024-04-18', '2025-10-17', 45, 28.00, 1, 1, 1, NOW(), NOW()),
(5, 'B20240501', '2024-05-10', '2025-05-09', 30, 8.50, 4, 1, 1, NOW(), NOW()),
(6, 'B20240502', '2024-05-15', '2025-05-14', 50, 5.00, 4, 1, 1, NOW(), NOW());
