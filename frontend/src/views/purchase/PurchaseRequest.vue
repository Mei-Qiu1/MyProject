<template>
  <div class="purchase-request">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索申请单号" class="search-input" clearable />
      <el-select v-model="status" placeholder="选择状态" clearable>
        <el-option label="全部" :value="-1" />
        <el-option label="待审批" :value="1" />
        <el-option label="已批准" :value="2" />
        <el-option label="已拒绝" :value="3" />
        <el-option label="已生成订单" :value="4" />   <!-- 新增 -->
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openAddDialog">新增申请</el-button>
    </div>

    <el-table :data="requestList" border>
      <el-table-column prop="requestNo" label="申请单号" width="180" />
      <el-table-column prop="planName" label="所属计划" />
      <el-table-column prop="supplierName" label="供应商" />
      <el-table-column label="申请金额" width="120">
        <template #default="scope">
          {{ formatAmount(scope.row.totalAmount) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditComment" label="审批意见" />
      <el-table-column prop="createTime" label="申请时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 1" link type="primary" @click="auditRequest(scope.row)">审批</el-button>
          <el-button v-if="scope.row.status === 2" link type="success" @click="createOrder(scope.row)">生成订单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @current-change="handlePageChange"
        layout="prev, pager, next, jumper"
    />

    <!-- 新增申请对话框 -->
    <el-dialog title="新增采购申请" v-model="showAddModal" width="700px" @close="resetForm">
      <el-form :model="formData" ref="formRef" label-width="100px" :rules="formRules">
        <el-form-item label="申请单号" prop="requestNo">
          <el-input v-model="formData.requestNo" disabled>{{ currentRequestNo }}</el-input>
        </el-form-item>
        <el-form-item label="采购计划" prop="planId">
          <el-select v-model="formData.planId" clearable @change="onPlanChange" placeholder="非必填，选择后自动加载药品">
            <el-option v-for="plan in plans" :key="plan.id" :label="plan.planName" :value="plan.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商" prop="supplierId" required>
          <el-select v-model="formData.supplierId" clearable placeholder="请选择供应商">
            <el-option v-for="sup in suppliers" :key="sup.id" :label="sup.supplierName" :value="sup.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark" required>
          <el-input type="textarea" v-model="formData.remark" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>

      <div class="detail-section">
        <h4>采购明细</h4>
        <el-table :data="detailList" border>
          <el-table-column prop="drugName" label="药品名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="quantity" label="数量">
            <template #default="scope">
              <el-input v-model.number="scope.row.quantity" style="width: 80px" @input="calcAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" />
          <el-table-column prop="unitPrice" label="单价">
            <template #default="scope">
              <el-input v-model.number="scope.row.unitPrice" style="width: 100px" @input="calcAmount(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" />
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button link type="danger" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="openDrugSelector">添加药品</el-button>
      </div>

      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveRequest">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 药品选择对话框 -->
    <el-dialog title="药品选择" v-model="showDrugSelector" width="600px" @opened="loadInitialDrugs">
      <el-input v-model="drugKeyword" placeholder="搜索药品" @input="searchDrugs" clearable />
      <el-table :data="drugOptions" border @row-click="selectDrug" style="margin-top: 10px" v-loading="drugLoading">
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="dosageForm" label="剂型" />
        <el-table-column prop="purchasePrice" label="采购价" />
      </el-table>
      <el-pagination
          v-if="drugTotal > 0"
          :current-page="drugPage"
          :page-size="drugSize"
          :total="drugTotal"
          @current-change="handleDrugPageChange"
          layout="prev, pager, next"
          small
          style="margin-top: 10px; justify-content: center;"
      />
      <template #footer>
        <el-button @click="showDrugSelector = false">取消</el-button>
        <el-button type="primary" @click="confirmDrugSelection">确认添加</el-button>
      </template>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="审批申请" v-model="showAuditModal" width="400px">
      <el-form :model="auditData" label-width="100px">
        <el-form-item label="审批结果">
          <el-radio-group v-model="auditData.result">
            <el-radio :value="2">批准</el-radio>
            <el-radio :value="3">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input type="textarea" v-model="auditData.comment" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAuditModal = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认审批</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="申请详情" v-model="showDetailModal" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请单号">{{ currentRequest.requestNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentRequest.status)">
            {{ getStatusName(currentRequest.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属计划">{{ currentRequest.planName || '无' }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ currentRequest.supplierName || '未指定' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentRequest.createTime }}</el-descriptions-item>
        <el-descriptions-item label="审批意见">{{ currentRequest.auditComment || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <h4>采购明细</h4>
      <el-table :data="currentDetails" border>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="amount" label="金额" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

// 搜索与分页
const keyword = ref('')
const status = ref(-1)
const requestList = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })

// 新增申请表单
const showAddModal = ref(false)
const formRef = ref(null)
const formData = reactive({ requestNo: '', planId: null, supplierId: null, remark: '' })
const detailList = ref([])
const formRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  remark: [{ required: true, message: '请输入备注', trigger: 'blur' }]
}
const currentRequestNo = ref('')

const generateRequestNo = () => {
  const now = new Date()
  const dateStr = now.toISOString().slice(2, 10).replace(/-/g, '')
  const timeStr = now.getTime().toString().slice(-6)
  const randomStr = String(Math.random()).slice(-3)
  return 'PR' + dateStr + timeStr + randomStr
}

// 采购计划 & 供应商
const plans = ref([])
const suppliers = ref([])

// 药品选择
const showDrugSelector = ref(false)
const drugKeyword = ref('')
const drugOptions = ref([])
const drugLoading = ref(false)
const drugPage = ref(1)
const drugSize = ref(10)
const drugTotal = ref(0)

// 审批
const showAuditModal = ref(false)
const auditData = reactive({ requestId: null, result: 2, comment: '' })

// 详情
const showDetailModal = ref(false)
const currentRequest = ref({})
const currentDetails = ref([])

// 状态映射
const statusNames = { 1: '待审批', 2: '已批准', 3: '已拒绝', 4: '已生成订单' }
const statusTagTypes = { 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
const getStatusName = (s) => statusNames[Number(s)] || '未知'
const getStatusTagType = (s) => statusTagTypes[Number(s)] || 'default'

const formatAmount = (amount) => {
  if (amount === null || amount === undefined) return '0.00'
  return Number(amount).toFixed(2)
}

// 加载数据
const loadRequests = async () => {
  try {
    const res = await axios.get('/purchase/requests', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        status: status.value === -1 ? undefined : status.value
      }
    })
    if (res.code === 200) {
      requestList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载采购申请失败')
  }
}

const loadPlans = async () => {
  try {
    const res = await axios.get('/purchase/plans')
    if (res.code === 200) plans.value = res.data
  } catch (error) {
    ElMessage.error('加载采购计划失败')
  }
}

const loadSuppliers = async () => {
  try {
    const res = await axios.get('/drugs/suppliers')
    if (res.code === 200) suppliers.value = res.data.records || res.data
  } catch (error) {
    ElMessage.error('加载供应商失败')
  }
}

// 采购计划变更 → 加载明细
const onPlanChange = async (planId) => {
  if (!planId) {
    detailList.value = []
    return
  }
  try {
    const res = await axios.get(`/purchase/requests/plan-details/${planId}`)
    if (res.code === 200) {
      // 将计划明细转换为统一格式
      const newDetails = res.data.map(d => ({
        drugId: d.drugId,
        drugName: d.drugName,
        spec: d.spec,
        quantity: d.quantity,
        unit: d.unit,
        unitPrice: d.unitPrice,
        amount: d.amount
      }))
      
      // 合并重复药品：相同药品的数量相加
      const mergedDetails = []
      const drugMap = new Map()
      
      // 遍历新的明细
      newDetails.forEach(item => {
        const key = `${item.drugId}-${item.spec}`
        if (drugMap.has(key)) {
          // 如果已存在，累加数量和金额
          const existing = drugMap.get(key)
          existing.quantity += item.quantity
          existing.amount = (existing.quantity * existing.unitPrice).toFixed(2)
        } else {
          // 如果不存在，添加新项
          drugMap.set(key, { ...item })
          mergedDetails.push(drugMap.get(key))
        }
      })
      
      detailList.value = mergedDetails
    } else {
      detailList.value = []
    }
  } catch (error) {
    ElMessage.error('加载计划明细失败')
  }
}

// 药品选择
const searchDrugs = async (page = 1) => {
  drugLoading.value = true
  try {
    const res = await axios.get('/drugs', { params: { page, size: drugSize.value, keyword: drugKeyword.value } })
    if (res.code === 200) {
      drugOptions.value = res.data.records || res.data
      drugTotal.value = res.data.total || 0
      drugPage.value = page
    }
  } catch (error) {
    ElMessage.error('搜索药品失败')
  } finally {
    drugLoading.value = false
  }
}

const loadInitialDrugs = () => {
  drugKeyword.value = ''
  searchDrugs(1)
}

const openDrugSelector = () => {
  showDrugSelector.value = true
}

const confirmDrugSelection = () => {
  showDrugSelector.value = false
  ElMessage.success('药品已添加到采购明细')
}

const handleDrugPageChange = (page) => {
  searchDrugs(page)
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
  } else {
    ElMessage.warning('该药品已在列表中')
  }
}

const calcAmount = (row) => {
  if (row.quantity && row.unitPrice) {
    row.amount = (row.quantity * row.unitPrice).toFixed(2)
  }
}

const removeDetail = (index) => {
  detailList.value.splice(index, 1)
}

// 提交新增申请
const saveRequest = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请填写完整信息')
      return
    }
    if (detailList.value.length === 0) {
      ElMessage.warning('请至少添加一种药品')
      return
    }
    const payload = {
      requestNo: currentRequestNo.value,
      planId: formData.planId || null,
      supplierId: formData.supplierId,
      remark: formData.remark,
      details: detailList.value.map(d => ({
        drugId: d.drugId,
        drugName: d.drugName,
        spec: d.spec,
        quantity: d.quantity,
        unit: d.unit,
        unitPrice: d.unitPrice,
        amount: d.amount
      }))
    }
    try {
      const res = await axios.post('/purchase/requests', payload)
      if (res.code === 200) {
        ElMessage.success('申请提交成功')
        showAddModal.value = false
        loadRequests()
        resetForm()
      } else {
        ElMessage.error(res.message || '提交失败')
      }
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '提交失败')
    }
  })
}

