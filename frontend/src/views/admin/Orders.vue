<template>
  <div class="admin-orders">
    <h2>订单管理</h2>
    <div class="orders-toolbar">
      <div class="filter-box">
        <select v-model="statusFilter" @change="handleFilterChange">
          <option value="">全部状态</option>
          <option value="0">客户下单</option>
          <option value="1">商家确认</option>
          <option value="2">备货完成</option>
          <option value="3">开始发货</option>
          <option value="4">交易完成</option>
          <option value="5">已取消</option>
        </select>
        <input type="date" v-model="dateFilter" @change="handleFilterChange" />
        <button class="btn btn-filter" @click="handleFilterChange">筛选</button>
      </div>
      <div class="search-box">
        <input type="text" placeholder="搜索订单号或收货人姓名" v-model="searchQuery" @keyup.enter="handleSearch" />
        <button class="btn btn-search" @click="handleSearch">搜索</button>
      </div>
    </div>
    <div class="orders-table-container">
      <table class="orders-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>收货人姓名</th>
            <th>总金额</th>
            <th>订单状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="loading-row">
            <td colspan="6" class="loading-text">加载中...</td>
          </tr>
          <tr v-else-if="orders.length === 0" class="empty-row">
            <td colspan="6" class="empty-text">暂无订单数据</td>
          </tr>
          <tr v-else v-for="order in orders" :key="order.id">
            <td>{{ order.orderNo }}</td>
            <td>{{ order.username }}</td>
            <td>¥{{ parseFloat(order.totalAmount).toFixed(2) }}</td>
            <td>
              <span class="status-badge" :class="`status-${getStatusClass(order.status)}`">
                {{ getStatusText(order.status) }}
              </span>
            </td>
            <td>{{ formatDate(order.createTime) }}</td>
            <td>
              <button class="btn btn-small btn-view" @click="viewOrder(order.id)">
                查看
              </button>
              <!-- <button class="btn btn-small btn-edit" @click="editOrder(order.id)">
                确认交易
              </button> -->
              <button class="btn btn-small btn-delete" @click="deleteOrder(order.id)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-container" v-if="totalItems > 0">
      <div class="pagination-info">
        <span>共 {{ totalItems }} 条记录</span>
        <span>每页显示</span>
        <select v-model="pageSize" @change="handlePageSizeChange" class="page-size-selector">
          <option value="10">10</option>
          <option value="20">20</option>
          <option value="50">50</option>
        </select>
        <span>条</span>
      </div>
      
      <div class="pagination-controls">
        <button 
          class="btn btn-small" 
          @click="goToPage(1)" 
          :disabled="currentPage === 1 || loading"
        >
          首页
        </button>
        <button 
          class="btn btn-small" 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 1 || loading"
        >
          上一页
        </button>
        
        <!-- 页码按钮 -->
        <div class="page-numbers">
          <button 
            v-for="page in getVisiblePageNumbers()" 
            :key="page"
            class="btn btn-small page-number-btn"
            :class="{ 'active': page === currentPage }"
            @click="goToPage(page)"
            :disabled="loading"
          >
            {{ page }}
          </button>
        </div>
        
        <button 
          class="btn btn-small" 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage === totalPages || loading"
        >
          下一页
        </button>
        <button 
          class="btn btn-small" 
          @click="goToPage(totalPages)" 
          :disabled="currentPage === totalPages || loading"
        >
          末页
        </button>
        
        <!-- 跳转到指定页 -->
        <div class="jump-to-page">
          <span>跳转到</span>
          <input 
            type="number" 
            v-model.number="jumpPage" 
            min="1" 
            :max="totalPages" 
            class="jump-input"
          />
          <span>页</span>
          <button class="btn btn-small" @click="jumpToPage" :disabled="loading">
            确定
          </button>
        </div>
      </div>
      
      <div class="current-page-info">
        第 {{ currentPage }} / {{ totalPages }} 页
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()

