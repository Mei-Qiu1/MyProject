
<template>
  <div class="prescription-list">
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
      <el-button type="success" @click="showAddModal = true">新增处方</el-button>
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
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="link" @click="viewDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 1" type="link" @click="auditPrescription(scope.row)">审核</el-button>
          <el-button v-if="scope.row.status === 2" type="link" @click="dispense(scope.row)">调配</el-button>
          <el-button v-if="scope.row.status === 3" type="link" @click="dispense(scope.row)">发药</el-button>
          <el-button v-if="scope.row.status === 4" type="link" @click="returnDrug(scope.row)">退药</el-button>
          <el-button v-if="scope.row.status === 6" type="link" disabled>已拒绝</el-button>
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
    
    <el-dialog title="处方详情" v-model="showDetailModal" width="700px">
      <el-descriptions :column="2" :data="detailData">
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
      <div v-if="detailData.auditComment" style="margin-top: 20px">
        <h4>审核意见</h4>
        <p>{{ detailData.auditComment }}</p>
      </div>
    </el-dialog>
    
    <el-dialog title="处方审核" v-model="showAuditModal">
      <el-form :model="auditData" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditData.result">
            <el-radio :value="2">通过</el-radio>
            <el-radio :value="3">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input type="textarea" v-model="auditData.comment"></el-input>
        </el-form-item>
        <div v-if="auditWarnings.length > 0" style="color: #f56c6c">
          <h4>审核警告：</h4>
          <ul>
            <li v-for="(warning, index) in auditWarnings" :key="index">{{ warning }}</li>
          </ul>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="showAuditModal = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确认审核</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="新增处方" v-model="showAddModal" width="700px">
      <el-form :model="formData" ref="formRef" :rules="formRules" label-width="100px">
        <el-form-item label="患者姓名" prop="patientName" required>
          <el-input v-model="formData.patientName"></el-input>
        </el-form-item>
        <el-form-item label="患者ID" prop="patientId" required>
          <el-input v-model="formData.patientId"></el-input>
        </el-form-item>
        <el-form-item label="年龄" prop="patientAge" required>
          <el-input v-model.number="formData.patientAge"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="patientSex" required>
          <el-select v-model="formData.patientSex">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="科室" prop="department" required>
          <el-input v-model="formData.department"></el-input>
        </el-form-item>
        <el-form-item label="医生" prop="doctorName" required>
          <el-input v-model="formData.doctorName"></el-input>
        </el-form-item>
        <el-form-item label="处方类型" prop="type" required>
          <el-select v-model="formData.type">
            <el-option label="门诊" :value="1"></el-option>
            <el-option label="住院" :value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="detail-section">
        <h4>处方明细</h4>
        <el-table :data="detailList" border>
          <el-table-column prop="drugName" label="药品名称" />
          <el-table-column prop="spec" label="规格" />
          <el-table-column prop="quantity" label="数量">
            <template #default="scope">
              <el-input v-model.number="scope.row.quantity" style="width: 80px"></el-input>
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" />
          <el-table-column prop="usage" label="用法">
            <template #default="scope">
              <el-input v-model="scope.row.usage" style="width: 150px"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="link" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="openDrugSelector">添加药品</el-button>
      </div>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="savePrescription">保存处方</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="药品选择" v-model="showDrugSelector" width="600px">
      <el-input v-model="drugKeyword" placeholder="搜索药品" @input="searchDrugs"></el-input>
      <el-table :data="drugOptions" border @row-click="selectDrug">
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="dosageForm" label="剂型" />
        <el-table-column prop="retailPrice" label="零售价" />
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
const prescriptionList = ref([])
const drugOptions = ref([])
const drugKeyword = ref('')
const showDetailModal = ref(false)
const showAuditModal = ref(false)
const showAddModal = ref(false)
const showDrugSelector = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const detailData = reactive({})
const auditData = reactive({
  prescriptionId: null,
  result: 2,
  comment: ''
})

const auditWarnings = ref([])

const formData = reactive({
  patientName: '',
  patientId: '',
  patientAge: null,
  patientSex: '',
  department: '',
  doctorName: '',
  type: 1
})

const formRules = {
  patientName: [
    { required: true, message: '请输入患者姓名', trigger: 'blur' }
  ],
  patientId: [
    { required: true, message: '请输入患者ID', trigger: 'blur' }
  ],
  patientAge: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', min: 0, max: 150, message: '年龄必须在0-150之间', trigger: 'blur' }
  ],
  patientSex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  department: [
    { required: true, message: '请输入科室', trigger: 'blur' }
  ],
  doctorName: [
    { required: true, message: '请输入医生姓名', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择处方类型', trigger: 'change' }
  ]
}

const detailList = ref([])

const statusNames = { 1: '待审核', 2: '已审核', 3: '已调配', 4: '已发药', 5: '已退药', 6: '已拒绝' }
const statusTagTypes = { 1: 'warning', 2: 'success', 3: 'primary', 4: 'info', 5: 'danger', 6: 'danger' }

const getStatusName = (status) => statusNames[status] || '未知'
const getStatusTagType = (status) => statusTagTypes[status] || 'default'

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
      prescriptionList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载处方列表失败')
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
      unit: drug.unit,
      quantity: 1,
      usage: '',
      price: drug.retailPrice,
      amount: drug.retailPrice
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
  loadPrescriptions()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadPrescriptions()
}

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

const auditPrescription = async (row) => {
  auditData.prescriptionId = row.id
  auditWarnings.value = []
  
  try {
    const response = await axios.get(`/pharmacy/prescriptions/${row.id}/audit-check`)
    if (response.code === 200) {
      auditWarnings.value = response.data.warnings || []
    }
  } catch (error) {
    console.log('审核检查失败:', error)
  }
  
  showAuditModal.value = true
}

const submitAudit = async () => {
  try {
    const response = await axios.put(`/pharmacy/prescriptions/${auditData.prescriptionId}/audit`, {
      status: auditData.result,
      comment: auditData.comment
    })
    if (response.code === 200) {
      ElMessage.success('审核成功')
      showAuditModal.value = false
      loadPrescriptions()
    } else {
      ElMessage.error(response.message || '审核失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '审核失败')
  }
}

const dispense = async (row) => {
  try {
    const response = await axios.post(`/pharmacy/prescriptions/${row.id}/dispense`)
    if (response.code === 200) {
      ElMessage.success('操作成功')
      loadPrescriptions()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const returnDrug = async (row) => {
  if (confirm(`确定要执行退药操作吗？`)) {
    try {
      const response = await axios.post(`/pharmacy/prescriptions/${row.id}/return`)
      if (response.code === 200) {
        ElMessage.success('退药成功')
        loadPrescriptions()
      } else {
        ElMessage.error(response.message || '退药失败')
      }
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '退药失败')
    }
  }
}

const savePrescription = async () => {
  if (!formRef.value) {
    return
  }
  try {
    await formRef.value.validate()
    if (detailList.value.length === 0) {
      ElMessage.warning('请添加药品')
      return
    }
    await axios.post('/pharmacy/prescriptions', {
      ...formData,
      details: detailList.value
    })
    ElMessage.success('处方保存成功')
    showAddModal.value = false
    loadPrescriptions()
    resetForm()
  } catch (error) {
    if (error.name !== 'ElFormValidationError') {
      ElMessage.error('保存失败')
    }
  }
}

const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = key === 'type' ? 1 : null
  })
  detailList.value = []
}

onMounted(() => {
  loadPrescriptions()
})
</script>

<style scoped>
.prescription-list {
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
