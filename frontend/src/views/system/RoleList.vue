<template>
  <div class="role-list">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索角色名称" class="search-input"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <el-table :data="roleList" border>
      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="roleCode" label="角色编码" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="link" @click="editRole(scope.row)">编辑</el-button>
          <el-button type="link" @click="viewPermissions(scope.row)">查看设置</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑角色对话框：标题固定为“编辑角色” -->
    <el-dialog title="编辑角色" v-model="showEditModal" width="500px">
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName"></el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="formData.description"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditModal = false">取消</el-button>
        <el-button type="primary" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 查看权限对话框：只读展示 -->
    <el-dialog title="查看权限" v-model="showPermissionModal" width="600px">
      <el-tree
          :data="menuTree"
          :props="{ label: 'menuName', children: 'children' }"
          default-expand-all
          ref="treeRef"
          node-key="id"
          :show-checkbox="false"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            {{ node.label }}
            <span v-if="hasPermission(data.id)" class="permission-mark">✓ 已授权</span>
          </span>
        </template>
      </el-tree>
      <template #footer>
        <el-button @click="showPermissionModal = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const roleList = ref([])
const showEditModal = ref(false)        // 改为编辑专用
const showPermissionModal = ref(false)
const formRef = ref(null)
const treeRef = ref(null)

const formData = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: ''
})

const currentRole = ref(null)
const currentPermissions = ref([])

// 菜单树数据（实际应从后端获取，此处沿用前端硬编码示例）
const menuTree = ref([
  { id: 1, menuName: '系统管理', children: [
      { id: 11, menuName: '用户管理' },
      { id: 12, menuName: '角色管理' },
      { id: 13, menuName: '系统日志' }
    ]},
  { id: 2, menuName: '药品管理', children: [
      { id: 21, menuName: '药品字典' },
      { id: 22, menuName: '药品分类' },
      { id: 23, menuName: '供应商管理' }
    ]},
  { id: 3, menuName: '采购管理', children: [
      { id: 31, menuName: '采购申请' },
      { id: 32, menuName: '采购订单' }
    ]},
  { id: 4, menuName: '库存管理', children: [
      { id: 41, menuName: '库存查询' },
      { id: 42, menuName: '库存预警' }
    ]},
  { id: 5, menuName: '药房管理', children: [
      { id: 51, menuName: '处方管理' },
      { id: 52, menuName: '药品调配' }
    ]},
  { id: 6, menuName: '统计报表', children: [
      { id: 61, menuName: '库存报表' },
      { id: 62, menuName: '采购报表' },
      { id: 63, menuName: '消耗报表' }
    ]},
  { id: 7, menuName: '特殊药品', children: [
      { id: 71, menuName: '特殊药品管理' }
    ]}
])

const defaultRolePermissions = {
  ADMIN: [1, 11, 12, 13, 2, 21, 22, 23, 3, 31, 32, 4, 41, 42, 5, 51, 52, 6, 61, 62, 63, 7, 71],
  PHARMACIST: [2, 21, 4, 41, 42, 5, 51, 52],
  DOCTOR: [5, 51, 52],
  PURCHASER: [2, 21, 23, 3, 31, 32],
  STOCK_MANAGER: [2, 21, 4, 41, 42],
  SPECIAL_PHARMACIST: [2, 21, 4, 41, 7, 71],
  PHARMACY_DIRECTOR: [3, 31, 32, 7, 71, 6, 61, 62, 63]
}

const hasSetPermissions = ref({})

const loadRoles = async () => {
  try {
    const response = await axios.get('/system/roles', {
      params: { keyword: keyword.value }
    })
    if (response.code === 200) {
      roleList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  }
}

const handleSearch = () => {
  loadRoles()
}

const editRole = (row) => {
  formData.id = row.id
  formData.roleName = row.roleName
  formData.roleCode = row.roleCode
  formData.description = row.description
  showEditModal.value = true
}

const viewPermissions = async (row) => {
  currentRole.value = row
  showPermissionModal.value = true
  await nextTick()
  await loadRolePermissions(row.id)
}

const hasPermission = (menuId) => {
  return currentPermissions.value.includes(menuId)
}

const loadRolePermissions = async (roleId) => {
  try {
    const response = await axios.get(`/system/roles/${roleId}/permissions`)
    if (response.code === 200) {
      const savedPermissions = response.data || []
      if (savedPermissions.length > 0) {
        currentPermissions.value = savedPermissions
        hasSetPermissions.value[roleId] = true
      } else if (!hasSetPermissions.value[roleId]) {
        const roleCode = currentRole.value.roleCode
        const defaultPermissions = defaultRolePermissions[roleCode] || []
        currentPermissions.value = defaultPermissions
      } else {
        currentPermissions.value = []
      }
    }
  } catch (error) {
    if (!hasSetPermissions.value[roleId]) {
      const roleCode = currentRole.value.roleCode
      const defaultPermissions = defaultRolePermissions[roleCode] || []
      currentPermissions.value = defaultPermissions
    } else {
      currentPermissions.value = []
    }
  }
}

const saveRole = async () => {
  try {
    await axios.put(`/system/roles/${formData.id}`, formData)
    ElMessage.success('保存成功')
    showEditModal.value = false
    loadRoles()
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
  formData.id = null
  formData.roleName = ''
  formData.roleCode = ''
  formData.description = ''
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.role-list {
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
.tree-node {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.permission-mark {
  color: #67c23a;
  font-size: 12px;
  margin-left: 8px;
}
</style>