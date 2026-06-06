<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h2>药品管理系统</h2>
      </div>
      <el-menu :default-active="activeMenu" class="menu" mode="vertical" :router="true">
        <!-- 首页始终显示，指向角色对应的Dashboard -->
        <el-menu-item :index="dashboardPath">
          <el-icon><component :is="'HomeFilled'" /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 医生专用菜单（处方管理 + 医嘱管理） -->
        <template v-if="userRole === 'DOCTOR'">
          <el-menu-item index="/doctor/dispensing">
            <el-icon><component :is="'Document'" /></el-icon>
            <span>处方管理</span>
          </el-menu-item>
          <el-menu-item index="/clinical/orders">
            <el-icon><component :is="'Hospital'" /></el-icon>
            <span>医嘱管理</span>
          </el-menu-item>
        </template>

        <!-- 其他角色菜单（管理员、药剂师、采购员等） -->
        <template v-else>
          <!-- 系统管理：仅 ADMIN -->
          <el-sub-menu index="system" v-if="userRole === 'ADMIN'">
            <template #title>
              <el-icon><component :is="'Setting'" /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/users">用户管理</el-menu-item>
            <el-menu-item index="/system/roles">角色权限</el-menu-item>
            <el-menu-item index="/system/logs">系统日志</el-menu-item>
          </el-sub-menu>

          <!-- 药品管理：所有非医生角色均可见，但子项根据角色显示 -->
          <el-sub-menu index="drug">
            <template #title>
              <el-icon><component :is="'Pill'" /></el-icon>
              <span>药品管理</span>
            </template>
            <el-menu-item index="/drugs/list">药品字典</el-menu-item>
            <!-- 药品分类：ADMIN、PHARMACY_DIRECTOR 可见 -->
            <el-menu-item index="/drugs/categories" v-if="['ADMIN', 'PHARMACY_DIRECTOR'].includes(userRole)">药品分类</el-menu-item>
            <!-- 供应商管理：ADMIN、PURCHASER、PHARMACY_DIRECTOR 可见 -->
            <el-menu-item index="/drugs/suppliers" v-if="['ADMIN', 'PURCHASER', 'PHARMACY_DIRECTOR'].includes(userRole)">供应商管理</el-menu-item>
          </el-sub-menu>

          <!-- 采购管理：仅 ADMIN、PURCHASER 或 PHARMACY_DIRECTOR -->
          <el-sub-menu index="purchase" v-if="['ADMIN', 'PURCHASER', 'PHARMACY_DIRECTOR'].includes(userRole)">
            <template #title>
              <el-icon><component :is="'ShoppingCart'" /></el-icon>
              <span>采购管理</span>
            </template>
            <el-menu-item index="/purchase/requests">采购申请</el-menu-item>
            <el-menu-item index="/purchase/orders">采购订单</el-menu-item>
          </el-sub-menu>

          <!-- 库存管理：ADMIN、PHARMACIST、STOCK_MANAGER、SPECIAL_PHARMACIST 可见 -->
          <el-sub-menu index="inventory" v-if="['ADMIN', 'PHARMACIST', 'STOCK_MANAGER', 'SPECIAL_PHARMACIST'].includes(userRole)">
            <template #title>
              <el-icon><component :is="'Package'" /></el-icon>
              <span>库存管理</span>
            </template>
            <el-menu-item index="/inventory/list">库存查询</el-menu-item>
            <!-- 库存预警：ADMIN、PHARMACIST、STOCK_MANAGER 可见，SPECIAL_PHARMACIST 不可见 -->
            <el-menu-item index="/inventory/warning" v-if="['ADMIN', 'PHARMACIST', 'STOCK_MANAGER'].includes(userRole)">库存预警</el-menu-item>
          </el-sub-menu>

          <!-- 药房管理： ADMIN、PHARMACIST 可见（医生已单独处理） -->
          <el-sub-menu index="pharmacy" v-if="['ADMIN', 'PHARMACIST'].includes(userRole)">
            <template #title>
              <el-icon><component :is="'Stethoscope'" /></el-icon>
              <span>药房管理</span>
            </template>
            <el-menu-item index="/pharmacy/prescriptions">处方管理</el-menu-item>
            <el-menu-item index="/pharmacy/dispensing">药品调配</el-menu-item>
          </el-sub-menu>

          <!-- 临床用药：仅 ADMIN（医生已单独处理） -->
          <el-sub-menu index="clinical" v-if="userRole === 'ADMIN'">
            <template #title>
              <el-icon><component :is="'Hospital'" /></el-icon>
              <span>临床用药</span>
            </template>
            <el-menu-item index="/clinical/orders">医嘱管理</el-menu-item>
          </el-sub-menu>

          <!-- 特殊药品：ADMIN、SPECIAL_PHARMACIST 或 PHARMACY_DIRECTOR -->
          <el-sub-menu index="special" v-if="['ADMIN', 'SPECIAL_PHARMACIST', 'PHARMACY_DIRECTOR'].includes(userRole)">
            <template #title>
              <el-icon><component :is="'Shield'" /></el-icon>
              <span>特殊药品</span>
            </template>
            <el-menu-item index="/special/drugs">特殊药品管理</el-menu-item>
          </el-sub-menu>

          <!-- 统计报表：ADMIN、PHARMACY_DIRECTOR 可见 -->
          <el-sub-menu index="reports" v-if="['ADMIN', 'PHARMACY_DIRECTOR'].includes(userRole)">
            <template #title>
              <el-icon><component :is="'PieChart'" /></el-icon>
              <span>统计报表</span>
            </template>
            <el-menu-item index="/reports/inventory">库存报表</el-menu-item>
            <el-menu-item index="/reports/purchase">采购报表</el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-right">
          <span>{{ userInfo.realName }} ({{ roleNameMap[userRole] }})</span>
          <el-button link @click="showChangePassword = true">修改密码</el-button>
          <el-button link @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="showChangePassword" width="400px">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'


