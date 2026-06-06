
<template>
  <div class="special-drug">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="库存管理" name="inventory">
        <div class="inventory-section">
          <div class="search-bar">
            <el-input v-model="keyword" placeholder="搜索药品名称" class="search-input"></el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
          <el-table :data="inventoryList" border>
            <el-table-column prop="drugCode" label="药品编码" />
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="batchNo" label="批号" />
            <el-table-column prop="expireDate" label="有效期" />
            <el-table-column prop="quantity" label="库存数量" />
            <el-table-column prop="warehouseName" label="仓库" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="link" @click="viewDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="领用记录" name="records">
        <div class="records-section">
          <div class="search-bar">
            <el-input v-model="recordKeyword" placeholder="搜索处方号或患者" class="search-input"></el-input>
            <el-button type="primary" @click="loadRecords">搜索</el-button>
          </div>
          <el-table :data="recordList" border>
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="batchNo" label="批号" />
            <el-table-column prop="prescriptionNo" label="处方号" />
            <el-table-column prop="quantity" label="数量" />
            <el-table-column prop="purpose" label="用途" />
            <el-table-column prop="user1" label="授权人1" />
            <el-table-column prop="user2" label="授权人2" />
            <el-table-column prop="recycleStatus" label="回收状态">
              <template #default="scope">
                <el-tag :type="scope.row.recycleStatus === '已回收' ? 'success' : 'warning'">
                  {{ scope.row.recycleStatus || '未回收' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="领用时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button v-if="scope.row.recycleStatus !== '已回收'" type="link" @click="recycle(scope.row)">空安瓿回收</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="领用申请" name="apply">
        <div class="apply-section">
          <el-button type="success" @click="showApplyModal = true">新建领用申请</el-button>
          
          <el-table :data="applyList" border>
            <el-table-column prop="applyNo" label="申请单号" />
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="quantity" label="数量" />
            <el-table-column prop="purpose" label="用途" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">
                  {{ getStatusName(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button v-if="scope.row.status === 1" type="link" @click="approveApply(scope.row)">审批</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog title="空安瓿回收" v-model="showRecycleModal">
      <el-form :model="recycleForm" label-width="100px">
        <el-form-item label="回收单号">
          <el-input v-model="recycleForm.recycleNo" :disabled="true">{{ autoGenRecycleNo }}</el-input>
        </el-form-item>
        <el-form-item label="回收数量" prop="quantity">
          <el-input v-model.number="recycleForm.quantity"></el-input>
        </el-form-item>
        <el-form-item label="回收人" prop="recycler">
          <el-input v-model="recycleForm.recycler"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRecycleModal = false">取消</el-button>
        <el-button type="primary" @click="submitRecycle">确认回收</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="库存详情" v-model="showDetailModal" width="500px">
      <el-form :model="detailForm" label-width="100px">
        <el-form-item label="药品编码">
          <el-input :value="detailForm.drugCode" disabled></el-input>
        </el-form-item>
        <el-form-item label="药品名称">
          <el-input :value="detailForm.drugName" disabled></el-input>
        </el-form-item>
        <el-form-item label="规格">
          <el-input :value="detailForm.spec" disabled></el-input>
        </el-form-item>
        <el-form-item label="批号">
          <el-input :value="detailForm.batchNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="有效期">
          <el-input :value="detailForm.expireDate" disabled></el-input>
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input :value="detailForm.quantity" disabled></el-input>
        </el-form-item>
        <el-form-item label="仓库">
          <el-input :value="detailForm.warehouse" disabled></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="领用申请" v-model="showApplyModal" width="600px">
      <el-form :model="applyForm" ref="applyFormRef" label-width="100px">
        <el-form-item label="药品" prop="drugId">
          <el-select v-model="applyForm.drugId" @change="onDrugChange">
            <el-option v-for="drug in specialDrugs" :key="drug.id" :label="drug.drugName + ' - ' + drug.spec" :value="drug.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input v-model.number="applyForm.quantity"></el-input>
        </el-form-item>
        <el-form-item label="处方号" prop="prescriptionNo">
          <el-input v-model="applyForm.prescriptionNo"></el-input>
        </el-form-item>
        <el-form-item label="用途" prop="purpose">
          <el-input type="textarea" v-model="applyForm.purpose"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApplyModal = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const activeTab = ref('inventory')
const keyword = ref('')
const recordKeyword = ref('')
const inventoryList = ref([])
const recordList = ref([])
const applyList = ref([])
const specialDrugs = ref([])
const showRecycleModal = ref(false)
const showApplyModal = ref(false)
const showDetailModal = ref(false)
const applyFormRef = ref(null)

const detailForm = reactive({
  drugCode: '',
  drugName: '',
  spec: '',
  batchNo: '',
  expireDate: '',
  quantity: null,
  warehouse: ''
})

const recycleForm = reactive({
  recordId: null,
  recycleNo: '',
  quantity: null,
  recycler: ''
})

const applyForm = reactive({
  drugId: null,
  drugName: '',
  quantity: null,
  prescriptionNo: '',
  purpose: ''
})

const currentRecord = ref(null)

const autoGenRecycleNo = computed(() => {
  return 'RC' + new Date().toISOString().slice(2, 10).replace(/-/g, '') + String(Math.random()).slice(-4)
})

const statusNames = { 1: '待审批', 2: '已批准', 3: '已领用', 4: '已拒绝' }
const statusTagTypes = { 1: 'warning', 2: 'success', 3: 'info', 4: 'danger' }

const getStatusName = (status) => statusNames[status] || '未知'
const getStatusTagType = (status) => statusTagTypes[status] || 'default'

const onDrugChange = () => {
  if (applyForm.drugId) {
    const drug = specialDrugs.value.find(d => d.id === applyForm.drugId)
    if (drug) {
      applyForm.drugName = drug.drugName
    }
  } else {
    applyForm.drugName = ''
  }
}

const loadInventory = async () => {
  try {
    const response = await axios.get('/special/drugs/inventory', {
      params: { keyword: keyword.value }
    })
    if (response.code === 200) {
      inventoryList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载库存失败')
  }
}

const loadRecords = async () => {
  try {
    const response = await axios.get('/special/drugs/records', {
      params: { keyword: recordKeyword.value }
    })
    if (response.code === 200) {
      recordList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载领用记录失败')
  }
}

const loadApplyList = async () => {
  try {
    const response = await axios.get('/special/drugs/applies')
    if (response.code === 200) {
      applyList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载申请列表失败')
  }
}

const loadSpecialDrugs = async () => {
  try {
    const response = await axios.get('/drugs', { params: { isSpecial: 1 } })
    if (response.code === 200) {
      specialDrugs.value = response.data.records || response.data
    }
  } catch (error) {
    ElMessage.error('加载特殊药品失败')
  }
}

const handleSearch = () => {
  loadInventory()
}

const viewDetail = (row) => {
  detailForm.drugCode = row.drugCode || ''
  detailForm.drugName = row.drugName || ''
  detailForm.spec = row.spec || ''
  detailForm.batchNo = row.batchNo || ''
  detailForm.expireDate = row.expireDate || ''
  detailForm.quantity = row.quantity || null
  detailForm.warehouse = row.warehouse || ''
  showDetailModal.value = true
}

const recycle = (row) => {
  currentRecord.value = row
  recycleForm.recordId = row.id
  recycleForm.quantity = row.quantity
  showRecycleModal.value = true
}

const submitRecycle = async () => {
  try {
    await axios.post(`/special/drugs/records/${recycleForm.recordId}/recycle`, {
      recycleNo: autoGenRecycleNo.value,
      quantity: recycleForm.quantity,
      recycler: recycleForm.recycler
    })
    ElMessage.success('回收成功')
    showRecycleModal.value = false
    loadRecords()
    recycleForm.recycler = ''
  } catch (error) {
    ElMessage.error('回收失败')
  }
}

const submitApply = async () => {
  try {
    await axios.post('/special/drugs/applies', applyForm)
    ElMessage.success('申请提交成功')
    showApplyModal.value = false
    loadApplyList()
    applyForm.drugId = null
    applyForm.quantity = null
    applyForm.prescriptionNo = ''
    applyForm.purpose = ''
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const approveApply = async (row) => {
  if (confirm('确定批准此领用申请吗？需要双人授权。')) {
    try {
      await axios.put(`/special/drugs/applies/${row.id}/approve`, {
        user1: 'user1',
        user2: 'user2'
      })
      ElMessage.success('审批成功')
      loadApplyList()
    } catch (error) {
      ElMessage.error('审批失败')
    }
  }
}

onMounted(() => {
  loadInventory()
  loadRecords()
  loadApplyList()
  loadSpecialDrugs()
})
</script>

<style scoped>
.special-drug {
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

.inventory-section, .records-section, .apply-section {
  padding: 20px 0;
}
</style>
