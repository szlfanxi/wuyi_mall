<template>
  <div class="cart-container">
    <h1>我的购物车</h1>

    <div v-if="loading">加载中...</div>

    <div v-else>
      <div v-if="items.length === 0" class="empty-cart">购物车空空如也~</div>

      <div v-else>
        <div class="cart-list">
          <div
            v-for="item in items"
            :key="item.productId"
            class="cart-item"
          >
            <img :src="item.productImage" alt="" v-if="item.productImage" />
            <div class="info">
              <h3>{{ item.productName }}</h3>
              <p>单价：￥{{ item.productPrice }}</p>
              <div>
                数量：
                <button @click="changeQuantity(item, item.quantity - 1)">-</button>
                <input v-model.number="item.quantity" @change="changeQuantity(item, item.quantity)" />
                <button @click="changeQuantity(item, item.quantity + 1)">+</button>
              </div>
              <p>小计：￥{{ item.totalPrice }}</p>
              <button @click="removeItem(item)">删除</button>
            </div>
          </div>
        </div>

        <div class="total">
          <div>总价：￥{{ totalAmount }}</div>
          <button @click="goCheckout">去结算</button>
        </div>
      </div>
    </div>

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
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'
import { useRouter } from 'vue-router'

const items = ref([])
const loading = ref(false)
const router = useRouter()

// 模态对话框相关
const showCheckoutModal = ref(false)
const formData = ref({
  name: '',
  phone: '',
  address: '',
  remark: ''
})
const errors = ref({})

const fetchCart = async () => {
  loading.value = true
  const res = await request.get('/api/cart/list')
  items.value = res
  loading.value = false
}

const totalAmount = computed(() => {
  return items.value.reduce((sum, item) => {
    return sum + Number(item.totalPrice || 0)
  }, 0)
})

const changeQuantity = async (item, newQty) => {
  if (newQty <= 0) {
    if (confirm('确定要删除这件商品吗？')) {
      await removeItem(item)
    }
    return
  }
  await request.post('/api/cart/update', null, {
    params: {
      productId: item.productId,
      quantity: newQty
    }
  })
  await fetchCart()
}

const removeItem = async (item) => {
  await request.post('/api/cart/remove', null, {
    params: {
      productId: item.productId
    }
  })
  await fetchCart()
}

const goCheckout = () => {
  // 显示结算模态对话框
  showCheckoutModal.value = true
}

const closeModal = () => {
  // 关闭模态对话框并重置表单
  showCheckoutModal.value = false
  resetForm()
}

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
      totalAmount: totalAmount.value,
      orderItems: items.value.map(item => ({
        productId: item.productId,
        productName: item.productName,
        productPrice: item.productPrice,
        quantity: item.quantity,
        totalPrice: item.totalPrice
      })),
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
    
    // 清空购物车
    await Promise.all(items.value.map(item => 
      request.post('/api/cart/remove', null, {
        params: {
          productId: item.productId
        }
      })
    ))
    
    // 重新获取购物车数据
    await fetchCart()
    
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

onMounted(fetchCart)
</script>

<style scoped>
/* 购物车容器 */
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 购物车标题 */
h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 28px;
}

/* 空购物车提示 */
.empty-cart {
  text-align: center;
  padding: 50px 0;
  color: #666;
  font-size: 18px;
}

/* 购物车列表 */
.cart-list {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 购物车商品项 */
.cart-item {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding: 15px 20px;
  transition: background-color 0.2s;
}

.cart-item:hover {
  background-color: #f9f9f9;
}

/* 商品图片 */
.cart-item img {
  width: 100px;
  height: 100px;
  margin-right: 20px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

/* 商品信息 */
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 商品名称 */
.info h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

/* 商品单价 */
.info p {
  margin: 0;
  font-size: 16px;
  color: #666;
}

/* 商品数量控制 */
.info div {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 数量按钮 */
.info button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background-color: white;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.info button:hover {
  background-color: #f0f0f0;
  border-color: #ccc;
}

/* 数量输入框 */
.info input {
  width: 50px;
  height: 30px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

/* 小计和删除按钮 */
.info p:nth-child(4) {
  font-weight: 600;
  color: #ff4444;
}

/* 删除按钮 */
.info button:last-child {
  width: auto;
  height: auto;
  padding: 6px 12px;
  background-color: #ff4444;
  color: white;
  border: none;
  font-size: 14px;
  font-weight: 500;
}

.info button:last-child:hover {
  background-color: #ff6666;
}

/* 总价和结算按钮 */
.total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

/* 总价文本 */
.total > div {
  font-size: 24px;
  font-weight: bold;
  color: #ff4444;
}

/* 结算按钮 */
.total button {
  padding: 12px 30px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.total button:hover {
  background-color: #3aa373;
  transform: translateY(-1px);
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
  .cart-container {
    padding: 0 10px;
  }
  
  h1 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    padding: 15px;
    gap: 15px;
  }
  
  .cart-item img {
    width: 80px;
    height: 80px;
    margin-right: 0;
  }
  
  .info {
    width: 100%;
  }
  
  .info h3 {
    font-size: 16px;
  }
  
  .info p {
    font-size: 14px;
  }
  
  .total {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
    padding: 15px;
  }
  
  .total > div {
    font-size: 20px;
    text-align: center;
  }
  
  .total button {
    width: 100%;
  }
  
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