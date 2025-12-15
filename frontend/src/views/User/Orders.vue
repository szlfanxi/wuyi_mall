<template>
  <div class="orders-container">
    <h1>我的订单</h1>

    <!-- 搜索和排序栏 -->
    <div class="search-sort-bar">
      <!-- 搜索框 -->
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索订单号或商品名称" 
          class="search-input"
          @input="handleSearch"
        />
      </div>
      
      <!-- 状态筛选 -->
      <div class="status-filter">
        <label for="status-filter">订单状态：</label>
        <select id="status-filter" v-model="statusFilter" @change="handleFilterChange">
          <option value="">全部状态</option>
          <option value="0">客户下单</option>
          <option value="1">商家确认</option>
          <option value="2">备货完成</option>
          <option value="3">开始发货</option>
          <option value="4">交易完成</option>
          <option value="5">已取消</option>
        </select>
      </div>
      
      <!-- 排序选择 -->
      <div class="sort-select">
        <label for="sort-order">排序方式：</label>
        <select id="sort-order" v-model="sortOrder" @change="handleSort">
          <option value="desc">最新订单</option>
          <option value="asc">最早订单</option>
        </select>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="filteredOrders.length === 0" class="empty-orders">
        <p>暂无订单</p>
        <router-link to="/" class="go-shopping-btn">去购物</router-link>
      </div>
      
      <div v-else>
        <div v-for="order in paginatedOrders" :key="order.id" class="order-card">
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
                v-if="order.status === 0 || order.status === 1" 
                @click="cancelOrder(order.id)"
                class="action-btn cancel-btn"
              >
                取消订单
              </button>
              <button 
                v-if="order.status === 3" 
                @click="confirmReceipt(order.id)"
                class="action-btn confirm-btn"
              >
                确认收货
              </button>
              <button 
                v-if="order.status === 4 || order.status === 5" 
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
    
    <!-- 分页控件 -->
    <div v-if="totalPages > 1" class="pagination">
      <button 
        class="page-btn" 
        @click="goToFirstPage" 
        :disabled="currentPage === 1"
      >
        首页
      </button>
      <button 
        class="page-btn" 
        @click="goToPrevPage" 
        :disabled="currentPage === 1"
      >
        上一页
      </button>
      
      <span class="page-info">
        第 {{ currentPage }} / {{ totalPages }} 页
      </span>
      
      <button 
        class="page-btn" 
        @click="goToNextPage" 
        :disabled="currentPage === totalPages"
      >
        下一页
      </button>
      <button 
        class="page-btn" 
        @click="goToLastPage" 
        :disabled="currentPage === totalPages"
      >
        末页
      </button>
      
      <div class="page-size-selector">
        <label for="page-size">每页条数：</label>
        <select id="page-size" v-model.number="pageSize" @change="currentPage = 1">
          <option value="5">5条</option>
          <option value="10">10条</option>
          <option value="20">20条</option>
          <option value="50">50条</option>
        </select>
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

// 搜索和排序相关
const searchQuery = ref('')
const statusFilter = ref('') // 订单状态筛选
const sortOrder = ref('desc') // 默认降序（最新订单）
const filteredOrders = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10) // 默认每页10条
const totalPages = computed(() => Math.ceil(filteredOrders.value.length / pageSize.value))
const paginatedOrders = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  return filteredOrders.value.slice(startIndex, startIndex + pageSize.value)
})

// 处理排序
const handleSort = () => {
  sortOrders()
}

// 处理搜索
const handleSearch = () => {
  filterOrders()
  currentPage.value = 1 // 搜索后重置到第一页
}

// 排序订单
const sortOrders = () => {
  filteredOrders.value.sort((a, b) => {
    const dateA = new Date(a.createTime)
    const dateB = new Date(b.createTime)
    return sortOrder.value === 'desc' ? dateB - dateA : dateA - dateB
  })
}

