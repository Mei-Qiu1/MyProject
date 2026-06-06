
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const supplierId = ref(0)
const dateRange = ref([])
const suppliers = ref([])
const isMounted = ref(false)
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

const formatDate = (date) => {
  if (date instanceof Date) {
    return date.toISOString().split('T')[0]
  } else if (typeof date === 'string') {
    return date.split('T')[0]
  }
  return null
}

const loadSummary = async () => {
  try {
    const params = {}
    if (supplierId.value !== 0) {
      params.supplierId = supplierId.value
    }
    if (dateRange.value.length >= 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const response = await axios.get('/reports/purchase/summary', { params })
    if (response.code === 200) {
      Object.assign(summary, response.data)
    } else {
      console.warn('API返回非200状态码:', response.code)
      loadMockSummary()
    }
  } catch (error) {
    console.error('加载汇总数据失败, 使用模拟数据:', error)
    loadMockSummary()
  }
}

const loadMockSummary = () => {
  Object.assign(summary, {
    orderCount: 15,
    totalAmount: 298000.00,
    completedCount: 8,
    pendingCount: 7
  })
}

const loadDetail = async () => {
  try {
    const params = {
      page: pagination.current,
      size: pagination.size
    }
    if (supplierId.value !== 0) {
      params.supplierId = supplierId.value
    }
    if (dateRange.value.length >= 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const response = await axios.get('/reports/purchase/detail', { params })
    if (response.code === 200) {
      orderDetail.value = response.data.records
      pagination.total = response.data.total
    } else {
      console.warn('API返回非200状态码:', response.code)
      loadMockDetail()
    }
  } catch (error) {
    console.error('加载订单明细失败, 使用模拟数据:', error)
    loadMockDetail()
  }
}

const loadMockDetail = () => {
  orderDetail.value = [
    { orderNo: 'PO20240601001', supplierName: '华北制药集团有限公司', orderDate: '2024-06-01', totalAmount: 55000.00, status: 4, deliveryDate: '2024-06-05' },
    { orderNo: 'PO20240605002', supplierName: '拜耳医药保健有限公司', orderDate: '2024-06-05', totalAmount: 48000.00, status: 4, deliveryDate: '2024-06-10' },
    { orderNo: 'PO20240610003', supplierName: '国药集团药业股份有限公司', orderDate: '2024-06-10', totalAmount: 62000.00, status: 3, deliveryDate: '2024-06-15' },
    { orderNo: 'PO20240615004', supplierName: '华北制药集团有限公司', orderDate: '2024-06-15', totalAmount: 35000.00, status: 2, deliveryDate: '2024-06-20' },
    { orderNo: 'PO20240620005', supplierName: '拜耳医药保健有限公司', orderDate: '2024-06-20', totalAmount: 58000.00, status: 1, deliveryDate: '2024-06-25' },
    { orderNo: 'PO20240622006', supplierName: '国药集团药业股份有限公司', orderDate: '2024-06-22', totalAmount: 40000.00, status: 3, deliveryDate: '2024-06-27' },
    { orderNo: 'PO20240625007', supplierName: '华北制药集团有限公司', orderDate: '2024-06-25', totalAmount: 32000.00, status: 2, deliveryDate: '2024-06-30' },
    { orderNo: 'PO20240628008', supplierName: '拜耳医药保健有限公司', orderDate: '2024-06-28', totalAmount: 55000.00, status: 1, deliveryDate: '2024-07-03' },
    { orderNo: 'PO20240701009', supplierName: '国药集团药业股份有限公司', orderDate: '2024-07-01', totalAmount: 48000.00, status: 4, deliveryDate: '2024-07-05' },
    { orderNo: 'PO20240705010', supplierName: '华北制药集团有限公司', orderDate: '2024-07-05', totalAmount: 38000.00, status: 3, deliveryDate: '2024-07-10' }
  ]
  pagination.total = 15
}

const loadSupplierStats = async () => {
  try {
    const response = await axios.get('/reports/purchase/supplier-stats')
    if (response.code === 200) {
      supplierStats.value = response.data
    } else {
      console.warn('API返回非200状态码:', response.code)
      loadMockSupplierStats()
    }
  } catch (error) {
    console.error('加载供应商统计失败, 使用模拟数据:', error)
    loadMockSupplierStats()
  }
}

const loadMockSupplierStats = () => {
  supplierStats.value = [
    { supplierName: '华北制药集团有限公司', orderCount: 5, totalAmount: 125000.00, avgDeliveryDays: 4.5, complianceRate: 98 },
    { supplierName: '拜耳医药保健有限公司', orderCount: 4, totalAmount: 161000.00, avgDeliveryDays: 5.2, complianceRate: 100 },
    { supplierName: '国药集团药业股份有限公司', orderCount: 6, totalAmount: 150000.00, avgDeliveryDays: 4.0, complianceRate: 95 },
    { supplierName: '山东新华制药股份有限公司', orderCount: 3, totalAmount: 85000.00, avgDeliveryDays: 5.8, complianceRate: 90 }
  ]
}

const loadTrend = async () => {
  try {
    const response = await axios.get('/reports/purchase/trend')
    if (response.code === 200) {
      trendData.value = response.data
    } else {
      console.warn('API返回非200状态码:', response.code)
      loadMockTrend()
    }
  } catch (error) {
    console.error('加载趋势数据失败, 使用模拟数据:', error)
    loadMockTrend()
  }
}

const loadMockTrend = () => {
  trendData.value = [
    { month: '1月', amount: 125000.00 },
    { month: '2月', amount: 138000.00 },
    { month: '3月', amount: 142000.00 },
    { month: '4月', amount: 156000.00 },
    { month: '5月', amount: 148000.00 },
    { month: '6月', amount: 165000.00 }
  ]
}

const handlePageChange = (page) => {
  pagination.current = page
  loadDetail()
}

onMounted(() => {
  isMounted.value = true
  loadSuppliers()
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
