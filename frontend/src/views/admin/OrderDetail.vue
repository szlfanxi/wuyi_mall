<template>
  <div class="order-detail-container">
    <h2>订单详情</h2>
    
    <div v-if="loading" class="loading">加载中...</div>
    
    <div v-else-if="!order" class="error-message">
      <p>订单不存在或已被删除</p>
      <button class="btn btn-primary" @click="goBack">返回订单列表</button>
    </div>
    
    <div v-else class="order-detail">
      <!-- 订单基本信息 -->
      <div class="order-info-section">
        <h3>订单信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>订单号：</label>
            <span>{{ order.orderNo }}</span>
          </div>
          <div class="info-item">
            <label>订单状态：</label>
            <span class="status-badge" :class="`status-${getStatusClass(order.status)}`">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          <div class="info-item">
            <label>创建时间：</label>
            <span>{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="info-item">
            <label>总金额：</label>
            <span class="total-amount">¥{{ parseFloat(order.totalAmount).toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 收货人信息 -->
      <div class="receiver-info-section">
        <h3>收货人信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>收货人姓名：</label>
            <span>{{ order.username }}</span>
          </div>
          <div class="info-item">
            <label>手机号码：</label>
            <span>{{ order.orderItems[0]?.receivePhone || '未知' }}</span>
          </div>
          <div class="info-item full-width">
            <label>收货地址：</label>
            <span>{{ order.orderItems[0]?.receiveAddress || '未知' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 订单商品列表 -->
      <div class="order-items-section">
        <h3>订单商品</h3>
        <div class="order-items-table-container">
          <table class="order-items-table">
            <thead>
              <tr>
                <th>商品信息</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in order.orderItems" :key="item.productId">
                <td>
                  <div class="product-info-cell">
                    <img v-if="item.productImage" :src="item.productImage" alt="商品图片" class="product-image" />
                    <span class="product-name">{{ item.productName }}</span>
                  </div>
                </td>
                <td>¥{{ parseFloat(item.productPrice).toFixed(2) }}</td>
                <td>{{ item.quantity }}</td>
                <td>¥{{ parseFloat(item.totalPrice).toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="btn btn-primary" @click="goBack">返回订单列表</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../../utils/request'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const loading = ref(false)

// 获取订单ID
const orderId = ref(route.params.id)

// 获取订单详情
const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const response = await request.get(`/api/admin/orders/${orderId.value}`)
    order.value = response
  } catch (error) {
    console.error('获取订单详情失败:', error)
    order.value = null
  } finally {
    loading.value = false
  }
}

// 返回订单列表
const goBack = () => {
  router.push('/admin/orders')
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '已付款',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态类名
const getStatusClass = (status) => {
  const classMap = {
    0: 'pending',
    1: 'paid',
    2: 'shipped',
    3: 'delivered',
    4: 'cancelled'
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

// 组件挂载时获取订单详情
onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin: 20px;
}

h2 {
  color: #333;
  margin-bottom: 20px;
}

h3 {
  color: #555;
  margin-bottom: 15px;
  font-size: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.loading, .error-message {
  text-align: center;
  padding: 50px 0;
  color: #666;
}

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.info-item span {
  font-size: 15px;
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

.status-paid {
  background-color: #d4edda;
  color: #155724;
}

.status-shipped {
  background-color: #cce5ff;
  color: #004085;
}

.status-delivered {
  background-color: #e2f0d9;
  color: #388e3c;
}

.status-cancelled {
  background-color: #f8d7da;
  color: #721c24;
}

.total-amount {
  font-size: 18px;
  font-weight: 600;
  color: #dc3545;
}

.order-info-section,
.receiver-info-section,
.order-items-section {
  background-color: #f9f9f9;
  border-radius: 6px;
  padding: 15px;
}

.order-items-table-container {
  overflow-x: auto;
}

.order-items-table {
  width: 100%;
  border-collapse: collapse;
}

.order-items-table th,
.order-items-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.order-items-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #333;
}

.product-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

.product-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
}

.action-buttons {
  display: flex;
  justify-content: flex-start;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}
</style>