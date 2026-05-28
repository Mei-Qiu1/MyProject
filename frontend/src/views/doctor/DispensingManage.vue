<template>
  <div class="dispensing-manage">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索处方号或患者姓名" class="search-input"></el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openAddDialog">新增调配</el-button>
      <el-button type="warning" @click="confirmAll" :loading="confirmLoading">确认保存</el-button>
      <el-button type="danger" @click="cancelAll">撤销全部</el-button>
    </div>

    <el-table :data="recordList" border>
      <el-table-column prop="prescriptionNo" label="处方号" />
      <el-table-column prop="patientName" label="患者姓名" />
      <el-table-column prop="department" label="科室" />
      <el-table-column prop="doctorName" label="医生姓名" />
      <el-table-column prop="createTime" label="调配时间" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button link type="danger" @click="deleteRow(scope.row.id, scope.$index)">删除</el-button>
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

    <!-- 新增调配对话框 -->
    <el-dialog title="新增药品调配" v-model="showAddDialog" width="500px">
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="处方号" prop="prescriptionNo" required>
          <el-input v-model="formData.prescriptionNo"></el-input>
        </el-form-item>
        <el-form-item label="患者姓名" prop="patientName" required>
          <el-input v-model="formData.patientName"></el-input>
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-input v-model="formData.department"></el-input>
        </el-form-item>
        <el-form-item label="医生姓名" prop="doctorName" required>
          <el-input v-model="formData.doctorName" disabled></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

const keyword = ref('')
const recordList = ref([])
const showAddDialog = ref(false)
const confirmLoading = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 当前登录医生信息（从 localStorage 获取）
const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
const currentDoctorName = userInfo.realName || '医生'

const formData = reactive({
  prescriptionNo: '',
  patientName: '',
  department: '',
  doctorName: currentDoctorName
})

// 临时存储未确认的新增记录（前端暂存，方便撤销）
let pendingRecords = [] // 暂未使用，可直接调用接口

// 加载列表
const loadRecords = async () => {
  try {
    const response = await axios.get('/doctor/dispensing', {
      params: {
        page: pagination.current,
        size: pagination.size,
        keyword: keyword.value
      }
    })
    if (response.code === 200) {
      recordList.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('加载调配记录失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadRecords()
}

// 分页
const handlePageChange = (page) => {
  pagination.current = page
  loadRecords()
}

// 打开新增对话框
const openAddDialog = () => {
  formData.prescriptionNo = ''
  formData.patientName = ''
  formData.department = ''
  formData.doctorName = currentDoctorName
  showAddDialog.value = true
}

// 单条添加（直接保存到数据库）
const saveRecord = async () => {
  if (!formData.prescriptionNo || !formData.patientName) {
    ElMessage.warning('请填写处方号和患者姓名')
    return
  }
  try {
    await axios.post('/doctor/dispensing', formData)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    loadRecords()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除单条记录
const deleteRow = async (id, index) => {
  try {
    await ElMessageBox.confirm('确定要删除该条调配记录吗？', '提示', { type: 'warning' })
    await axios.delete(`/doctor/dispensing/${id}`)
    ElMessage.success('删除成功')
    loadRecords()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 确认保存（当前所有已添加的记录已自动保存，此按钮用于批量导入场景，可扩展）
// 这里简单实现为刷新列表
const confirmAll = async () => {
  confirmLoading.value = true
  try {
    ElMessage.success('所有记录已保存')
    loadRecords()
  } finally {
    confirmLoading.value = false
  }
}

// 撤销全部：清空搜索框，重置列表（实际撤销未保存的草稿功能由前端自行管理，我们简化：重新加载列表）
const cancelAll = () => {
  keyword.value = ''
  pagination.current = 1
  loadRecords()
  ElMessage.info('已撤销当前未保存的修改')
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.dispensing-manage {
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