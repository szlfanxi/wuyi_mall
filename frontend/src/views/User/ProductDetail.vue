<template>
  <div>
    <h1>商品详情</h1>
    <div v-if="loading">加载中...</div>
    <div v-else-if="product && product.stock > 0" class="product-detail">
      <div class="image-container">
        <!-- 缩略图列表 -->
        <div class="thumbnail-list" v-if="hasMultipleImages">
          <div 
            v-for="(image, index) in allImages" 
            :key="index"
            class="thumbnail-item"
            :class="{ 'active': selectedImageIndex === index }"
            @click="selectedImageIndex = index"
          >
            <img :src="image" :alt="`商品图片${index + 1}`" />
          </div>
        </div>
        <!-- 主图 -->
        <img :src="currentImage" alt="" class="main-image" />
      </div>
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
            @click="buyNow" 
            :disabled="product.stock <= 0"
            class="buy-now-btn"
          >
            立即购买
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
    <div v-else-if="product && product.stock <= 0" class="product-unavailable">
      <h2>商品库存不足</h2>
      <p>很抱歉，该商品目前已售罄，请关注其他商品</p>
      <button @click="goBack" class="back-btn">返回商品列表</button>
    </div>
    <div v-else>商品不存在</div>
    
    <!-- 结算模态对话框 -->
    <div v-if="showCheckoutModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>填写收货信息</h2>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitOrder" class="checkout-form">
            <div class="form-group">
              <label for="name">姓名 *</label>
              <input 
                type="text" 
                id="name" 
                v-model="formData.name"
                placeholder="请输入姓名"
                required
              />
              <span v-if="errors.name" class="error-message">{{ errors.name }}</span>
            </div>

            <div class="form-group">
              <label for="phone">联系电话 *</label>
              <input 
                type="tel" 
                id="phone" 
                v-model="formData.phone"
                placeholder="请输入联系电话"
                required
              />
              <span v-if="errors.phone" class="error-message">{{ errors.phone }}</span>
            </div>

            <div class="form-group">
              <label for="address">收货地址 *</label>
              <textarea 
                id="address" 
                v-model="formData.address"
                placeholder="请输入详细收货地址"
                rows="4"
                required
              ></textarea>
              <span v-if="errors.address" class="error-message">{{ errors.address }}</span>
            </div>

            <div class="form-group">
              <label for="remark">订单备注</label>
              <textarea 
                id="remark" 
                v-model="formData.remark"
                placeholder="请输入订单备注（可选）"
                rows="2"
              ></textarea>
            </div>

            <div class="modal-footer">
              <button type="button" class="cancel-btn" @click="closeModal">取消</button>
              <button type="submit" class="submit-btn">提交订单</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import request from '../../utils/request'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(false)
const quantity = ref(1)
const isFavorite = ref(false) // Favorite status for the current product
const selectedImageIndex = ref(0) // 当前选中的图片索引

// 结算模态对话框相关
const showCheckoutModal = ref(false)
const formData = ref({
  name: '',
  phone: '',
  address: '',
  remark: ''
})
const errors = ref({})

// 计算属性：所有图片（主图 + 额外图片）
const allImages = computed(() => {
  const images = []
  if (product.value?.mainImage) {
    images.push(product.value.mainImage)
  }
  if (product.value?.images && Array.isArray(product.value.images)) {
    images.push(...product.value.images)
  }
  return images
})

// 计算属性：当前显示的图片
const currentImage = computed(() => {
  return allImages.value[selectedImageIndex.value] || ''
})

