
-- 初始化角色数据（使用INSERT IGNORE避免重复插入）
INSERT IGNORE INTO sys_role (role_name, role_code, description, status, create_time, update_time) VALUES
('系统管理员', 'ADMIN', '系统最高权限管理员', 1, NOW(), NOW()),
('药剂师', 'PHARMACIST', '药房药品管理人员', 1, NOW(), NOW()),
('采购员', 'PURCHASER', '药品采购人员', 1, NOW(), NOW()),
('医生', 'DOCTOR', '临床医生', 1, NOW(), NOW()),
('特殊药品管理员', 'SPECIAL_PHARMACIST', '毒麻精放药品管理员', 1, NOW(), NOW()),
('库存管理员', 'STOCK_MANAGER', '仓库库存管理人员', 1, NOW(), NOW()),
('药剂科主任', 'PHARMACY_DIRECTOR', '药剂科负责人，负责采购审批、特殊药品管理和统计报表', 1, NOW(), NOW());

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

-- 初始化特殊药品使用记录
INSERT INTO special_drug_record (drug_id, batch_no, prescription_no, quantity, amount, purpose, user1, user2, recycle_status, warehouse_id, create_time) VALUES
(5, 'B20240501', 'PR20240601001', 5, 75.00, '术后镇痛', '王医生', '张药师', '已回收', 4, '2024-06-01 10:30:00'),
(5, 'B20240501', 'PR20240605002', 3, 45.00, '癌症晚期止痛', '李医生', '张药师', '已回收', 4, '2024-06-05 14:20:00'),
(6, 'B20240502', 'PR20240608003', 10, 100.00, '术前镇静', '赵医生', '王药师', '使用中', 4, '2024-06-08 09:15:00'),
(5, 'B20240501', 'PR20240612004', 2, 30.00, '急性疼痛处理', '孙医生', '张药师', '已回收', 4, '2024-06-12 16:45:00'),
(6, 'B20240502', 'PR20240615005', 15, 150.00, 'ICU镇静', '周医生', '王药师', '使用中', 4, '2024-06-15 11:00:00');

-- 初始化特殊药品申请记录
INSERT INTO special_drug_apply (apply_no, drug_id, drug_name, quantity, prescription_no, purpose, status, user1, user2, create_time) VALUES
('SA20240620001', 5, '吗啡注射液', 10, 'PR20240620006', '晚期癌症患者止痛', 2, '刘医生', '陈主任', '2024-06-20 09:30:00'),
('SA20240621002', 6, '地西泮片', 20, 'PR20240621007', '术前患者镇静', 2, '马医生', '陈主任', '2024-06-21 14:00:00'),
('SA20240622003', 5, '吗啡注射液', 5, 'PR20240622008', '术后镇痛', 1, NULL, NULL, '2024-06-22 10:15:00'),
('SA20240623004', 6, '地西泮片', 15, 'PR20240623009', '精神科患者治疗', 1, NULL, NULL, '2024-06-23 15:30:00');

-- 初始化采购订单数据
INSERT INTO purchase_order (order_no, supplier_id, total_amount, status, remark, create_time, update_time) VALUES
('PO20240601001', 1, 55000.00, 3, '常规采购', '2024-06-01 09:00:00', '2024-06-05 14:00:00'),
('PO20240605002', 2, 48000.00, 3, '常规采购', '2024-06-05 10:30:00', '2024-06-10 11:00:00'),
('PO20240610003', 3, 62000.00, 2, '审批中', '2024-06-10 14:00:00', '2024-06-10 15:30:00'),
('PO20240615004', 1, 35000.00, 1, '待审批', '2024-06-15 09:30:00', '2024-06-15 09:30:00'),
('PO20240620005', 2, 58000.00, 1, '待审批', '2024-06-20 11:00:00', '2024-06-20 11:00:00');

