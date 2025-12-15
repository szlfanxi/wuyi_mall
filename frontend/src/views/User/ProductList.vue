<template>
  <div class="product-container">
    <header class="product-header">
      <h1>商品列表</h1>
    </header>
    <div v-if="loading">加载中...</div>
    <div v-else class="product-list">
      <div v-if="availableProducts.length === 0" class="no-products">
        <p>暂无可用商品</p>
      </div>
      <div v-else v-for="p in availableProducts" :key="p.id" class="product-item">
        <div class="product-image">
          <img :src="p.mainImage" alt="" v-if="p.mainImage" />
          <div class="no-image" v-else>暂无图片</div>
          <button 
            @click.stop="toggleFavorite(p.id)" 
            class="favorite-btn"
            :class="{ 'favorite-active': isFavorite(p.id) }"
            :title="isFavorite(p.id) ? '取消收藏' : '添加到收藏'"
          >
            ❤
          </button>
        </div>
        <h3>{{ p.name }}</h3>
        <p>￥{{ p.price }}</p>
        <button @click="goDetail(p.id)">查看详情</button>
      </div>
    </div>
    
    <!-- Notification Component -->
    <div 
      v-if="notification.show" 
      class="notification" 
      :class="notification.type"
    >
      {{ notification.message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../../utils/request'
import { useRouter } from 'vue-router'

const products = ref([])
const loading = ref(false)
const favoriteProducts = ref(new Set())
const notification = ref({ show: false, message: '', type: 'success' })
const router = useRouter()

// 过滤出库存大于0的商品
const availableProducts = computed(() => {
  return products.value.filter(product => product.stock > 0)
})

const fetchProducts = async () => {
  loading.value = true
  const res = await request.get('/api/products')
  products.value = res
  loading.value = false
  // Fetch favorite status for all products after loading
  await fetchFavoriteStatuses()
}

const fetchFavoriteStatuses = async () => {
  try {
    // Fetch all favorites for the current user
    const res = await request.get('/api/favorites')
    if (res.success && res.favorites) {
      // Extract product IDs from favorites
      const favoriteIds = res.favorites.map(fav => fav.productId)
      // Update the favoriteProducts Set
      favoriteProducts.value = new Set(favoriteIds)
    }
  } catch (error) {
    console.error('Failed to fetch favorite statuses:', error)
  }
}

const isFavorite = (productId) => {
  return favoriteProducts.value.has(productId)
}

const toggleFavorite = async (productId) => {
  try {
    if (isFavorite(productId)) {
      // Remove from favorites without confirmation
      await request.post(`/api/favorites/remove`, null, {
        params: {
          productId: productId
        }
      })
      // 重新获取收藏列表数据，确保与后端同步
      await fetchFavoriteStatuses()
      // Show success message with animation
      showNotification('已成功取消收藏', 'success')
    } else {
      // Add to favorites
      await request.post(`/api/favorites/add`, null, {
        params: {
          productId: productId
        }
      })
      // 重新获取收藏列表数据，确保与后端同步
      await fetchFavoriteStatuses()
      // Show success message with animation
      showNotification('已成功添加到收藏', 'success')
    }
  } catch (error) {
    console.error('Failed to toggle favorite:', error)
    console.error('Error details:', error.response?.data, error.response?.status)
    if (error.response?.status === 401) {
      alert('请先登录')
      router.push('/login')
    } else if (error.response?.status === 403) {
      showNotification('权限不足，请先登录', 'error')
    } else {
      showNotification('操作失败，请稍后重试', 'error')
    }
  }
}

// Notification system
const showNotification = (message, type) => {
  notification.value.message = message
  notification.value.type = type
  notification.value.show = true
  
  // Auto hide after 2 seconds
  setTimeout(() => {
    notification.value.show = false
  }, 2000)
}

const goDetail = (id) => {
  router.push(`/product/${id}`)
}

onMounted(fetchProducts)
</script>

<style scoped>
.product-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-header {
  text-align: center;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.login-entry {
  display: flex;
  gap: 10px;
}

.login-btn {
  padding: 8px 16px;
  background-color: #42b883;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #3aa373;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
}
.product-item {
  width: 200px;
  margin: 10px;
  border: 1px solid #eee;
  padding: 10px;
  text-align: center;
}
.product-image {
  width: 100%;
  height: 150px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  margin-bottom: 10px;
  overflow: hidden;
}
.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.no-image {
  color: #999;
  font-size: 14px;
}
.product-item h3 {
  margin: 10px 0;
  font-size: 16px;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.product-item p {
  font-size: 18px;
  color: #ff4444;
  margin: 10px 0;
}
.product-item button {
  padding: 8px 16px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
.product-item button:hover {
  background-color: #3aa373;
}

/* Favorite button styles */
.product-image {
  position: relative;
}

.favorite-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 30px;
  height: 30px;
  border: none;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  font-size: 16px;
  cursor: pointer;
  color: #ccc;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.favorite-btn:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
  color: #ff4444;
  animation: pulse 0.3s ease;
}

.favorite-btn.favorite-active {
  color: #ff4444;
  background: rgba(255, 68, 68, 0.2);
  box-shadow: 0 0 0 2px rgba(255, 68, 68, 0.3);
}

.favorite-btn.favorite-active:hover {
  background: rgba(255, 68, 68, 0.3);
  transform: scale(1.1);
}

/* Notification styles */
.notification {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 6px;
  color: white;
  font-weight: bold;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  animation: slideInRight 0.3s ease, fadeOut 0.3s ease 1.7s;
  opacity: 0;
  transform: translateX(100%);
  opacity: 1;
  transform: translateX(0);
}

.notification.success {
  background-color: #28a745;
}

.notification.error {
  background-color: #dc3545;
}

/* No products message */
.no-products {
  width: 100%;
  text-align: center;
  padding: 60px 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin: 20px 0;
}

.no-products p {
  font-size: 18px;
  color: #666;
  margin: 0;
}

/* Animations */
@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
  }
  to {
    opacity: 0;
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}
</style>
