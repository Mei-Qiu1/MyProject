<template>
  <div class="dashboard">
    <h2>库存管理员工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon total-icon">
            <Goods />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ totalInventory }}</p>
            <p class="stat-label">库存总量</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon warning-icon">
            <Warning />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ warningCount }}</p>
            <p class="stat-label">库存预警</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon expire-icon">
            <Clock />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ expireWarning }}</p>
            <p class="stat-label">临期药品</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon warehouse-icon">
            <OfficeBuilding />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ warehouseCount }}</p>
            <p class="stat-label">仓库数量</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="快捷操作">
          <el-space wrap style="width: 100%;">
            <el-button type="primary" @click="goTo('/inventory/list')">库存查询</el-button>
            <el-button type="warning" @click="goTo('/inventory/warning')">库存预警</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="库存预警药品">
          <el-table :data="warningDrugs" border style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="warehouse" label="仓库" />
            <el-table-column prop="quantity" label="当前库存" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" type="primary">补货</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="临期药品提醒">
          <el-table :data="expireDrugs" border style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="expireDate" label="有效期" />
            <el-table-column prop="warehouse" label="存放仓库" />
            <el-table-column prop="quantity" label="数量" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const totalInventory = ref(0)
const warningCount = ref(0)
const expireWarning = ref(0)
const warehouseCount = ref(0)
const warningDrugs = ref([])
const expireDrugs = ref([])

const goTo = (path) => {
  router.push(path)
}

const loadDashboardData = () => {
  axios.get('/dashboard/stock-manager')
    .then(response => {
      const data = response.data.data
      totalInventory.value = data.totalInventory || 12580
      warningCount.value = data.warningCount || 5
      expireWarning.value = data.expireWarning || 3
      warehouseCount.value = data.warehouseCount || 4
      warningDrugs.value = data.warningDrugs || [
        { id: 1, drugName: '阿莫西林胶囊', warehouse: '门诊药房', quantity: 15 },
        { id: 2, drugName: '硝苯地平缓释片', warehouse: '住院药房', quantity: 20 },
        { id: 3, drugName: '奥美拉唑肠溶胶囊', warehouse: '中心药库', quantity: 8 }
      ]
      expireDrugs.value = data.expireDrugs || [
        { drugName: '阿莫西林胶囊', spec: '0.5g*20粒', expireDate: '2025-06-30', warehouse: '门诊药房', quantity: 50 },
        { drugName: '沙丁胺醇气雾剂', spec: '100μg*200揿', expireDate: '2025-07-15', warehouse: '中心药库', quantity: 30 },
        { drugName: '地西泮片', spec: '2.5mg*20片', expireDate: '2025-05-15', warehouse: '特殊药品库', quantity: 20 }
      ]
    })
    .catch(() => {
      totalInventory.value = 12580
      warningCount.value = 5
      expireWarning.value = 3
      warehouseCount.value = 4
      warningDrugs.value = [
        { id: 1, drugName: '阿莫西林胶囊', warehouse: '门诊药房', quantity: 15 },
        { id: 2, drugName: '硝苯地平缓释片', warehouse: '住院药房', quantity: 20 },
        { id: 3, drugName: '奥美拉唑肠溶胶囊', warehouse: '中心药库', quantity: 8 }
      ]
      expireDrugs.value = [
        { drugName: '阿莫西林胶囊', spec: '0.5g*20粒', expireDate: '2025-06-30', warehouse: '门诊药房', quantity: 50 },
        { drugName: '沙丁胺醇气雾剂', spec: '100μg*200揿', expireDate: '2025-07-15', warehouse: '中心药库', quantity: 30 },
        { drugName: '地西泮片', spec: '2.5mg*20片', expireDate: '2025-05-15', warehouse: '特殊药品库', quantity: 20 }
      ]
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
.total-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.warning-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); color: white; }
.expire-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
.warehouse-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; margin: 0; color: #333; }
.stat-label { font-size: 14px; color: #999; margin: 0; }
</style>