const resetForm = () => {
  currentRequestNo.value = generateRequestNo()
  formData.requestNo = currentRequestNo.value
  formData.planId = null
  formData.supplierId = null
  formData.remark = ''
  detailList.value = []
  if (formRef.value) formRef.value.resetFields()
}

// 审批
const auditRequest = (row) => {
  auditData.requestId = row.id
  showAuditModal.value = true
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
    ElMessage.error(error.response?.data?.message || '审批失败')
  }
}

// 生成订单
const createOrder = (row) => {
  const requestId = parseInt(row.id)
  if (!requestId || requestId <= 0) {
    ElMessage.error('无效的申请单ID')
    return
  }
  ElMessageBox.confirm(`确定要为申请单 ${row.requestNo} 生成采购订单吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    axios.post(`/purchase/orders/from-request/${requestId}`)
        .then((res) => {
          if (res.code === 200) {
            ElMessage.success('订单生成成功')
            loadRequests()
          } else {
            ElMessage.error(res.message || '订单生成失败')
          }
        })
        .catch((error) => {
          const msg = error.response?.data?.message || error.message || '订单生成失败'
          ElMessage.error(msg)
        })
  }).catch(() => {})
}

// 详情
const viewDetail = async (row) => {
  try {
    const response = await axios.get(`/purchase/requests/${row.id}`)
    if (response.code === 200) {
      currentRequest.value = response.data      // 包含 planName, supplierName
      currentDetails.value = response.data.details || []
      showDetailModal.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 搜索分页
const handleSearch = () => {
  pagination.current = 1
  loadRequests()
}
const handlePageChange = (page) => {
  pagination.current = page
  loadRequests()
}
const openAddDialog = () => {
  resetForm()
  showAddModal.value = true
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