// 计算属性：是否有多张图片
const hasMultipleImages = computed(() => {
  return allImages.value.length > 1
})

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
      // Remove from favorites without request body, use query params
      await request.post('/api/favorites/remove', null, {
        params: {
          productId: product.value.id
        }
      })
      isFavorite.value = false
    } else {
      // Add to favorites without request body, use query params
      await request.post('/api/favorites/add', null, {
        params: {
          productId: product.value.id
        }
      })
      isFavorite.value = true
    }
  } catch (error) {
    console.error('切换收藏状态失败:', error)
    console.error('Error details:', error.response?.data, error.response?.status)
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

const buyNow = () => {
  if (!product.value) return
  
  // 打开结算模态对话框
  openModal()
}

// 打开结算模态对话框
const openModal = () => {
  showCheckoutModal.value = true
}

// 关闭模态对话框
const closeModal = () => {
  showCheckoutModal.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.value = {
    name: '',
    phone: '',
    address: '',
    remark: ''
  }
  errors.value = {}
}

// 表单验证
const validateForm = () => {
  const newErrors = {}
  
  // 姓名验证
  if (!formData.value.name.trim()) {
    newErrors.name = '请输入姓名'
  }
  
  // 电话验证
  const phoneRegex = /^1[3-9]\d{9}$|^0\d{10,12}$/
  if (!formData.value.phone.trim()) {
    newErrors.phone = '请输入联系电话'
  } else if (!phoneRegex.test(formData.value.phone)) {
    newErrors.phone = '请输入正确的手机号码（11位手机号或带0开头的固定电话）'
  }
  
  // 地址验证
  if (!formData.value.address.trim()) {
    newErrors.address = '请输入收货地址'
  } else if (formData.value.address.trim().length < 5) {
    newErrors.address = '收货地址至少5个字符'
  }
  
  errors.value = newErrors
  return Object.keys(newErrors).length === 0
}

// 提交订单
const submitOrder = async () => {
  // 表单验证
  if (!validateForm()) {
    return
  }
  
  try {
    // 准备订单数据
    const orderData = {
      orderItems: [
        {
          productId: product.value.id,
          productName: product.value.name,
          productPrice: product.value.price,
          quantity: quantity.value,
          totalPrice: product.value.price * quantity.value
        }
      ],
      // 表单数据
      address: formData.value.address,
      phone: formData.value.phone,
      name: formData.value.name,
      remark: formData.value.remark,
      // 后端CreateOrderRequest需要的字段
      addressId: "1", // 暂时使用固定值，后续可以从地址管理中获取
      paymentMethod: "online" // 默认在线支付
    }
    
    // 调用提交订单API
    await request.post('/api/orders/create', orderData)
    
    // 显示成功提示
    alert('订单提交成功')
    
    // 关闭模态对话框
    closeModal()
    
    // 跳转到订单列表页面
    router.push('/orders')
  } catch (error) {
    // 打印详细的错误信息到控制台
    console.error('提交订单失败:', error)
    console.error('错误响应:', error.response)
    console.error('错误数据:', error.response?.data)
    console.error('错误状态:', error.response?.status)
    console.error('错误头:', error.response?.headers)
    
    // 显示更详细的错误信息
    const errorMessage = error.response?.data?.message || error.message || '提交订单失败，请稍后重试'
    alert(`提交订单失败: ${errorMessage}`)
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

.image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.main-image {
  width: 400px;
  height: 400px;
  object-fit: cover;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: white;
  transition: all 0.3s ease;
}

/* 缩略图列表样式 */
.thumbnail-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
  padding: 10px;
  background-color: white;
  border-radius: 8px;
  border: 1px solid #eee;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.thumbnail-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.thumbnail-item.active {
  border-color: #42b883;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(66, 184, 131, 0.2);
}

.thumbnail-item.active img {
  transform: scale(1.05);
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

.buy-now-btn {
  background-color: #ff6b35;
  color: white;
  flex: 1;
}

.buy-now-btn:hover:not(:disabled) {
  background-color: #e55a2a;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.buy-now-btn:disabled {
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

/* Product unavailable styles */
.product-unavailable {
  text-align: center;
  padding: 60px 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin: 20px;
}

.product-unavailable h2 {
  color: #ff4444;
  margin-bottom: 20px;
  font-size: 24px;
}

.product-unavailable p {
  color: #666;
  margin-bottom: 30px;
  font-size: 18px;
  line-height: 1.6;
}

.product-unavailable .back-btn {
  padding: 12px 24px;
  font-size: 18px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.product-unavailable .back-btn:hover {
  background-color: #3aa373;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 模态对话框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-btn:hover {
  background-color: #f0f0f0;
  color: #333;
}

.modal-body {
  padding: 20px;
}

.checkout-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-group input,
.form-group textarea {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 2px rgba(66, 184, 131, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.error-message {
  font-size: 12px;
  color: #ff4444;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 0 0 0;
}

.cancel-btn,
.submit-btn {
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
  border-color: #ccc;
}

.submit-btn {
  background-color: #42b883;
  color: white;
}

.submit-btn:hover {
  background-color: #3aa373;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  /* 模态对话框响应式样式 */
  .modal-content {
    width: 95%;
    margin: 10px;
  }
  
  .modal-header,
  .modal-body {
    padding: 15px;
  }
  
  .modal-header h2 {
    font-size: 18px;
  }
  
  .modal-footer {
    flex-direction: column;
  }
  
  .cancel-btn,
  .submit-btn {
    width: 100%;
  }
}
</style>