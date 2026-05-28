
<template>
  <div class="inventory-warning">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="低库存预警" name="lowStock">
        <div class="warning-section">
          <div class="filter-bar">
            <el-input v-model="lowStockKeyword" placeholder="搜索药品" class="search-input"></el-input>
            <el-select v-model="threshold" placeholder="预警阈值">
              <el-option label="10" :value="10"></el-option>
              <el-option label="20" :value="20"></el-option>
              <el-option label="50" :value="50"></el-option>
              <el-option label="100" :value="100"></el-option>
            </el-select>
            <el-button type="primary" @click="loadLowStock">搜索</el-button>
            <el-button type="success" @click="createPurchasePlan">生成采购计划</el-button>
          </div>
          <el-table :data="lowStockList" border>
            <el-table-column prop="drugCode" label="药品编码" />
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="unit" label="单位" />
            <el-table-column prop="currentStock" label="当前库存" />
            <el-table-column prop="threshold" label="预警阈值" />
            <el-table-column prop="suggestOrder" label="建议采购量" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" @click="createRequest(scope.row)">创建采购申请</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="效期预警" name="expiring">
        <div class="warning-section">
          <div class="filter-bar">
            <el-input v-model="expiringKeyword" placeholder="搜索药品" class="search-input"></el-input>
            <el-select v-model="expireDays" placeholder="预警天数">
              <el-option label="30天" :value="30"></el-option>
              <el-option label="90天" :value="90"></el-option>
              <el-option label="180天" :value="180"></el-option>
            </el-select>
            <el-button type="primary" @click="loadExpiring">搜索</el-button>
          </div>
          <el-table :data="expiringList" border>
            <el-table-column prop="drugCode" label="药品编码" />
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="spec" label="规格" />
            <el-table-column prop="batchNo" label="批号" />
            <el-table-column prop="quantity" label="库存数量" />
            <el-table-column prop="expireDate" label="有效期">
              <template #default="scope">
                <el-tag :type="getExpireTagType(scope.row.expireDate)">
                  {{ scope.row.expireDate }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="daysLeft" label="剩余天数">
              <template #default="scope">
                <el-tag :type="scope.row.daysLeft < 30 ? 'danger' : 'warning'">
                  {{ scope.row.daysLeft }}天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="text" @click="transfer(scope.row)">调拨</el-button>
                <el-button type="text" @click="writeOff(scope.row)">报损</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const activeTab = ref('lowStock')
const lowStockKeyword = ref('')
const expiringKeyword = ref('')
const threshold = ref(20)
const expireDays = ref(90)
const lowStockList = ref([])
const expiringList = ref([])

const getExpireTagType = (expireDate) => {
  const expire = new Date(expireDate)
  const now = new Date()
  const diffDays = Math.floor((expire - now) / (1000 * 60 * 60 * 24))
  
  if (diffDays < 30) return 'danger'
  if (diffDays < 90) return 'warning'
  if (diffDays < 180) return 'info'
  return ''
}

const loadLowStock = async () => {
  try {
    const response = await axios.get('/inventory/low-stock', {
      params: { threshold: threshold.value, keyword: lowStockKeyword.value }
    })
    if (response.code === 200) {
      lowStockList.value = response.data.map(item => ({
        ...item,
        suggestOrder: Math.max(threshold.value * 2 - item.currentStock, threshold.value)
      }))
    }
  } catch (error) {
    ElMessage.error('加载低库存数据失败')
  }
}

const loadExpiring = async () => {
  try {
    const response = await axios.get('/inventory/expiring', {
      params: { days: expireDays.value, keyword: expiringKeyword.value }
    })
    if (response.code === 200) {
      expiringList.value = response.data.map(item => {
        const expire = new Date(item.expireDate)
        const now = new Date()
        const daysLeft = Math.floor((expire - now) / (1000 * 60 * 60 * 24))
        return { ...item, daysLeft }
      })
    }
  } catch (error) {
    ElMessage.error('加载效期预警数据失败')
  }
}

const createPurchasePlan = () => {
  const items = lowStockList.value.map(item => ({
    drugId: item.id,
    drugName: item.drugName,
    quantity: item.suggestOrder
  }))
  axios.post('/purchase/plans', {
    planName: '自动生成采购计划',
    planType: 1,
    items
  })
  .then(() => {
    ElMessage.success('采购计划生成成功')
  })
  .catch(() => ElMessage.error('生成失败'))
}

const createRequest = (row) => {
  axios.post('/purchase/requests', {
    planId: null,
    supplierId: null,
    details: [{ drugId: row.id, quantity: row.suggestOrder }]
  })
  .then(() => {
    ElMessage.success('采购申请创建成功')
    loadLowStock()
  })
  .catch(() => ElMessage.error('创建失败'))
}

const transfer = (row) => {
  ElMessage.info('调拨功能开发中')
}

const writeOff = (row) => {
  if (confirm(`确定要报损药品 ${row.drugName} 吗？`)) {
    axios.post(`/inventory/${row.id}/decrease`, {}, { params: { quantity: row.quantity } })
      .then(() => {
        ElMessage.success('报损成功')
        loadExpiring()
      })
      .catch(() => ElMessage.error('报损失败'))
  }
}

onMounted(() => {
  loadLowStock()
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
}

.search-input {
  width: 300px;
}
</style>
