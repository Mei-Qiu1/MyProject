
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
        <div class="stat-value">{{ summary.totalConsumption }}</div>
        <div class="stat-label">消耗药品总数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">¥{{ summary.totalAmount.toLocaleString() }}</div>
        <div class="stat-label">消耗总金额</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.drugCount }}</div>
        <div class="stat-label">消耗药品种类</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.departmentCount }}</div>
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
        <el-table-column prop="quantity" label="消耗数量" />
        <el-table-column prop="amount" label="消耗金额" />
        <el-table-column prop="percentage" label="占比">
          <template #default="scope">
            {{ scope.row.percentage }}%
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <div class="report-section">
      <h3>科室消耗统计</h3>
      <el-table :data="departmentStats" border>
        <el-table-column prop="departmentName" label="科室名称" />
        <el-table-column prop="totalAmount" label="消耗金额" />
        <el-table-column prop="drugCount" label="消耗药品种类" />
        <el-table-column prop="avgDaily" label="日均消耗">
          <template #default="scope">
            ¥{{ scope.row.avgDaily.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="percentage" label="占比">
          <template #default="scope">
            {{ scope.row.percentage }}%
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <div class="report-section">
      <h3>医生用药统计</h3>
      <el-table :data="doctorStats" border>
        <el-table-column prop="doctorName" label="医生姓名" />
        <el-table-column prop="department" label="科室" />
        <el-table-column prop="prescriptionCount" label="处方数" />
        <el-table-column prop="drugCount" label="药品种类" />
        <el-table-column prop="totalAmount" label="用药金额" />
      </el-table>
    </div>
    
    <div class="report-section">
      <h3>消耗趋势分析</h3>
      <div class="chart-container">
        <div v-for="item in trendData" :key="item.month" class="bar-item">
          <div class="bar" :style="{ height: (item.amount / maxAmount * 100) + '%' }"></div>
          <span>{{ item.month }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const departmentId = ref(0)
const dateRange = ref([])
const departments = ref([])
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
  return Math.max(...trendData.value.map(t => t.amount), 1)
})

const refreshReport = async () => {
  await loadSummary()
  await loadDrugRanking()
  await loadDepartmentStats()
  await loadDoctorStats()
  await loadTrend()
}

const loadDepartments = async () => {
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
  try {
    const response = await axios.get('/reports/consumption/summary', {
      params: {
        departmentId: departmentId.value === 0 ? undefined : departmentId.value,
        startDate: dateRange.value[0]?.format('YYYY-MM-DD'),
        endDate: dateRange.value[1]?.format('YYYY-MM-DD')
      }
    })
    if (response.code === 200) {
      Object.assign(summary, response.data)
    }
  } catch (error) {
    ElMessage.error('加载汇总数据失败')
  }
}

const loadDrugRanking = async () => {
  try {
    const response = await axios.get('/reports/consumption/drug-ranking', {
      params: { limit: 10 }
    })
    if (response.code === 200) {
      drugRanking.value = response.data.map((item, index) => ({ ...item, rank: index + 1 }))
    }
  } catch (error) {
    ElMessage.error('加载药品排名失败')
  }
}

const loadDepartmentStats = async () => {
  try {
    const response = await axios.get('/reports/consumption/department-stats')
    if (response.code === 200) {
      departmentStats.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载科室统计失败')
  }
}

const loadDoctorStats = async () => {
  try {
    const response = await axios.get('/reports/consumption/doctor-stats')
    if (response.code === 200) {
      doctorStats.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载医生统计失败')
  }
}

const loadTrend = async () => {
  try {
    const response = await axios.get('/reports/consumption/trend')
    if (response.code === 200) {
      trendData.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载趋势数据失败')
  }
}

const exportReport = async () => {
  try {
    const response = await axios.get('/reports/consumption/export', {
      params: {
        departmentId: departmentId.value === 0 ? undefined : departmentId.value,
        startDate: dateRange.value[0]?.format('YYYY-MM-DD'),
        endDate: dateRange.value[1]?.format('YYYY-MM-DD')
      },
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
  loadDepartments()
  refreshReport()
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
