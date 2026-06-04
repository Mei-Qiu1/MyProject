
<template>
  <div class="medical-order">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索医嘱号或患者姓名" class="search-input"></el-input>
      <el-select v-model="status" placeholder="选择状态">
        <el-option label="全部" :value="-1"></el-option>
        <el-option label="待执行" :value="1"></el-option>
        <el-option label="执行中" :value="2"></el-option>
        <el-option label="已完成" :value="3"></el-option>
        <el-option label="已取消" :value="4"></el-option>
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="showAddModal = true">新增医嘱</el-button>
    </div>
    
    <el-table :data="orderList" border>
      <el-table-column prop="orderNo" label="医嘱号" />
      <el-table-column prop="patientId" label="患者ID" />
      <el-table-column prop="patientName" label="患者姓名" />
      <el-table-column prop="department" label="科室" />
      <el-table-column prop="bedNo" label="床位号" />
      <el-table-column prop="doctorName" label="医生" />
      <el-table-column prop="type" label="医嘱类型">
        <template #default="scope">
          <el-tag :type="scope.row.type === 1 ? 'primary' : 'warning'">
            {{ scope.row.type === 1 ? '长期' : '临时' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="orderTime" label="开立时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 1" type="text" @click="executeOrder(scope.row)">执行</el-button>
          <el-button v-if="scope.row.status === 2" type="text" @click="createDelivery(scope.row)">生成配送单</el-button>
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
    
    <el-dialog title="医嘱详情" v-model="showDetailModal" width="700px">
      <el-descriptions :column="2" :data="detailData">
        <el-descriptions-item label="医嘱号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="患者ID">{{ detailData.patientId }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ detailData.department }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ detailData.bedNo }}</el-descriptions-item>
        <el-descriptions-item label="医生">{{ detailData.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="医嘱类型">{{ detailData.type === 1 ? '长期' : '临时' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusName(detailData.status) }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin-top: 20px">用药明细</h4>
      <el-table :data="detailData.details" border>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="frequency" label="频次" />
        <el-table-column prop="duration" label="疗程" />
      </el-table>
    </el-dialog>
    
    <el-dialog title="新增医嘱" v-model="showAddModal" width="700px">
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="患者ID" prop="patientId">
          <el-input v-model="formData.patientId"></el-input>
        </el-form-item>
        <el-form-item label="患者姓名" prop="patientName">
          <el-input v-model="formData.patientName"></el-input>
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-input v-model="formData.department"></el-input>
        </el-form-item>
        <el-form-item label="床位号" prop="bedNo">
          <el-input v-model="formData.bedNo"></el-input>
        </el-form-item>
        <el-form-item label="医生" prop="doctorName">
          <el-input v-model="formData.doctorName"></el-input>
        </el-form-item>
        <el-form-item label="医嘱类型" prop="type">
          <el-select v-model="formData.type">
            <el-option label="长期医嘱" :value="1"></el-option>
            <el-option label="临时医嘱" :value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="detail-section">
        <h4>用药明细</h4>
        <el-table :data="detailList" border>
          <el-table-column prop="drugName" label="药品名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="quantity" label="数量">
            <template #default="scope">
              <el-input v-model.number="scope.row.quantity" style="width: 80px"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="frequency" label="频次">
            <template #default="scope">
              <el-input v-model="scope.row.frequency" style="width: 100px"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="疗程">
            <template #default="scope">
              <el-input v-model="scope.row.duration" style="width: 80px"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="text" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="openDrugSelector">添加药品</el-button>
      </div>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveOrder">保存医嘱</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="药品选择" v-model="showDrugSelector" width="600px">
      <el-input v-model="drugKeyword" placeholder="搜索药品" @input="searchDrugs"></el-input>
      <el-table :data="drugOptions" border @row-click="selectDrug">
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="dosageForm" label="剂型" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const status = ref(-1)
const orderList = ref([])
const drugOptions = ref([])
const drugKeyword = ref('')
const showDetailModal = ref(false)
const showAddModal = ref(false)
const showDrugSelector = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const detailData = reactive({})

const formData = reactive({
  patientId: '',
  patientName: '',
  department: '',
  bedNo: '',
  doctorName: '',
  type: 1
})

const detailList = ref([])

const statusNames = { 1: '待执行', 2: '执行中', 3: '已完成', 4: '已取消' }
const statusTagTypes = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }

const getStatusName = (status) => statusNames[status] || '未知'
const getStatusTagType = (status) => statusTagTypes[status] || 'default'

const loadOrders = async () => {
  try {
    const response = await axios.get('/clinical/orders', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        status: status.value === -1 ? undefined : status.value
      }
    })
    if (response.code === 200) {
      orderList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载医嘱列表失败')
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

const openDrugSelector = () => {
  drugKeyword.value = ''
  showDrugSelector.value = true
  searchDrugs()
}

const selectDrug = (drug) => {
  const existing = detailList.value.find(d => d.drugId === drug.id)
  if (!existing) {
    detailList.value.push({
      drugId: drug.id,
      drugName: drug.drugName,
      spec: drug.spec,
      quantity: 1,
      frequency: '每日一次',
      duration: '7天'
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
  loadOrders()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadOrders()
}

const viewDetail = async (row) => {
  try {
    const response = await axios.get(`/clinical/orders/${row.id}`)
    if (response.code === 200) {
      Object.assign(detailData, response.data)
      showDetailModal.value = true
    }
  } catch (error) {
    ElMessage.error('获取医嘱详情失败')
  }
}

const executeOrder = async (row) => {
  try {
    await axios.put(`/clinical/orders/${row.id}/execute`)
    ElMessage.success('医嘱已执行')
    loadOrders()
  } catch (error) {
    ElMessage.error('执行失败')
  }
}

const createDelivery = async (row) => {
  try {
    const response = await axios.post(`/pharmacy/delivery/order/${row.id}`)
    if (response.code === 200) {
      ElMessage.success('配送单已生成')
      loadOrders()
    } else {
      ElMessage.error(response.message || '生成失败')
    }
  } catch (error) {
    ElMessage.error('生成失败')
  }
}

const saveOrder = async () => {
  if (detailList.value.length === 0) {
    ElMessage.warning('请添加用药明细')
    return
  }
  try {
    await axios.post('/clinical/orders', {
      ...formData,
      details: detailList.value
    })
    ElMessage.success('医嘱保存成功')
    showAddModal.value = false
    loadOrders()
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = key === 'type' ? 1 : ''
  })
  detailList.value = []
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.medical-order {
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
