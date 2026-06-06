<template>
  <div class="dashboard">
    <h2>医生工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon order-icon">
            <Document />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ todayOrders }}</p>
            <p class="stat-label">今日医嘱</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon dispensing-icon">
            <Clock />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ pendingDispensing }}</p>
            <p class="stat-label">待调配</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon patient-icon">
            <User />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ patientCount }}</p>
            <p class="stat-label">今日接诊</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon drug-icon">
            <Pill />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ commonDrugs }}</p>
            <p class="stat-label">常用药品</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="快捷操作">
          <el-space wrap style="width: 100%;">
            <el-button type="primary" @click="goTo('/doctor/dispensing')">开具处方</el-button>
            <el-button type="success" @click="goTo('/clinical/orders')">医嘱管理</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="我的待处理医嘱">
          <el-table :data="orders" border style="width: 100%">
            <el-table-column prop="patientName" label="患者姓名" />
            <el-table-column prop="ward" label="病区/床号" />
            <el-table-column prop="orderType" label="医嘱类型" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" @click="goToOrder(scope.row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="常用药品快速选择">
          <el-scrollbar style="height: 150px;">
            <el-space wrap>
              <el-tag v-for="drug in commonDrugsList" :key="drug.id" closable @close="removeDrug(drug.id)" style="cursor: pointer;" @click="selectDrug(drug)">
                {{ drug.name }} ({{ drug.spec }})
              </el-tag>
            </el-space>
          </el-scrollbar>
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
const todayOrders = ref(0)
const pendingDispensing = ref(0)
const patientCount = ref(0)
const commonDrugs = ref(0)
const orders = ref([])
const commonDrugsList = ref([])

const goTo = (path) => {
  router.push(path)
}

const goToOrder = (id) => {
  router.push('/clinical/orders')
}

const selectDrug = (drug) => {
  router.push('/doctor/dispensing')
}

const removeDrug = (id) => {
  commonDrugsList.value = commonDrugsList.value.filter(d => d.id !== id)
}

const loadDashboardData = () => {
  axios.get('/dashboard/doctor')
    .then(response => {
      const data = response.data
      todayOrders.value = data.todayOrders
      pendingDispensing.value = data.pendingDispensing
      patientCount.value = data.patientCount
      commonDrugs.value = data.commonDrugs
      orders.value = data.orders || []
      commonDrugsList.value = data.commonDrugsList || []
    })
    .catch(error => {
      console.error('Failed to load dashboard data:', error)
      todayOrders.value = 0
      pendingDispensing.value = 0
      patientCount.value = 0
      commonDrugs.value = 0
      orders.value = []
      commonDrugsList.value = []
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
.order-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.dispensing-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
.patient-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); color: white; }
.drug-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; margin: 0; color: #333; }
.stat-label { font-size: 14px; color: #999; margin: 0; }
</style>