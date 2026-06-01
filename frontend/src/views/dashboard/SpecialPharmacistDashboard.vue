<template>
  <div class="dashboard">
    <h2>特殊药品管理员工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon special-icon">
            <Lock />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ specialDrugCount }}</p>
            <p class="stat-label">特殊药品种类</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon record-icon">
            <Document />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ todayRecords }}</p>
            <p class="stat-label">今日发放记录</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon inventory-icon">
            <Goods />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ specialInventory }}</p>
            <p class="stat-label">特殊药品库存</p>
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
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="快捷操作">
          <el-space wrap style="width: 100%;">
            <el-button type="primary" @click="goTo('/special/drugs')">特殊药品管理</el-button>
            <el-button type="success" @click="goTo('/inventory/list')">库存查询</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="今日发放记录">
          <el-table :data="records" border style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="patientName" label="患者" />
            <el-table-column prop="doctorName" label="开方医生" />
            <el-table-column prop="quantity" label="发放数量" />
            <el-table-column prop="createTime" label="发放时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="特殊药品库存">
          <el-table :data="specialDrugs" border style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="category" label="药品类别" />
            <el-table-column prop="quantity" label="库存数量" />
            <el-table-column prop="warehouse" label="存放位置" />
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
const specialDrugCount = ref(0)
const todayRecords = ref(0)
const specialInventory = ref(0)
const lowStockCount = ref(0)
const records = ref([])
const specialDrugs = ref([])

const goTo = (path) => {
  router.push(path)
}

const loadDashboardData = () => {
  axios.get('/dashboard/special-pharmacist')
    .then(response => {
      const data = response.data.data
      specialDrugCount.value = data.specialDrugCount || 12
      todayRecords.value = data.todayRecords || 8
      specialInventory.value = data.specialInventory || 350
      lowStockCount.value = data.lowStockCount || 2
      records.value = data.records || [
        { drugName: '吗啡注射液', patientName: '张三', doctorName: '李医生', quantity: '1支', createTime: '09:00' },
        { drugName: '地西泮片', patientName: '李四', doctorName: '王医生', quantity: '20片', createTime: '09:30' },
        { drugName: '吗啡注射液', patientName: '王五', doctorName: '张医生', quantity: '2支', createTime: '10:00' }
      ]
      specialDrugs.value = data.specialDrugs || [
        { drugName: '吗啡注射液', spec: '10mg/1ml*5支', category: '麻醉药品', quantity: 30, warehouse: '特殊药品库' },
        { drugName: '地西泮片', spec: '2.5mg*20片', category: '精神药品', quantity: 50, warehouse: '特殊药品库' },
        { drugName: '哌替啶注射液', spec: '50mg/2ml*5支', category: '麻醉药品', quantity: 25, warehouse: '特殊药品库' },
        { drugName: '阿普唑仑片', spec: '0.4mg*20片', category: '精神药品', quantity: 40, warehouse: '特殊药品库' }
      ]
    })
    .catch(() => {
      specialDrugCount.value = 12
      todayRecords.value = 8
      specialInventory.value = 350
      lowStockCount.value = 2
      records.value = [
        { drugName: '吗啡注射液', patientName: '张三', doctorName: '李医生', quantity: '1支', createTime: '09:00' },
        { drugName: '地西泮片', patientName: '李四', doctorName: '王医生', quantity: '20片', createTime: '09:30' },
        { drugName: '吗啡注射液', patientName: '王五', doctorName: '张医生', quantity: '2支', createTime: '10:00' }
      ]
      specialDrugs.value = [
        { drugName: '吗啡注射液', spec: '10mg/1ml*5支', category: '麻醉药品', quantity: 30, warehouse: '特殊药品库' },
        { drugName: '地西泮片', spec: '2.5mg*20片', category: '精神药品', quantity: 50, warehouse: '特殊药品库' },
        { drugName: '哌替啶注射液', spec: '50mg/2ml*5支', category: '麻醉药品', quantity: 25, warehouse: '特殊药品库' },
        { drugName: '阿普唑仑片', spec: '0.4mg*20片', category: '精神药品', quantity: 40, warehouse: '特殊药品库' }
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
.special-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.record-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
.inventory-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); color: white; }
.warning-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; margin: 0; color: #333; }
.stat-label { font-size: 14px; color: #999; margin: 0; }
</style>