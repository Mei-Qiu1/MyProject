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
                <el-button size="small" type="primary" @click="openAuditDialog(scope.row)">审批</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 审批对话框 -->
  <el-dialog title="审批采购申请" v-model="showAuditDialog" width="400px">
    <el-form :model="auditForm" label-width="80px">
      <el-form-item label="药品名称">
        <span>{{ currentRequest?.drugName }}</span>
      </el-form-item>
      <el-form-item label="申请数量">
        <span>{{ currentRequest?.quantity }}</span>
      </el-form-item>
      <el-form-item label="审批结果">
        <el-select v-model="auditForm.status">
          <el-option label="通过" :value="2" />
          <el-option label="拒绝" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="审批意见">
        <el-textarea v-model="auditForm.comment" rows="3"></el-textarea>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showAuditDialog = false">取消</el-button>
      <el-button type="primary" @click="handleAudit">确认审批</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const router = useRouter()
const pendingApprovals = ref(0)
const specialDrugs = ref(0)
const monthlyAmount = ref(0)
const drugCount = ref(0)
const pendingRequests = ref([])
const specialDrugStock = ref([])

// 审批对话框相关
const showAuditDialog = ref(false)
const currentRequest = ref(null)
const auditForm = ref({
  status: 2,
  comment: ''
})

const goTo = (path) => {
  router.push(path)
}

const loadDashboardData = () => {
  axios.get('/dashboard/pharmacy-director')
    .then(response => {
      const data = response.data
      pendingApprovals.value = data.pendingApprovals
      specialDrugs.value = data.specialDrugs
      monthlyAmount.value = data.monthlyAmount || '¥0'
      drugCount.value = data.drugCount
      pendingRequests.value = data.pendingRequests || []
      specialDrugStock.value = data.specialDrugStock || []
    })
    .catch(error => {
      console.error('Failed to load dashboard data:', error)
      pendingApprovals.value = 0
      specialDrugs.value = 0
      monthlyAmount.value = '¥0'
      drugCount.value = 0
      pendingRequests.value = []
      specialDrugStock.value = []
    })
}

const openAuditDialog = (row) => {
  currentRequest.value = row
  auditForm.value = {
    status: 2,
    comment: ''
  }
  showAuditDialog.value = true
}

const handleAudit = () => {
  if (!currentRequest.value?.id) {
    ElMessage.error('请选择要审批的申请')
    return
  }
  
  axios.put(`/purchase/requests/${currentRequest.value.id}/audit`, {
    status: auditForm.value.status,
    comment: auditForm.value.comment
  })
  .then(response => {
    if (response.code === 200) {
      ElMessage.success('审批成功')
      showAuditDialog.value = false
      loadDashboardData()
    } else {
      ElMessage.error(response.message || '审批失败')
    }
  })
  .catch(error => {
    console.error('Audit failed:', error)
    ElMessage.error('审批失败')
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