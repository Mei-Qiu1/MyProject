<template>
  <div class="inventory-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
          v-model="keyword"
          placeholder="搜索药品名称或批号"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
      />
      <el-select v-model="warehouseId" placeholder="选择仓库" clearable>
        <el-option label="全部" :value="0" />
        <el-option v-for="wh in warehouses" :key="wh.id" :label="wh.warehouseName" :value="wh.id" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openStockInDialog">入库</el-button>
      <el-button type="warning" @click="openStockOutDialog">出库</el-button>
    </div>

    <!-- 库存列表 -->
    <el-table :data="inventoryList" border>
      <el-table-column prop="drugCode" label="药品编码" />
      <el-table-column prop="drugName" label="药品名称" />
      <el-table-column prop="spec" label="规格" />
      <el-table-column prop="batchNo" label="批号" />
      <el-table-column prop="productionDate" label="生产日期" />
      <el-table-column prop="expireDate" label="有效期">
        <template #default="scope">
          <el-tag :type="getExpireTagType(scope.row.expireDate)">
            {{ formatDate(scope.row.expireDate) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="库存数量" />
      <el-table-column prop="unitPrice" label="单价" />
      <el-table-column prop="warehouseName" label="仓库" />
    </el-table>

    <!-- 分页 -->
    <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @current-change="handlePageChange"
        layout="prev, pager, next, jumper"
    />

    <!-- 入库对话框（无批号输入） -->
    <el-dialog title="入库登记" v-model="showStockInModal" width="600px" @close="resetStockInForm">
      <el-form :model="stockInForm" ref="stockInFormRef" label-width="100px">
        <el-form-item label="药品" prop="drugId" required>
          <el-select
              v-model="stockInForm.drugId"
              filterable
              placeholder="请选择药品"
              @change="onDrugSelect"
          >
            <el-option
                v-for="drug in drugOptions"
                :key="drug.id"
                :label="drug.drugName + ' - ' + drug.spec"
                :value="drug.id"
            />
          </el-select>
        </el-form-item>
        <!-- 批号由系统自动生成，前端不显示 -->
        <el-form-item label="生产日期" prop="productionDate">
          <el-date-picker v-model="stockInForm.productionDate" type="date" placeholder="请选择生产日期" @change="onProductionDateChange" />
        </el-form-item>
        <el-form-item label="有效期" prop="expireDate" required>
          <el-date-picker v-model="stockInForm.expireDate" type="date" placeholder="请选择有效期" :disabled="!stockInForm.productionDate" :picker-options="expireDatePickerOptions" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity" required>
          <el-input v-model.number="stockInForm.quantity" type="number" :min="1" placeholder="请输入数量" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input v-model.number="stockInForm.unitPrice" type="number" step="0.01" placeholder="自动带出采购价，可修改" />
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId" required>
          <el-select v-model="stockInForm.warehouseId" placeholder="请选择仓库">
            <el-option v-for="wh in warehouses" :key="wh.id" :label="wh.warehouseName" :value="wh.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="stockInForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStockInModal = false">取消</el-button>
        <el-button type="primary" @click="submitStockIn">确认入库</el-button>
      </template>
    </el-dialog>

    <!-- 出库对话框（批次自动匹配） -->
    <el-dialog title="出库登记" v-model="showStockOutModal" width="600px" @close="resetStockOutForm">
      <el-form :model="stockOutForm" ref="stockOutFormRef" label-width="100px">
        <el-form-item label="药品" prop="drugId" required>
          <el-select
              v-model="stockOutForm.drugId"
              filterable
              placeholder="请选择药品"
              @change="onDrugChangeForOut"
          >
            <el-option
                v-for="drug in drugOptions"
                :key="drug.id"
                :label="drug.drugName + ' - ' + drug.spec"
                :value="drug.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="批次" prop="inventoryId" required>
          <el-select
              v-model="stockOutForm.inventoryId"
              :disabled="!stockOutForm.drugId"
              placeholder="请先选择药品"
          >
            <el-option
                v-for="inv in currentInventories"
                :key="inv.id"
                :label="`${inv.batchNo} (剩余: ${inv.quantity} ${inv.unit})`"
                :value="inv.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity" required>
          <el-input v-model.number="stockOutForm.quantity" type="number" :min="1" :max="maxOutQuantity" />
          <div v-if="maxOutQuantity > 0" style="font-size:12px; color:#909399">
            当前批次最多可出库 {{ maxOutQuantity }}
          </div>
        </el-form-item>
        <el-form-item label="出库类型" prop="type">
          <el-select v-model="stockOutForm.type">
            <el-option label="领用出库" :value="1" />
            <el-option label="调拨出库" :value="2" />
            <el-option label="报损出库" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="stockOutForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStockOutModal = false">取消</el-button>
        <el-button type="primary" @click="submitStockOut">确认出库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

// ---------- 搜索与列表 ----------
const keyword = ref('')
const warehouseId = ref(0)
const inventoryList = ref([])
const warehouses = ref([])
const drugOptions = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })

// ---------- 入库表单 ----------
const showStockInModal = ref(false)
const stockInFormRef = ref(null)
const stockInForm = reactive({
  drugId: null,
  productionDate: null,
  expireDate: null,
  quantity: null,
  unitPrice: null,
  warehouseId: null,
  remark: ''
})

// 有效期日期选择器配置（限制只能选择生产日期之后的日期）
const expireDatePickerOptions = computed(() => {
  if (!stockInForm.productionDate) {
    return {}
  }
  return {
    disabledDate: (time) => {
      // 确保两个日期都是 Date 对象进行比较
      const productionDate = new Date(stockInForm.productionDate)
      const selectedDate = new Date(time.getTime())
      // 禁用生产日期及之前的日期
      return selectedDate <= productionDate
    }
  }
})

// ---------- 出库表单 ----------
const showStockOutModal = ref(false)
const stockOutFormRef = ref(null)
const stockOutForm = reactive({
  drugId: null,
  inventoryId: null,
  quantity: null,
  type: 1,
  remark: ''
})
const currentInventories = ref([])

// 最大可出库数量
const maxOutQuantity = computed(() => {
  const inv = currentInventories.value.find(i => i.id === stockOutForm.inventoryId)
  return inv ? inv.quantity : 0
})

// ---------- 辅助函数 ----------
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
  if (diffDays < 30) return 'danger'
  if (diffDays < 90) return 'warning'
  if (diffDays < 180) return 'info'
  return ''
}

// ---------- API 调用 ----------
const loadInventory = async () => {
  try {
    const response = await axios.get('/inventory', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        warehouseId: warehouseId.value === 0 ? undefined : warehouseId.value
      }
    })
    if (response.code === 200) {
      inventoryList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载库存列表失败')
  }
}

