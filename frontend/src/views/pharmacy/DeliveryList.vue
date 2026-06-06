<template>
  <div class="delivery-list">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索配送单号或患者姓名" class="search-input"></el-input>
      <el-select v-model="status" placeholder="选择状态">
        <el-option label="全部" :value="-1"></el-option>
        <el-option label="待配送" :value="1"></el-option>
        <el-option label="已签收" :value="2"></el-option>
      </el-select>
      <el-input v-model="department" placeholder="科室" class="search-input"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    
    <el-table :data="deliveryList" border>
      <el-table-column prop="deliveryNo" label="配送单号" />
      <el-table-column prop="patientName" label="患者姓名" />
      <el-table-column prop="patientId" label="患者ID" />
      <el-table-column prop="department" label="科室" />
      <el-table-column prop="bedNo" label="床位号" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="生成时间" />
      <el-table-column prop="signer" label="签收人" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="link" @click="viewDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 1" type="link" @click="handleSign(scope.row)">签收</el-button>
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
    
    <el-dialog title="配送单详情" v-model="showDetailModal" width="700px">
      <el-descriptions :column="2" :data="detailData">
        <el-descriptions-item label="配送单号">{{ detailData.deliveryNo }}</el-descriptions-item>
        <el-descriptions-item label="医嘱号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="患者ID">{{ detailData.patientId }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ detailData.department }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ detailData.bedNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusName(detailData.status) }}</el-descriptions-item>
        <el-descriptions-item label="签收人">{{ detailData.signer || '-' }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin-top: 20px">配送明细</h4>
      <el-table :data="detailData.details" border>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="frequency" label="频次" />
        <el-table-column prop="duration" label="疗程" />
      </el-table>
    </el-dialog>
    
    <el-dialog title="签收配送单" v-model="showSignModal">
      <el-form :model="signForm" ref="signFormRef" label-width="100px">
        <el-form-item label="签收人" prop="signer">
          <el-input v-model="signForm.signer"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSignModal = false">取消</el-button>
        <el-button type="primary" @click="confirmSign">确认签收</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const status = ref(-1)
const department = ref('')
const deliveryList = ref([])
const showDetailModal = ref(false)
const showSignModal = ref(false)
const signFormRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const detailData = reactive({})

const signForm = reactive({
  signer: ''
})

const currentDeliveryId = ref(null)

const statusNames = { 1: '待配送', 2: '已签收' }
const statusTagTypes = { 1: 'warning', 2: 'success' }

const getStatusName = (status) => statusNames[status] || '未知'
const getStatusTagType = (status) => statusTagTypes[status] || 'default'

const loadDeliveries = async () => {
  try {
    const response = await axios.get('/pharmacy/delivery', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        status: status.value === -1 ? undefined : status.value,
        department: department.value || undefined
      }
    })
    if (response.code === 200) {
      deliveryList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载配送单列表失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadDeliveries()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadDeliveries()
}

const viewDetail = async (row) => {
  try {
    const response = await axios.get(`/pharmacy/delivery/${row.id}`)
    if (response.code === 200) {
      Object.assign(detailData, response.data)
      showDetailModal.value = true
    }
  } catch (error) {
    ElMessage.error('获取配送单详情失败')
  }
}

const handleSign = (row) => {
  currentDeliveryId.value = row.id
  signForm.signer = ''
  showSignModal.value = true
}

const confirmSign = async () => {
  if (!signForm.signer.trim()) {
    ElMessage.warning('请输入签收人')
    return
  }
  try {
    const response = await axios.put(`/pharmacy/delivery/${currentDeliveryId.value}/sign`, {
      signer: signForm.signer
    })
    if (response.code === 200) {
      ElMessage.success('签收成功')
      showSignModal.value = false
      loadDeliveries()
    } else {
      ElMessage.error(response.message || '签收失败')
    }
  } catch (error) {
    ElMessage.error('签收失败')
  }
}

onMounted(() => {
  loadDeliveries()
})
</script>

<style scoped>
.delivery-list {
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
  width: 200px;
}
</style>