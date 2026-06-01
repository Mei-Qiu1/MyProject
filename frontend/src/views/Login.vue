
<template>
  <div class="login-container">
    <div class="login-box">
      <h2>医院药品管理系统</h2>
      <el-form :model="loginForm" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item class="forgot-password">
          <span @click="showChangePasswordModal = true" class="change-password-btn">修改密码</span>
        </el-form-item>
      </el-form>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" v-model="showChangePasswordModal" width="400px">
      <el-form :model="passwordForm" ref="passwordFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="passwordForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入旧密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePasswordModal = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

const loginForm = reactive({
  username: '',
  password: ''
})

const passwordForm = reactive({
  username: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loginFormRef = ref(null)
const passwordFormRef = ref(null)
const showChangePasswordModal = ref(false)

const handleLogin = async () => {
  try {
    const response = await axios.post('/auth/login', loginForm)
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      ElMessage.success('登录成功')
      window.location.href = '/'
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名和密码')
  }
}

const handleChangePassword = async () => {
  // 表单验证
  if (!passwordForm.username.trim()) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!passwordForm.oldPassword) {
    ElMessage.error('请输入旧密码')
    return
  }
  if (!passwordForm.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }

  try {
    // 先获取用户ID
    const userResponse = await axios.get('/system/users', {
      params: { keyword: passwordForm.username }
    })
    if (userResponse.code === 200 && userResponse.data.list && userResponse.data.list.length > 0) {
      const userId = userResponse.data.list[0].id
      
      // 修改密码
      const response = await axios.put(`/system/users/${userId}/password`, {
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      
      if (response.code === 200) {
        ElMessage.success('密码修改成功')
        showChangePasswordModal.value = false
        // 重置表单
        passwordForm.username = ''
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } else {
        ElMessage.error(response.message || '密码修改失败')
      }
    } else {
      ElMessage.error('用户不存在')
    }
  } catch (error) {
    ElMessage.error('密码修改失败：' + (error.response?.data?.message || error.message))
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.forgot-password {
  text-align: center;
  margin-top: 10px;
}

.change-password-btn {
  color: #409eff;
  cursor: pointer;
  font-size: 14px;
}

.change-password-btn:hover {
  text-decoration: underline;
}
</style>