const orders = ref([])
const searchQuery = ref('')
const statusFilter = ref('')
const dateFilter = ref('')
const loading = ref(false)

// Notification state
const notification = ref({
  show: false,
  message: '',
  type: 'success' // success or error
})

// Pagination state
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = ref(0)
const jumpPage = ref(1) // For jump to page functionality
const originalOrders = ref([]) // Store all orders for filtering and pagination

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/admin/orders/list')
    originalOrders.value = response
    // Reset to first page and apply filtering/pagination
    currentPage.value = 1
    jumpPage.value = 1
    filterOrders()
  } catch (error) {
    console.error('获取订单列表失败:', error)
    originalOrders.value = []
    orders.value = []
    totalItems.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

// 过滤订单并处理分页
const filterOrders = () => {
  let filteredOrders = originalOrders.value
  
  // 应用状态过滤
  if (statusFilter.value) {
    const status = parseInt(statusFilter.value)
    filteredOrders = filteredOrders.filter(order => order.status === status)
  }
  
  // 应用日期过滤
  if (dateFilter.value) {
    const filterDate = new Date(dateFilter.value)
    filteredOrders = filteredOrders.filter(order => {
      const orderDate = new Date(order.createTime)
      // 只比较年月日部分
      return orderDate.getFullYear() === filterDate.getFullYear() &&
             orderDate.getMonth() === filterDate.getMonth() &&
             orderDate.getDate() === filterDate.getDate()
    })
  }
  
  // 应用搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filteredOrders = filteredOrders.filter(order => {
      return order.orderNo.toLowerCase().includes(query) ||
             order.username.toLowerCase().includes(query)
    })
  }
  
  // 计算分页信息
  totalItems.value = filteredOrders.length
  totalPages.value = Math.ceil(totalItems.value / pageSize.value)
  
  // 确保当前页不超出范围
  if (currentPage.value > totalPages.value) {
    currentPage.value = Math.max(1, totalPages.value)
  }
  
  // 应用分页
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  orders.value = filteredOrders.slice(startIndex, endIndex)
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '客户下单',
    1: '商家确认',
    2: '备货完成',
    3: '开始发货',
    4: '交易完成',
    5: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态类名
const getStatusClass = (status) => {
  const classMap = {
    0: 'pending',
    1: 'confirmed',
    2: 'prepared',
    3: 'shipping',
    4: 'completed',
    5: 'cancelled'
  }
  return classMap[status] || 'unknown'
}

// 格式化日期
const formatDate = (date) => {
  if (!(date instanceof Date)) {
    date = new Date(date)
  }
  return date.toLocaleString('zh-CN')
}

// 查看订单
const viewOrder = (orderId) => {
  // 跳转到订单详情页
  router.push(`/admin/orders/${orderId}`)
}

// 编辑订单
// const editOrder = (orderId) => {
//   // 编辑订单逻辑
//   console.log('编辑订单:', orderId)
// }

// 删除订单
const deleteOrder = async (orderId) => {
  // 删除订单逻辑
  if (confirm('确定要删除该订单吗？')) {
    try {
      await request.post('/api/admin/orders/delete', { id: orderId })
      // 删除成功后刷新列表
      fetchOrders()
      // Show success notification
      showNotification('订单删除成功', 'success')
    } catch (error) {
      console.error('删除订单失败:', error)
      // Show error notification
      showNotification('删除订单失败，请稍后重试', 'error')
    }
  }
}

// 处理筛选变化
const handleFilterChange = () => {
  // Filter change resets to first page
  currentPage.value = 1
  jumpPage.value = 1
  filterOrders()
}

// 处理搜索
const handleSearch = () => {
  // Search resets to first page
  currentPage.value = 1
  jumpPage.value = 1
  filterOrders()
}

