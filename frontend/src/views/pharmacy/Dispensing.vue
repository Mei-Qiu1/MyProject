
<template>
  <div class="dispensing">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索处方号或患者姓名" class="search-input"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    
    <div class="dispensing-area">
      <div class="prescription-list-panel">
        <h3>待调配处方</h3>
        <el-table :data="pendingList" border @row-click="selectPrescription">
          <el-table-column prop="prescriptionNo" label="处方号" />
          <el-table-column prop="patientName" label="患者" />
          <el-table-column prop="department" label="科室" />
          <el-table-column prop="doctorName" label="医生" />
          <el-table-column prop="createTime" label="时间" />
        </el-table>
      </div>
      
      <div class="dispensing-panel">
        <h3>处方详情</h3>
        <div v-if="selectedPrescription">
          <el-descriptions :column="2" :data="selectedPrescription">
            <el-descriptions-item label="处方号">{{ selectedPrescription.prescriptionNo }}</el-descriptions-item>
            <el-descriptions-item label="患者姓名">{{ selectedPrescription.patientName }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ selectedPrescription.patientAge }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ selectedPrescription.patientSex }}</el-descriptions-item>
            <el-descriptions-item label="科室">{{ selectedPrescription.department }}</el-descriptions-item>
            <el-descriptions-item label="医生">{{ selectedPrescription.doctorName }}</el-descriptions-item>
          </el-descriptions>
          
          <h4 style="margin-top: 20px">药品明细</h4>
          <el-table :data="selectedPrescription.details" border>
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="quantity" label="数量" />
            <el-table-column prop="usage" label="用法" />
            <el-table-column prop="dispensed" label="已调配">
              <template #default="scope">
                <el-checkbox v-model="scope.row.dispensed"></el-checkbox>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="action-bar">
            <el-button v-if="selectedPrescription.status === 2" type="primary" @click="confirmDispensing">确认调配</el-button>
            <el-button v-if="selectedPrescription.status === 3" type="success" @click="confirmDispense">确认发药</el-button>
          </div>
        </div>
        <div v-else class="empty-tip">
          <el-icon size="48" style="color: #ccc">Package</el-icon>
          <p>请选择一个处方</p>
        </div>
      </div>
    </div>
    
    <div class="recent-dispense">
      <h3>最近调配记录</h3>
      <el-table :data="recentList" border>
        <el-table-column prop="prescriptionNo" label="处方号" />
        <el-table-column prop="patientName" label="患者" />
        <el-table-column prop="dispenser" label="调配人" />
        <el-table-column prop="createTime" label="时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const pendingList = ref([])
const recentList = ref([])
const selectedPrescription = ref(null)

const loadPending = async () => {
  try {
    const params = { status: 2 }
    if (keyword.value && keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }
    const response = await axios.get('/pharmacy/prescriptions', { params })
    if (response.code === 200) {
      pendingList.value = (response.data.records || response.data).map(p => ({
        ...p,
        details: p.details?.map(d => ({ ...d, dispensed: false })) || []
      }))
    }
  } catch (error) {
    ElMessage.error('加载待调配处方失败')
  }
}

const loadRecent = async () => {
  try {
    const response = await axios.get('/pharmacy/dispensing/recent')
    if (response.code === 200) {
      recentList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载调配记录失败')
  }
}

const handleSearch = () => {
  loadPending()
}

const selectPrescription = (row) => {
  selectedPrescription.value = row
}

const confirmDispensing = async () => {
  if (!selectedPrescription.value) {
    ElMessage.warning('请选择处方')
    return
  }
  
  const allDispensed = selectedPrescription.value.details.every(d => d.dispensed)
  if (!allDispensed) {
    ElMessage.warning('请先勾选所有已调配的药品')
    return
  }
  
  try {
    const response = await axios.post(`/pharmacy/prescriptions/${selectedPrescription.value.id}/dispense`)
    if (response.code === 200) {
      ElMessage.success('调配完成')
      selectedPrescription.value = null
      loadPending()
      loadRecent()
    } else {
      ElMessage.error(response.message || '调配失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '调配失败')
  }
}

const confirmDispense = async () => {
  if (!selectedPrescription.value) {
    ElMessage.warning('请选择处方')
    return
  }
  
  try {
    const response = await axios.post(`/pharmacy/prescriptions/${selectedPrescription.value.id}/dispense`)
    if (response.code === 200) {
      ElMessage.success('发药完成')
      selectedPrescription.value = null
      loadPending()
      loadRecent()
    } else {
      ElMessage.error(response.message || '发药失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发药失败')
  }
}

onMounted(() => {
  loadPending()
  loadRecent()
})
</script>

<style scoped>
.dispensing {
  background: white;
  border-radius: 10px;
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.dispensing-area {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 20px;
  margin-bottom: 20px;
}

.prescription-list-panel, .dispensing-panel {
  background: #f9fafb;
  border-radius: 8px;
  padding: 15px;
  max-height: 400px;
  overflow-y: auto;
}

.prescription-list-panel h3, .dispensing-panel h3 {
  margin-bottom: 15px;
  font-size: 14px;
  color: #333;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #999;
}

.action-bar {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.recent-dispense {
  margin-top: 20px;
}

.recent-dispense h3 {
  margin-bottom: 15px;
  font-size: 14px;
  color: #333;
}
</style>
