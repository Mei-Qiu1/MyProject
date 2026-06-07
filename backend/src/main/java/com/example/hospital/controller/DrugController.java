package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.hospital.common.PageResult;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Drug;
import com.example.hospital.entity.DrugCategory;
import com.example.hospital.mapper.DrugCategoryMapper;
import com.example.hospital.service.DrugService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drugs")
public class DrugController {

    private final DrugService drugService;
    private final DrugCategoryMapper drugCategoryMapper;

    public DrugController(DrugService drugService, DrugCategoryMapper drugCategoryMapper) {
        this.drugService = drugService;
        this.drugCategoryMapper = drugCategoryMapper;
    }

    // 在 DrugController.java 中修改 list 方法
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) Long manageCategoryId,
                          @RequestParam(required = false) Integer isSpecial) {  // 新增
        IPage<Drug> drugPage = drugService.list(page, size, keyword, categoryId, manageCategoryId, isSpecial);
        return Result.success(PageResult.of(drugPage.getRecords(), drugPage.getTotal(),
                (int) drugPage.getCurrent(), (int) drugPage.getSize()));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Drug drug = drugService.findById(id);
        if (drug != null) {
            return Result.success(drug);
        }
        return Result.fail("药品不存在");
    }

    @PostMapping
    public Result<?> create(@RequestBody Drug drug) {
        Drug existingDrug = drugService.findByCode(drug.getDrugCode());
        if (existingDrug != null) {
            return Result.fail("药品编码已存在");
        }
        drugService.save(drug);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Drug drug) {
        drug.setId(id);
        drugService.update(drug);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        drugService.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        drugService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    @GetMapping("/export")
    public void exportDrugs(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) Long manageCategoryId,
                            HttpServletResponse response) throws Exception {
        LambdaQueryWrapper<Drug> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Drug::getDrugName, keyword).or().like(Drug::getDrugCode, keyword));
        }
        if (categoryId != null && categoryId != 0) {
            wrapper.eq(Drug::getCategoryId, categoryId);
        }
        if (manageCategoryId != null && manageCategoryId != 0) {
            wrapper.eq(Drug::getManageCategoryId, manageCategoryId);
        }
        wrapper.orderByAsc(Drug::getDrugCode);
        List<Drug> drugs = drugService.listAll(wrapper);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("药品列表");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"药品编码", "药品名称", "规格", "剂型", "生产厂家", "批准文号",
                    "药理分类ID", "管理分类ID", "单位", "特殊药品", "采购价", "零售价", "批发价", "状态", "备注"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            for (Drug drug : drugs) {
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0).setCellValue(drug.getDrugCode() == null ? "" : drug.getDrugCode());
                row.createCell(1).setCellValue(drug.getDrugName() == null ? "" : drug.getDrugName());
                row.createCell(2).setCellValue(drug.getSpec() == null ? "" : drug.getSpec());
                row.createCell(3).setCellValue(drug.getDosageForm() == null ? "" : drug.getDosageForm());
                row.createCell(4).setCellValue(drug.getManufacturer() == null ? "" : drug.getManufacturer());
                row.createCell(5).setCellValue(drug.getApprovalNumber() == null ? "" : drug.getApprovalNumber());
                row.createCell(6).setCellValue(drug.getCategoryId() == null ? "" : drug.getCategoryId().toString());
                row.createCell(7).setCellValue(drug.getManageCategoryId() == null ? "" : drug.getManageCategoryId().toString());
                row.createCell(8).setCellValue(drug.getUnit() == null ? "" : drug.getUnit());
                row.createCell(9).setCellValue(drug.getIsSpecial() == 1 ? "是" : "否");
                row.createCell(10).setCellValue(drug.getPurchasePrice() == null ? 0 : drug.getPurchasePrice().doubleValue());
                row.createCell(11).setCellValue(drug.getRetailPrice() == null ? 0 : drug.getRetailPrice().doubleValue());
                row.createCell(12).setCellValue(drug.getWholesalePrice() == null ? 0 : drug.getWholesalePrice().doubleValue());
                row.createCell(13).setCellValue(drug.getStatus() == 1 ? "启用" : "禁用");
                row.createCell(14).setCellValue(drug.getRemark() == null ? "" : drug.getRemark());
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("药品列表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @PostMapping("/import")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importDrugs(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("上传文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            return Result.fail("Excel格式错误，请上传 .xlsx 或 .xls 文件");
        }

        // 预加载分类
        List<DrugCategory> allCategories = drugCategoryMapper.selectList(null);
        Map<String, Long> pharmacologicalMap = new HashMap<>();
        Map<String, Long> managementMap = new HashMap<>();
        for (DrugCategory cat : allCategories) {
            if (cat.getType() == 1) pharmacologicalMap.put(cat.getCategoryName(), cat.getId());
            else if (cat.getType() == 2) managementMap.put(cat.getCategoryName(), cat.getId());
        }

        Set<String> existingDrugCodes = drugService.getAllDrugCodes();
        List<Drug> drugList = new ArrayList<>();
        StringBuilder errorLog = new StringBuilder();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = originalFilename.endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return Result.fail("文件中没有数据行");
            }

            // 校验标题行
            Row headerRow = sheet.getRow(0);
            String[] expectedHeaders = {"药品编码", "药品名称", "规格", "剂型", "生产厂家", "批准文号",
                    "药理分类名称", "管理分类名称", "单位", "是否特殊", "采购价", "零售价", "批发价", "状态", "备注"};
            for (int i = 0; i < expectedHeaders.length; i++) {
                String cellValue = getCellStringValue(headerRow.getCell(i));
                if (cellValue == null || !expectedHeaders[i].equals(cellValue.trim())) {
                    return Result.fail("Excel格式错误，请确保列顺序为：" + String.join("、", expectedHeaders));
                }
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                int rowNum = row.getRowNum() + 1;

                String drugCode = getCellStringValue(row.getCell(0));
                String drugName = getCellStringValue(row.getCell(1));
                String spec = getCellStringValue(row.getCell(2));
                String dosageForm = getCellStringValue(row.getCell(3));
                String manufacturer = getCellStringValue(row.getCell(4));
                String approvalNumber = getCellStringValue(row.getCell(5));
                String pharmacologicalName = getCellStringValue(row.getCell(6));
                String managementName = getCellStringValue(row.getCell(7));
                String unit = getCellStringValue(row.getCell(8));
                String isSpecialStr = getCellStringValue(row.getCell(9));
                String purchasePriceStr = getCellStringValue(row.getCell(10));
                String retailPriceStr = getCellStringValue(row.getCell(11));
                String wholesalePriceStr = getCellStringValue(row.getCell(12));
                String statusStr = getCellStringValue(row.getCell(13));
                String remark = getCellStringValue(row.getCell(14));

                // 全部字段必填校验
                if (isBlank(drugCode)) { errorLog.append("第").append(rowNum).append("行药品编码缺失；"); continue; }
                if (isBlank(drugName)) { errorLog.append("第").append(rowNum).append("行药品名称缺失；"); continue; }
                if (isBlank(spec)) { errorLog.append("第").append(rowNum).append("行规格缺失；"); continue; }
                if (isBlank(dosageForm)) { errorLog.append("第").append(rowNum).append("行剂型缺失；"); continue; }
                if (isBlank(manufacturer)) { errorLog.append("第").append(rowNum).append("行生产厂家缺失；"); continue; }
                if (isBlank(approvalNumber)) { errorLog.append("第").append(rowNum).append("行批准文号缺失；"); continue; }
                if (isBlank(pharmacologicalName)) { errorLog.append("第").append(rowNum).append("行药理分类名称缺失；"); continue; }
                if (isBlank(managementName)) { errorLog.append("第").append(rowNum).append("行管理分类名称缺失；"); continue; }
                if (isBlank(unit)) { errorLog.append("第").append(rowNum).append("行单位缺失；"); continue; }
                if (isBlank(isSpecialStr)) { errorLog.append("第").append(rowNum).append("行是否特殊缺失；"); continue; }
                if (isBlank(purchasePriceStr)) { errorLog.append("第").append(rowNum).append("行采购价缺失；"); continue; }
                if (isBlank(retailPriceStr)) { errorLog.append("第").append(rowNum).append("行零售价缺失；"); continue; }
                if (isBlank(wholesalePriceStr)) { errorLog.append("第").append(rowNum).append("行批发价缺失；"); continue; }
                if (isBlank(statusStr)) { errorLog.append("第").append(rowNum).append("行状态缺失；"); continue; }
                if (isBlank(remark)) { errorLog.append("第").append(rowNum).append("行备注缺失；"); continue; }

                // 编码唯一性
                if (existingDrugCodes.contains(drugCode)) {
                    errorLog.append("第").append(rowNum).append("行药品编码【").append(drugCode).append("】已存在；");
                    continue;
                }
                if (drugList.stream().anyMatch(d -> d.getDrugCode().equals(drugCode))) {
                    errorLog.append("第").append(rowNum).append("行药品编码【").append(drugCode).append("】在本次导入中重复；");
                    continue;
                }

                // 是否特殊
                Integer isSpecial;
                if ("是".equals(isSpecialStr)) isSpecial = 1;
                else if ("否".equals(isSpecialStr)) isSpecial = 0;
                else { errorLog.append("第").append(rowNum).append("行是否特殊只能为'是'或'否'；"); continue; }

                // 状态
                Integer status;
                if ("启用".equals(statusStr)) status = 1;
                else if ("禁用".equals(statusStr)) status = 0;
                else { errorLog.append("第").append(rowNum).append("行状态只能为'启用'或'禁用'；"); continue; }

                // 分类映射
                Long categoryId = pharmacologicalMap.get(pharmacologicalName);
                if (categoryId == null) {
                    errorLog.append("第").append(rowNum).append("行药理分类【").append(pharmacologicalName).append("】不存在；");
                    continue;
                }
                Long manageCategoryId = managementMap.get(managementName);
                if (manageCategoryId == null) {
                    errorLog.append("第").append(rowNum).append("行管理分类【").append(managementName).append("】不存在；");
                    continue;
                }

                // 价格
                BigDecimal purchasePrice, retailPrice, wholesalePrice;
                try {
                    purchasePrice = new BigDecimal(purchasePriceStr);
                    if (purchasePrice.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    errorLog.append("第").append(rowNum).append("行采购价格式错误；"); continue;
                }
                try {
                    retailPrice = new BigDecimal(retailPriceStr);
                    if (retailPrice.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    errorLog.append("第").append(rowNum).append("行零售价格式错误；"); continue;
                }
                try {
                    wholesalePrice = new BigDecimal(wholesalePriceStr);
                    if (wholesalePrice.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    errorLog.append("第").append(rowNum).append("行批发价格式错误；"); continue;
                }

                Drug drug = new Drug();
                drug.setDrugCode(drugCode);
                drug.setDrugName(drugName);
                drug.setSpec(spec);
                drug.setDosageForm(dosageForm);
                drug.setManufacturer(manufacturer);
                drug.setApprovalNumber(approvalNumber);
                drug.setCategoryId(categoryId);
                drug.setManageCategoryId(manageCategoryId);
                drug.setUnit(unit);
                drug.setIsSpecial(isSpecial);
                drug.setPurchasePrice(purchasePrice);
                drug.setRetailPrice(retailPrice);
                drug.setWholesalePrice(wholesalePrice);
                drug.setStatus(status);
                drug.setRemark(remark);
                drug.setCreateTime(LocalDateTime.now());
                drug.setUpdateTime(LocalDateTime.now());

                drugList.add(drug);
            }

            if (errorLog.length() > 0) {
                return Result.fail("导入失败：存在无效数据，未导入任何药品。错误详情：" + errorLog.toString());
            }
            if (drugList.isEmpty()) {
                return Result.fail("导入失败：没有有效数据行。");
            }

            for (Drug drug : drugList) {
                drugService.save(drug);
            }
            return Result.success("导入成功，共导入 " + drugList.size() + " 条药品记录");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("导入失败：" + e.getMessage());
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return null;
        }
    }
}