// 分页事件处理
const handlePageSizeChange = () => {
  // Changing page size resets to first page
  currentPage.value = 1
  jumpPage.value = 1
  filterOrders()
}

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value || page === currentPage.value) {
    return
  }
  currentPage.value = page
  jumpPage.value = page
  filterOrders()
}

const jumpToPage = () => {
  let page = jumpPage.value
  // Ensure page is within valid range
  page = Math.max(1, Math.min(page, totalPages.value))
  jumpPage.value = page
  goToPage(page)
}

// Show notification message
const showNotification = (message, type) => {
  notification.value.message = message
  notification.value.type = type
  notification.value.show = true
  
  // Auto hide after 2 seconds
  setTimeout(() => {
    notification.value.show = false
  }, 2000)
}

// Calculate visible page numbers (show max 7 pages)
const getVisiblePageNumbers = () => {
  const pageNumbers = []
  const maxVisiblePages = 7
  
  if (totalPages.value <= maxVisiblePages) {
    // Show all pages if there aren't many
    for (let i = 1; i <= totalPages.value; i++) {
      pageNumbers.push(i)
    }
  } else {
    // Show current page and surrounding pages
    const halfVisible = Math.floor(maxVisiblePages / 2)
    let startPage = currentPage.value - halfVisible
    let endPage = currentPage.value + halfVisible
    
    // Adjust start and end pages to stay within bounds
    if (startPage < 1) {
      startPage = 1
      endPage = maxVisiblePages
    }
    if (endPage > totalPages.value) {
      endPage = totalPages.value
      startPage = endPage - maxVisiblePages + 1
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pageNumbers.push(i)
    }
  }
  
  return pageNumbers
}

// 组件挂载时获取订单列表
onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-orders {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.orders-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-box {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-box select,
.filter-box input[type="date"] {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-box input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 250px;
}

.orders-table-container {
  overflow-x: auto;
}

.orders-table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th,
.orders-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.orders-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #333;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending {
  background-color: #fff3cd;
  color: #856404;
}

.status-confirmed {
  background-color: #d4edda;
  color: #155724;
}

.status-prepared {
  background-color: #cce5ff;
  color: #004085;
}

.status-shipping {
  background-color: #e2f0d9;
  color: #388e3c;
}

.status-completed {
  background-color: #d1ecf1;
  color: #0c5460;
}

.status-cancelled {
  background-color: #f8d7da;
  color: #721c24;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-filter {
  background-color: #6c757d;
  color: white;
}

.btn-filter:hover {
  background-color: #5a6268;
}

.btn-search {
  background-color: #6c757d;
  color: white;
}

.btn-search:hover {
  background-color: #5a6268;
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
  margin-right: 5px;
}

.btn-view {
  background-color: #17a2b8;
  color: white;
}

.btn-view:hover {
  background-color: #138496;
}

.btn-edit {
  background-color: #ffc107;
  color: #212529;
}

.btn-edit:hover {
  background-color: #e0a800;
}

.btn-delete {
  background-color: #dc3545;
  color: white;
}

.btn-delete:hover {
  background-color: #c82333;
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.pagination-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #666;
}

.page-size-selector {
  padding: 4px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-number-btn.active {
  background-color: #17a2b8;
  color: white;
}

.jump-to-page {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-left: 15px;
}

.jump-input {
  width: 60px;
  padding: 4px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.current-page-info {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .pagination-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .pagination-controls {
    flex-direction: column;
    gap: 10px;
  }
  
  .page-numbers {
    justify-content: center;
  }
  
  .jump-to-page {
    margin-left: 0;
    justify-content: center;
  }
}

/* Notification styles */
.notification {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 24px;
  border-radius: 4px;
  color: white;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  animation: slideInRight 0.3s ease, fadeOut 0.3s ease 1.7s;
  opacity: 1;
  transform: translateX(0);
}

.notification.success {
  background-color: #52c41a;
}

.notification.error {
  background-color: #ff4d4f;
}

/* Animation for notification */
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
    transform: translateX(100%);
  }
}
</style>