<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1>注册</h1>
      <form @submit.prevent="handleRegister">
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
        
        <!-- 确认密码输入框 -->
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <div class="password-input">
            <input 
              :type="showConfirmPassword ? 'text' : 'password'" 
              id="confirmPassword" 
              v-model="form.confirmPassword" 
              placeholder="请再次输入密码" 
              required
            />
            <button 
              type="button" 
              class="toggle-password" 
              @click="showConfirmPassword = !showConfirmPassword"
            >
              {{ showConfirmPassword ? '隐藏' : '显示' }}
            </button>
          </div>
          <div class="error" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</div>
        </div>
        
        <!-- 注册按钮 -->
        <button type="submit" class="auth-button" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
        
        <!-- 登录链接 -->
        <div class="auth-switch">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
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
const showConfirmPassword = ref(false)
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
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
  } else if (form.username.length > 20) {
    newErrors.username = '用户名不能超过20个字符'
  }
  
  // 密码验证
  if (!form.password) {
    newErrors.password = '密码不能为空'
  } else if (form.password.length < 6) {
    newErrors.password = '密码至少6个字符'
  } else if (form.password.length > 20) {
    newErrors.password = '密码不能超过20个字符'
  }
  
  // 确认密码验证
  if (!form.confirmPassword) {
    newErrors.confirmPassword = '请再次输入密码'
  } else if (form.password !== form.confirmPassword) {
    newErrors.confirmPassword = '两次输入的密码不一致'
  }
  
  // 更新错误信息
  Object.assign(errors, newErrors)
  return Object.keys(newErrors).length === 0
}

// 注册处理
const handleRegister = async () => {
  // 表单验证
  if (!validateForm()) {
    return
  }
  
  loading.value = true
  
  try {
    await request.post('/api/users/register', {
      username: form.username,
      password: form.password,
      nickname: form.username // 默认使用用户名作为昵称
    })
    
    // 注册成功，跳转到登录页面
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
    console.error('错误详情:', error.response?.data)
    // 处理注册失败情况
    if (error.response?.data) {
      if (error.response.data.message) {
        // 如果后端返回了具体的错误信息
        errors.username = error.response.data.message
      } else {
        errors.username = '注册失败，请稍后重试'
      }
    } else if (error.response) {
      errors.username = `注册失败: ${error.response.status}`
    } else {
      errors.username = '网络错误，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 复用登录页面的样式，保持一致的设计风格 */
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