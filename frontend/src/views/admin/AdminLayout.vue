<template>
  <div class="admin-layout">
    <!-- 侧边导航栏 -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2>后台管理</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin/dashboard" class="nav-item" :class="{ active: $route.path === '/admin/dashboard' }">
          <span class="nav-icon">📊</span>
          <span class="nav-text">仪表盘</span>
        </router-link>
        <router-link to="/admin/products" class="nav-item" :class="{ active: $route.path === '/admin/products' }">
          <span class="nav-icon">📦</span>
          <span class="nav-text">商品管理</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item" :class="{ active: $route.path === '/admin/categories' }">
          <span class="nav-icon">📁</span>
          <span class="nav-text">类目管理</span>
        </router-link>
        <router-link to="/admin/orders" class="nav-item" :class="{ active: $route.path === '/admin/orders' }">
          <span class="nav-icon">📋</span>
          <span class="nav-text">订单管理</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" :class="{ active: $route.path === '/admin/users' }">
          <span class="nav-icon">👥</span>
          <span class="nav-text">用户管理</span>
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button @click="handleLogout" class="logout-btn">
          <span class="nav-icon">🚪</span>
          <span class="nav-text">退出登录</span>
        </button>
      </div>
    </aside>
    <!-- 主内容区域 -->
    <main class="admin-main">
      <!-- 顶部导航栏 -->
      <header class="admin-header">
        <div class="header-left">
          <button @click="toggleSidebar" class="sidebar-toggle">
            <span>☰</span>
          </button>
          <h1>{{ currentPageTitle }}</h1>
        </div>
        <div class="header-right">
          <div class="user-info">
            <span class="user-name">管理员</span>
          </div>
        </div>
      </header>
      <!-- 内容区域 -->
      <div class="admin-content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isSidebarCollapsed = ref(false)

// 根据当前路由设置页面标题
const currentPageTitle = computed(() => {
  const path = router.currentRoute.value.path
  switch (path) {
    case '/admin/dashboard':
      return '仪表盘'
    case '/admin/products':
      return '商品管理'
    case '/admin/categories':
      return '类目管理'
    case '/admin/orders':
      return '订单管理'
    case '/admin/users':
      return '用户管理'
    default:
      return '后台管理'
  }
})

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

// 退出登录
const handleLogout = () => {
  // 清除localStorage中的token和isAdmin
  localStorage.removeItem('token')
  localStorage.removeItem('isAdmin')
  // 跳转到登录页
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;
}

/* 侧边导航栏 */
.admin-sidebar {
  width: 250px;
  background-color: #34495e;
  color: white;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.admin-sidebar.collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #2c3e50;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: white;
  text-decoration: none;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background-color: #2c3e50;
  border-left-color: #42b883;
}

.nav-item.active {
  background-color: #2c3e50;
  border-left-color: #42b883;
}

.nav-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 20px;
  text-align: center;
}

.nav-text {
  font-size: 16px;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #2c3e50;
}

.logout-btn {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 15px 20px;
  background: none;
  border: none;
  color: white;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.logout-btn:hover {
  background-color: #e74c3c;
}

/* 主内容区域 */
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sidebar-toggle {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.sidebar-toggle:hover {
  background-color: #f0f0f0;
}

.admin-header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-weight: 600;
  color: #333;
}

.admin-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1000;
  }
  
  .admin-sidebar.collapsed {
    transform: translateX(-100%);
  }
  
  .admin-main {
    margin-left: 0;
  }
}
</style>