// 筛选订单
const filterOrders = () => {
  let result = [...orders.value]
  
  // 状态筛选
  if (statusFilter.value) {
    const status = parseInt(statusFilter.value)
    result = result.filter(order => order.status === status)
  }
  
  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(order => {
      // 搜索订单号
      if (order.orderNo.toLowerCase().includes(query)) {
        return true
      }
      
      // 搜索商品名称
      return order.orderItems.some(item => 
        item.productName.toLowerCase().includes(query)
      )
    })
  }
  
  filteredOrders.value = result
  sortOrders() // 每次筛选后重新排序
}

// 处理筛选变化
const handleFilterChange = () => {
  currentPage.value = 1 // 筛选变化后重置到第一页
  filterOrders()
}

/**
 * 获取订单列表
 */
const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/orders')
    orders.value = res
    filterOrders() // 初始化筛选和排序
  } catch (error) {
    console.error('获取订单列表失败:', error)
    alert('获取订单列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 分页方法
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const goToFirstPage = () => {
  currentPage.value = 1
}

const goToLastPage = () => {
  currentPage.value = totalPages.value
}

const goToPrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
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
    case 0: return 'status-pending' // 客户下单
    case 1: return 'status-confirmed' // 商家确认
    case 2: return 'status-prepared' // 备货完成
    case 3: return 'status-shipping' // 开始发货
    case 4: return 'status-completed' // 交易完成
    case 5: return 'status-canceled' // 已取消
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
      alert(res.message || '订单已取消')
      fetchOrders() // 刷新订单列表
    } catch (error) {
      console.error('取消订单失败:', error)
      // 尝试获取后端返回的具体错误消息
      const errorMessage = error.response?.data?.message || '取消订单失败，请稍后重试'
      alert(errorMessage)
    }
  }
}

/**
 * 支付订单功能已整合到新的状态流程中，不再需要单独的支付按钮和方法
 * @param {number} orderId 订单ID
 */
// const payOrder = async (orderId) => {
//   try {
//     const res = await request.post(`/api/orders/${orderId}/pay`)
//     alert(res.data.message || '支付成功')
//     fetchOrders() // 刷新订单列表
//   } catch (error) {
//     console.error('支付订单失败:', error)
//     alert('支付订单失败，请稍后重试')
//   }
// }

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

.status-confirmed {
  color: #2196f3;
}

.status-prepared {
  color: #4caf50;
}

.status-shipping {
  color: #8bc34a;
}

.status-completed {
  color: #9c27b0;
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

/* 搜索和排序栏 */
.search-sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
  gap: 15px;
}

/* 状态筛选 */
.status-filter {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.status-filter label {
  font-weight: 500;
  color: #333;
}

.status-filter select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.status-filter select:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 2px rgba(66, 184, 131, 0.2);
}

/* 搜索框 */
.search-box {
  flex: 1;
  max-width: 400px;
  margin-right: 20px;
}

.search-input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 2px rgba(66, 184, 131, 0.2);
}

/* 排序选择 */
.sort-select {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.sort-select select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.sort-select select:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 2px rgba(66, 184, 131, 0.2);
}

/* 分页控件 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.page-btn {
  padding: 8px 16px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: #e0e0e0;
  border-color: #ccc;
}

.page-btn:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
  border-color: #eee;
}

.page-info {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.page-size-selector select {
  padding: 6px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-size-selector select:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 2px rgba(66, 184, 131, 0.2);
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
  
  /* 搜索和排序栏响应式 */
  .search-sort-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
    padding: 15px;
  }
  
  .search-box {
    max-width: none;
    margin-right: 0;
  }
  
  /* 分页控件响应式 */
  .pagination {
    flex-wrap: wrap;
    gap: 10px;
    padding: 10px;
  }
  
  .page-btn {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  .page-size-selector {
    flex-basis: 100%;
    justify-content: center;
  }
  
  /* 订单卡片响应式 */
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
