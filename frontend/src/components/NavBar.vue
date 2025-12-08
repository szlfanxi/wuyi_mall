<template>
  <nav class="navbar">
    <div class="navbar-container">
      <div class="navbar-brand">
        <router-link to="/" class="brand-link">
          <h1>无艺商城</h1>
        </router-link>
      </div>
      <div class="navbar-menu">
        <div class="navbar-links" v-if="isLoggedIn">
          <!-- 首页始终显示 -->
          <router-link to="/" class="nav-link">首页</router-link>
          <!-- 已登录状态显示的导航 -->
          <template v-if="isLoggedIn">
            <router-link to="/profile" class="nav-link profile-link">
              <span class="profile-icon">👤</span>
              <span class="nav-text">个人中心</span>
            </router-link>
            <button @click="handleLogout" class="action-btn logout-btn">
              退出登录
            </button>
          </template>
        </div>
        
        <!-- 未登录状态显示的登录/注册按钮 -->
        <div class="navbar-actions" v-else>
          <button @click="handleLogin" class="action-btn login-btn">
            登录
          </button>
          <button @click="handleRegister" class="action-btn register-btn">
            注册
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, watchEffect } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式状态：是否已登录
const isLoggedIn = ref(false)

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
const checkLoginStatus = () => {
  // 检查localStorage中是否存在token
  const token = localStorage.getItem('token')
  return !!token
}

/**
 * 更新登录状态
 */
const updateLoginStatus = () => {
  isLoggedIn.value = checkLoginStatus()
}

/**
 * 处理登录按钮点击
 */
const handleLogin = () => {
  router.push('/login')
}

/**
 * 处理注册按钮点击
 */
const handleRegister = () => {
  router.push('/register')
}

/**
 * 处理退出登录
 */
const handleLogout = () => {
  // 清除localStorage中的token
  localStorage.removeItem('token')
  // 更新登录状态
  updateLoginStatus()
  // 跳转到首页
  router.push('/')
}

// 监听localStorage变化的方法
const handleStorageChange = (e) => {
  if (e.key === 'token') {
    updateLoginStatus()
  }
}

// 监听自定义登录状态变化事件
const handleLoginStatusChanged = () => {
  updateLoginStatus()
}

// 组件挂载时检查登录状态
onMounted(() => {
  // 立即更新登录状态
  updateLoginStatus()
  
  // 添加localStorage变化监听，以便在其他页面登录/登出时更新导航栏
  window.addEventListener('storage', handleStorageChange)
  
  // 添加自定义事件监听，以便在同一页面登录/登出时更新导航栏
  window.addEventListener('loginStatusChanged', handleLoginStatusChanged)
})

// 使用setInterval定期检查登录状态，确保状态同步
// 解决某些情况下事件监听失效的问题
setInterval(() => {
  updateLoginStatus()
}, 1000) // 每秒检查一次

// 优化：监听localStorage中token的变化，确保UI更新
watchEffect(() => {
  // 每次依赖项变化时，重新检查登录状态
  // watchEffect会自动跟踪依赖，当localStorage变化时会触发
  const token = localStorage.getItem('token')
  isLoggedIn.value = !!token
  console.log('登录状态变化:', isLoggedIn.value)
})
</script>

<style scoped>
.navbar {
  background-color: #42b883;
  color: white;
  padding: 10px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-brand .brand-link {
  color: white;
  text-decoration: none;
}

.navbar-brand h1 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: 20px;
}

.navbar-links {
  display: flex;
  gap: 25px;
  align-items: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s ease;
  padding: 8px 12px;
  border-radius: 4px;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-1px);
}

.nav-icon {
  font-size: 18px;
}

.cart-link {
  position: relative;
}

.cart-icon {
  font-size: 20px;
}

.profile-link {
  position: relative;
}

.profile-icon {
  font-size: 20px;
}

.navbar-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn {
  background-color: transparent;
  color: white;
  border: 1px solid white;
}

.login-btn:hover {
  background-color: white;
  color: #42b883;
}

.register-btn {
  background-color: white;
  color: #42b883;
}

.register-btn:hover {
  background-color: #f0f0f0;
  transform: translateY(-1px);
}

.logout-btn {
  background-color: transparent;
  color: white;
  border: 1px solid white;
}

.logout-btn:hover {
  background-color: #ff4444;
  color: white;
  border-color: #ff4444;
}

/* 响应式设计 - 大型桌面端（1200px+） */
@media (min-width: 1200px) {
  .navbar-container {
    max-width: 1400px;
  }
  
  .navbar-links {
    gap: 30px;
  }
  
  .nav-link {
    font-size: 18px;
    padding: 10px 15px;
  }
}

/* 响应式设计 - 中等桌面端（992px-1199px） */
@media (max-width: 1199px) {
  .navbar-container {
    max-width: 1000px;
  }
  
  .navbar-links {
    gap: 20px;
  }
  
  .nav-link {
    font-size: 16px;
  }
  
  .navbar-brand h1 {
    font-size: 22px;
  }
}

/* 响应式设计 - 小型桌面端（769px-991px） */
@media (max-width: 991px) {
  .navbar-container {
    max-width: 800px;
    padding: 0 15px;
  }
  
  .navbar-menu {
    gap: 15px;
  }
  
  .navbar-links {
    gap: 15px;
  }
  
  .nav-link {
    font-size: 15px;
    padding: 8px 10px;
  }
  
  .navbar-brand h1 {
    font-size: 20px;
  }
  
  .action-btn {
    padding: 6px 12px;
    font-size: 13px;
  }
}

/* 响应式设计 - 移动端（≤768px） */
@media (max-width: 768px) {
  .navbar-container {
    flex-direction: column;
    gap: 15px;
    padding: 10px 20px;
    max-width: 100%;
  }

  .navbar-menu {
    flex-direction: column;
    gap: 15px;
    width: 100%;
  }

  .navbar-links {
    width: 100%;
    justify-content: center;
    gap: 15px;
  }

  .navbar-actions {
    width: 100%;
    justify-content: center;
  }

  .nav-text {
    display: none;
  }

  .nav-link {
    padding: 10px;
  }

  .cart-icon, .profile-icon {
    font-size: 22px;
  }
  
  .navbar-brand h1 {
    font-size: 18px;
  }
}
</style>