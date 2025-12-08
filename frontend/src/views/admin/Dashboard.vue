<template>
  <div class="dashboard-container">
    <h2>仪表盘</h2>
    
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-content">
          <h3>商品总数</h3>
          <p class="stat-value">{{ productCount }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📋</div>
        <div class="stat-content">
          <h3>订单总数</h3>
          <p class="stat-value">{{ orderCount }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <h3>用户总数</h3>
          <p class="stat-value">{{ userCount }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <h3>总销售额</h3>
          <p class="stat-value">￥{{ totalSales }}</p>
        </div>
      </div>
    </div>
    
    <!-- 最近订单 -->
    <div class="recent-orders">
      <h3>最近订单</h3>
      <div class="orders-table-container">
        <table class="orders-table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>用户名</th>
              <th>订单金额</th>
              <th>订单状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in recentOrders" :key="order.id">
              <td>{{ order.orderNo }}</td>
              <td>{{ order.username }}</td>
              <td>￥{{ order.totalAmount }}</td>
              <td>
                <span :class="getStatusClass(order.status)">{{ order.statusText }}</span>
              </td>
              <td>{{ formatDate(order.createTime) }}</td>
              <td>
                <button class="view-btn">查看详情</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="recentOrders.length === 0" class="no-data">暂无订单数据</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

// 统计数据
const productCount = ref(0)
const orderCount = ref(0)
const userCount = ref(0)
const totalSales = ref(0)
const recentOrders = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    // 从API获取统计数据
    const statsResponse = await request.get('/api/admin/dashboard/stats')
    
    // 更新商品总数：直接从API响应中获取商品数量
    productCount.value = statsResponse.productCount
    
    // 更新订单总数
    orderCount.value = statsResponse.orderCount
    
    // 更新用户总数
    userCount.value = statsResponse.userCount
    
    // 更新总销售额
    totalSales.value = statsResponse.totalSales
    
    // 从API获取最近订单数据
    const ordersResponse = await request.get('/api/admin/dashboard/recent-orders')
    recentOrders.value = ordersResponse.map(order => ({
      id: order.id,
      orderNo: order.orderNo,
      username: order.username,
      totalAmount: order.totalAmount,
      status: order.status,
      statusText: order.statusText,
      createTime: order.createTime
    }))
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 失败时使用默认数据
    productCount.value = 0
    orderCount.value = 0
    userCount.value = 0
    totalSales.value = 0
    recentOrders.value = []
  }
}

// 根据订单状态获取样式类名
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

// 格式化日期
const formatDate = (dateTime) => {
  if (!dateTime) return ''
  // 处理后端返回的LocalDateTime格式（ISO 8601）
  const date = new Date(dateTime)
  // 确保日期有效
  if (isNaN(date.getTime())) {
    return ''
  }
  // 格式化日期为本地时间
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 组件挂载时获取数据
onMounted(fetchStats)
</script>

<style scoped>
.dashboard-container {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  margin: 0 0 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 36px;
}

.stat-content h3 {
  margin: 0;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.stat-value {
  margin: 5px 0 0 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

/* 最近订单 */
.recent-orders h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.orders-table-container {
  overflow-x: auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  background-color: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.orders-table td {
  color: #555;
}

/* 订单状态样式 */
.status-pending {
  color: #ff9800;
  font-weight: 600;
}

.status-paid {
  color: #4caf50;
  font-weight: 600;
}

.status-shipped {
  color: #2196f3;
  font-weight: 600;
}

.status-completed {
  color: #8bc34a;
  font-weight: 600;
}

.status-canceled {
  color: #f44336;
  font-weight: 600;
}

/* 操作按钮 */
.view-btn {
  padding: 6px 12px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.view-btn:hover {
  background-color: #3aa373;
}

/* 无数据提示 */
.no-data {
  padding: 30px;
  text-align: center;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .orders-table {
    font-size: 14px;
  }
  
  .orders-table th,
  .orders-table td {
    padding: 8px;
  }
}
</style>
