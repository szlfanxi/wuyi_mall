<template>
  <div class="favorites-container">
    <header class="favorites-header">
      <h1>我的收藏</h1>
    </header>
    
    <div v-if="loading" class="loading">加载中...</div>
    
    <div v-else-if="error" class="error-message">
      <p>{{ error }}</p>
      <button @click="fetchFavorites">重试</button>
    </div>
    
    <div v-else>
      <!-- Filter and sort options -->
      <div class="filter-sort-section">
        <div class="sort-options">
          <label for="sort-by">排序方式：</label>
          <select id="sort-by" v-model="sortBy" @change="applySortAndFilter">
            <option value="createdAtDesc">收藏时间（最新优先）</option>
            <option value="createdAtAsc">收藏时间（最早优先）</option>
            <option value="priceDesc">价格（从高到低）</option>
            <option value="priceAsc">价格（从低到高）</option>
          </select>
        </div>
        
        <div class="filter-options">
          <label for="price-range">价格范围：</label>
          <input 
            type="number" 
            id="min-price" 
            v-model.number="priceRange.min" 
            placeholder="最小" 
            min="0"
          />
          <span>-</span>
          <input 
            type="number" 
            id="max-price" 
            v-model.number="priceRange.max" 
            placeholder="最大" 
            min="0"
          />
          <button @click="applySortAndFilter" class="filter-btn">应用筛选</button>
          <button @click="resetFilters" class="reset-btn">重置</button>
        </div>
      </div>
      
      <!-- Favorites list -->
      <div v-if="filteredFavorites.length === 0" class="empty-favorites">
        <p>您还没有收藏任何商品</p>
        <router-link to="/" class="go-shopping-btn">去购物</router-link>
      </div>
      
      <div v-else class="favorites-list">
        <div v-for="favorite in filteredFavorites" :key="favorite.id" class="favorite-item">
          <div class="favorite-image">
            <img :src="favorite.product.mainImage" alt="" v-if="favorite.product.mainImage" />
            <div class="no-image" v-else>暂无图片</div>
          </div>
          
          <div class="favorite-info">
            <h3>{{ favorite.product.name }}</h3>
            <p class="product-price">￥{{ favorite.product.price }}</p>
            <p class="favorite-date">收藏时间：{{ formatDate(favorite.createTime) }}</p>
            <div class="favorite-actions">
              <button @click="goDetail(favorite.product.id)" class="view-detail-btn">查看详情</button>
              <button @click="removeFavorite(favorite)" class="remove-btn">取消收藏</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Pagination -->
      <div v-if="totalFavorites > 0" class="pagination">
        <span>共 {{ totalFavorites }} 个商品</span>
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

const router = useRouter()
const loading = ref(false)
const error = ref('')
const favorites = ref([])
const totalFavorites = ref(0)
// 通知组件状态
const notification = ref({
  show: false,
  message: '',
  type: 'success'
})

// 通知组件实现
const showNotification = (message, type) => {
  notification.value.message = message
  notification.value.type = type
  notification.value.show = true
  
  // Auto hide after 2 seconds
  setTimeout(() => {
    notification.value.show = false
  }, 2000)
}

// Filter and sort state
const sortBy = ref('createdAtDesc')
const priceRange = ref({
  min: null,
  max: null
})

