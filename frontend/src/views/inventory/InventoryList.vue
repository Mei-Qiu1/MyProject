
<template>
  <div class="inventory-list">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索药品名称或批号" class="search-input"></el-input>
      <el-select v-model="warehouseId" placeholder="选择仓库">
        <el-option label="全部" :value="0"></el-option>
        <el-option v-for="wh in warehouses" :key="wh.id" :label="wh.warehouseName" :value="wh.id"></el-option>
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="showStockInModal = true">入库</el-button>
      <el-button type="warning" @click="showStockOutModal = true">出库</el-button>
    </div>
    
    <el-table :data="inventoryList" border>
      <el-table-column prop="drugCode" label="药品编码" />
      <el-table-column prop="drugName" label="药品名称" />
      <el-table-column prop="spec" label="规格" />
      <el-table-column prop="batchNo" label="批号" />
      <el-table-column prop="productionDate" label="生产日期" />
      <el-table-column prop="expireDate" label="有效期">
        <template #default="scope">
          <el-tag :type="getExpireTagType(scope.row.expireDate)">
            {{ scope.row.expireDate }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="库存数量" />
      <el-table-column prop="unitPrice" label="单价" />
      <el-table-column prop="warehouseName" label="仓库" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row)">详情</el-button>
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
    
    <el-dialog title="入库登记" v-model="showStockInModal" width="600px">
      <el-form :model="stockInForm" ref="stockInFormRef" label-width="100px">
        <el-form-item label="药品" prop="drugId">
          <el-select v-model="stockInForm.drugId" @change="onDrugSelect">
            <el-option v-for="drug in drugOptions" :key="drug.id" :label="drug.drugName + ' - ' + drug.spec" :value="drug.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="批号" prop="batchNo">
          <el-input v-model="stockInForm.batchNo"></el-input>
        </el-form-item>
        <el-form-item label="生产日期" prop="productionDate">
          <el-date-picker v-model="stockInForm.productionDate" type="date"></el-date-picker>
        </el-form-item>
        <el-form-item label="有效期" prop="expireDate">
          <el-date-picker v-model="stockInForm.expireDate" type="date"></el-date-picker>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input v-model.number="stockInForm.quantity"></el-input>
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input v-model.number="stockInForm.unitPrice"></el-input>
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="stockInForm.warehouseId">
            <el-option v-for="wh in warehouses" :key="wh.id" :label="wh.warehouseName" :value="wh.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="stockInForm.remark"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStockInModal = false">取消</el-button>
        <el-button type="primary" @click="submitStockIn">确认入库</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="出库登记" v-model="showStockOutModal" width="600px">
      <el-form :model="stockOutForm" ref="stockOutFormRef" label-width="100px">
        <el-form-item label="药品" prop="drugId">
          <el-select v-model="stockOutForm.drugId" @change="onDrugSelectForOut">
            <el-option v-for="drug in drugOptions" :key="drug.id" :label="drug.drugName + ' - ' + drug.spec" :value="drug.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="批次" prop="inventoryId">
          <el-select v-model="stockOutForm.inventoryId">
            <el-option v-for="inv in currentInventories" :key="inv.id" :label="inv.batchNo + ' (剩余: ' + inv.quantity + ')' " :value="inv.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity">
          <el-input v-model.number="stockOutForm.quantity"></el-input>
        </el-form-item>
        <el-form-item label="出库类型" prop="type">
          <el-select v-model="stockOutForm.type">
            <el-option label="领用出库" :value="1"></el-option>
            <el-option label="调拨出库" :value="2"></el-option>
            <el-option label="报损出库" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="stockOutForm.remark"></el-input>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const warehouseId = ref(0)
const inventoryList = ref([])
const warehouses = ref([])
const drugOptions = ref([])
const currentInventories = ref([])
const showStockInModal = ref(false)
const showStockOutModal = ref(false)
const stockInFormRef = ref(null)
const stockOutFormRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const stockInForm = reactive({
  drugId: null,
  batchNo: '',
  productionDate: null,
  expireDate: null,
  quantity: null,
  unitPrice: null,
  warehouseId: null,
  remark: ''
})

const stockOutForm = reactive({
  drugId: null,
  inventoryId: null,
  quantity: null,
  type: 1,
  remark: ''
})

const getExpireTagType = (expireDate) => {
  const expire = new Date(expireDate)
  const now = new Date()
  const diffDays = Math.floor((expire - now) / (1000 * 60 * 60 * 24))
  
  if (diffDays < 30) return 'danger'
  if (diffDays < 90) return 'warning'
  if (diffDays < 180) return 'info'
  return ''
}

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
    ElMessage.error('加载库存失败')
  }
}

const loadWarehouses = async () => {
  try {
    const response = await axios.get('/inventory/warehouses')
    if (response.code === 200) {
      warehouses.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载仓库失败')
  }
}

const loadDrugs = async () => {
  try {
    const response = await axios.get('/drugs', { params: { size: 100 } })
    if (response.code === 200) {
      drugOptions.value = response.data.records || response.data
    }
  } catch (error) {
    ElMessage.error('加载药品失败')
  }
}

const onDrugSelect = (drugId) => {
  const drug = drugOptions.value.find(d => d.id === drugId)
  if (drug) {
    stockInForm.unitPrice = drug.purchasePrice
  }
}

const onDrugSelectForOut = async (drugId) => {
  try {
    const response = await axios.get('/inventory', { params: { drugId } })
    if (response.code === 200) {
      currentInventories.value = response.data.records || response.data
    }
  } catch (error) {
    ElMessage.error('获取库存批次失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadInventory()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadInventory()
}

const viewDetail = (row) => {
}

const submitStockIn = async () => {
  try {
    await axios.post('/inventory', stockInForm)
    ElMessage.success('入库成功')
    showStockInModal.value = false
    loadInventory()
    resetStockInForm()
  } catch (error) {
    ElMessage.error('入库失败')
  }
}

const submitStockOut = async () => {
  try {
    await axios.post(`/inventory/${stockOutForm.inventoryId}/decrease`, {}, {
      params: { quantity: stockOutForm.quantity }
    })
    ElMessage.success('出库成功')
    showStockOutModal.value = false
    loadInventory()
    resetStockOutForm()
  } catch (error) {
    ElMessage.error('出库失败')
  }
}

const resetStockInForm = () => {
  Object.keys(stockInForm).forEach(key => {
    stockInForm[key] = null
  })
  stockInForm.remark = ''
}

const resetStockOutForm = () => {
  Object.keys(stockOutForm).forEach(key => {
    stockOutForm[key] = null
  })
  stockOutForm.type = 1
  stockOutForm.remark = ''
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
}

.search-input {
  width: 300px;
}
</style>