-- 初始化采购订单明细数据
INSERT INTO purchase_order_detail (order_id, drug_id, drug_name, spec, quantity, unit_price, amount) VALUES
(1, 1, '阿莫西林胶囊', '0.5g*20粒', 1000, 15.00, 15000.00),
(1, 2, '硝苯地平缓释片', '20mg*30片', 500, 35.00, 17500.00),
(1, 3, '奥美拉唑肠溶胶囊', '20mg*14粒', 400, 42.00, 16800.00),
(2, 4, '沙丁胺醇气雾剂', '100μg*200揿', 300, 28.00, 8400.00),
(2, 2, '硝苯地平缓释片', '20mg*30片', 600, 35.00, 21000.00);

-- 初始化处方数据
INSERT INTO prescription (prescription_no, patient_name, patient_id, patient_age, patient_sex, department, doctor_name, type, status, create_time) VALUES
('PR20240601001', '张三', 'P001', 45, '男', '内科', '王医生', 1, 3, '2024-06-01 10:00:00'),
('PR20240603002', '李四', 'P002', 32, '女', '外科', '李医生', 1, 3, '2024-06-03 14:30:00'),
('PR20240605003', '王五', 'P003', 58, '男', '内科', '张医生', 2, 2, '2024-06-05 09:15:00'),
('PR20240608004', '赵六', 'P004', 26, '女', '妇产科', '刘医生', 1, 3, '2024-06-08 16:00:00'),
('PR20240610005', '孙七', 'P005', 42, '男', '急诊科', '陈医生', 2, 1, '2024-06-10 11:30:00');

-- 初始化处方明细数据
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, quantity, usage_info, price, amount) VALUES
(1, 1, '阿莫西林胶囊', '0.5g*20粒', 2, '每日三次，每次2粒', 25.00, 50.00),
(1, 2, '硝苯地平缓释片', '20mg*30片', 1, '每日一次，每次1片', 58.00, 58.00),
(2, 1, '阿莫西林胶囊', '0.5g*20粒', 3, '每日三次，每次2粒', 25.00, 75.00),
(2, 3, '奥美拉唑肠溶胶囊', '20mg*14粒', 1, '每日一次，每次1粒', 68.00, 68.00),
(3, 4, '沙丁胺醇气雾剂', '100μg*200揿', 1, '按需使用', 45.00, 45.00),
(4, 2, '硝苯地平缓释片', '20mg*30片', 2, '每日一次，每次1片', 58.00, 116.00),
(4, 3, '奥美拉唑肠溶胶囊', '20mg*14粒', 1, '每日一次，每次1粒', 68.00, 68.00),
(5, 1, '阿莫西林胶囊', '0.5g*20粒', 1, '每日三次，每次2粒', 25.00, 25.00);

-- 初始化医嘱数据
INSERT INTO medical_order (order_no, patient_id, patient_name, department, bed_no, doctor_name, type, status, order_time, create_time) VALUES
('MO20240602001', 'P001', '张三', '内科', '12', '王医生', 1, 3, '2024-06-02 08:00:00', '2024-06-02 08:00:00'),
('MO20240604002', 'P002', '李四', '外科', '25', '李医生', 2, 2, '2024-06-04 09:30:00', '2024-06-04 09:30:00'),
('MO20240606003', 'P003', '王五', '内科', '15', '张医生', 1, 1, '2024-06-06 10:00:00', '2024-06-06 10:00:00');

-- 初始化医嘱明细数据
INSERT INTO medical_order_detail (order_id, drug_id, drug_name, spec, quantity, frequency, duration, create_time) VALUES
(1, 1, '阿莫西林胶囊', '0.5g*20粒', 14, '每日三次', '7天', '2024-06-02 08:00:00'),
(1, 2, '硝苯地平缓释片', '20mg*30片', 7, '每日一次', '7天', '2024-06-02 08:00:00'),
(2, 4, '沙丁胺醇气雾剂', '100μg*200揿', 3, '按需使用', '3天', '2024-06-04 09:30:00'),
(3, 3, '奥美拉唑肠溶胶囊', '20mg*14粒', 14, '每日一次', '14天', '2024-06-06 10:00:00');
