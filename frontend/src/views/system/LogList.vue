
<template>
  <div class="log-list">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索操作或用户名" class="search-input"></el-input>
      <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="warning" @click="exportLogs">导出日志</el-button>
    </div>
    
    <el-table :data="logList" border>
      <el-table-column prop="username" label="操作人" />
      <el-table-column prop="operation" label="操作描述" />
      <el-table-column prop="method" label="请求方法" />
      <el-table-column prop="params" label="请求参数" />
      <el-table-column prop="ipAddress" label="IP地址" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="errorMessage" label="错误信息" />
      <el-table-column prop="createTime" label="操作时间" />
    </el-table>
    
    <el-pagination
      :current-page="pagination.current"
      :page-size="pagination.size"
      :total="pagination.total"
      @current-change="handlePageChange"
      layout="prev, pager, next, jumper"
    ></el-pagination>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const dateRange = ref([])
const logList = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const loadLogs = async () => {
  try {
    const response = await axios.get('/system/logs', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value,
        startDate: dateRange.value[0]?.format('YYYY-MM-DD'),
        endDate: dateRange.value[1]?.format('YYYY-MM-DD')
      }
    })
    if (response.code === 200) {
      logList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载日志失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadLogs()
}

const handlePageChange = (page) => {
  pagination.current = page
  loadLogs()
}

const exportLogs = async () => {
  try {
    const response = await axios.get('/system/logs/export', {
      params: {
        keyword: keyword.value,
        startDate: dateRange.value[0]?.format('YYYY-MM-DD'),
        endDate: dateRange.value[1]?.format('YYYY-MM-DD')
      },
      responseType: 'blob'
    })
    const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '系统日志.xlsx'
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
  loadLogs()
})
</script>

<style scoped>
.log-list {
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
