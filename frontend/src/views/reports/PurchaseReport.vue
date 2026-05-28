
<template>
  <div class="report-container">
    <div class="report-header">
      <h2>采购报表</h2>
      <div class="report-actions">
        <el-select v-model="supplierId" placeholder="选择供应商">
          <el-option label="全部" :value="0"></el-option>
          <el-option v-for="sup in suppliers" :key="sup.id" :label="sup.supplierName" :value="sup.id"></el-option>
        </el-select>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
        <el-button type="primary" @click="refreshReport">刷新报表</el-button>
        <el-button type="warning" @click="exportReport">导出Excel</el-button>
      </div>
    </div>
    
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-value">{{ summary.orderCount }}</div>
        <div class="stat-label">采购订单数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.totalAmount.toLocaleString() }}</div>
        <div class="stat-label">采购总金额</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.completedCount }}</div>
        <div class="stat-label">已完成订单</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ summary.pendingCount }}</div>
        <div class="stat-label">待收货订单</div>
      </div>
    </div>
    
    <div class="report-section">
      <h3>采购订单明细</h3>
      <el-table :data="orderDetail" border>
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="orderDate" label="下单日期" />
        <el-table-column prop="totalAmount" label="订单金额" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deliveryDate" label="预计到货" />
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
      <h3>供应商供货统计</h3>
      <el-table :data="supplierStats" border>
        <el-table-column prop="supplierName" label="供应商名称" />
        <el-table-column prop="orderCount" label="订单数" />
        <el-table-column prop="totalAmount" label="供货金额" />
        <el-table-column prop="avgDeliveryDays" label="平均到货天数" />
        <el-table-column prop="complianceRate" label="履约率">
          <template #default="scope">
            {{ scope.row.complianceRate }}%
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <div class="report-section">
      <h3>月度采购趋势</h3>
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

const supplierId = ref(0)
const dateRange = ref([])
const suppliers = ref([])
const summary = reactive({
  orderCount: 0,
  totalAmount: 0,
  completedCount: 0,
  pendingCount: 0
})
const orderDetail = ref([])
const supplierStats = ref([])
const trendData = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const maxAmount = computed(() => {
  return Math.max(...trendData.value.map(t => t.amount), 1)
})

const statusNames = { 1: '待发货', 2: '已发货', 3: '已验收', 4: '已完成' }
const statusTagTypes = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info' }

const getStatusName = (status) => statusNames[status] || '未知'
const getStatusTagType = (status) => statusTagTypes[status] || 'default'

const refreshReport = async () => {
  await loadSummary()
  await loadDetail()
  await loadSupplierStats()
  await loadTrend()
}

const loadSuppliers = async () => {
  try {
    const response = await axios.get('/drugs/suppliers')
    if (response.code === 200) {
      suppliers.value = response.data.records || response.data
    }
  } catch (error) {
    ElMessage.error('加载供应商失败')
  }
}

const loadSummary = async () => {
  try {
    const response = await axios.get('/reports/purchase/summary', {
      params: {
        supplierId: supplierId.value === 0 ? undefined : supplierId.value,
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

const loadDetail = async () => {
  try {
    const response = await axios.get('/reports/purchase/detail', {
      params: {
        page: pagination.current,
        size: pagination.size,
        supplierId: supplierId.value === 0 ? undefined : supplierId.value
      }
    })
    if (response.code === 200) {
      orderDetail.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载订单明细失败')
  }
}

const loadSupplierStats = async () => {
  try {
    const response = await axios.get('/reports/purchase/supplier-stats')
    if (response.code === 200) {
      supplierStats.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载供应商统计失败')
  }
}

const loadTrend = async () => {
  try {
    const response = await axios.get('/reports/purchase/trend')
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
    const response = await axios.get('/reports/purchase/export', {
      params: {
        supplierId: supplierId.value === 0 ? undefined : supplierId.value,
        startDate: dateRange.value[0]?.format('YYYY-MM-DD'),
        endDate: dateRange.value[1]?.format('YYYY-MM-DD')
      },
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '采购报表.xlsx'
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
  loadSuppliers()
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
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
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
  background: linear-gradient(180deg, #11998e 0%, #38ef7d 100%);
  border-radius: 8px 8px 0 0;
  min-height: 10px;
}
</style>
