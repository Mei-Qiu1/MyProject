<template>
  <div class="purchase-order">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索订单号" class="search-input" clearable />
      <el-select v-model="status" placeholder="选择状态" clearable>
        <el-option label="全部" :value="-1" />
        <el-option label="待发货" :value="1" />
        <el-option label="已发货" :value="2" />
        <el-option label="已验收" :value="3" />
        <el-option label="已完成" :value="4" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <el-table :data="orderList" border>
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="supplierName" label="供应商" />
      <el-table-column prop="totalAmount" label="订单金额" />
      <el-table-column prop="deliveryDate" label="预计到货" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusName(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button type="text" @click="viewDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === 2" type="text" @click="receiveOrder(scope.row)">到货验收</el-button>
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

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" v-model="showDetailModal" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detailData.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">{{ detailData.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="预计到货">{{ detailData.deliveryDate || '待定' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(detailData.status)">
            {{ getStatusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin-top: 20px">订单明细</h4>
      <el-table :data="detailData.details" border>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="amount" label="金额" />
      </el-table>
    </el-dialog>

    <!-- 到货验收对话框 -->
    <el-dialog title="到货验收" v-model="showReceiveModal" width="700px">
      <h4>{{ receiveData.orderNo }} 到货验收</h4>
      <el-table :data="receiveDetails" border>
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="orderQuantity" label="订单数量" />
        <el-table-column prop="receivedQuantity" label="到货数量">
          <template #default="scope">
            <el-input v-model.number="scope.row.receivedQuantity" style="width: 80px" />
          </template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批号">
          <template #default="scope">
            <el-input v-model="scope.row.batchNo" />
          </template>
        </el-table-column>
        <el-table-column prop="expireDate" label="效期">
          <template #default="scope">
            <el-date-picker v-model="scope.row.expireDate" type="date" />
          </template>
        </el-table-column>
        <el-table-column prop="qualityStatus" label="质量验收">
          <template #default="scope">
            <el-radio-group v-model="scope.row.qualityStatus">
              <el-radio :value="1">合格</el-radio>
              <el-radio :value="0">不合格</el-radio>
            </el-radio-group>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showReceiveModal = false">取消</el-button>
        <el-button type="primary" @click="submitReceive">确认验收</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const status = ref(-1)
const orderList = ref([])
const showDetailModal = ref(false)
const showReceiveModal = ref(false)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const detailData = reactive({
  orderNo: '',
  supplierName: '',
  totalAmount: '',
  deliveryDate: '',
  status: '',
  createTime: '',
  details: []
})

const receiveData = reactive({})
const receiveDetails = ref([])

// ✅ 状态映射（关键：添加 status=1 的映射）
const statusNames = {
  1: '待发货',
  2: '已发货',
  3: '已验收',
  4: '已完成'
}
const statusTagTypes = {
  1: 'warning',
  2: 'primary',
  3: 'success',
  4: 'info'
}

const getStatusName = (status) => statusNames[status] || '未知'
const getStatusTagType = (status) => statusTagTypes[status] || 'default'

const loadOrders = async () => {
  try {
    const response = await axios.get('/purchase/orders', {
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
    ElMessage.error('加载订单列表失败')
  }
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
    const response = await axios.get(`/purchase/orders/${row.id}`)
    if (response.code === 200) {
      Object.assign(detailData, response.data)
      showDetailModal.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const receiveOrder = async (row) => {
  try {
    const response = await axios.get(`/purchase/orders/${row.id}`)
    if (response.code === 200) {
      Object.assign(receiveData, response.data)
      // 初始化验收明细
      receiveDetails.value = (response.data.details || []).map(d => ({
        ...d,
        orderQuantity: d.quantity,
        receivedQuantity: d.quantity,
        batchNo: '',
        expireDate: null,
        qualityStatus: 1
      }))
      showReceiveModal.value = true
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

const submitReceive = async () => {
  const hasEmpty = receiveDetails.value.some(d => !d.batchNo || !d.expireDate)
  if (hasEmpty) {
    ElMessage.warning('请填写完整的批号和效期信息')
    return
  }
  try {
    await axios.post(`/purchase/orders/${receiveData.id}/receive`, {
      details: receiveDetails.value
    })
    ElMessage.success('验收成功')
    showReceiveModal.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error('验收失败')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.purchase-order {
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