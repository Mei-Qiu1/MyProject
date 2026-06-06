<template>
  <div class="prescription-manage">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索处方号或患者姓名" class="search-input"></el-input>
      <el-select v-model="status" placeholder="选择状态">
        <el-option label="全部" :value="-1"></el-option>
        <el-option label="待审核" :value="1"></el-option>
        <el-option label="已审核" :value="2"></el-option>
        <el-option label="已调配" :value="3"></el-option>
        <el-option label="已发药" :value="4"></el-option>
        <el-option label="已退药" :value="5"></el-option>
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openAddDialog">开具处方</el-button>
    </div>
    
    <el-table :data="prescriptionList" border>
      <el-table-column prop="prescriptionNo" label="处方号" />
      <el-table-column prop="patientName" label="患者姓名" />
      <el-table-column prop="patientAge" label="年龄" />
      <el-table-column prop="patientSex" label="性别" />
      <el-table-column prop="department" label="科室" />
      <el-table-column prop="doctorName" label="开方医生" />
      <el-table-column prop="type" label="处方类型">
        <template #default="scope">
          <el-tag :type="scope.row.type === 1 ? 'primary' : 'warning'">
            {{ scope.row.type === 1 ? '门诊' : '住院' }}
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
      <el-table-column prop="createTime" label="开方时间" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 1" link type="danger" @click="deletePrescription(scope.row)">删除</el-button>
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
    
    <!-- 处方详情对话框 -->
    <el-dialog title="处方详情" v-model="showDetailModal" width="700px">
      <el-descriptions :column="2">
        <el-descriptions-item label="处方号">{{ detailData.prescriptionNo }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detailData.patientAge }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.patientSex }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ detailData.department }}</el-descriptions-item>
        <el-descriptions-item label="开方医生">{{ detailData.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="处方类型">{{ detailData.type === 1 ? '门诊' : '住院' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusName(detailData.status) }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin-top: 20px">处方明细</h4>
      <el-table :data="detailData.details" border>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="usage" label="用法" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="amount" label="金额" />
      </el-table>
    </el-dialog>
    
    <!-- 开具处方对话框 -->
    <el-dialog title="开具处方" v-model="showAddModal" width="800px">
      <el-form :model="formData" ref="formRef" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者姓名" prop="patientName">
              <el-input v-model="formData.patientName" placeholder="请输入患者姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="患者ID" prop="patientId">
              <el-input v-model="formData.patientId" placeholder="请输入患者ID"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄" prop="patientAge">
              <el-input-number v-model="formData.patientAge" :min="0" :max="150" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="patientSex">
              <el-select v-model="formData.patientSex" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科室" prop="department">
              <el-input v-model="formData.department" placeholder="请输入科室"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处方类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择处方类型" style="width: 100%">
                <el-option label="门诊" :value="1"></el-option>
                <el-option label="住院" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="开方医生">
          <el-input v-model="currentDoctorName" disabled></el-input>
        </el-form-item>
      </el-form>
      
      <div class="detail-section">
        <div class="detail-header">
          <h4>处方明细</h4>
          <el-button type="primary" size="small" @click="openDrugSelector">添加药品</el-button>
        </div>
        <el-table :data="detailList" border>
          <el-table-column prop="drugName" label="药品名称" width="150" />
          <el-table-column prop="spec" label="规格" width="100" />
          <el-table-column prop="quantity" label="数量" width="120">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" :max="999" size="small" @change="updateAmount(scope.row)"></el-input-number>
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="60" />
          <el-table-column prop="usage" label="用法">
            <template #default="scope">
              <el-input v-model="scope.row.usage" placeholder="如：每日3次，每次1片" size="small"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="80">
            <template #default="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="80">
            <template #default="scope">
              ¥{{ scope.row.amount }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button link type="danger" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="detailList.length > 0" class="total-amount">
          <span>合计金额：<strong>¥{{ totalAmount.toFixed(2) }}</strong></span>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="savePrescription" :loading="saveLoading">保存处方</el-button>
      </template>
    </el-dialog>
    
    <!-- 药品选择对话框 -->
    <el-dialog title="选择药品" v-model="showDrugSelector" width="700px">
      <div class="drug-selector">
        <el-input v-model="drugKeyword" placeholder="搜索药品名称或编码" @input="searchDrugs" clearable>
          <template #prefix>
            <el-icon>Search</el-icon>
          </template>
        </el-input>
        <el-table :data="drugOptions" border @row-click="selectDrug" style="margin-top: 10px">
          <el-table-column prop="drugName" label="药品名称" />
          <el-table-column prop="drugCode" label="药品编码" width="120" />
          <el-table-column prop="spec" label="规格" width="100" />
          <el-table-column prop="dosageForm" label="剂型" width="80" />
          <el-table-column prop="unit" label="单位" width="60" />
          <el-table-column prop="retailPrice" label="零售价" width="80">
            <template #default="scope">
              ¥{{ scope.row.retailPrice }}
            </template>
          </el-table-column>
          <el-table-column label="库存" width="80">
            <template #default="scope">
              <el-tag v-if="getStock(scope.row.id) > 0" type="success">{{ getStock(scope.row.id) }}</el-tag>
              <el-tag v-else type="danger">无库存</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 10px; color: #999; font-size: 12px;">
          提示：点击药品行即可添加到处方明细
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

// 当前登录医生信息
const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
const currentDoctorName = userInfo.realName || '医生'
const currentDoctorId = userInfo.id

const keyword = ref('')
const status = ref(-1)
const prescriptionList = ref([])
const drugOptions = ref([])
const drugKeyword = ref('')
const showDetailModal = ref(false)
const showAddModal = ref(false)
const showDrugSelector = ref(false)
const formRef = ref(null)
const saveLoading = ref(false)

// 药品库存缓存
const stockMap = ref({})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const detailData = reactive({
  prescriptionNo: '',
  patientName: '',
  patientAge: null,
  patientSex: '',
  department: '',
  doctorName: '',
  type: 1,
  status: 1,
  details: []
})

const formData = reactive({
  patientName: '',
  patientId: '',
  patientAge: null,
  patientSex: '',
  department: '',
  type: 1
})

const formRules = {
  patientName: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
  patientId: [{ required: true, message: '请输入患者ID', trigger: 'blur' }],
  patientAge: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  patientSex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  department: [{ required: true, message: '请输入科室', trigger: 'blur' }],
  type: [{ required: true, message: '请选择处方类型', trigger: 'change' }]
}

const detailList = ref([])

// 计算合计金额
const totalAmount = computed(() => {
  return detailList.value.reduce((sum, item) => sum + (item.amount || 0), 0)
})

const statusNames = { 1: '待审核', 2: '已审核', 3: '已调配', 4: '已发药', 5: '已退药', 6: '已拒绝' }
const statusTagTypes = { 1: 'warning', 2: 'success', 3: 'primary', 4: 'info', 5: 'danger', 6: 'danger' }

const getStatusName = (s) => statusNames[s] || '未知'
const getStatusTagType = (s) => statusTagTypes[s] || 'default'

// 获取药品库存
const getStock = (drugId) => {
  return stockMap.value[drugId] || 0
}

// 加载处方列表
const loadPrescriptions = async () => {
  try {
    const params = {
      page: pagination.current,
      size: pagination.size
    }
    if (keyword.value && keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }
    if (status.value !== -1 && status.value !== undefined) {
      params.status = status.value
    }
    const response = await axios.get('/pharmacy/prescriptions', { params })
    if (response.code === 200) {
      // 过滤出当前医生开具的处方
      prescriptionList.value = (response.data.records || []).filter(p => p.doctorId === currentDoctorId || p.doctorName === currentDoctorName)
      pagination.total = prescriptionList.value.length
    }
  } catch (error) {
    ElMessage.error('加载处方列表失败')
  }
}

// 搜索药品
const searchDrugs = async () => {
  try {
    const response = await axios.get('/drugs', { params: { keyword: drugKeyword.value, size: 50 } })
    if (response.code === 200) {
      drugOptions.value = response.data.records || response.data || []
      // 加载库存信息
      loadStockForDrugs()
    }
  } catch (error) {
    ElMessage.error('搜索药品失败')
  }
}

// 加载药品库存
const loadStockForDrugs = async () => {
  try {
    const response = await axios.get('/inventory', { params: { size: 1000 } })
    if (response.code === 200) {
      const inventoryList = response.data.records || response.data || []
      // 按药品ID汇总库存
      const stockData = {}
      inventoryList.forEach(inv => {
        if (inv.quantity > 0) {
          stockData[inv.drugId] = (stockData[inv.drugId] || 0) + inv.quantity
        }
      })
      stockMap.value = stockData
    }
  } catch (error) {
    console.error('加载库存失败:', error)
  }
}

// 打开新增处方对话框
const openAddDialog = () => {
  // 重置表单
  Object.keys(formData).forEach(key => {
    formData[key] = key === 'type' ? 1 : (key === 'patientAge' ? null : '')
  })
  detailList.value = []
  showAddModal.value = true
}

// 打开药品选择器
const openDrugSelector = () => {
  drugKeyword.value = ''
  showDrugSelector.value = true
  searchDrugs()
}

// 选择药品
const selectDrug = (drug) => {
  const existing = detailList.value.find(d => d.drugId === drug.id)
  if (existing) {
    ElMessage.warning('该药品已在处方中')
    return
  }
  
  // 检查库存
  const stock = getStock(drug.id)
  if (stock <= 0) {
    ElMessage.warning('该药品暂无库存')
    return
  }
  
  detailList.value.push({
    drugId: drug.id,
    drugName: drug.drugName,
    spec: drug.spec,
    unit: drug.unit,
    quantity: 1,
    usage: '',
    price: drug.retailPrice,
    amount: drug.retailPrice
  })
  showDrugSelector.value = false
  ElMessage.success(`已添加 ${drug.drugName}`)
}

// 更新金额
const updateAmount = (row) => {
  row.amount = (row.price || 0) * (row.quantity || 0)
}

// 删除药品明细
const removeDetail = (index) => {
  detailList.value.splice(index, 1)
}

// 保存处方
const savePrescription = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (detailList.value.length === 0) {
      ElMessage.warning('请添加药品')
      return
    }
    
    // 检查用法是否填写
    const emptyUsage = detailList.value.find(d => !d.usage || !d.usage.trim())
    if (emptyUsage) {
      ElMessage.warning('请填写所有药品的用法')
      return
    }
    
    saveLoading.value = true
    
    await axios.post('/pharmacy/prescriptions', {
      ...formData,
      doctorName: currentDoctorName,
      doctorId: currentDoctorId,
      details: detailList.value
    })
    
    ElMessage.success('处方开具成功，等待审核')
    showAddModal.value = false
    loadPrescriptions()
  } catch (error) {
    if (error.name !== 'ElFormValidationError') {
      ElMessage.error(error.response?.data?.message || '保存失败')
    }
  } finally {
    saveLoading.value = false
  }
}

// 查看详情
const viewDetail = async (row) => {
  try {
    const response = await axios.get(`/pharmacy/prescriptions/${row.id}`)
    if (response.code === 200) {
      Object.assign(detailData, response.data)
      showDetailModal.value = true
    } else {
      ElMessage.error(response.message || '获取处方详情失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取处方详情失败')
  }
}

// 删除处方（仅待审核状态可删除）
const deletePrescription = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该处方吗？删除后无法恢复。', '提示', { type: 'warning' })
    await axios.delete(`/pharmacy/prescriptions/${row.id}`)
    ElMessage.success('删除成功')
    loadPrescriptions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadPrescriptions()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadPrescriptions()
}

onMounted(() => {
  loadPrescriptions()
  loadStockForDrugs()
})
</script>

<style scoped>
.prescription-manage {
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

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.detail-header h4 {
  margin: 0;
}

.total-amount {
  margin-top: 15px;
  text-align: right;
  font-size: 16px;
  color: #333;
}

.total-amount strong {
  color: #f56c6c;
  font-size: 18px;
}

.drug-selector {
  min-height: 300px;
}
</style>