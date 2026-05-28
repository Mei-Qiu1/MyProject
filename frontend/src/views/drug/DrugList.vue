
<template>
  <div class="drug-list">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索药品名称或编码" class="search-input"></el-input>
      <el-select v-model="categoryId" placeholder="选择分类">
        <el-option label="全部" :value="0"></el-option>
        <el-option v-for="cat in categories" :key="cat.id" :label="cat.categoryName" :value="cat.id"></el-option>
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="showAddModal = true">新增药品</el-button>
      <el-button type="warning" @click="exportDrugs">导出Excel</el-button>
    </div>
    
    <el-table :data="drugList" border>
      <el-table-column prop="drugCode" label="药品编码" />
      <el-table-column prop="drugName" label="药品名称" />
      <el-table-column prop="spec" label="规格" />
      <el-table-column prop="dosageForm" label="剂型" />
      <el-table-column prop="manufacturer" label="生产厂家" />
      <el-table-column prop="purchasePrice" label="采购价" />
      <el-table-column prop="retailPrice" label="零售价" />
      <el-table-column prop="isSpecial" label="特殊药品">
        <template #default="scope">
          <el-tag :type="scope.row.isSpecial === 1 ? 'danger' : 'success'">
            {{ scope.row.isSpecial === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-switch :value="scope.row.status === 1" @change="toggleStatus(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="text" @click="editDrug(scope.row)">编辑</el-button>
          <el-button type="text" @click="deleteDrug(scope.row)">删除</el-button>
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
    
    <el-dialog title="新增/编辑药品" v-model="showAddModal" width="600px">
      <el-form :model="formData" ref="formRef" label-width="120px">
        <el-form-item label="药品编码" prop="drugCode">
          <el-input v-model="formData.drugCode"></el-input>
        </el-form-item>
        <el-form-item label="药品名称" prop="drugName">
          <el-input v-model="formData.drugName"></el-input>
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="formData.spec"></el-input>
        </el-form-item>
        <el-form-item label="剂型" prop="dosageForm">
          <el-input v-model="formData.dosageForm"></el-input>
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="formData.manufacturer"></el-input>
        </el-form-item>
        <el-form-item label="批准文号" prop="approvalNumber">
          <el-input v-model="formData.approvalNumber"></el-input>
        </el-form-item>
        <el-form-item label="药理分类" prop="categoryId">
          <el-select v-model="formData.categoryId">
            <el-option v-for="cat in pharmacologicalCategories" :key="cat.id" :label="cat.categoryName" :value="cat.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="管理分类" prop="manageCategoryId">
          <el-select v-model="formData.manageCategoryId">
            <el-option v-for="cat in managementCategories" :key="cat.id" :label="cat.categoryName" :value="cat.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit"></el-input>
        </el-form-item>
        <el-form-item label="采购价" prop="purchasePrice">
          <el-input v-model="formData.purchasePrice" type="number"></el-input>
        </el-form-item>
        <el-form-item label="零售价" prop="retailPrice">
          <el-input v-model="formData.retailPrice" type="number"></el-input>
        </el-form-item>
        <el-form-item label="批发价" prop="wholesalePrice">
          <el-input v-model="formData.wholesalePrice" type="number"></el-input>
        </el-form-item>
        <el-form-item label="特殊药品">
          <el-switch v-model="formData.isSpecial"></el-switch>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="formData.remark"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveDrug">保存</el-button>
      </template>
    </el-dialog>
    
    <el-dialog title="药品详情" v-model="showDetailModal">
      <el-descriptions :column="2" :data="detailData">
        <el-descriptions-item label="药品编码">{{ detailData.drugCode }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ detailData.drugName }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ detailData.spec }}</el-descriptions-item>
        <el-descriptions-item label="剂型">{{ detailData.dosageForm }}</el-descriptions-item>
        <el-descriptions-item label="生产厂家">{{ detailData.manufacturer }}</el-descriptions-item>
        <el-descriptions-item label="批准文号">{{ detailData.approvalNumber }}</el-descriptions-item>
        <el-descriptions-item label="采购价">{{ detailData.purchasePrice }}</el-descriptions-item>
        <el-descriptions-item label="零售价">{{ detailData.retailPrice }}</el-descriptions-item>
        <el-descriptions-item label="特殊药品">{{ detailData.isSpecial === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const categoryId = ref(0)
const drugList = ref([])
const categories = ref([])
const pharmacologicalCategories = ref([])
const managementCategories = ref([])
const showAddModal = ref(false)
const showDetailModal = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const formData = reactive({
  id: null,
  drugCode: '',
  drugName: '',
  spec: '',
  dosageForm: '',
  manufacturer: '',
  approvalNumber: '',
  categoryId: null,
  manageCategoryId: null,
  unit: '',
  isSpecial: 0,
  purchasePrice: null,
  retailPrice: null,
  wholesalePrice: null,
  remark: ''
})

const detailData = reactive({})

const loadDrugs = async () => {
  try {
    const response = await axios.get('/drugs', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        categoryId: categoryId.value || undefined
      }
    })
    if (response.code === 200) {
      drugList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载药品列表失败')
  }
}

const loadCategories = async () => {
  try {
    const response = await axios.get('/drugs/categories')
    if (response.code === 200) {
      categories.value = response.data
      pharmacologicalCategories.value = response.data.filter(c => c.type === 1)
      managementCategories.value = response.data.filter(c => c.type === 2)
    }
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadDrugs()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadDrugs()
}

const toggleStatus = async (row) => {
  try {
    await axios.put(`/drugs/${row.id}/status`, {}, { params: { status: row.status === 1 ? 0 : 1 } })
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

const editDrug = (row) => {
  Object.assign(formData, row)
  showAddModal.value = true
}

const deleteDrug = async (row) => {
  if (confirm(`确定要删除药品 ${row.drugName} 吗？`)) {
    try {
      await axios.delete(`/drugs/${row.id}`)
      ElMessage.success('删除成功')
      loadDrugs()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }
}

const viewDetail = (row) => {
  Object.assign(detailData, row)
  showDetailModal.value = true
}

const saveDrug = async () => {
  try {
    if (formData.id) {
      await axios.put(`/drugs/${formData.id}`, formData)
    } else {
      await axios.post('/drugs', formData)
    }
    ElMessage.success('保存成功')
    showAddModal.value = false
    loadDrugs()
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = null
  })
  formData.isSpecial = 0
}

const exportDrugs = async () => {
  try {
    const response = await axios.get('/drugs/export', {
      params: { keyword: keyword.value, categoryId: categoryId.value || undefined },
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '药品列表.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadDrugs()
  loadCategories()
})
</script>

<style scoped>
.drug-list {
  background: white;
  border-radius: 10px;
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  width: 300px;
}
</style>
