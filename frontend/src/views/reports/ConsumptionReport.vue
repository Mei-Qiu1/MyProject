<template>
  <div class="report-container">
    <div class="report-header">
      <h2>药品消耗报表</h2>
      <div class="report-actions">
        <el-select v-model="departmentId" placeholder="选择科室">
          <el-option label="全部" :value="0"></el-option>
          <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
        </el-select>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
        <el-button type="primary" @click="refreshReport">刷新报表</el-button>
        <el-button type="warning" @click="exportReport">导出Excel</el-button>
      </div>
    </div>
    
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-value">{{ summary.totalConsumption || 0 }}</div>
        <div class="stat-label">消耗药品总数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">¥{{ formatNumber(summary.totalAmount || 0) }}</div>
        <div class="stat-label">消耗总金额</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.drugCount || 0 }}</div>
        <div class="stat-label">消耗药品种类</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.departmentCount || 0 }}</div>
        <div class="stat-label">消耗科室数</div>
      </div>
    </div>
    
    <div class="report-section">
      <h3>药品消耗排名</h3>
      <el-table :data="drugRanking" border>
        <el-table-column prop="rank" label="排名">
          <template #default="scope">
            <el-tag v-if="scope.row.rank <= 3" :type="scope.row.rank === 1 ? 'danger' : scope.row.rank === 2 ? 'warning' : 'info'">
              {{ scope.row.rank }}
            </el-tag>
            <span v-else>{{ scope.row.rank }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="consumption" label="消耗数量" />
        <el-table-column prop="amount" label="消耗金额" />
      </el-table>
    </div>
    
    <div class="report-section">
      <h3>科室消耗统计</h3>
      <el-table :data="departmentStats" border>
        <el-table-column prop="departmentName" label="科室名称" />
        <el-table-column prop="consumption" label="消耗数量" />
        <el-table-column prop="amount" label="消耗金额" />
      </el-table>
    </div>
    
    <div class="report-section">
      <h3>医生用药统计</h3>
      <el-table :data="doctorStats" border>
        <el-table-column prop="doctorName" label="医生姓名" />
        <el-table-column prop="prescriptionCount" label="处方数" />
        <el-table-column prop="amount" label="用药金额" />
      </el-table>
    </div>
    
    <div class="report-section">
      <h3>消耗趋势分析</h3>
      <div class="chart-container">
        <div v-for="item in trendData" :key="item.month" class="bar-item">
          <div class="bar" :style="{ height: getBarHeight(item.amount) + '%' }"></div>
          <span>{{ item.month }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const departmentId = ref(0)
const dateRange = ref([])
const departments = ref([])
const isMounted = ref(false)

const summary = reactive({
  totalConsumption: 0,
  totalAmount: 0,
  drugCount: 0,
  departmentCount: 0
})

const drugRanking = ref([])
const departmentStats = ref([])
const doctorStats = ref([])
const trendData = ref([])

const maxAmount = computed(() => {
  return Math.max(...trendData.value.map(t => t.amount || 0), 1)
})

const formatNumber = (num) => {
  return Number(num).toLocaleString()
}

const getBarHeight = (amount) => {
  return (amount / maxAmount.value * 100)
}

const refreshReport = async () => {
  if (!isMounted.value) return
  
  try {
    await Promise.all([
      loadSummary(),
      loadDrugRanking(),
      loadDepartmentStats(),
      loadDoctorStats(),
      loadTrend()
    ])
  } catch (error) {
    console.error('刷新报表失败:', error)
  }
}

const loadDepartments = () => {
  departments.value = [
    { id: 1, name: '内科' },
    { id: 2, name: '外科' },
    { id: 3, name: '急诊科' },
    { id: 4, name: '妇产科' },
    { id: 5, name: '儿科' },
    { id: 6, name: '骨科' },
    { id: 7, name: '皮肤科' },
    { id: 8, name: '眼科' }
  ]
}

const loadSummary = async () => {
  if (!isMounted.value) return
  
  try {
    const params = {}
    if (departmentId.value !== 0) {
      params.departmentId = departmentId.value
    }
    if (dateRange.value.length >= 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const response = await axios.get('/reports/consumption/summary', { params })
    if (response.code === 200 && isMounted.value) {
      Object.assign(summary, response.data)
    }
  } catch (error) {
    console.error('加载汇总数据失败, 使用模拟数据:', error)
    loadMockSummary()
  }
}

const loadMockSummary = () => {
  Object.assign(summary, {
    totalConsumption: 3560,
    totalAmount: 128560.00,
    drugCount: 156,
    departmentCount: 8
  })
}

const formatDate = (date) => {
  if (date instanceof Date) {
    return date.toISOString().split('T')[0]
  } else if (typeof date === 'string') {
    return date.split('T')[0]
  }
  return null
}

const loadDrugRanking = async () => {
  if (!isMounted.value) return
  
  try {
    const response = await axios.get('/reports/consumption/drug-ranking', {
      params: { limit: 10 }
    })
    if (response.code === 200 && isMounted.value) {
      drugRanking.value = response.data.map((item, index) => ({ ...item, rank: index + 1 }))
    }
  } catch (error) {
    console.error('加载药品排名失败, 使用模拟数据:', error)
    loadMockDrugRanking()
  }
}

const loadMockDrugRanking = () => {
  drugRanking.value = [
    { rank: 1, drugName: '阿莫西林胶囊', spec: '0.5g*20粒', consumption: 520, amount: 13000.00 },
    { rank: 2, drugName: '硝苯地平缓释片', spec: '20mg*30片', consumption: 480, amount: 27840.00 },
    { rank: 3, drugName: '奥美拉唑肠溶胶囊', spec: '20mg*14粒', consumption: 360, amount: 24480.00 },
    { rank: 4, drugName: '沙丁胺醇气雾剂', spec: '100μg*200揿', consumption: 280, amount: 12600.00 },
    { rank: 5, drugName: '地西泮片', spec: '2.5mg*20片', consumption: 180, amount: 1800.00 },
    { rank: 6, drugName: '吗啡注射液', spec: '10mg/1ml*5支', consumption: 120, amount: 1800.00 },
    { rank: 7, drugName: '头孢克肟胶囊', spec: '100mg*12粒', consumption: 240, amount: 8400.00 },
    { rank: 8, drugName: '布洛芬缓释胶囊', spec: '0.3g*20粒', consumption: 320, amount: 6400.00 },
    { rank: 9, drugName: '氨溴索口服液', spec: '100ml:300mg', consumption: 160, amount: 4800.00 },
    { rank: 10, drugName: '葡萄糖注射液', spec: '5% 500ml', consumption: 420, amount: 6300.00 }
  ]
}

const loadDepartmentStats = async () => {
  if (!isMounted.value) return
  
  try {
    const response = await axios.get('/reports/consumption/department-stats')
    if (response.code === 200 && isMounted.value) {
      departmentStats.value = response.data
    }
  } catch (error) {
    console.error('加载科室统计失败, 使用模拟数据:', error)
    loadMockDepartmentStats()
  }
}

const loadMockDepartmentStats = () => {
  departmentStats.value = [
    { departmentName: '内科', consumption: 1560, amount: 45600.00 },
    { departmentName: '外科', consumption: 1280, amount: 38400.00 },
    { departmentName: '妇产科', consumption: 890, amount: 26700.00 },
    { departmentName: '儿科', consumption: 670, amount: 20100.00 },
    { departmentName: '急诊科', consumption: 1120, amount: 33600.00 },
    { departmentName: '骨科', consumption: 780, amount: 23400.00 },
    { departmentName: '皮肤科', consumption: 320, amount: 9600.00 },
    { departmentName: '眼科', consumption: 450, amount: 13500.00 }
  ]
}

const loadDoctorStats = async () => {
  if (!isMounted.value) return
  
  try {
    const response = await axios.get('/reports/consumption/doctor-stats')
    if (response.code === 200 && isMounted.value) {
      doctorStats.value = response.data
    }
  } catch (error) {
    console.error('加载医生统计失败, 使用模拟数据:', error)
    loadMockDoctorStats()
  }
}

const loadMockDoctorStats = () => {
  doctorStats.value = [
    { doctorName: '王医生', prescriptionCount: 156, amount: 46800.00 },
    { doctorName: '李医生', prescriptionCount: 134, amount: 40200.00 },
    { doctorName: '张医生', prescriptionCount: 128, amount: 38400.00 },
    { doctorName: '刘医生', prescriptionCount: 112, amount: 33600.00 },
    { doctorName: '陈医生', prescriptionCount: 98, amount: 29400.00 },
    { doctorName: '赵医生', prescriptionCount: 86, amount: 25800.00 },
    { doctorName: '孙医生', prescriptionCount: 74, amount: 22200.00 },
    { doctorName: '周医生', prescriptionCount: 68, amount: 20400.00 }
  ]
}

const loadTrend = async () => {
  if (!isMounted.value) return
  
  try {
    const response = await axios.get('/reports/consumption/trend')
    if (response.code === 200 && isMounted.value) {
      trendData.value = response.data
    }
  } catch (error) {
    console.error('加载趋势数据失败, 使用模拟数据:', error)
    loadMockTrend()
  }
}

const loadMockTrend = () => {
  trendData.value = [
    { month: '1月', amount: 85000.00 },
    { month: '2月', amount: 92000.00 },
    { month: '3月', amount: 88000.00 },
    { month: '4月', amount: 95000.00 },
    { month: '5月', amount: 91000.00 },
    { month: '6月', amount: 98000.00 }
  ]
}

const exportReport = async () => {
  try {
    const params = {}
    if (departmentId.value !== 0) {
      params.departmentId = departmentId.value
    }
    if (dateRange.value.length >= 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const response = await axios.get('/reports/consumption/export', {
      params,
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '消耗报表.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  isMounted.value = true
  loadDepartments()
  refreshReport()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style scoped>
.report-container {
  background: white;
  border-radius: 10px;
  padding: 20px;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.report-actions {
  display: flex;
  gap: 10px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%);
  border-radius: 10px;
  padding: 20px;
  color: white;
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.report-section {
  margin-bottom: 20px;
}

.report-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #333;
}

.chart-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 200px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar {
  width: 40px;
  background: linear-gradient(180deg, #fc4a1a 0%, #f7b733 100%);
  border-radius: 8px 8px 0 0;
  min-height: 10px;
}
</style>
