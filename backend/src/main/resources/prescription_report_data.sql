-- 处方统计数据样本
-- 为测试处方统计报表功能，插入样本数据

-- 处方数据（处方主表）
INSERT INTO prescription (prescription_no, patient_name, patient_id, patient_age, patient_sex, department, doctor_name, doctor_id, type, status, create_time, update_time) VALUES
('RX20260101001', '张三', '110101199001011234', 35, '男', '内科', '李医生', 1, 1, 3, '2026-01-02 09:30:00', '2026-01-02 10:00:00'),
('RX20260101002', '李四', '110101199502022345', 30, '女', '外科', '王医生', 2, 1, 3, '2026-01-02 10:15:00', '2026-01-02 10:45:00'),
('RX20260101003', '王五', '110101198803031234', 37, '男', '内科', '李医生', 1, 1, 3, '2026-01-03 08:45:00', '2026-01-03 09:20:00'),
('RX20260101004', '赵六', '110101201005044567', 20, '女', '儿科', '张医生', 3, 1, 3, '2026-01-03 14:20:00', '2026-01-03 15:00:00'),
('RX20260101005', '孙七', '110101197012125678', 53, '男', '心内科', '刘医生', 4, 2, 2, '2026-01-04 11:00:00', '2026-01-04 11:30:00'),
('RX20260102001', '周八', '110101199308086789', 32, '女', '妇产科', '陈医生', 5, 1, 3, '2026-01-05 09:00:00', '2026-01-05 09:30:00'),
('RX20260102002', '吴九', '110101196505157890', 60, '男', '神经内科', '刘医生', 4, 2, 3, '2026-01-05 10:30:00', '2026-01-05 11:00:00'),
('RX20260102003', '郑十', '110101201208189012', 13, '女', '儿科', '张医生', 3, 1, 3, '2026-01-06 08:30:00', '2026-01-06 09:00:00'),
('RX20260102004', '钱一', '110101198010201234', 45, '男', '消化内科', '李医生', 1, 1, 3, '2026-01-06 15:00:00', '2026-01-06 15:30:00'),
('RX20260103001', '陈二', '110101197503103456', 50, '女', '内分泌科', '王医生', 2, 1, 3, '2026-01-07 09:15:00', '2026-01-07 09:45:00'),
('RX20260103002', '刘三', '110101199112114567', 34, '男', '呼吸内科', '刘医生', 4, 1, 3, '2026-01-07 14:00:00', '2026-01-07 14:30:00'),
('RX20260103003', '杨四', '110101200205225678', 21, '女', '皮肤科', '陈医生', 5, 1, 3, '2026-01-08 10:00:00', '2026-01-08 10:30:00'),
('RX20260103004', '黄五', '110101196807157890', 57, '男', '骨科', '张医生', 3, 1, 3, '2026-01-08 11:30:00', '2026-01-08 12:00:00'),
('RX20260104001', '林六', '110101199610168901', 29, '女', '眼科', '李医生', 1, 1, 3, '2026-01-09 08:00:00', '2026-01-09 08:30:00'),
('RX20260104002', '徐七', '110101198209279012', 43, '男', '泌尿外科', '王医生', 2, 1, 3, '2026-01-09 14:30:00', '2026-01-09 15:00:00'),
('RX20260104003', '何八', '110101200408280123', 17, '女', '儿科', '张医生', 3, 1, 3, '2026-01-10 09:00:00', '2026-01-10 09:30:00'),
('RX20260104004', '许九', '110101197011011234', 54, '男', '肿瘤科', '刘医生', 4, 2, 2, '2026-01-10 15:30:00', '2026-01-10 16:00:00'),
('RX20260201001', '冯十', '110101199304021345', 32, '女', '妇产科', '陈医生', 5, 1, 3, '2026-02-01 10:00:00', '2026-02-01 10:30:00'),
('RX20260201002', '曹一', '110101196608032456', 59, '男', '心内科', '刘医生', 4, 2, 3, '2026-02-02 08:30:00', '2026-02-02 09:00:00'),
('RX20260201003', '蒋二', '110101200512143567', 10, '女', '儿科', '张医生', 3, 1, 3, '2026-02-03 09:30:00', '2026-02-03 10:00:00'),
('RX20260202001', '沈三', '110101198706254678', 38, '男', '消化内科', '李医生', 1, 1, 3, '2026-02-04 11:00:00', '2026-02-04 11:30:00'),
('RX20260202002', '韩四', '110101199008065789', 35, '女', '神经内科', '刘医生', 4, 1, 3, '2026-02-05 14:00:00', '2026-02-05 14:30:00'),
('RX20260202003', '杨五', '110101197212176890', 53, '男', '呼吸内科', '王医生', 2, 1, 3, '2026-02-06 09:00:00', '2026-02-06 09:30:00'),
('RX20260203001', '秦六', '110101201103287901', 24, '女', '皮肤科', '陈医生', 5, 1, 3, '2026-02-07 10:30:00', '2026-02-07 11:00:00'),
('RX20260203002', '尤七', '110101198810299012', 37, '男', '骨科', '张医生', 3, 1, 3, '2026-02-10 08:00:00', '2026-02-10 08:30:00'),
('RX20260301001', '许八', '110101199505101234', 30, '女', '眼科', '李医生', 1, 1, 3, '2026-03-01 09:00:00', '2026-03-01 09:30:00'),
('RX20260301002', '何九', '110101196903111345', 56, '男', '泌尿外科', '王医生', 2, 1, 3, '2026-03-02 14:00:00', '2026-03-02 14:30:00'),
('RX20260301003', '吕十', '110101200201221456', 24, '女', '儿科', '张医生', 3, 1, 3, '2026-03-03 10:00:00', '2026-03-03 10:30:00'),
('RX20260302001', '施一', '110101197706132567', 48, '男', '肿瘤科', '刘医生', 4, 2, 2, '2026-03-05 11:00:00', '2026-03-05 11:30:00'),
('RX20260302002', '张二', '110101199211243678', 33, '女', '妇产科', '陈医生', 5, 1, 3, '2026-03-06 08:30:00', '2026-03-06 09:00:00');