// Fetch favorites from API
const fetchFavorites = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await request.get('/api/favorites')
    if (res.success && res.favorites) {
      // Fetch full product details for each favorite
      const favoritesWithDetails = await Promise.all(
        res.favorites.map(async (favorite) => {
          const productRes = await request.get(`/api/products/${favorite.productId}`)
          return {
            id: favorite.id,
            productId: favorite.productId,
            createTime: favorite.createTime,
            product: productRes
          }
        })
      )
      favorites.value = favoritesWithDetails
      totalFavorites.value = favoritesWithDetails.length
    } else {
      favorites.value = []
      totalFavorites.value = 0
    }
  } catch (err) {
    console.error('Failed to fetch favorites:', err)
    if (err.response?.status === 401) {
      error.value = '请先登录'
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      error.value = '获取收藏列表失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}

// Format date
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// Go to product detail
const goDetail = (productId) => {
  router.push(`/product/${productId}`)
}

// Remove favorite
// 修改Favorites.vue中的removeFavorite函数
// Favorites.vue 修复后
const removeFavorite = async (favorite) => {
  try {
    // 发送删除请求
    await request.post('/api/favorites/remove', null, {
      params: {
        productId: favorite.productId
      }
    })
    // 重新获取数据，确保与后端同步
    await fetchFavorites()
    // 显示成功提示
    showNotification('取消收藏成功', 'success')
  } catch (err) {
    console.error('Failed to remove favorite:', err)
    // 显示失败提示
    const errorMsg = err.response?.data?.message || '网络错误，请稍后重试'
    showNotification(`取消收藏失败: ${errorMsg}`, 'error')
  }
}


// Apply sort and filter
const applySortAndFilter = () => {
  // The filtering is handled by computed property
}

// Reset filters
const resetFilters = () => {
  sortBy.value = 'createdAtDesc'
  priceRange.value = { min: null, max: null }
}

// Filtered and sorted favorites (computed)
const filteredFavorites = computed(() => {
  let result = [...favorites.value]
  
  // Apply price filter
  if (priceRange.value.min !== null && !isNaN(priceRange.value.min)) {
    result = result.filter(fav => fav.product.price >= priceRange.value.min)
  }
  
  if (priceRange.value.max !== null && !isNaN(priceRange.value.max)) {
    result = result.filter(fav => fav.product.price <= priceRange.value.max)
  }
  
  // Apply sort
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'createdAtDesc':
        return new Date(b.createTime) - new Date(a.createTime)
      case 'createdAtAsc':
        return new Date(a.createTime) - new Date(b.createTime)
      case 'priceDesc':
        return parseFloat(b.product.price) - parseFloat(a.product.price)
      case 'priceAsc':
        return parseFloat(a.product.price) - parseFloat(b.product.price)
      default:
        return 0
    }
  })
  
  return result
})

// Initial fetch
onMounted(fetchFavorites)
</script>

<style scoped>
.favorites-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.favorites-header h1 {
  margin: 0;
  color: #333;
  font-size: 28px;
  margin-bottom: 20px;
}

.loading {
  text-align: center;
  padding: 40px 0;
  color: #666;
  font-size: 18px;
}

.error-message {
  text-align: center;
  padding: 40px 0;
  color: #dc3545;
}

.error-message button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.filter-sort-section {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.sort-options,
.filter-options {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-options select,
.filter-options input {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.filter-btn,
.reset-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.filter-btn {
  background-color: #007bff;
  color: white;
}

.filter-btn:hover {
  background-color: #0056b3;
}

.reset-btn {
  background-color: #6c757d;
  color: white;
}

.reset-btn:hover {
  background-color: #5a6268;
}

.empty-favorites {
  text-align: center;
  padding: 60px 0;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.empty-favorites p {
  margin: 0 0 20px 0;
  color: #666;
  font-size: 18px;
}

.go-shopping-btn {
  display: inline-block;
  padding: 10px 20px;
  background-color: #42b883;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.go-shopping-btn:hover {
  background-color: #3aa373;
}

.favorites-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.favorite-item {
  background-color: white;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.favorite-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.favorite-image {
  width: 100%;
  height: 200px;
  background-color: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.favorite-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  color: #999;
  font-size: 14px;
}

.favorite-info {
  padding: 15px;
}

.favorite-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  color: #ff4444;
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}

.favorite-date {
  font-size: 12px;
  color: #999;
  margin: 10px 0;
}

.favorite-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.view-detail-btn,
.remove-btn {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.view-detail-btn {
  flex: 1;
  background-color: #42b883;
  color: white;
}

.view-detail-btn:hover {
  background-color: #3aa373;
}

.remove-btn {
  background-color: #dc3545;
  color: white;
}

.remove-btn:hover {
  background-color: #c82333;
}

.pagination {
  margin-top: 20px;
  text-align: center;
  color: #666;
}

/* Responsive design */
@media (max-width: 768px) {
  .filter-sort-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .sort-options,
  .filter-options {
    flex-wrap: wrap;
  }
  
  .favorites-list {
    grid-template-columns: 1fr;
  }
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
</style>