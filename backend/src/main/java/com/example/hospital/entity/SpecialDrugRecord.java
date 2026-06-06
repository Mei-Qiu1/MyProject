package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("special_drug_record")
public class SpecialDrugRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long drugId;
    private String batchNo;
    private String prescriptionNo;
    private Integer quantity;
    private BigDecimal amount;
    private String purpose;
    private String user1;
    private String user2;
    private String recycleStatus;
    private String recycleNo;
    private Long warehouseId;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String patientName;

    @TableField(exist = false)
    private Long doctorId;

    @TableField(exist = false)
    private String unit;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDrugId() { return drugId; }
    public void setDrugId(Long drugId) { this.drugId = drugId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getPrescriptionNo() { return prescriptionNo; }
    public void setPrescriptionNo(String prescriptionNo) { this.prescriptionNo = prescriptionNo; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getUser1() { return user1; }
    public void setUser1(String user1) { this.user1 = user1; }
    public String getUser2() { return user2; }
    public void setUser2(String user2) { this.user2 = user2; }
    public String getRecycleStatus() { return recycleStatus; }
    public void setRecycleStatus(String recycleStatus) { this.recycleStatus = recycleStatus; }
    public String getRecycleNo() { return recycleNo; }
    public void setRecycleNo(String recycleNo) { this.recycleNo = recycleNo; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