-- 处方明细数据
-- 处方1的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(1, 1, '阿莫西林胶囊', '0.25g*24粒', '盒', 2, '口服，每次0.5g，每日3次', 15.80, 31.60),
(1, 2, '感冒灵颗粒', '10g*9袋', '盒', 1, '口服，每次1袋，每日3次', 12.50, 12.50);

-- 处方2的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(2, 3, '布洛芬缓释胶囊', '0.3g*20粒', '盒', 1, '口服，每次0.3g，每日2次', 18.90, 18.90),
(2, 4, '云南白药胶囊', '0.25g*16粒', '盒', 2, '口服，每次0.5g，每日3次', 28.50, 57.00);

-- 处方3的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(3, 1, '阿莫西林胶囊', '0.25g*24粒', '盒', 3, '口服，每次0.5g，每日3次', 15.80, 47.40),
(3, 5, '复方甘草片', '100片', '瓶', 1, '口服，每次3片，每日3次', 8.50, 8.50);

-- 处方4的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(4, 6, '小儿氨酚黄那敏颗粒', '6g*10袋', '盒', 1, '口服，每次1袋，每日3次', 16.80, 16.80),
(4, 7, '阿奇霉素干混悬剂', '0.1g*6袋', '盒', 1, '口服，每次0.1g，每日1次', 32.50, 32.50);

-- 处方5的明细（精神药品）
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(5, 8, '地西泮片', '2.5mg*100片', '瓶', 1, '口服，每次5mg，每晚1次', 15.20, 15.20),
(5, 9, '酒石酸美托洛尔片', '25mg*20片', '盒', 2, '口服，每次25mg，每日2次', 18.60, 37.20);

-- 处方6的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(6, 10, '硝苯地平缓释片', '20mg*30片', '盒', 1, '口服，每次20mg，每日2次', 24.80, 24.80),
(6, 11, '叶酸片', '0.4mg*31片', '盒', 1, '口服，每次0.4mg，每日1次', 12.00, 12.00);

-- 处方7的明细（精神药品）
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(7, 8, '地西泮片', '2.5mg*100片', '瓶', 1, '口服，每次5mg，每晚1次', 15.20, 15.20),
(7, 12, '盐酸氟桂利嗪胶囊', '5mg*20粒', '盒', 1, '口服，每次10mg，每晚1次', 22.50, 22.50);

-- 处方8的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(8, 6, '小儿氨酚黄那敏颗粒', '6g*10袋', '盒', 2, '口服，每次1袋，每日3次', 16.80, 33.60),
(8, 13, '蒙脱石散', '3g*10袋', '盒', 1, '口服，每次1袋，每日3次', 21.50, 21.50);

-- 处方9的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(9, 14, '奥美拉唑肠溶胶囊', '20mg*14粒', '盒', 1, '口服，每次20mg，每日1次', 26.80, 26.80),
(9, 15, '铝碳酸镁片', '0.5g*36片', '盒', 1, '口服，每次0.5g，每日3次', 19.80, 19.80);

-- 处方10的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(10, 16, '盐酸二甲双胍片', '0.25g*48片', '盒', 1, '口服，每次0.5g，每日3次', 18.50, 18.50),
(10, 17, '格列齐特缓释片', '30mg*30片', '盒', 1, '口服，每次30mg，每日1次', 35.60, 35.60);

-- 处方11的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(11, 18, '阿奇霉素片', '0.25g*6片', '盒', 2, '口服，每次0.25g，每日1次', 24.80, 49.60),
(11, 19, '氨茶碱片', '0.1g*100片', '瓶', 1, '口服，每次0.1g，每日3次', 8.80, 8.80);

-- 处方12的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(12, 20, '氯雷他定片', '10mg*12片', '盒', 1, '口服，每次10mg，每日1次', 16.80, 16.80),
(12, 21, '炉甘石洗剂', '100ml', '瓶', 1, '外用，每日2次', 12.50, 12.50);