const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const userRole = ref(userInfo.value.role || 'USER')

// 修改密码相关
const showChangePassword = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const roleNameMap = {
  ADMIN: '系统管理员',
  PHARMACIST: '药剂师',
  PURCHASER: '采购员',
  DOCTOR: '医生',
  SPECIAL_PHARMACIST: '特殊药品管理员',
  STOCK_MANAGER: '库存管理员',
  PHARMACY_DIRECTOR: '药剂科主任',
  USER: '普通用户'
}

const roleDashboardMap = {
  ADMIN: '/dashboard/admin',
  PHARMACIST: '/dashboard/pharmacist',
  PURCHASER: '/dashboard/purchaser',
  DOCTOR: '/dashboard/doctor',
  SPECIAL_PHARMACIST: '/dashboard/special-pharmacist',
  STOCK_MANAGER: '/dashboard/stock-manager',
  PHARMACY_DIRECTOR: '/dashboard/pharmacy-director',
  USER: '/'
}

const activeMenu = computed(() => router.currentRoute.value.path)

const dashboardPath = computed(() => roleDashboardMap[userRole.value] || '/')

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('退出成功')
  router.push('/login')
}

const handleChangePassword = async () => {
  if (!passwordForm.value.oldPassword) {
    ElMessage.error('请输入原密码')
    return
  }
  if (!passwordForm.value.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    const response = await axios.post('/system/users/change-password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    if (response.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      showChangePassword.value = false
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      handleLogout()
    } else {
      ElMessage.error(response.message || '修改失败')
    }
  } catch (error) {
    console.error('Change password error:', error)
    console.error('Error response:', error.response)
    if (error.response) {
      // 请求已发出但服务器返回错误状态码
      ElMessage.error(`修改失败: ${error.response.status} - ${error.response.data?.message || '未知错误'}`)
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('修改失败: 无法连接到服务器，请检查网络或联系管理员')
    } else {
      // 设置请求时发生错误
      ElMessage.error(`修改失败: ${error.message}`)
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.sidebar {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: white;
}
.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.logo h2 {
  margin: 0;
  font-size: 16px;
}
.menu {
  border-right: none;
}
.header {
  background: white;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 20px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.main-content {
  padding: 20px;
  background: #f5f7fa;
}
</style>