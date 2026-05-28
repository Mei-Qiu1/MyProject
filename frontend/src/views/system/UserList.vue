<template>
  <div class="user-list">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索用户名或姓名" class="search-input"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openAddDialog">新增用户</el-button>
      <el-button type="warning" @click="handleBatchImport">批量导入</el-button>
    </div>

    <el-table :data="userList" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">{{ getRoleName(scope.row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="toggleStatus(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="230">
        <template #default="scope">
          <!-- 使用 type="text" 保留蓝色文字效果，并添加样式避免弃用警告 -->
          <el-button type="text" class="action-btn" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="text" class="action-btn" @click="openRoleDialog(scope.row)">权限管理</el-button>
          <el-button type="text" class="action-btn" @click="deleteUser(scope.row)">删除</el-button>
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

    <!-- 新增/编辑用户对话框（v-if 确保每次打开全新实例） -->
    <el-dialog
      :title="dialogTitle"
      v-model="showAddModal"
      @close="onDialogClose"
      width="600px"
      v-if="showAddModal"
      destroy-on-close
    >
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit" prop="password">
          <el-input type="password" v-model="formData.password"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role">
            <el-option label="系统管理员" value="ADMIN"></el-option>
            <el-option label="药剂师" value="PHARMACIST"></el-option>
            <el-option label="采购员" value="PURCHASER"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="库存管理员" value="STOCK_MANAGER"></el-option>
            <el-option label="特殊药品管理员" value="SPECIAL_PHARMACIST"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>

    <!-- 权限管理（修改角色）对话框 -->
    <el-dialog title="权限管理" v-model="showRoleModal" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="用户">
          <span>{{ currentUser?.realName }} ({{ currentUser?.username }})</span>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="roleForm.role">
            <el-option label="系统管理员" value="ADMIN"></el-option>
            <el-option label="药剂师" value="PHARMACIST"></el-option>
            <el-option label="采购员" value="PURCHASER"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="库存管理员" value="STOCK_MANAGER"></el-option>
            <el-option label="特殊药品管理员" value="SPECIAL_PHARMACIST"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRoleModal = false">取消</el-button>
        <el-button type="primary" @click="updateRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入用户" v-model="showImportModal" width="500px">
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
            仅支持 .xlsx 或 .xls 格式，表格需包含列：用户名、姓名、电话、邮箱、角色（可选）
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
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import axios from '../../utils/axios'

const keyword = ref('')
const userList = ref([])
const showAddModal = ref(false)
const showRoleModal = ref(false)
const showImportModal = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const dialogTitle = ref('新增用户')
const currentUser = ref(null)
const uploading = ref(false)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 使用 reactive 但每次打开对话框时会重新初始化（通过 resetForm）
const formData = reactive({
  id: null,
  username: '',
  realName: '',
  password: '',
  phone: '',
  email: '',
  role: 'USER'
})

const roleForm = reactive({
  userId: null,
  role: ''
})

const roleNames = {
  ADMIN: '系统管理员',
  PHARMACIST: '药剂师',
  PURCHASER: '采购员',
  DOCTOR: '医生',
  SPECIAL_PHARMACIST: '特殊药品管理员',
  STOCK_MANAGER: '库存管理员',
  USER: '普通用户'
}

const roleTagTypes = {
  ADMIN: 'danger',
  PHARMACIST: 'primary',
  PURCHASER: 'success',
  DOCTOR: 'warning',
  SPECIAL_PHARMACIST: 'danger',
  STOCK_MANAGER: 'info',
  USER: 'default'
}

const getRoleName = (role) => roleNames[role] || role
const getRoleTagType = (role) => roleTagTypes[role] || 'default'

// 加载用户列表
const loadUsers = async () => {
  try {
    const response = await axios.get('/system/users', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value
      }
    })
    if (response.code === 200) {
      userList.value = response.data.records.map(user => ({
        ...user,
        status: Number(user.status)
      }))
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

// 完全重置表单（重新初始化 formData 对象）
const resetForm = () => {
  // 直接修改所有属性
  formData.id = null
  formData.username = ''
  formData.realName = ''
  formData.password = ''
  formData.phone = ''
  formData.email = ''
  formData.role = 'USER'
  isEdit.value = false
  dialogTitle.value = '新增用户'
  // 清除验证状态
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 对话框关闭时的回调（额外确保重置）
const onDialogClose = () => {
  resetForm()
}

// 打开新增对话框
const openAddDialog = () => {
  resetForm()
  showAddModal.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  resetForm()
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  formData.id = row.id
  formData.username = row.username
  formData.realName = row.realName
  formData.phone = row.phone
  formData.email = row.email
  formData.role = row.role
  // 编辑时不显示密码
  formData.password = ''
  showAddModal.value = true
}

// 保存用户（新增或编辑）
const saveUser = async () => {
  try {
    if (isEdit.value) {
      await axios.put(`/system/users/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      if (!formData.password) {
        ElMessage.warning('请输入密码')
        return
      }
      await axios.post('/system/users', formData)
      ElMessage.success('创建成功')
    }
    showAddModal.value = false
    await loadUsers()
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 切换状态开关
const toggleStatus = async (row) => {
  const originalStatus = row.status
  try {
    await axios.put(`/system/users/${row.id}/status`, {}, { params: { status: row.status } })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = originalStatus
    ElMessage.error('状态更新失败')
  }
}

// 删除用户
const deleteUser = async (row) => {
  if (confirm(`确定要删除用户 ${row.username} 吗？`)) {
    try {
      await axios.delete(`/system/users/${row.id}`)
      ElMessage.success('删除成功')
      await loadUsers()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }
}

// 打开权限管理对话框
const openRoleDialog = (row) => {
  currentUser.value = row
  roleForm.userId = row.id
  roleForm.role = row.role
  showRoleModal.value = true
}

// 更新角色权限
const updateRole = async () => {
  try {
    await axios.put(`/system/users/${roleForm.userId}`, { role: roleForm.role })
    ElMessage.success('权限更新成功')
    showRoleModal.value = false
    await loadUsers()
  } catch (error) {
    ElMessage.error('权限更新失败')
  }
}

// 批量导入
let selectedFile = null
const handleFileChange = (file) => {
  selectedFile = file.raw
}
const handleBatchImport = () => {
  showImportModal.value = true
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
    const response = await axios.post('/system/users/import', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (response.code === 200) {
      ElMessage.success('导入成功')
      showImportModal.value = false
      await loadUsers()
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

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadUsers()
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-list {
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
/* 恢复操作按钮的蓝色高亮（覆盖可能的 link 样式） */
.action-btn {
  color: #409eff !important;
  margin-right: 8px;
}
.action-btn:hover {
  color: #66b1ff !important;
}
</style>