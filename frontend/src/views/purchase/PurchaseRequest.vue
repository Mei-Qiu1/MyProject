
<template>
  <div class="purchase-request">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索申请单号" class="search-input"></el-input>
      <el-select v-model="status" placeholder="选择状态">
        <el-option label="全部" :value="-1"></el-option>
        <el-option label="待审批" :value="1"></el-option>
        <el-option label="已批准" :value="2"></el-option>
        <el-option label="已拒绝" :value="3"></el-option>
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="showAddModal = true">新增申请</el-button>
    </div>
    
    <el-table :data="requestList" border>
      <el-table-column prop="requestNo" label="申请单号" />
      <el-table-column prop="planName" label="所属计划" />
      <el-table-column prop="supplierName" label="供应商" />
      <el-table-column prop="totalAmount" label="申请金额" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditComment" label="审批意见" />
      <el-table-column prop="createTime" label="申请时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 1" type="text" @click="auditRequest(scope.row)">审批</el-button>
          <el-button v-if="scope.row.status === 2" type="text" @click="createOrder(scope.row)">生成订单</el-button>
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
    
    <el-dialog title="新增采购申请" v-model="showAddModal" width="700px">
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="申请单号" prop="requestNo">
          <el-input v-model="formData.requestNo" disabled>{{ autoGenNo }}</el-input>
        </el-form-item>
        <el-form-item label="采购计划" prop="planId">
          <el-select v-model="formData.planId">
            <el-option v-for="plan in plans" :key="plan.id" :label="plan.planName" :value="plan.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="formData.supplierId">
            <el-option v-for="sup in suppliers" :key="sup.id" :label="sup.supplierName" :value="sup.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="formData.remark"></el-input>
        </el-form-item>
      </el-form>
      <div class="detail-section">
        <h4>采购明细</h4>
        <el-table :data="detailList" border>
          <el-table-column prop="drugName" label="药品名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="quantity" label="数量">
            <template #default="scope">
              <el-input v-model.number="scope.row.quantity" style="width: 80px"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" />
          <el-table-column prop="unitPrice" label="单价">
            <template #default="scope">
              <el-input v-model.number="scope.row.unitPrice" style="width: 100px"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="text" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="showDrugSelector = true">添加药品</el-button>
      </div>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveRequest">提交申请</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="药品选择" v-model="showDrugSelector" width="600px">
      <el-input v-model="drugKeyword" placeholder="搜索药品" @input="searchDrugs"></el-input>
      <el-table :data="drugOptions" border @row-click="selectDrug">
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="dosageForm" label="剂型" />
        <el-table-column prop="purchasePrice" label="采购价" />
      </el-table>
    </el-dialog>
    
    <el-dialog title="审批申请" v-model="showAuditModal">
      <el-form :model="auditData" label-width="100px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="auditData.result">
            <el-radio :value="2">批准</el-radio>
            <el-radio :value="3">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input type="textarea" v-model="auditData.comment"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAuditModal = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认审批</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const status = ref(-1)
const requestList = ref([])
const plans = ref([])
const suppliers = ref([])
const drugOptions = ref([])
const drugKeyword = ref('')
const showAddModal = ref(false)
const showDrugSelector = ref(false)
const showAuditModal = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  requestNo: '',
  planId: null,
  supplierId: null,
  remark: ''
})

const detailList = ref([])

const auditData = reactive({
  requestId: null,
  result: 2,
  comment: ''
})

const currentRequest = ref(null)

const autoGenNo = computed(() => {
  return 'PR' + new Date().toISOString().slice(2, 10).replace(/-/g, '') + String(Math.random()).slice(-4)
})

const statusNames = { 1: '待审批', 2: '已批准', 3: '已拒绝' }
const statusTagTypes = { 1: 'warning', 2: 'success', 3: 'danger' }

const getStatusName = (status) => statusNames[status] || '未知'
const getStatusTagType = (status) => statusTagTypes[status] || 'default'

const loadRequests = async () => {
  try {
    const response = await axios.get('/purchase/requests', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        status: status.value === -1 ? undefined : status.value
      }
    })
    if (response.code === 200) {
      requestList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载采购申请失败')
  }
}

const loadPlans = async () => {
  try {
    const response = await axios.get('/purchase/plans')
    if (response.code === 200) {
      plans.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载采购计划失败')
  }
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

const searchDrugs = async () => {
  try {
    const response = await axios.get('/drugs', { params: { keyword: drugKeyword.value, size: 20 } })
    if (response.code === 200) {
      drugOptions.value = response.data.records || response.data
    }
  } catch (error) {
    ElMessage.error('搜索药品失败')
  }
}

const selectDrug = (drug) => {
  const existing = detailList.value.find(d => d.drugId === drug.id)
  if (!existing) {
    detailList.value.push({
      drugId: drug.id,
      drugName: drug.drugName,
      spec: drug.spec,
      unit: drug.unit,
      quantity: 1,
      unitPrice: drug.purchasePrice,
      amount: drug.purchasePrice
    })
    showDrugSelector.value = false
  } else {
    ElMessage.warning('该药品已在列表中')
  }
}

const removeDetail = (index) => {
  detailList.value.splice(index, 1)
}

const handleSearch = () => {
  pagination.current = 1
  loadRequests()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadRequests()
}

const viewDetail = (row) => {
  currentRequest.value = row
}

const auditRequest = (row) => {
  auditData.requestId = row.id
  showAuditModal.value = true
}

const createOrder = (row) => {
  if (confirm(`确定要为申请单 ${row.requestNo} 生成采购订单吗？`)) {
    axios.post(`/purchase/orders/from-request/${row.id}`)
      .then(() => {
        ElMessage.success('订单生成成功')
        loadRequests()
      })
      .catch(() => ElMessage.error('订单生成失败'))
  }
}

const submitAudit = async () => {
  try {
    await axios.put(`/purchase/requests/${auditData.requestId}/audit`, {
      status: auditData.result,
      comment: auditData.comment
    })
    ElMessage.success('审批成功')
    showAuditModal.value = false
    loadRequests()
  } catch (error) {
    ElMessage.error('审批失败')
  }
}

const saveRequest = async () => {
  if (detailList.value.length === 0) {
    ElMessage.warning('请添加采购明细')
    return
  }
  try {
    await axios.post('/purchase/requests', {
      ...formData,
      requestNo: autoGenNo.value,
      details: detailList.value
    })
    ElMessage.success('申请提交成功')
    showAddModal.value = false
    loadRequests()
    resetForm()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = null
  })
  detailList.value = []
}

onMounted(() => {
  loadRequests()
  loadPlans()
  loadSuppliers()
})
</script>

<style scoped>
.purchase-request {
  background: white;
  border-radius: 10px;
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.detail-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
