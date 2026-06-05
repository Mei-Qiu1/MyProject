<template>
  <div class="drug-list">
    <!-- 搜索栏 - 紧凑布局 -->
    <div class="search-bar">
      <el-input
          v-model="keyword"
          placeholder="搜索药品名称或编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
      />
      <el-select
          v-model="categoryId"
          placeholder="药理分类"
          clearable
          style="width: 140px"
      >
        <el-option label="全部" :value="0"></el-option>
        <el-option
            v-for="cat in pharmacologicalCategories"
            :key="cat.id"
            :label="cat.categoryName"
            :value="cat.id"
        ></el-option>
      </el-select>
      <el-select
          v-model="manageCategoryId"
          placeholder="管理分类"
          clearable
          style="width: 140px"
      >
        <el-option label="全部" :value="0"></el-option>
        <el-option
            v-for="cat in managementCategories"
            :key="cat.id"
            :label="cat.categoryName"
            :value="cat.id"
        ></el-option>
      </el-select>
      <el-button type="primary" size="small" @click="handleSearch">搜索</el-button>
      <el-button type="success" size="small" @click="openAddDialog">新增</el-button>
      <el-button type="warning" size="small" @click="exportDrugs">导出</el-button>
      <el-button type="info" size="small" @click="showImportModal = true">导入</el-button>
    </div>

    <!-- 药品表格 -->
    <el-table :data="drugList" border>
      <el-table-column prop="drugCode" label="药品编码" width="120" />
      <el-table-column prop="drugName" label="药品名称" width="150" />
      <el-table-column prop="spec" label="规格" width="120" />
      <el-table-column prop="dosageForm" label="剂型" width="80" />
      <el-table-column prop="manufacturer" label="生产厂家" width="180" />
      <el-table-column prop="purchasePrice" label="采购价" width="80" />
      <el-table-column prop="retailPrice" label="零售价" width="80" />
      <el-table-column prop="isSpecial" label="特殊药品" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.isSpecial === 1 ? 'danger' : 'success'">
            {{ scope.row.isSpecial === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="70">
        <template #default="scope">
          <el-switch
              :value="scope.row.status === 1"
              @change="toggleStatus(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button type="text" size="small" @click="editDrug(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="deleteDrug(scope.row)">删除</el-button>
          <el-button type="text" size="small" @click="viewDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @current-change="handlePageChange"
        layout="prev, pager, next, jumper"
        class="pagination"
    />

    <!-- 新增/编辑药品对话框 -->
    <el-dialog :title="dialogTitle" v-model="showAddModal" width="600px" @close="resetForm">
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="药品编码" prop="drugCode" required>
          <el-input v-model="formData.drugCode" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="药品名称" prop="drugName" required>
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
          <el-select v-model="formData.categoryId" placeholder="请选择" clearable>
            <el-option
                v-for="cat in pharmacologicalCategories"
                :key="cat.id"
                :label="cat.categoryName"
                :value="cat.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="管理分类" prop="manageCategoryId">
          <el-select v-model="formData.manageCategoryId" placeholder="请选择" clearable>
            <el-option
                v-for="cat in managementCategories"
                :key="cat.id"
                :label="cat.categoryName"
                :value="cat.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit"></el-input>
        </el-form-item>
        <el-form-item label="特殊药品">
          <el-switch v-model="formData.isSpecial" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="采购价" prop="purchasePrice">
          <el-input v-model="formData.purchasePrice" type="number" step="0.01"></el-input>
        </el-form-item>
        <el-form-item label="零售价" prop="retailPrice">
          <el-input v-model="formData.retailPrice" type="number" step="0.01"></el-input>
        </el-form-item>
        <el-form-item label="批发价" prop="wholesalePrice">
          <el-input v-model="formData.wholesalePrice" type="number" step="0.01"></el-input>
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

    <!-- 药品详情对话框 -->
    <el-dialog title="药品详情" v-model="showDetailModal" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="药品编码">{{ detailData.drugCode }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ detailData.drugName }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ detailData.spec }}</el-descriptions-item>
        <el-descriptions-item label="剂型">{{ detailData.dosageForm }}</el-descriptions-item>
        <el-descriptions-item label="生产厂家">{{ detailData.manufacturer }}</el-descriptions-item>
        <el-descriptions-item label="批准文号">{{ detailData.approvalNumber }}</el-descriptions-item>
        <el-descriptions-item label="采购价">{{ detailData.purchasePrice }}</el-descriptions-item>
        <el-descriptions-item label="零售价">{{ detailData.retailPrice }}</el-descriptions-item>
        <el-descriptions-item label="批发价">{{ detailData.wholesalePrice }}</el-descriptions-item>
        <el-descriptions-item label="特殊药品">{{ detailData.isSpecial === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailData.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入药品" v-model="showImportModal" width="500px">
      <el-upload
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          accept=".xlsx, .xls"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将Excel文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">
            仅支持 .xlsx 或 .xls 格式，表格列顺序为：<strong>药品编码、药品名称、规格、剂型、生产厂家、批准文号、药理分类名称、管理分类名称、单位、是否特殊、采购价、零售价、批发价、状态、备注</strong>。<br/>
            <span style="color: #f56c6c;">所有字段均为必填项！</span> 分类名称必须与系统中现有分类完全一致；是否特殊只能填“是”或“否”；状态只能填“启用”或“禁用”；价格必须为非负数字。<br/>
            <a href="#" @click.prevent="downloadTemplate">点击下载导入模板</a>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="showImportModal = false">取消</el-button>
        <el-button type="primary" @click="uploadFile" :loading="uploading">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import axios from '../../utils/axios'

// 搜索相关
const keyword = ref('')
const categoryId = ref(0)
const manageCategoryId = ref(0)
const drugList = ref([])
const pharmacologicalCategories = ref([])
const managementCategories = ref([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 新增/编辑相关
const showAddModal = ref(false)
const isEdit = ref(false)
const dialogTitle = ref('新增药品')
const formRef = ref(null)
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

// 详情相关
const showDetailModal = ref(false)
const detailData = reactive({})

// 导入相关
const showImportModal = ref(false)
const uploading = ref(false)
let selectedFile = null

// 加载药品列表
const loadDrugs = async () => {
  try {
    const response = await axios.get('/drugs', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        categoryId: categoryId.value === 0 ? undefined : categoryId.value,
        manageCategoryId: manageCategoryId.value === 0 ? undefined : manageCategoryId.value
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

// 加载分类
const loadCategories = async () => {
  try {
    const response = await axios.get('/drugs/categories')
    if (response.code === 200) {
      pharmacologicalCategories.value = response.data.filter(c => c.type === 1)
      managementCategories.value = response.data.filter(c => c.type === 2)
    }
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadDrugs()
}

// 分页切换
const handlePageChange = (page) => {
  pagination.current = page
  loadDrugs()
}

// 切换状态
const toggleStatus = async (row) => {
  try {
    await axios.put(`/drugs/${row.id}/status`, null, { params: { status: row.status === 1 ? 0 : 1 } })
    row.status = row.status === 1 ? 0 : 1
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

// 打开新增对话框
const openAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增药品'
  resetForm()
  showAddModal.value = true
}

// 编辑药品
const editDrug = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑药品'
  Object.assign(formData, row)
  showAddModal.value = true
}

// 保存药品
const saveDrug = async () => {
  try {
    if (isEdit.value) {
      await axios.put(`/drugs/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      const existing = drugList.value.find(d => d.drugCode === formData.drugCode)
      if (existing) {
        ElMessage.error('药品编码已存在')
        return
      }
      await axios.post('/drugs', formData)
      ElMessage.success('创建成功')
    }
    showAddModal.value = false
    loadDrugs()
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除药品
const deleteDrug = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除药品 ${row.drugName} 吗？`, '提示', { type: 'warning' })
    await axios.delete(`/drugs/${row.id}`)
    ElMessage.success('删除成功')
    loadDrugs()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 查看详情
const viewDetail = async (row) => {
  try {
    const response = await axios.get(`/drugs/${row.id}`)
    if (response.code === 200) {
      Object.assign(detailData, response.data)
      showDetailModal.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 导出Excel
const exportDrugs = async () => {
  try {
    const response = await axios.get('/drugs/export', {
      params: {
        keyword: keyword.value,
        categoryId: categoryId.value === 0 ? undefined : categoryId.value,
        manageCategoryId: manageCategoryId.value === 0 ? undefined : manageCategoryId.value
      },
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `药品列表.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 导入相关
const handleFileChange = (file) => {
  selectedFile = file.raw
}

const uploadFile = async () => {
  if (!selectedFile) {
    ElMessage.warning('请选择文件')
    return
  }
  const fd = new FormData()
  fd.append('file', selectedFile)
  uploading.value = true
  try {
    const response = await axios.post('/drugs/import', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (response.code === 200) {
      ElMessage.success(response.message || '导入成功')
      showImportModal.value = false
      pagination.current = 1
      loadDrugs()
      selectedFile = null
    } else {
      ElMessage.error(response.message || '导入失败')
    }
  } catch (error) {
    ElMessage.error('导入失败，请检查文件格式')
  } finally {
    uploading.value = false
  }
}

const downloadTemplate = () => {
  import('xlsx').then(XLSX => {
    const headers = ['药品编码', '药品名称', '规格', '剂型', '生产厂家', '批准文号',
      '药理分类名称', '管理分类名称', '单位', '是否特殊', '采购价', '零售价', '批发价', '状态', '备注']
    const sampleData = ['D0007', '布洛芬缓释胶囊', '0.3g*20粒', '胶囊剂', '某某制药', '国药准字H12345678',
      '非甾体抗炎药', '普通药品', '盒', '否', '5.00', '12.00', '8.00', '启用', '解热镇痛']
    const wsData = [headers, sampleData]
    const ws = XLSX.utils.aoa_to_sheet(wsData)
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, '药品导入模板')
    XLSX.writeFile(wb, '药品导入模板.xlsx')
  }).catch(() => {
    ElMessage.error('下载模板失败，请检查网络')
  })
}

const resetForm = () => {
  formData.id = null
  formData.drugCode = ''
  formData.drugName = ''
  formData.spec = ''
  formData.dosageForm = ''
  formData.manufacturer = ''
  formData.approvalNumber = ''
  formData.categoryId = null
  formData.manageCategoryId = null
  formData.unit = ''
  formData.isSpecial = 0
  formData.purchasePrice = null
  formData.retailPrice = null
  formData.wholesalePrice = null
  formData.remark = ''
  if (formRef.value) formRef.value.resetFields()
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
  flex-wrap: wrap;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.el-button--small {
  padding: 5px 12px;
}
</style>