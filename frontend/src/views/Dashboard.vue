<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><Pill /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ drugCount }}</div>
          <div class="stat-label">药品种类</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><Package /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ inventoryCount }}</div>
          <div class="stat-label">库存总量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ pendingOrders }}</div>
          <div class="stat-label">待处理订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon red">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ expiringCount }}</div>
          <div class="stat-label">近效期预警</div>
        </div>
      </div>
    </div>
    
    <div class="charts-row">
      <div class="chart-card">
        <h3>药品类别分布</h3>
        <div class="category-chart">
          <div v-for="cat in categoryData" :key="cat.name" class="category-item">
            <span>{{ cat.name }}</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: cat.percent + '%', background: cat.color }"></div>
            </div>
            <span>{{ cat.count }}</span>
          </div>
        </div>
      </div>
      
      <div class="chart-card">
        <h3>本月采购统计</h3>
        <div class="purchase-chart">
          <div v-for="(value, index) in purchaseData" :key="index" class="bar-item">
            <div class="bar" :style="{ height: value.amount + '%' }"></div>
            <span>{{ value.month }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="recent-activities">
      <h3>最近操作记录</h3>
      <el-table :data="recentLogs" border>
        <el-table-column prop="username" label="操作人" />
        <el-table-column prop="operation" label="操作" />
        <el-table-column prop="createTime" label="时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const drugCount = ref(1256)
const inventoryCount = ref(85420)
const pendingOrders = ref(12)
const expiringCount = ref(8)

const categoryData = ref([
  { name: '抗感染药物', count: 320, percent: 25, color: '#409EFF' },
  { name: '心血管药物', count: 280, percent: 22, color: '#67C23A' },
  { name: '消化系统药物', count: 240, percent: 19, color: '#E6A23C' },
  { name: '呼吸系统药物', count: 180, percent: 14, color: '#F56C6C' },
  { name: '其他', count: 236, percent: 20, color: '#909399' }
])

const purchaseData = ref([
  { month: '1月', amount: 65 },
  { month: '2月', amount: 78 },
  { month: '3月', amount: 52 },
  { month: '4月', amount: 89 },
  { month: '5月', amount: 73 },
  { month: '6月', amount: 95 }
])

const recentLogs = ref([
  { username: '张药师', operation: '审核处方 #202401001', createTime: '2024-01-15 14:30' },
  { username: '李采购', operation: '创建采购订单 #PO2024011501', createTime: '2024-01-15 14:25' },
  { username: '王管理员', operation: '新增用户 陈护士', createTime: '2024-01-15 14:20' },
  { username: '张药师', operation: '药品入库 D0001 100盒', createTime: '2024-01-15 13:45' },
  { username: '李采购', operation: '审批采购申请 #PR2024011401', createTime: '2024-01-15 10:30' }
])

onMounted(() => {
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-icon.blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.stat-icon.orange { background: linear-gradient(135deg, #fc4a1a 0%, #f7b733 100%); }
.stat-icon.red { background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%); }

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin-bottom: 20px;
  font-size: 16px;
  color: #333;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.category-item span:first-child {
  width: 120px;
  font-size: 14px;
}

.progress-bar {
  flex: 1;
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 0.3s;
}

.category-item span:last-child {
  width: 40px;
  text-align: right;
  font-size: 14px;
}

.purchase-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 200px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar {
  width: 40px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px 8px 0 0;
  min-height: 10px;
}

.recent-activities {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.recent-activities h3 {
  margin-bottom: 20px;
  font-size: 16px;
  color: #333;
}
</style>