<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1>登录</h1>
      <form @submit.prevent="handleLogin">
        <!-- 用户名输入框 -->
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="form.username" 
            placeholder="请输入用户名" 
            required
          />
          <div class="error" v-if="errors.username">{{ errors.username }}</div>
        </div>
        
        <!-- 密码输入框 -->
        <div class="form-group">
          <label for="password">密码</label>
          <div class="password-input">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="form.password" 
              placeholder="请输入密码" 
              required
            />
            <button 
              type="button" 
              class="toggle-password" 
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? '隐藏' : '显示' }}
            </button>
          </div>
          <div class="error" v-if="errors.password">{{ errors.password }}</div>
        </div>
        
        <!-- 记住我和忘记密码 -->
        <div class="auth-options">
          <div class="remember-me">
            <input type="checkbox" id="remember" v-model="form.remember" />
            <label for="remember">记住我</label>
          </div>
          <a href="#" class="forgot-password">忘记密码？</a>
        </div>
        
        <!-- 登录按钮 -->
        <button type="submit" class="auth-button" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        
        <!-- 注册链接 -->
        <div class="auth-switch">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()
const showPassword = ref(false)
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  remember: false
})
const errors = reactive({})

// 表单验证
const validateForm = () => {
  const newErrors = {}
  
  // 用户名验证
  if (!form.username.trim()) {
    newErrors.username = '用户名不能为空'
  } else if (form.username.length < 3) {
    newErrors.username = '用户名至少3个字符'
  }
  
  // 密码验证
  if (!form.password) {
    newErrors.password = '密码不能为空'
  } else if (form.password.length < 6) {
    newErrors.password = '密码至少6个字符'
  }
  
  // 更新错误信息
  Object.assign(errors, newErrors)
  return Object.keys(newErrors).length === 0
}

// 登录处理
const handleLogin = async () => {
  // 表单验证
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  
  try {
    const res = await request.post('/api/users/login', {
      username: form.username,
      password: form.password
    })
    
    // 保存 token 和 isAdmin 到 localStorage
    localStorage.setItem('token', res.token)
    localStorage.setItem('isAdmin', res.isAdmin ? '1' : '0')
    
    // 根据用户角色决定跳转页面
    if (res.isAdmin) {
      // 管理员跳转到后台管理首页
      router.push('/admin/dashboard')
    } else {
      // 普通用户跳转到商品列表
      router.push('/')
    }
    
    // 触发自定义事件，通知其他组件登录状态已更新
    window.dispatchEvent(new Event('loginStatusChanged'))
  } catch (error) {
    console.error('登录失败:', error)
    console.error('错误详情:', error.response?.data)
    // 处理登录失败情况
    if (error.response?.data) {
      errors.password = error.response.data.message || '用户名或密码错误'
    } else {
      errors.password = '网络错误，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.auth-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px;
  width: 100%;
  max-width: 400px;
}

.auth-card h1 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #42b883;
}

.password-input {
  display: flex;
  align-items: center;
  position: relative;
}

.password-input input {
  flex: 1;
  padding-right: 80px;
}

.toggle-password {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  color: #42b883;
  cursor: pointer;
  font-size: 14px;
}

.error {
  margin-top: 5px;
  color: #ff4444;
  font-size: 14px;
}

.auth-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remember-me label {
  margin-bottom: 0;
  cursor: pointer;
  font-weight: normal;
}

.forgot-password {
  color: #42b883;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password:hover {
  text-decoration: underline;
}

.auth-button {
  width: 100%;
  padding: 12px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.auth-button:hover:not(:disabled) {
  background-color: #3aa373;
}

.auth-button:disabled {
  background-color: #a3d9b1;
  cursor: not-allowed;
}

.auth-switch {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
  font-size: 14px;
}

.auth-switch a {
  color: #42b883;
  text-decoration: none;
  font-weight: 500;
}

.auth-switch a:hover {
  text-decoration: underline;
}
</style>