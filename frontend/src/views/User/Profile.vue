<template>
  <div class="profile-container">
    <h1 class="page-title">个人中心</h1>
    
    <div class="profile-content">
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar-container">
            <span class="avatar-icon">👤</span>
          </div>
          <h2 class="username">欢迎，{{ username }}</h2>
          <p class="user-info">这是您的个人中心页面</p>
        </div>
      </div>
      
      <div class="profile-actions">
        <div class="action-card">
          <div class="action-icon cart-icon-large">🛒</div>
          <h3 class="action-title">我的购物车</h3>
          <p class="action-description">查看管理您的购物车商品</p>
          <router-link to="/cart" class="action-button primary-btn">
            <span class="btn-icon">🛒</span>
            <span>前往购物车</span>
          </router-link>
        </div>
        
        <div class="action-card">
          <div class="action-icon order-icon">📋</div>
          <h3 class="action-title">我的订单</h3>
          <p class="action-description">查看您的历史订单记录</p>
          <router-link to="/orders" class="action-button primary-btn">
            <span class="btn-icon">📋</span>
            <span>查看订单</span>
          </router-link>
        </div>
        
        <div class="action-card">
          <div class="action-icon favorite-icon">❤️</div>
          <h3 class="action-title">我的收藏</h3>
          <p class="action-description">查看管理您的收藏商品</p>
          <router-link to="/favorites" class="action-button primary-btn">
            <span class="btn-icon">❤️</span>
            <span>查看收藏</span>
          </router-link>
        </div>
        
        <div class="action-card">
          <div class="action-icon settings-icon">⚙️</div>
          <h3 class="action-title">账户设置</h3>
          <p class="action-description">修改您的账户信息</p>
          <button class="action-button secondary-btn">
            <span class="btn-icon">⚙️</span>
            <span>账户设置</span>
          </button>
        </div>
      </div>
      
      <div class="profile-stats">
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">待付款</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">待发货</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">待收货</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">0</div>
          <div class="stat-label">待评价</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()
const username = ref('用户')

const fetchUserInfo = async () => {
  try {
    const res = await request.get('/api/user/info')
    username.value = res.data.username || '用户'
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const goToOrders = () => {
  router.push('/orders')
}

onMounted(fetchUserInfo)
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  color: #333;
  font-size: 32px;
  margin-bottom: 30px;
  font-weight: bold;
}

.profile-content {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-container {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background-color: #42b883;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.avatar-icon {
  font-size: 60px;
}

.username {
  font-size: 24px;
  color: #333;
  margin: 0;
  font-weight: bold;
}

.user-info {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.profile-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.action-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 25px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.action-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.cart-icon-large {
  color: #42b883;
}

.order-icon {
  color: #ff6b6b;
}

.favorite-icon {
  color: #ff4444;
}

.settings-icon {
  color: #95e1d3;
}

.action-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: bold;
}

.action-description {
  font-size: 14px;
  color: #666;
  margin: 0 0 20px 0;
  line-height: 1.4;
}

.action-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.primary-btn {
  background-color: #42b883;
  color: white;
}

.primary-btn:hover {
  background-color: #3aa373;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(66, 184, 131, 0.3);
}

.secondary-btn {
  background-color: white;
  color: #666;
  border: 2px solid #ddd;
}

.secondary-btn:hover {
  background-color: #f5f5f5;
  border-color: #42b883;
  color: #42b883;
  transform: translateY(-2px);
}

.btn-icon {
  font-size: 18px;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px;
  background-color: #f0f0f0;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background-color: white;
  border-radius: 6px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #42b883;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-actions {
    grid-template-columns: 1fr;
  }

  .profile-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .action-card {
    padding: 20px;
  }

  .action-button {
    width: 100%;
    justify-content: center;
  }

  .page-title {
    font-size: 24px;
  }

  .username {
    font-size: 20px;
  }

  .avatar-container {
    width: 100px;
    height: 100px;
  }

  .avatar-icon {
    font-size: 50px;
  }
}

@media (max-width: 480px) {
  .profile-stats {
    grid-template-columns: 1fr;
  }

  .profile-content {
    padding: 20px 15px;
  }

  .action-icon {
    font-size: 40px;
  }

  .action-title {
    font-size: 16px;
  }

  .action-description {
    font-size: 13px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>