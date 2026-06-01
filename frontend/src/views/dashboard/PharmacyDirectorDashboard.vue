<template>
  <div class="dashboard">
    <h2>药剂科主任工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon approval-icon">
            <ClipboardCheck />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ pendingApprovals }}</p>
            <p class="stat-label">待审批采购</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon special-icon">
            <Lock />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ specialDrugs }}</p>
            <p class="stat-label">特殊药品</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon report-icon">
            <BarChart />
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ monthlyAmount }}</p>
            <p class="stat-label">本月采购额</p>
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
            <p class="stat-label">药品种类</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card title="快捷操作">
          <el-space wrap style="width: 100%;">
            <el-button type="primary" @click="goTo('/purchase/requests')">采购审批</el-button>
            <el-button type="success" @click="goTo('/special/drugs')">特殊药品管理</el-button>
            <el-button type="warning" @click="goTo('/reports/inventory')">库存报表</el-button>
            <el-button type="info" @click="goTo('/reports/purchase')">采购报表</el-button>
            <el-button type="default" @click="goTo('/reports/consumption')">消耗报表</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card title="待审批采购申请">
          <el-table :data="pendingRequests" border style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="quantity" label="申请数量" />
            <el-table-column prop="applicant" label="申请人" />
            <el-table-column prop="createTime" label="申请时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" type="primary">审批</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card title="特殊药品库存概览">
          <el-table :data="specialDrugStock" border style="width: 100%">
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
const pendingApprovals = ref(0)
const specialDrugs = ref(0)
const monthlyAmount = ref(0)
const drugCount = ref(0)
const pendingRequests = ref([])
const specialDrugStock = ref([])

const goTo = (path) => {
  router.push(path)
}

const loadDashboardData = () => {
  axios.get('/dashboard/pharmacy-director')
    .then(response => {
      const data = response.data.data
      pendingApprovals.value = data.pendingApprovals || 6
      specialDrugs.value = data.specialDrugs || 12
      monthlyAmount.value = data.monthlyAmount || '¥156,800'
      drugCount.value = data.drugCount || 356
      pendingRequests.value = data.pendingRequests || [
        { id: 1, drugName: '阿莫西林胶囊', quantity: 200, applicant: '张药师', createTime: '09:00' },
        { id: 2, drugName: '硝苯地平缓释片', quantity: 150, applicant: '李药师', createTime: '09:30' },
        { id: 3, drugName: '奥美拉唑肠溶胶囊', quantity: 100, applicant: '王药师', createTime: '10:00' }
      ]
      specialDrugStock.value = data.specialDrugStock || [
        { drugName: '吗啡注射液', spec: '10mg/1ml*5支', category: '麻醉药品', quantity: 30, warehouse: '特殊药品库' },
        { drugName: '地西泮片', spec: '2.5mg*20片', category: '精神药品', quantity: 50, warehouse: '特殊药品库' },
        { drugName: '哌替啶注射液', spec: '50mg/2ml*5支', category: '麻醉药品', quantity: 25, warehouse: '特殊药品库' }
      ]
    })
    .catch(() => {
      pendingApprovals.value = 6
      specialDrugs.value = 12
      monthlyAmount.value = '¥156,800'
      drugCount.value = 356
      pendingRequests.value = [
        { id: 1, drugName: '阿莫西林胶囊', quantity: 200, applicant: '张药师', createTime: '09:00' },
        { id: 2, drugName: '硝苯地平缓释片', quantity: 150, applicant: '李药师', createTime: '09:30' },
        { id: 3, drugName: '奥美拉唑肠溶胶囊', quantity: 100, applicant: '王药师', createTime: '10:00' }
      ]
      specialDrugStock.value = [
        { drugName: '吗啡注射液', spec: '10mg/1ml*5支', category: '麻醉药品', quantity: 30, warehouse: '特殊药品库' },
        { drugName: '地西泮片', spec: '2.5mg*20片', category: '精神药品', quantity: 50, warehouse: '特殊药品库' },
        { drugName: '哌替啶注射液', spec: '50mg/2ml*5支', category: '麻醉药品', quantity: 25, warehouse: '特殊药品库' }
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
.approval-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.special-icon { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); color: white; }
.report-icon { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; }
.drug-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; margin: 0; color: #333; }
.stat-label { font-size: 14px; color: #999; margin: 0; }
</style>