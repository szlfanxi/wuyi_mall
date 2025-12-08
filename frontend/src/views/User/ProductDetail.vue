<template>
  <div>
    <h1>商品详情</h1>
    <div v-if="loading">加载中...</div>
    <div v-else-if="product" class="product-detail">
      <img :src="product.mainImage" alt="" v-if="product.mainImage" class="main-image" />
      <div class="product-info">
        <h2>{{ product.name }}</h2>
        <p class="price">￥{{ product.price }}</p>
        <p class="stock" v-if="product.stock > 0">库存：{{ product.stock }}件</p>
        <p class="stock out-of-stock" v-else>库存不足</p>
        <div class="description">
          <h3>商品描述</h3>
          <p>{{ product.description }}</p>
        </div>
        <div class="quantity-selector">
          <label for="quantity">数量：</label>
          <div class="quantity-control">
            <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
            <input 
              type="number" 
              id="quantity" 
              v-model.number="quantity" 
              min="1" 
              :max="product.stock"
            />
            <button @click="increaseQuantity" :disabled="quantity >= product.stock">+</button>
          </div>
        </div>
        <div class="action-buttons">
          <button 
            @click="addToCart" 
            :disabled="product.stock <= 0"
            class="add-to-cart-btn"
          >
            加入购物车
          </button>
          <button 
            @click="toggleFavorite" 
            :class="['favorite-btn-detail', { 'favorite-active': isFavorite }]"
          >
            <span class="favorite-icon">❤</span>
            <span class="favorite-text">{{ isFavorite ? '已收藏' : '收藏' }}</span>
          </button>
          <button @click="goBack" class="back-btn">返回列表</button>
        </div>
      </div>
    </div>
    <div v-else>商品不存在</div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '../../utils/request'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(false)
const quantity = ref(1)
const isFavorite = ref(false) // Favorite status for the current product

const fetchProduct = async () => {
  const id = route.params.id
  loading.value = true
  try {
    const res = await request.get(`/api/products/${id}`)
    product.value = res
    // Fetch favorite status for the product after loading details
    await checkFavoriteStatus()
  } catch (error) {
    console.error('获取商品详情失败:', error)
    alert('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const checkFavoriteStatus = async () => {
  if (!product.value) return
  try {
    const res = await request.get(`/api/favorites/check?productId=${product.value.id}`)
    if (res.success) {
      isFavorite.value = res.isFavorite
    }
  } catch (error) {
    console.error('获取收藏状态失败:', error)
    isFavorite.value = false
  }
}

const toggleFavorite = async () => {
  if (!product.value) return
  try {
    if (isFavorite.value) {
      // Remove from favorites
      await request.post('/api/favorites/remove', { productId: product.value.id })
      isFavorite.value = false
    } else {
      // Add to favorites
      await request.post('/api/favorites/add', { productId: product.value.id })
      isFavorite.value = true
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    if (error.response?.status === 401) {
      alert('请先登录')
      router.push('/login')
    } else {
      alert('操作失败，请稍后重试')
    }
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQuantity = () => {
  if (product.value && quantity.value < product.value.stock) {
    quantity.value++
  }
}

const addToCart = async () => {
  if (!product.value) return
  
  try {
    await request.post('/api/cart/add', {
      productId: product.value.id,
      quantity: quantity.value
    })
    alert('商品已成功加入购物车')
    quantity.value = 1 // 重置数量为1
  } catch (error) {
    console.error('加入购物车失败:', error)
    if (error.response?.data?.message) {
      alert(error.response.data.message)
    } else if (error.response?.status === 401) {
      alert('请先登录')
      router.push('/login')
    } else {
      alert('加入购物车失败')
    }
  }
}

const goBack = () => {
  router.push('/')
}

onMounted(fetchProduct)
</script>

<style scoped>
.product-detail {
  display: flex;
  gap: 40px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.main-image {
  width: 400px;
  height: 400px;
  object-fit: cover;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: white;
}

.product-info {
  flex: 1;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.product-info h2 {
  margin-top: 0;
  color: #333;
  font-size: 28px;
  margin-bottom: 15px;
}

.price {
  font-size: 32px;
  color: #ff4444;
  margin: 15px 0;
  font-weight: bold;
}

.stock {
  margin: 15px 0;
  font-size: 18px;
  color: #666;
}

.out-of-stock {
  color: #ff4444;
  font-weight: bold;
}

.description {
  margin: 25px 0;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
  line-height: 1.6;
}

.description h3 {
  margin-top: 0;
  color: #333;
  font-size: 20px;
  margin-bottom: 10px;
}

.quantity-selector {
  margin: 25px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-selector label {
  font-size: 18px;
  color: #333;
  font-weight: bold;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.quantity-control button {
  width: 40px;
  height: 40px;
  background-color: #f0f0f0;
  border: none;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
}

.quantity-control button:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.quantity-control button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-control input {
  width: 60px;
  height: 40px;
  border: none;
  text-align: center;
  font-size: 18px;
  outline: none;
  color: #333;
}

.quantity-control input::-webkit-inner-spin-button,
.quantity-control input::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.action-buttons button {
  padding: 12px 24px;
  font-size: 18px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.add-to-cart-btn {
  background-color: #42b883;
  color: white;
  flex: 1;
}

.add-to-cart-btn:hover:not(:disabled) {
  background-color: #3aa373;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.add-to-cart-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  opacity: 0.7;
}

.back-btn {
  background-color: #f0f0f0;
  color: #333;
  min-width: 120px;
}

.back-btn:hover {
  background-color: #e0e0e0;
}

/* Favorite button styles */
.favorite-btn-detail {
  background-color: #f0f0f0;
  color: #333;
  min-width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s ease;
}

.favorite-btn-detail:hover {
  background-color: #e0e0e0;
}

.favorite-btn-detail.favorite-active {
  background-color: #fff0f0;
  color: #ff4444;
  border: 1px solid #ffcccc;
}

.favorite-btn-detail.favorite-active:hover {
  background-color: #ffe0e0;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(255, 68, 68, 0.1);
}

.favorite-icon {
  font-size: 18px;
}
</style>