
<template>
  <div class="report-container">
    <div class="report-header">
      <h2>库存报表</h2>
      <div class="report-actions">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
        <el-button type="primary" @click="refreshReport">刷新报表</el-button>
        <el-button type="warning" @click="exportReport">导出Excel</el-button>
      </div>
    </div>
    
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-value">{{ summary.totalDrugCount }}</div>
        <div class="stat-label">药品种类数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.totalQuantity }}</div>
        <div class="stat-label">库存总数量</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">¥{{ summary.totalAmount.toLocaleString() }}</div>
        <div class="stat-label">库存总金额</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.expiringCount }}</div>
        <div class="stat-label">近效期药品数</div>
      </div>
    </div>
    
    <div class="report-section">
      <h3>库存药品明细</h3>
      <el-table :data="inventoryDetail" border>
        <el-table-column prop="drugCode" label="药品编码" />
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="quantity" label="库存数量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="warehouseName" label="仓库" />
      </el-table>
      <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @current-change="handlePageChange"
        layout="prev, pager, next, jumper"
      ></el-pagination>
    </div>
    
    <div class="report-section">
      <h3>库存周转率分析</h3>
      <div class="chart-container">
        <div v-for="item in turnoverData" :key="item.month" class="bar-item">
          <div class="bar" :style="{ height: item.rate * 3 + 'px', background: getBarColor(item.rate) }"></div>
          <span>{{ item.month }}</span>
        </div>
      </div>
    </div>
    
    <div class="report-section">
      <h3>ABC分类分析</h3>
      <el-table :data="abcData" border>
        <el-table-column prop="level" label="类别">
          <template #default="scope">
            <el-tag :type="scope.row.level === 'A' ? 'danger' : scope.row.level === 'B' ? 'warning' : 'success'">
              {{ scope.row.level }}类
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="percentage" label="占比">
          <template #default="scope">
            {{ scope.row.percentage }}%
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const dateRange = ref([])
const summary = reactive({
  totalDrugCount: 0,
  totalQuantity: 0,
  totalAmount: 0,
  expiringCount: 0
})
const inventoryDetail = ref([])
const turnoverData = ref([])
const abcData = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getBarColor = (rate) => {
  if (rate > 5) return '#f56c6c'
  if (rate > 3) return '#e6a23c'
  return '#67c23a'
}

const refreshReport = async () => {
  await loadSummary()
  await loadDetail()
  await loadTurnover()
  await loadABC()
}

const loadSummary = async () => {
  try {
    const response = await axios.get('/reports/inventory/summary')
    if (response.code === 200) {
      Object.assign(summary, response.data)
    }
  } catch (error) {
    ElMessage.error('加载汇总数据失败')
  }
}

const loadDetail = async () => {
  try {
    const response = await axios.get('/reports/inventory/detail', {
      params: {
        page: pagination.current,
        size: pagination.size
      }
    })
    if (response.code === 200) {
      inventoryDetail.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载明细数据失败')
  }
}

const loadTurnover = async () => {
  try {
    const response = await axios.get('/reports/inventory/turnover')
    if (response.code === 200) {
      turnoverData.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载周转率数据失败')
  }
}

const loadABC = async () => {
  try {
    const response = await axios.get('/reports/inventory/abc')
    if (response.code === 200) {
      abcData.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载ABC分析数据失败')
  }
}

const handlePageChange = (page) => {
  pagination.current = page
  loadDetail()
}

const exportReport = async () => {
  try {
    const response = await axios.get('/reports/inventory/export', { responseType: 'blob' })
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '库存报表.xlsx'
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  border-radius: 8px 8px 0 0;
  min-height: 10px;
}
</style>
