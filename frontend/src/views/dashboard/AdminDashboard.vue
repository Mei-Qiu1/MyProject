<template>
  <div class="dashboard">
    <h2>系统管理员控制台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon admin-icon">
            <User />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ userCount }}</p>
            <p class="stat-label">系统用户</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon drug-icon">
            <Pill />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ drugCount }}</p>
            <p class="stat-label">药品数量</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon inventory-icon">
            <Package />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ inventoryCount }}</p>
            <p class="stat-label">库存总量</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon order-icon">
            <ShoppingCart />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ orderCount }}</p>
            <p class="stat-label">采购订单</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="系统操作快捷入口">
          <el-space wrap style="width: 100%;">
            <el-button type="primary" @click="goTo('/system/users')">用户管理</el-button>
            <el-button type="success" @click="goTo('/drugs/list')">药品管理</el-button>
            <el-button type="warning" @click="goTo('/purchase/orders')">采购管理</el-button>
            <el-button type="info" @click="goTo('/inventory/list')">库存管理</el-button>
            <el-button type="danger" @click="goTo('/special/drugs')">特殊药品</el-button>
            <el-button type="default" @click="goTo('/system/logs')">系统日志</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="最近操作日志">
          <el-timeline>
            <el-timeline-item v-for="log in recentLogs" :key="log.id" :timestamp="log.time">
              <p>{{ log.action }}</p>
              <p style="color:#999;font-size:12px">{{ log.operator }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../../utils/axios'

const router = useRouter()
const userCount = ref(0)
const drugCount = ref(0)
const inventoryCount = ref(0)
const orderCount = ref(0)
const recentLogs = ref([])

const goTo = (path) => {
  router.push(path)
}

const loadDashboardData = () => {
  axios.get('/dashboard/admin')
    .then(response => {
      const data = response.data
      userCount.value = data.userCount
      drugCount.value = data.drugCount
      inventoryCount.value = data.inventoryCount
      orderCount.value = data.orderCount
      recentLogs.value = data.recentLogs || []
    })
    .catch(error => {
      console.error('Failed to load dashboard data:', error)
      userCount.value = 0
      drugCount.value = 0
      inventoryCount.value = 0
      orderCount.value = 0
      recentLogs.value = []
    })
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
}
.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.admin-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.drug-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
.inventory-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); color: white; }
.order-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #333;
}
.stat-label {
  font-size: 14px;
  color: #999;
  margin: 0;
}
</style>