
<template>
  <div class="supplier-list">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索供应商名称或编码" class="search-input"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="handleAdd">新增供应商</el-button>
    </div>
    
    <el-table :data="supplierList" border>
      <el-table-column prop="supplierCode" label="供应商编码" />
      <el-table-column prop="supplierName" label="供应商名称" />
      <el-table-column prop="contactName" label="联系人" />
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="qualificationNo" label="资质证书" />
      <el-table-column prop="qualificationExpireDate" label="资质有效期" />
      <el-table-column prop="cooperationStatus" label="合作状态">
        <template #default="scope">
          <el-tag :type="scope.row.cooperationStatus === 1 ? 'success' : 'warning'">
            {{ scope.row.cooperationStatus === 1 ? '合作中' : '暂停' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(scope.row, $event)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="link" @click="editSupplier(scope.row)">编辑</el-button>
          <el-button type="link" @click="deleteSupplier(scope.row)">删除</el-button>
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
    
    <el-dialog title="新增/编辑供应商" v-model="showAddModal" width="600px">
      <el-form :model="formData" ref="formRef" label-width="120px">
        <el-form-item label="供应商编码" prop="supplierCode">
          <el-input v-model="formData.supplierCode"></el-input>
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="formData.supplierName"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="formData.contactName"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input type="textarea" v-model="formData.address"></el-input>
        </el-form-item>
        <el-form-item label="资质证书号" prop="qualificationNo">
          <el-input v-model="formData.qualificationNo"></el-input>
        </el-form-item>
        <el-form-item label="资质有效期" prop="qualificationExpireDate">
          <el-date-picker v-model="formData.qualificationExpireDate" type="date"></el-date-picker>
        </el-form-item>
        <el-form-item label="银行账户" prop="bankAccount">
          <el-input v-model="formData.bankAccount"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" @change="onStatusChange">
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="合作状态" prop="cooperationStatus">
          <el-select v-model="formData.cooperationStatus" @change="onCooperationStatusChange">
            <el-option label="合作中" :value="1"></el-option>
            <el-option label="暂停合作" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="formData.remark"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveSupplier">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const supplierList = ref([])
const showAddModal = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  supplierCode: '',
  supplierName: '',
  contactName: '',
  phone: '',
  address: '',
  qualificationNo: '',
  qualificationExpireDate: null,
  bankAccount: '',
  status: 1,
  cooperationStatus: 1,
  remark: ''
})

// 状态改变时同步更新合作状态
const onStatusChange = () => {
  formData.cooperationStatus = formData.status
}

// 合作状态改变时同步更新状态
const onCooperationStatusChange = () => {
  formData.status = formData.cooperationStatus
}

// 新增供应商前先重置表单
const handleAdd = () => {
  resetForm()
  showAddModal.value = true
}

const loadSuppliers = async () => {
  try {
    const response = await axios.get('/drugs/suppliers', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value
      }
    })
    if (response.code === 200) {
      supplierList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载供应商列表失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadSuppliers()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadSuppliers()
}

const toggleStatus = async (row, newStatus) => {
  const oldStatus = row.status
  const oldCooperationStatus = row.cooperationStatus
  try {
    await axios.put(`/drugs/suppliers/${row.id}/status`, {}, { params: { status: newStatus } })
    row.status = newStatus
    // 状态和合作状态保持一致：开启→合作中，关闭→暂停
    row.cooperationStatus = newStatus
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = oldStatus
    row.cooperationStatus = oldCooperationStatus
  }
}

const editSupplier = (row) => {
  Object.assign(formData, row)
  showAddModal.value = true
}

const deleteSupplier = async (row) => {
  if (confirm(`确定要删除供应商 ${row.supplierName} 吗？`)) {
    try {
      await axios.delete(`/drugs/suppliers/${row.id}`)
      ElMessage.success('删除成功')
      loadSuppliers()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }
}

const saveSupplier = async () => {
  try {
    if (formData.id) {
      await axios.put(`/drugs/suppliers/${formData.id}`, formData)
    } else {
      await axios.post('/drugs/suppliers', formData)
    }
    ElMessage.success('保存成功')
    showAddModal.value = false
    loadSuppliers()
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
  formData.id = null
  formData.supplierCode = ''
  formData.supplierName = ''
  formData.contactName = ''
  formData.phone = ''
  formData.address = ''
  formData.qualificationNo = ''
  formData.qualificationExpireDate = null
  formData.bankAccount = ''
  formData.status = 1
  formData.cooperationStatus = 1
  formData.remark = ''
}

onMounted(() => {
  loadSuppliers()
})
</script>

<style scoped>
.supplier-list {
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
