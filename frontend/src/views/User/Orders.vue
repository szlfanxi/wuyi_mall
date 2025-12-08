<template>
  <div class="orders-container">
    <h1>我的订单</h1>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="orders.length === 0" class="empty-orders">
        <p>暂无订单</p>
        <router-link to="/" class="go-shopping-btn">去购物</router-link>
      </div>
      
      <div v-else>
        <div v-for="order in orders" :key="order.id" class="order-card">
          <!-- 订单头部 -->
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ formatDate(order.createTime) }}</span>
            </div>
            <div class="order-status">
              <span :class="getStatusClass(order.status)">{{ order.statusText }}</span>
            </div>
          </div>
          
          <!-- 订单商品项 -->
          <div class="order-items">
            <div 
              v-for="item in order.orderItems" 
              :key="item.productId" 
              class="order-item"
            >
              <img :src="item.productImage" alt="" class="product-image" v-if="item.productImage" />
              <div class="product-info">
                <h4 class="product-name">{{ item.productName }}</h4>
                <div class="product-details">
                  <span class="product-price">￥{{ item.productPrice }}</span>
                  <span class="product-quantity">x{{ item.quantity }}</span>
                  <span class="product-total">￥{{ item.totalPrice }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="total-amount">
              总计：<span class="total-price">￥{{ order.totalAmount }}</span>
            </div>
            <div class="order-actions">
              <button 
                v-if="order.status === 0" 
                @click="cancelOrder(order.id)"
                class="action-btn cancel-btn"
              >
                取消订单
              </button>
              <button 
                v-if="order.status === 0" 
                @click="payOrder(order.id)"
                class="action-btn pay-btn"
              >
                去支付
              </button>
              <button 
                v-if="order.status === 1" 
                @click="confirmReceipt(order.id)"
                class="action-btn confirm-btn"
              >
                确认收货
              </button>
              <button 
                v-if="order.status === 3 || order.status === 4" 
                @click="deleteOrder(order.id)"
                class="action-btn delete-btn"
              >
                删除订单
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()
const orders = ref([])
const loading = ref(false)

/**
 * 获取订单列表
 */
const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/orders')
    orders.value = res
  } catch (error) {
    console.error('获取订单列表失败:', error)
    alert('获取订单列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

/**
 * 格式化日期
 * @param {string} dateTime 日期时间字符串
 * @returns 格式化后的日期字符串
 */
const formatDate = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString()
}

/**
 * 获取订单状态类名
 * @param {number} status 订单状态
 * @returns 类名
 */
const getStatusClass = (status) => {
  switch (status) {
    case 0: return 'status-pending'
    case 1: return 'status-paid'
    case 2: return 'status-shipped'
    case 3: return 'status-completed'
    case 4: return 'status-canceled'
    default: return ''
  }
}

/**
 * 取消订单
 * @param {number} orderId 订单ID
 */
const cancelOrder = async (orderId) => {
  if (confirm('确定要取消这个订单吗？')) {
    try {
      const res = await request.post(`/api/orders/${orderId}/cancel`)
      alert(res.data.message || '订单已取消')
      fetchOrders() // 刷新订单列表
    } catch (error) {
      console.error('取消订单失败:', error)
      alert('取消订单失败，请稍后重试')
    }
  }
}

/**
 * 支付订单
 * @param {number} orderId 订单ID
 */
const payOrder = async (orderId) => {
  try {
    const res = await request.post(`/api/orders/${orderId}/pay`)
    alert(res.data.message || '支付成功')
    fetchOrders() // 刷新订单列表
  } catch (error) {
    console.error('支付订单失败:', error)
    alert('支付订单失败，请稍后重试')
  }
}

/**
 * 确认收货
 * @param {number} orderId 订单ID
 */
const confirmReceipt = async (orderId) => {
  if (confirm('确定已收到货物吗？')) {
    try {
      const res = await request.post(`/api/orders/${orderId}/confirm`)
      alert(res.data.message || '确认收货成功')
      fetchOrders() // 刷新订单列表
    } catch (error) {
      console.error('确认收货失败:', error)
      alert('确认收货失败，请稍后重试')
    }
  }
}

/**
 * 删除订单
 * @param {number} orderId 订单ID
 */
const deleteOrder = async (orderId) => {
  if (confirm('确定要删除这个订单吗？')) {
    try {
      const res = await request.post(`/api/orders/${orderId}/delete`)
      alert(res.data.message || '订单已删除')
      fetchOrders() // 刷新订单列表
    } catch (error) {
      console.error('删除订单失败:', error)
      alert('删除订单失败，请稍后重试')
    }
  }
}

// 组件挂载时获取订单列表
onMounted(fetchOrders)
</script>

<style scoped>
/* 订单容器 */
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 订单标题 */
h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
}

/* 订单列表 */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 50px 0;
  color: #666;
  font-size: 18px;
}

/* 空订单状态 */
.empty-orders {
  text-align: center;
  padding: 50px 0;
  color: #666;
  font-size: 18px;
}

.go-shopping-btn {
  display: inline-block;
  margin-top: 20px;
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

/* 订单卡片 */
.order-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.order-no {
  font-weight: 600;
  color: #333;
}

/* 订单状态 */
.order-status {
  font-weight: 600;
}

.status-pending {
  color: #ff9800;
}

.status-paid {
  color: #2196f3;
}

.status-shipped {
  color: #4caf50;
}

.status-completed {
  color: #8bc34a;
}

.status-canceled {
  color: #f44336;
}

/* 订单商品项 */
.order-items {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

/* 订单商品 */
.order-item {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.order-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

/* 商品图片 */
.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  margin-right: 15px;
  border-radius: 4px;
  border: 1px solid #eee;
}

/* 商品信息 */
.product-info {
  flex: 1;
}

.product-name {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

/* 商品详情 */
.product-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.product-price {
  color: #ff4444;
  font-weight: 600;
}

.product-quantity {
  color: #666;
}

.product-total {
  color: #ff4444;
  font-weight: 600;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #fafafa;
}

/* 总计金额 */
.total-amount {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.total-price {
  color: #ff4444;
  font-size: 20px;
}

/* 订单操作按钮 */
.order-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.pay-btn {
  background-color: #42b883;
  color: white;
}

.pay-btn:hover {
  background-color: #3aa373;
}

.confirm-btn {
  background-color: #2196f3;
  color: white;
}

.confirm-btn:hover {
  background-color: #1976d2;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #d32f2f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .orders-container {
    padding: 10px;
  }
  
  h1 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    padding: 10px 15px;
  }
  
  .order-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .order-items {
    padding: 10px 15px;
  }
  
  .order-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .product-image {
    width: 100%;
    height: auto;
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .product-details {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
    padding: 15px;
  }
  
  .order-actions {
    justify-content: center;
  }
  
  .action-btn {
    flex: 1;
  }
}
</style>