const loadWarehouses = async () => {
  try {
    const response = await axios.get('/inventory/warehouses')
    if (response.code === 200) {
      warehouses.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载仓库列表失败')
  }
}

const loadDrugs = async () => {
  try {
    const response = await axios.get('/drugs', { params: { size: 1000 } })
    if (response.code === 200) {
      drugOptions.value = response.data.records || response.data
    }
  } catch (error) {
    ElMessage.error('加载药品列表失败')
  }
}

// ---------- 入库 ----------
const openStockInDialog = () => {
  resetStockInForm()
  showStockInModal.value = true
}

const onDrugSelect = (drugId) => {
  const drug = drugOptions.value.find(d => d.id === drugId)
  if (drug) {
    stockInForm.unitPrice = drug.purchasePrice
  }
}

// 生产日期改变时，检查并清除无效的有效期
const onProductionDateChange = () => {
  if (stockInForm.expireDate && stockInForm.productionDate) {
    const productionDate = new Date(stockInForm.productionDate)
    const expireDate = new Date(stockInForm.expireDate)
    if (expireDate <= productionDate) {
      stockInForm.expireDate = null
      ElMessage.warning('有效期必须晚于生产日期，请重新选择')
    }
  }
}

const resetStockInForm = () => {
  stockInForm.drugId = null
  stockInForm.productionDate = null
  stockInForm.expireDate = null
  stockInForm.quantity = null
  stockInForm.unitPrice = null
  stockInForm.warehouseId = null
  stockInForm.remark = ''
  if (stockInFormRef.value) stockInFormRef.value.resetFields()
}

const submitStockIn = async () => {
  if (!stockInForm.drugId) {
    ElMessage.warning('请选择药品')
    return
  }
  if (!stockInForm.productionDate) {
    ElMessage.warning('请选择生产日期')
    return
  }
  if (!stockInForm.expireDate) {
    ElMessage.warning('请选择有效期')
    return
  }
  // 验证有效期必须大于生产日期
  const productionDate = new Date(stockInForm.productionDate)
  const expireDate = new Date(stockInForm.expireDate)
  if (expireDate <= productionDate) {
    ElMessage.warning('有效期必须晚于生产日期，请重新选择')
    return
  }
  if (!stockInForm.quantity || stockInForm.quantity <= 0) {
    ElMessage.warning('请输入有效数量')
    return
  }
  if (!stockInForm.warehouseId) {
    ElMessage.warning('请选择仓库')
    return
  }
  // 注意：不传 batchNo，后端会自动生成
  const payload = {
    drugId: stockInForm.drugId,
    productionDate: stockInForm.productionDate,
    expireDate: stockInForm.expireDate,
    quantity: stockInForm.quantity,
    unitPrice: stockInForm.unitPrice,
    warehouseId: stockInForm.warehouseId,
    remark: stockInForm.remark
  }
  try {
    const response = await axios.post('/inventory', payload)
    if (response.code === 200) {
      ElMessage.success(response.message || '入库成功')
      showStockInModal.value = false
      loadInventory()
      resetStockInForm()
    } else {
      ElMessage.error(response.message || '入库失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '入库失败')
  }
}

// ---------- 出库 ----------
const openStockOutDialog = () => {
  resetStockOutForm()
  showStockOutModal.value = true
}

const onDrugChangeForOut = async (drugId) => {
  stockOutForm.inventoryId = null
  stockOutForm.quantity = null
  if (!drugId) {
    currentInventories.value = []
    return
  }
  try {
    const response = await axios.get(`/inventory/batches/${drugId}`)
    if (response.code === 200) {
      currentInventories.value = response.data
    } else {
      currentInventories.value = []
    }
  } catch (error) {
    ElMessage.error('加载批次失败')
    currentInventories.value = []
  }
}

const resetStockOutForm = () => {
  stockOutForm.drugId = null
  stockOutForm.inventoryId = null
  stockOutForm.quantity = null
  stockOutForm.type = 1
  stockOutForm.remark = ''
  currentInventories.value = []
  if (stockOutFormRef.value) stockOutFormRef.value.resetFields()
}

const submitStockOut = async () => {
  if (!stockOutForm.drugId) {
    ElMessage.warning('请选择药品')
    return
  }
  if (!stockOutForm.inventoryId) {
    ElMessage.warning('请选择批次')
    return
  }
  if (!stockOutForm.quantity || stockOutForm.quantity <= 0) {
    ElMessage.warning('请输入有效数量')
    return
  }
  const selectedBatch = currentInventories.value.find(i => i.id === stockOutForm.inventoryId)
  if (selectedBatch && stockOutForm.quantity > selectedBatch.quantity) {
    ElMessage.warning(`库存不足，当前批次剩余 ${selectedBatch.quantity}`)
    return
  }
  try {
    await axios.post(`/inventory/${stockOutForm.inventoryId}/decrease`, null, {
      params: { quantity: stockOutForm.quantity }
    })
    ElMessage.success('出库成功')
    showStockOutModal.value = false
    loadInventory()
    resetStockOutForm()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '出库失败')
  }
}

// ---------- 搜索与分页 ----------
const handleSearch = () => {
  pagination.current = 1
  loadInventory()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadInventory()
}

onMounted(() => {
  loadInventory()
  loadWarehouses()
  loadDrugs()
})
</script>

<style scoped>
.inventory-list {
  background: white;
  border-radius: 10px;
  padding: 20px;
}
.search-bar {
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