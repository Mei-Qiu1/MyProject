<template>
  <div class="dashboard">
    <h2>药剂师工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon prescription-icon">
            <Document />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ pendingPrescriptions }}</p>
            <p class="stat-label">待审核处方</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon dispensing-icon">
            <Clock />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ todayDispensing }}</p>
            <p class="stat-label">今日调配</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon warning-icon">
            <Warning />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ lowStockCount }}</p>
            <p class="stat-label">库存预警</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon special-icon">
            <Lock />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ specialDrugsCount }}</p>
            <p class="stat-label">特殊药品</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="快捷操作">
          <el-space wrap style="width: 100%;">
            <el-button type="primary" @click="goTo('/pharmacy/prescriptions')">处方审核</el-button>
            <el-button type="success" @click="goTo('/pharmacy/dispensing')">药品调配</el-button>
            <el-button type="warning" @click="goTo('/inventory/warning')">库存预警</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="待审核处方">
          <el-table :data="prescriptions" border style="width: 100%">
            <el-table-column prop="patientName" label="患者" />
            <el-table-column prop="doctorName" label="医生" />
            <el-table-column prop="createTime" label="提交时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" type="primary">审核</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="库存预警药品">
          <el-table :data="lowStockDrugs" border style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="warehouse" label="仓库" />
            <el-table-column prop="quantity" label="当前库存" />
            <el-table-column prop="minStock" label="最低库存" />
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

const router = useRouter()
const pendingPrescriptions = ref(0)
const todayDispensing = ref(0)
const lowStockCount = ref(0)
const specialDrugsCount = ref(0)
const prescriptions = ref([])
const lowStockDrugs = ref([])

const goTo = (path) => {
  router.push(path)
}

const loadDashboardData = () => {
  axios.get('/dashboard/pharmacist')
    .then(response => {
      const data = response.data
      pendingPrescriptions.value = data.pendingPrescriptions
      todayDispensing.value = data.todayDispensing
      lowStockCount.value = data.lowStockCount
      specialDrugsCount.value = data.specialDrugsCount
      prescriptions.value = data.prescriptions || []
      lowStockDrugs.value = data.lowStockDrugs || []
    })
    .catch(error => {
      console.error('Failed to load dashboard data:', error)
      pendingPrescriptions.value = 0
      todayDispensing.value = 0
      lowStockCount.value = 0
      specialDrugsCount.value = 0
      prescriptions.value = []
      lowStockDrugs.value = []
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
.prescription-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.dispensing-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
.warning-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); color: white; }
.special-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; margin: 0; color: #333; }
.stat-label { font-size: 14px; color: #999; margin: 0; }
</style>