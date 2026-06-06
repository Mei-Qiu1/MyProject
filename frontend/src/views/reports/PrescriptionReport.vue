<template>
  <div class="report-container">
    <div class="report-header">
      <h2>处方统计报表</h2>
      <div class="report-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          class="date-picker"
        ></el-date-picker>
        <el-select v-model="searchDepartment" placeholder="选择科室" clearable class="search-select">
          <el-option label="全部科室" value="" />
          <el-option label="内科" value="内科" />
          <el-option label="外科" value="外科" />
          <el-option label="儿科" value="儿科" />
          <el-option label="妇产科" value="妇产科" />
          <el-option label="急诊科" value="急诊科" />
        </el-select>
        <el-input v-model="searchDoctorName" placeholder="请输入医生姓名" class="search-input"></el-input>
        <el-button type="primary" @click="refreshReport">查询</el-button>
        <el-button @click="exportReport">导出</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-value">{{ summary.prescriptionCount }}</div>
        <div class="stat-label">处方总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.patientCount }}</div>
        <div class="stat-label">患者人数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">¥{{ summary.totalAmount.toLocaleString() }}</div>
        <div class="stat-label">处方总金额</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.avgDrugsPerPrescription }}</div>
        <div class="stat-label">平均每处方药品数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.specialPrescriptionCount }}</div>
        <div class="stat-label">特殊药品处方</div>
      </div>
    </div>

    <!-- 处方趋势图 -->
    <div class="report-section">
      <h3>处方趋势</h3>
      <div class="chart-container">
        <div class="bar-chart">
          <div v-for="item in trendData" :key="item.month" class="bar-item">
            <div class="bar-wrapper">
              <div class="bar" :style="{ height: (item.count / maxTrendCount * 150) + 'px' }"></div>
            </div>
            <span class="bar-label">{{ item.month }}</span>
            <span class="bar-value">{{ item.count }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 处方明细列表 -->
    <div class="report-section">
      <h3>处方明细</h3>
      <el-table :data="prescriptionDetail" border stripe>
        <el-table-column prop="prescriptionNo" label="处方号" width="150" />
        <el-table-column prop="patientName" label="患者姓名" width="100" />
        <el-table-column prop="patientSex" label="性别" width="60" />
        <el-table-column prop="patientAge" label="年龄" width="60" />
        <el-table-column prop="department" label="科室" width="100" />
        <el-table-column prop="doctorName" label="医生" width="100" />
        <el-table-column prop="drugCount" label="药品数" width="80" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.totalAmount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="处方类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'success' : scope.row.type === 2 ? 'warning' : 'danger'">
              {{ scope.row.type === 1 ? '普通' : scope.row.type === 2 ? '精神药品' : '麻醉药品' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开具时间" width="180" />
      </el-table>
      <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @current-change="handlePageChange"
        layout="prev, pager, next, jumper"
        class="pagination"
      ></el-pagination>
    </div>

    <!-- 科室统计 -->
    <div class="report-section">
      <h3>科室统计</h3>
      <el-table :data="departmentStats" border stripe>
        <el-table-column prop="department" label="科室" width="150" />
        <el-table-column prop="prescriptionCount" label="处方数" width="100" />
        <el-table-column prop="patientCount" label="患者数" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="avgAmount" label="平均处方金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.avgAmount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="avgDrugsPerPrescription" label="平均药品数" width="120" />
        <el-table-column prop="specialCount" label="特殊处方数" width="120" />
      </el-table>
    </div>

    <!-- 医生统计 -->
    <div class="report-section">
      <h3>医生处方统计</h3>
      <el-table :data="doctorStats" border stripe>
        <el-table-column prop="doctorName" label="医生姓名" width="120" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="prescriptionCount" label="处方数" width="100" />
        <el-table-column prop="patientCount" label="患者数" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="avgDrugsPerPrescription" label="平均药品数" width="120" />
        <el-table-column prop="specialPrescriptionCount" label="特殊处方数" width="120" />
      </el-table>
    </div>

    <!-- 药品使用排行 -->
    <div class="report-section">
      <h3>药品使用排行</h3>
      <el-table :data="drugUsageRanking" border stripe>
        <el-table-column type="index" label="排名" width="80" />
        <el-table-column prop="drugName" label="药品名称" width="180" />
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="prescriptionCount" label="出处方数" width="120" />
        <el-table-column prop="totalQuantity" label="总用量" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.totalAmount.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="patientCount" label="患者数" width="100" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const dateRange = ref([])
const searchDepartment = ref('')
const searchDoctorName = ref('')

const summary = reactive({
  prescriptionCount: 0,
  patientCount: 0,
  totalAmount: 0,
  avgDrugsPerPrescription: 0,
  specialPrescriptionCount: 0
})

const prescriptionDetail = ref([])
const departmentStats = ref([])
const doctorStats = ref([])
const drugUsageRanking = ref([])
const trendData = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const maxTrendCount = computed(() => {
  if (trendData.value.length === 0) return 1
  return Math.max(...trendData.value.map(item => item.count))
})

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning', 3: 'primary', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已审核', 2: '已调配', 3: '已完成', 4: '已作废' }
  return texts[status] || '未知'
}

const refreshReport = async () => {
  await loadSummary()
  await loadDetail()
  await loadDepartmentStats()
  await loadDoctorStats()
  await loadDrugUsageRanking()
  await loadTrend()
}

const loadSummary = async () => {
  try {
    const params = {
      startDate: dateRange.value ? dateRange.value[0] : null,
      endDate: dateRange.value ? dateRange.value[1] : null,
      department: searchDepartment.value || null,
      doctorName: searchDoctorName.value || null
    }
    const response = await axios.get('/reports/prescription/summary', { params })
    if (response.code === 200) {
      Object.assign(summary, response.data)
    }
  } catch (error) {
    ElMessage.error('加载汇总数据失败')
  }
}

const loadDetail = async () => {
  try {
    const params = {
      page: pagination.current,
      size: pagination.size,
      startDate: dateRange.value ? dateRange.value[0] : null,
      endDate: dateRange.value ? dateRange.value[1] : null,
      department: searchDepartment.value || null,
      doctorName: searchDoctorName.value || null
    }
    const response = await axios.get('/reports/prescription/detail', { params })
    if (response.code === 200) {
      prescriptionDetail.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载明细数据失败')
  }
}

const loadDepartmentStats = async () => {
  try {
    const params = {
      startDate: dateRange.value ? dateRange.value[0] : null,
      endDate: dateRange.value ? dateRange.value[1] : null
    }
    const response = await axios.get('/reports/prescription/department-stats', { params })
    if (response.code === 200) {
      departmentStats.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载科室统计数据失败')
  }
}

const loadDoctorStats = async () => {
  try {
    const params = {
      startDate: dateRange.value ? dateRange.value[0] : null,
      endDate: dateRange.value ? dateRange.value[1] : null,
      department: searchDepartment.value || null
    }
    const response = await axios.get('/reports/prescription/doctor-stats', { params })
    if (response.code === 200) {
      doctorStats.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载医生统计数据失败')
  }
}

const loadDrugUsageRanking = async () => {
  try {
    const params = {
      startDate: dateRange.value ? dateRange.value[0] : null,
      endDate: dateRange.value ? dateRange.value[1] : null
    }
    const response = await axios.get('/reports/prescription/drug-usage', { params })
    if (response.code === 200) {
      drugUsageRanking.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载药品使用排行失败')
  }
}

const loadTrend = async () => {
  try {
    const response = await axios.get('/reports/prescription/trend')
    if (response.code === 200) {
      trendData.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载趋势数据失败')
  }
}

const handlePageChange = (page) => {
  pagination.current = page
  loadDetail()
}

const exportReport = async () => {
  try {
    const params = {
      startDate: dateRange.value ? dateRange.value[0] : null,
      endDate: dateRange.value ? dateRange.value[1] : null,
      department: searchDepartment.value || null,
      doctorName: searchDoctorName.value || null
    }
    const response = await axios.get('/reports/prescription/export', {
      params,
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `处方统计报表_${new Date().toLocaleDateString()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  // 默认加载最近30天的数据
  const today = new Date()
  const thirtyDaysAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
  dateRange.value = [
    thirtyDaysAgo.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]
  refreshReport()
})
</script>

<style scoped>
.report-container {
  padding: 20px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.report-header h2 {
  margin: 0;
  font-size: 24px;
}

.report-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  width: 200px;
}

.search-select {
  width: 150px;
}

.date-picker {
  width: 280px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card:nth-child(2) {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card:nth-child(3) {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card:nth-child(4) {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-card:nth-child(5) {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.report-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.report-section h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #303133;
}

.chart-container {
  padding: 20px 0;
}

.bar-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 200px;
  padding-top: 20px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80px;
}

.bar-wrapper {
  height: 150px;
  display: flex;
  align-items: flex-end;
}

.bar {
  width: 40px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
}

.bar-label {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
}

.bar-value {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
