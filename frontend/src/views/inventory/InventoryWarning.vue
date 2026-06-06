<template>
  <div class="inventory-warning">
    <el-tabs v-model="activeTab" type="card">
      <!-- 低库存预警标签页 -->
      <el-tab-pane label="低库存预警" name="lowStock">
        <div class="warning-section">
          <div class="filter-bar">
            <el-input
                v-model="lowStockKeyword"
                placeholder="搜索药品名称或编码"
                class="search-input"
                clearable
            />
            <el-select
                v-model="lowStockWarehouseId"
                placeholder="选择仓库"
                clearable
                style="width: 150px"
            >
              <el-option label="全部" :value="null" />
              <el-option
                  v-for="wh in warehouses"
                  :key="wh.id"
                  :label="wh.warehouseName"
                  :value="wh.id"
              />
            </el-select>
            <el-button type="primary" @click="loadLowStock">搜索</el-button>
            <el-button type="success" @click="openCreatePlanDialog">生成采购计划</el-button>
          </div>
          <el-table :data="lowStockList" border>
            <el-table-column prop="warehouseName" label="仓库" width="120" />
            <el-table-column prop="drugCode" label="药品编码" />
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="unit" label="单位" />
            <el-table-column prop="currentStock" label="当前库存" />
            <el-table-column prop="minStock" label="预警阈值" />
            <el-table-column label="建议采购量">
              <template #default="scope">
                {{ getSuggestQuantity(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="link" @click="openCreateRequestDialog(scope.row)">创建采购申请</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- 效期预警标签页 -->
      <el-tab-pane label="效期预警" name="expiring">
        <div class="warning-section">
          <div class="filter-bar">
            <el-input
                v-model="expiringKeyword"
                placeholder="搜索药品名称或批号"
                class="search-input"
                clearable
            />
            <el-select
                v-model="expiringWarehouseId"
                placeholder="选择仓库"
                clearable
                style="width: 150px"
            >
              <el-option label="全部" :value="null" />
              <el-option
                  v-for="wh in warehouses"
                  :key="wh.id"
                  :label="wh.warehouseName"
                  :value="wh.id"
              />
            </el-select>
            <el-select v-model="expireFilter" placeholder="请选择" style="width: 120px">
              <el-option label="全部" :value="'all'" />
              <el-option label="已过期" :value="'expired'" />
              <el-option label="即将过期" :value="'upcoming'" />
            </el-select>
            <el-button type="primary" @click="loadExpiring">搜索</el-button>
          </div>
          <el-table :data="expiringList" border>
            <el-table-column prop="drugCode" label="药品编码" />
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="unit" label="单位">
              <template #default="scope">{{ scope.row.unit || '—' }}</template>
            </el-table-column>
            <el-table-column prop="warehouseName" label="仓库" width="120" />
            <el-table-column prop="batchNo" label="批号" />
            <el-table-column prop="quantity" label="库存数量" />
            <el-table-column prop="expireDate" label="有效期">
              <template #default="scope">
                <el-tag :type="getExpireTagType(scope.row.expireDate)">
                  {{ formatDate(scope.row.expireDate) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="距过期天数">
              <template #default="scope">
                <el-tag :type="scope.row.daysLeft < 0 ? 'danger' : (scope.row.daysLeft < 30 ? 'warning' : 'info')">
                  <span v-if="scope.row.daysLeft < 0">已过期</span>
                  <span v-else>{{ scope.row.daysLeft }}天</span>
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button v-if="scope.row.daysLeft >= 0" type="link" @click="openTransferDialog(scope.row)">调拨</el-button>
                <el-button type="link" @click="writeOff(scope.row)">报损</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 创建采购申请对话框 -->
    <el-dialog title="创建采购申请" v-model="showCreateRequestDialog" width="600px">
      <el-form :model="requestForm" label-width="100px">
        <el-form-item label="供应商" required>
          <el-select v-model="requestForm.supplierId" filterable placeholder="请选择供应商">
            <el-option v-for="sup in suppliers" :key="sup.id" :label="sup.supplierName" :value="sup.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="药品" required>
          <el-select v-model="requestForm.drugId" disabled>
            <el-option :label="requestForm.drugName" :value="requestForm.drugId" />
          </el-select>
        </el-form-item>
        <el-form-item label="采购数量" required>
          <el-input v-model.number="requestForm.quantity" type="number" :min="1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="requestForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateRequestDialog = false">取消</el-button>
        <el-button type="primary" :loading="requestLoading" @click="submitCreateRequest">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 生成采购计划对话框 -->
    <el-dialog title="生成采购计划" v-model="showCreatePlanDialog" width="800px">
      <el-form :model="planForm">
        <el-form-item label="计划名称" required>
          <el-input v-model="planForm.planName" placeholder="请输入计划名称" />
        </el-form-item>
      </el-form>
      <el-table
          ref="planTableRef"
          :data="planCandidates"
          border
          row-key="drugId"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="drugCode" label="药品编码" />
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="currentStock" label="当前库存" />
        <el-table-column prop="minStock" label="预警阈值" />
        <el-table-column label="计划采购量" width="120">
          <template #default="scope">
            <el-input v-model.number="scope.row.planQuantity" type="number" :min="1" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showCreatePlanDialog = false">取消</el-button>
        <el-button type="primary" :loading="planLoading" @click="submitCreatePlan">生成计划</el-button>
      </template>
    </el-dialog>

    <!-- 调拨对话框 -->
    <el-dialog title="药品调拨" v-model="showTransferDialog" width="500px">
      <el-form :model="transferForm" label-width="100px">
        <el-form-item label="药品名称">
          <span>{{ transferForm.drugName }}</span>
        </el-form-item>
        <el-form-item label="批号">
          <span>{{ transferForm.batchNo }}</span>
        </el-form-item>
        <el-form-item label="源仓库">
          <span>{{ transferForm.fromWarehouse }}</span>
        </el-form-item>
        <el-form-item label="目标仓库" required>
          <el-select v-model="transferForm.toWarehouseId" placeholder="请选择目标仓库">
            <el-option
                v-for="wh in warehouses"
                :key="wh.id"
                :label="wh.warehouseName"
                :value="wh.id"
                :disabled="wh.id === transferForm.fromWarehouseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="调拨数量" required>
          <el-input v-model.number="transferForm.quantity" type="number" :min="1" :max="transferForm.maxQuantity" />
          <div style="font-size:12px; color:#909399">当前库存: {{ transferForm.maxQuantity }}</div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="transferForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showTransferDialog = false">取消</el-button>
        <el-button type="primary" :loading="transferLoading" @click="submitTransfer">确认调拨</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

// 当前标签页
const activeTab = ref('lowStock')

// ---------- 低库存预警 ----------
const lowStockKeyword = ref('')
const lowStockWarehouseId = ref(null)
const lowStockList = ref([])
const warehouses = ref([])

// 供应商列表
const suppliers = ref([])

// 采购申请相关
const showCreateRequestDialog = ref(false)
const requestLoading = ref(false)
const requestForm = ref({
  supplierId: null,
  drugId: null,
  drugName: '',
  quantity: 1,
  remark: ''
})

// 采购计划相关
const showCreatePlanDialog = ref(false)
const planLoading = ref(false)
const planForm = ref({ planName: '' })
const planCandidates = ref([])
const selectedPlanItems = ref([])
const planTableRef = ref(null)

// ---------- 效期预警 ----------
const expiringKeyword = ref('')
const expiringWarehouseId = ref(null)
const expireFilter = ref('all')
const expiringList = ref([])

// ---------- 调拨相关 ----------
const showTransferDialog = ref(false)
const transferLoading = ref(false)
const transferForm = ref({
  fromInventoryId: null,
  drugName: '',
  batchNo: '',
  fromWarehouseId: null,
  fromWarehouse: '',
  toWarehouseId: null,
  quantity: 1,
  maxQuantity: 0,
  remark: ''
})

// ========== 辅助函数 ==========
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return dateStr
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getExpireTagType = (expireDate) => {
  if (!expireDate) return ''
  const expire = new Date(expireDate)
  const now = new Date()
  const diffDays = Math.floor((expire - now) / (1000 * 60 * 60 * 24))
  if (diffDays < 0) return 'danger'
  if (diffDays < 30) return 'danger'
  if (diffDays < 90) return 'warning'
  if (diffDays < 180) return 'info'
  return ''
}

const getSuggestQuantity = (row) => {
  const threshold = row.minStock || 0
  // 建议采购量 = 预警阈值 * 3
  return threshold * 3
}

// ========== 低库存预警 ==========
const loadLowStock = async () => {
  try {
    const res = await axios.get('/inventory/low-stock', {
      params: {
        keyword: lowStockKeyword.value || undefined,
        warehouseId: lowStockWarehouseId.value || undefined
      }
    })
    if (res.code === 200) {
      lowStockList.value = res.data
      planCandidates.value = res.data.map(item => ({
        ...item,
        planQuantity: getSuggestQuantity(item)
      }))
    } else {
      lowStockList.value = []
    }
  } catch (error) {
    ElMessage.error('加载低库存数据失败')
  }
}

// 加载仓库列表
const loadWarehouses = async () => {
  try {
    const res = await axios.get('/inventory/warehouses')
    if (res.code === 200) {
      warehouses.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载仓库列表失败')
  }
}

// 加载供应商列表
const loadSuppliers = async () => {
  try {
    const res = await axios.get('/drugs/suppliers')
    if (res.code === 200) suppliers.value = res.data.records || res.data
  } catch (error) {
    ElMessage.error('加载供应商失败')
  }
}

// 创建采购申请
const openCreateRequestDialog = (row) => {
  requestForm.value = {
    supplierId: null,
    drugId: row.drugId,
    drugName: row.drugName,
    quantity: getSuggestQuantity(row),
    remark: ''
  }
  showCreateRequestDialog.value = true
}

const submitCreateRequest = async () => {
  if (!requestForm.value.supplierId) {
    ElMessage.warning('请选择供应商')
    return
  }
  if (!requestForm.value.quantity || requestForm.value.quantity <= 0) {
    ElMessage.warning('请输入有效数量')
    return
  }

  let unitPrice = 0
  try {
    const drugRes = await axios.get(`/drugs/${requestForm.value.drugId}`)
    if (drugRes.code === 200) unitPrice = drugRes.data.purchasePrice || 0
  } catch (error) {
    console.warn('获取药品单价失败', error)
  }
  const amount = requestForm.value.quantity * unitPrice

  const payload = {
    requestNo: 'PR' + Date.now(),
    planId: null,
    supplierId: requestForm.value.supplierId,
    remark: requestForm.value.remark,
    details: [{
      drugId: requestForm.value.drugId,
      drugName: requestForm.value.drugName,
      spec: lowStockList.value.find(d => d.drugId === requestForm.value.drugId)?.spec || '',
      quantity: requestForm.value.quantity,
      unit: lowStockList.value.find(d => d.drugId === requestForm.value.drugId)?.unit || '',
      unitPrice: unitPrice,
      amount: amount
    }]
  }

  requestLoading.value = true
  try {
    const response = await axios.post('/purchase/requests', payload)
    if (response.code === 200) {
      ElMessage.success('采购申请已提交')
      showCreateRequestDialog.value = false
      loadLowStock()
    } else {
      ElMessage.error(response.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '提交失败')
  } finally {
    requestLoading.value = false
  }
}

// 生成采购计划
const openCreatePlanDialog = () => {
  planForm.value.planName = ''
  selectedPlanItems.value = []
  planCandidates.value = lowStockList.value.map(item => ({
    ...item,
    planQuantity: getSuggestQuantity(item)
  }))
  showCreatePlanDialog.value = true
  if (planTableRef.value) planTableRef.value.clearSelection()
}

const handleSelectionChange = (selection) => {
  selectedPlanItems.value = selection
}

const submitCreatePlan = async () => {
  if (!planForm.value.planName.trim()) {
    ElMessage.warning('请输入计划名称')
    return
  }
  if (selectedPlanItems.value.length === 0) {
    ElMessage.warning('请至少勾选一个药品')
    return
  }
  const items = selectedPlanItems.value.map(item => ({
    drugId: item.drugId,
    drugName: item.drugName,
    quantity: item.planQuantity
  }))
  planLoading.value = true
  try {
    await axios.post('/purchase/plans', {
      planName: planForm.value.planName,
      planType: 1,
      items: items
    })
    ElMessage.success('采购计划生成成功')
    showCreatePlanDialog.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '生成失败')
  } finally {
    planLoading.value = false
  }
}

// ========== 效期预警 ==========
const loadExpiring = async () => {
  let days = 180
  let includeExpired = true

  if (expireFilter.value === 'expired') {
    days = 0
    includeExpired = true
  } else if (expireFilter.value === 'upcoming') {
    days = 180
    includeExpired = false
  } else {
    days = 180
    includeExpired = true
  }

  try {
    const res = await axios.get('/inventory/expiring', {
      params: {
        days: days,
        keyword: expiringKeyword.value || undefined,
        includeExpired: includeExpired,
        warehouseId: expiringWarehouseId.value || undefined
      }
    })
    if (res.code === 200) {
      expiringList.value = res.data.map(item => {
        const expire = new Date(item.expireDate)
        const now = new Date()
        const daysLeft = Math.floor((expire - now) / (1000 * 60 * 60 * 24))
        return { ...item, daysLeft }
      })
    } else {
      expiringList.value = []
    }
  } catch (error) {
    ElMessage.error('加载效期预警数据失败')
  }
}

// 报损操作
const writeOff = async (row) => {
  if (!confirm(`确定要报损药品 ${row.drugName} 批次 ${row.batchNo} 吗？`)) return
  try {
    await axios.post(`/inventory/${row.id}/decrease`, null, { params: { quantity: row.quantity } })
    ElMessage.success('报损成功')
    loadExpiring()
    localStorage.setItem('inventoryRefresh', Date.now().toString())
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '报损失败')
  }
}

// 打开调拨对话框
const openTransferDialog = (row) => {
  transferForm.value = {
    fromInventoryId: row.id,
    drugName: row.drugName,
    batchNo: row.batchNo,
    fromWarehouseId: row.warehouseId,
    fromWarehouse: row.warehouseName,
    toWarehouseId: null,
    quantity: 1,
    maxQuantity: row.quantity,
    remark: ''
  }
  showTransferDialog.value = true
}

// 提交调拨
const submitTransfer = async () => {
  if (!transferForm.value.toWarehouseId) {
    ElMessage.warning('请选择目标仓库')
    return
  }
  if (!transferForm.value.quantity || transferForm.value.quantity <= 0) {
    ElMessage.warning('请输入有效数量')
    return
  }
  if (transferForm.value.quantity > transferForm.value.maxQuantity) {
    ElMessage.warning('调拨数量不能超过当前库存')
    return
  }
  transferLoading.value = true
  try {
    await axios.post('/inventory/transfer', {
      fromInventoryId: transferForm.value.fromInventoryId,
      toWarehouseId: transferForm.value.toWarehouseId,
      quantity: transferForm.value.quantity,
      remark: transferForm.value.remark
    })
    ElMessage.success('调拨成功')
    showTransferDialog.value = false
    loadExpiring()
    loadLowStock()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '调拨失败')
  } finally {
    transferLoading.value = false
  }
}

// ========== 初始化 ==========
onMounted(() => {
  loadLowStock()
  loadExpiring()
  loadSuppliers()
  loadWarehouses()
})
</script>

<style scoped>
.inventory-warning {
  background: white;
  border-radius: 10px;
  padding: 20px;
}
.warning-section {
  padding: 20px 0;
}
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}
.search-input {
  width: 300px;
}
</style>