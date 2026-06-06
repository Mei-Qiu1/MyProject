import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: '/', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
      { path: '/dashboard/admin', name: 'AdminDashboard', component: () => import('../views/dashboard/AdminDashboard.vue') },
      { path: '/dashboard/doctor', name: 'DoctorDashboard', component: () => import('../views/dashboard/DoctorDashboard.vue') },
      { path: '/dashboard/pharmacist', name: 'PharmacistDashboard', component: () => import('../views/dashboard/PharmacistDashboard.vue') },
      { path: '/dashboard/purchaser', name: 'PurchaserDashboard', component: () => import('../views/dashboard/PurchaserDashboard.vue') },
      { path: '/dashboard/stock-manager', name: 'StockManagerDashboard', component: () => import('../views/dashboard/StockManagerDashboard.vue') },
      { path: '/dashboard/special-pharmacist', name: 'SpecialPharmacistDashboard', component: () => import('../views/dashboard/SpecialPharmacistDashboard.vue') },
      { path: '/dashboard/pharmacy-director', name: 'PharmacyDirectorDashboard', component: () => import('../views/dashboard/PharmacyDirectorDashboard.vue') },
      { path: '/system/users', name: 'UserList', component: () => import('../views/system/UserList.vue') },
      { path: '/system/roles', name: 'RoleList', component: () => import('../views/system/RoleList.vue') },
      { path: '/system/logs', name: 'LogList', component: () => import('../views/system/LogList.vue') },
      { path: '/drugs/list', name: 'DrugList', component: () => import('../views/drug/DrugList.vue') },
      { path: '/drugs/categories', name: 'DrugCategory', component: () => import('../views/drug/DrugCategory.vue') },
      { path: '/drugs/suppliers', name: 'SupplierList', component: () => import('../views/drug/SupplierList.vue') },
      { path: '/purchase/requests', name: 'PurchaseRequest', component: () => import('../views/purchase/PurchaseRequest.vue') },
      { path: '/purchase/orders', name: 'PurchaseOrder', component: () => import('../views/purchase/PurchaseOrder.vue') },
      { path: '/inventory/list', name: 'InventoryList', component: () => import('../views/inventory/InventoryList.vue') },
      { path: '/inventory/warning', name: 'InventoryWarning', component: () => import('../views/inventory/InventoryWarning.vue') },
      { path: '/pharmacy/prescriptions', name: 'PrescriptionList', component: () => import('../views/pharmacy/PrescriptionList.vue') },
      { path: '/pharmacy/dispensing', name: 'Dispensing', component: () => import('../views/pharmacy/Dispensing.vue') },
      { path: '/pharmacy/delivery', name: 'DeliveryList', component: () => import('../views/pharmacy/DeliveryList.vue') },
      { path: '/clinical/orders', name: 'MedicalOrder', component: () => import('../views/clinical/MedicalOrder.vue') },
      { path: '/special/drugs', name: 'SpecialDrug', component: () => import('../views/special/SpecialDrug.vue') },
      { path: '/reports/inventory', name: 'InventoryReport', component: () => import('../views/reports/InventoryReport.vue') },
      { path: '/reports/purchase', name: 'PurchaseReport', component: () => import('../views/reports/PurchaseReport.vue') },
      { path: '/reports/prescription', name: 'PrescriptionReport', component: () => import('../views/reports/PrescriptionReport.vue') },
      { path: '/doctor/dispensing', name: 'DoctorPrescription', component: () => import('../views/doctor/DispensingManage.vue') },
    ]
  }
]

// 角色允许访问的路径白名单（可进一步细化）
const roleRoutes = {
  ADMIN: [
    '/dashboard/admin',
    '/system/users', '/system/roles', '/system/logs',
    '/drugs/list', '/drugs/categories', '/drugs/suppliers',
    '/purchase/requests', '/purchase/orders',
    '/inventory/list', '/inventory/warning',
    '/pharmacy/prescriptions', '/pharmacy/dispensing',
    '/clinical/orders', '/special/drugs',
    '/reports/inventory', '/reports/purchase', '/reports/prescription'
  ],
  PHARMACIST: [
    '/dashboard/pharmacist',
    '/pharmacy/prescriptions', '/pharmacy/dispensing', '/pharmacy/delivery',
    '/drugs/list',
    '/inventory/list', '/inventory/warning'
  ],
  DOCTOR: [
    '/dashboard/doctor',
    '/pharmacy/prescriptions',
    '/drugs/list',
    '/doctor/dispensing',
    '/clinical/orders'
  ],
  SPECIAL_PHARMACIST: [
    '/dashboard/special-pharmacist',
    '/special/drugs', 
    '/drugs/list',
    '/inventory/list'
  ],
  PURCHASER: [
    '/dashboard/purchaser',
    '/purchase/requests', '/purchase/orders',
    '/drugs/list', '/drugs/suppliers'
  ],
  STOCK_MANAGER: [
    '/dashboard/stock-manager',
    '/inventory/list', '/inventory/warning',
    '/drugs/list'
  ],
  PHARMACY_DIRECTOR: [
    '/dashboard/pharmacy-director',
    '/purchase/requests', '/purchase/orders',
    '/special/drugs',
    '/drugs/list', '/drugs/categories', '/drugs/suppliers',
    '/inventory/list', '/inventory/warning',
    '/reports/inventory', '/reports/purchase', '/reports/prescription'
  ],
  USER: ['/', '/dashboard/admin']  // 普通用户只能访问首页
}

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 根据角色获取对应的Dashboard路径
const roleDashboard = {
  ADMIN: '/dashboard/admin',
  PHARMACIST: '/dashboard/pharmacist',
  PURCHASER: '/dashboard/purchaser',
  DOCTOR: '/dashboard/doctor',
  SPECIAL_PHARMACIST: '/dashboard/special-pharmacist',
  STOCK_MANAGER: '/dashboard/stock-manager',
  PHARMACY_DIRECTOR: '/dashboard/pharmacy-director',
  USER: '/'
}

// 全局前置守卫：登录验证 + 角色权限校验
router.beforeEach((to, from, next) => {
  // 登录页放行
  if (to.path === '/login') {
    next()
    return
  }

  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    return
  }

  // 获取用户角色
  const userStr = localStorage.getItem('user')
  let role = 'USER'
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      role = user.role || 'USER'
    } catch (e) {
      role = 'USER'
    }
  }

  // 如果访问根路径，自动重定向到对应角色的Dashboard
  if (to.path === '/') {
    next(roleDashboard[role] || '/')
    return
  }

  const allowed = roleRoutes[role] || roleRoutes.USER
  // 允许访问白名单中的路径（支持前缀匹配）
  const isAllowed = allowed.includes(to.path) || allowed.some(p => to.path.startsWith(p))

  if (isAllowed) {
    next()
  } else {
    ElMessage.error('您没有权限访问该页面')
    next(roleDashboard[role] || '/')
  }
})

export default router