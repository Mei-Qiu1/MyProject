<template>
  <div class="dashboard">
    <h2>采购员工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon request-icon">
            <Document />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ pendingRequests }}</p>
            <p class="stat-label">待处理申请</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon order-icon">
            <ShoppingCart />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ pendingOrders }}</p>
            <p class="stat-label">待收货订单</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon supplier-icon">
            <OfficeBuilding />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ supplierCount }}</p>
            <p class="stat-label">供应商数量</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon amount-icon">
            <Money />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ monthlyAmount }}</p>
            <p class="stat-label">本月采购额</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="快捷操作">
          <el-space wrap style="width: 100%;">
            <el-button type="primary" @click="goTo('/purchase/requests')">采购申请</el-button>
            <el-button type="success" @click="goTo('/purchase/orders')">采购订单</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="待处理采购申请">
          <el-table :data="requests" border style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="quantity" label="申请数量" />
            <el-table-column prop="applicant" label="申请人" />
            <el-table-column prop="createTime" label="申请时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" type="primary" @click="createOrder(scope.$index)">创建订单</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="供应商信息">
          <el-table :data="suppliers" border style="width: 100%">
            <el-table-column prop="supplierName" label="供应商名称" />
            <el-table-column prop="contactName" label="联系人" />
            <el-table-column prop="phone" label="联系电话" />
            <el-table-column prop="cooperationStatus" label="合作状态" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../../utils/axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const pendingRequests = ref(0)
const pendingOrders = ref(0)
const supplierCount = ref(0)
const monthlyAmount = ref(0)
const requests = ref([])
const suppliers = ref([])

const goTo = (path) => {
  router.push(path)
}

const createOrder = (index) => {
  if (index === null || index === undefined || index < 0) {
    ElMessage.error('无效的请求索引')
    return
  }
  
  const request = requests.value[index]
  if (!request) {
    ElMessage.error('请求数据不存在')
    return
  }
  
  let requestId = null
  
  if (request.id) {
    if (typeof request.id === 'string') {
      const match = request.id.match(/(\d+)/)
      requestId = match ? parseInt(match[1]) : null
    } else {
      requestId = parseInt(request.id)
    }
  }
  
  if (!requestId || requestId <= 0) {
    ElMessage.error('无效的申请单ID')
    return
  }
  
  console.log('Creating order for request ID:', requestId)
  
  axios.post('/purchase/orders/from-request/' + requestId)
    .then(response => {
      ElMessage.success('订单创建成功')
      loadDashboardData()
    })
    .catch(error => {
      console.error('Order creation error:', error)
      ElMessage.error('创建订单失败: ' + (error.response?.data?.message || error.message))
    })
}

const loadDashboardData = () => {
  axios.get('/dashboard/purchaser')
    .then(response => {
      const data = response.data
      pendingRequests.value = data.pendingRequests
      pendingOrders.value = data.pendingOrders
      supplierCount.value = data.supplierCount
      monthlyAmount.value = data.monthlyAmount || '¥0'
      requests.value = data.requests || []
      suppliers.value = data.suppliers || []
    })
    .catch(error => {
      console.error('Failed to load dashboard data:', error)
      pendingRequests.value = 0
      pendingOrders.value = 0
      supplierCount.value = 0
      monthlyAmount.value = '¥0'
      requests.value = []
      suppliers.value = []
    })
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard { padding: 20px; }
.stat-card { display: flex; align-items: center; gap: 15px; }
.stat-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
.request-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.order-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
.supplier-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); color: white; }
.amount-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; margin: 0; color: #333; }
.stat-label { font-size: 14px; color: #999; margin: 0; }
</style>