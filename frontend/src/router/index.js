
import { createRouter, createWebHistory } from 'vue-router'

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
      { path: '/clinical/orders', name: 'MedicalOrder', component: () => import('../views/clinical/MedicalOrder.vue') },
      { path: '/special/drugs', name: 'SpecialDrug', component: () => import('../views/special/SpecialDrug.vue') },
      { path: '/reports/inventory', name: 'InventoryReport', component: () => import('../views/reports/InventoryReport.vue') },
      { path: '/reports/purchase', name: 'PurchaseReport', component: () => import('../views/reports/PurchaseReport.vue') },
      { path: '/reports/consumption', name: 'ConsumptionReport', component: () => import('../views/reports/ConsumptionReport.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    next()
  } else {
    const token = localStorage.getItem('token')
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