-- 处方13的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(13, 22, '氨基葡萄糖胶囊', '0.48g*30粒', '盒', 1, '口服，每次0.48g，每日3次', 68.50, 68.50),
(13, 23, '双氯芬酸二乙胺乳胶剂', '20g', '支', 1, '外用，每日3次', 24.50, 24.50);

-- 处方14的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(14, 24, '玻璃酸钠滴眼液', '0.4ml*10支', '盒', 1, '滴眼，每次1滴，每日3次', 38.50, 38.50);

-- 处方15的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(15, 25, '盐酸坦索罗辛缓释胶囊', '0.2mg*10粒', '盒', 1, '口服，每次0.2mg，每日1次', 45.80, 45.80),
(15, 26, '非那雄胺片', '5mg*10片', '盒', 1, '口服，每次5mg，每日1次', 38.60, 38.60);

-- 处方16的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(16, 6, '小儿氨酚黄那敏颗粒', '6g*10袋', '盒', 2, '口服，每次1袋，每日3次', 16.80, 33.60);

-- 处方17的明细（精神药品）
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(17, 27, '盐酸吗啡缓释片', '30mg*10片', '盒', 1, '口服，每次30mg，每12小时1次', 89.50, 89.50);

-- 处方18的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(18, 28, '硝苯地平控释片', '30mg*7片', '盒', 2, '口服，每次30mg，每日1次', 32.80, 65.60),
(18, 29, '阿司匹林肠溶片', '100mg*30片', '盒', 1, '口服，每次100mg，每日1次', 15.80, 15.80);

-- 处方19的明细（精神药品）
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(19, 8, '地西泮片', '2.5mg*100片', '瓶', 1, '口服，每次5mg，每晚1次', 15.20, 15.20),
(19, 30, '盐酸曲美他嗪片', '20mg*30片', '盒', 1, '口服，每次20mg，每日3次', 42.50, 42.50);

-- 处方20的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(20, 31, '头孢克肟干混悬剂', '50mg*6袋', '盒', 1, '口服，每次50mg，每日2次', 28.50, 28.50),
(20, 32, '小儿止咳糖浆', '100ml', '瓶', 1, '口服，每次5ml，每日3次', 18.80, 18.80);

-- 处方21的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(21, 14, '奥美拉唑肠溶胶囊', '20mg*14粒', '盒', 2, '口服，每次20mg，每日1次', 26.80, 53.60),
(21, 33, '多潘立酮片', '10mg*30片', '盒', 1, '口服，每次10mg，每日3次', 22.80, 22.80);

-- 处方22的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(22, 12, '盐酸氟桂利嗪胶囊', '5mg*20粒', '盒', 1, '口服，每次10mg，每晚1次', 22.50, 22.50),
(22, 34, '血塞通软胶囊', '0.3g*30粒', '盒', 1, '口服，每次0.6g，每日3次', 38.80, 38.80);

-- 处方23的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(23, 18, '阿奇霉素片', '0.25g*6片', '盒', 3, '口服，每次0.25g，每日1次', 24.80, 74.40),
(23, 35, '复方甲氧那明胶囊', '36粒', '盒', 1, '口服，每次2粒，每日3次', 26.50, 26.50);

-- 处方24的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(24, 20, '氯雷他定片', '10mg*12片', '盒', 1, '口服，每次10mg，每日1次', 16.80, 16.80),
(24, 36, '丁酸氢化可的松乳膏', '20g', '支', 1, '外用，每日2次', 19.80, 19.80);

-- 处方25的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(25, 37, '硫酸氨基葡萄糖胶囊', '0.314g*30粒', '盒', 2, '口服，每次0.314g，每日3次', 72.50, 145.00);

-- 处方26的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(26, 24, '玻璃酸钠滴眼液', '0.4ml*10支', '盒', 2, '滴眼，每次1滴，每日3次', 38.50, 77.00);

-- 处方27的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(27, 38, '盐酸左氧氟沙星片', '0.5g*6片', '盒', 1, '口服，每次0.5g，每日1次', 28.80, 28.80);

-- 处方28的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(28, 6, '小儿氨酚黄那敏颗粒', '6g*10袋', '盒', 1, '口服，每次1袋，每日3次', 16.80, 16.80),
(28, 39, '维生素AD滴剂', '30粒', '盒', 1, '口服，每次1粒，每日1次', 32.50, 32.50);

-- 处方29的明细（精神药品）
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(29, 27, '盐酸吗啡缓释片', '30mg*10片', '盒', 2, '口服，每次30mg，每12小时1次', 89.50, 179.00),
(29, 40, '芬太尼透皮贴剂', '4.2mg*1贴', '盒', 1, '外用，每72小时1贴', 168.00, 168.00);

-- 处方30的明细
INSERT INTO prescription_detail (prescription_id, drug_id, drug_name, spec, unit, quantity, usage_info, price, amount) VALUES
(30, 41, '硝苯地平缓释片', '20mg*30片', '盒', 2, '口服，每次20mg，每日2次', 24.80, 49.60),
(30, 42, '滋肾育胎丸', '60g', '盒', 1, '口服，每次5g，每日3次', 48.00, 48.00);
