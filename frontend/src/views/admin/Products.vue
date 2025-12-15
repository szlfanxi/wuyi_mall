<template>
  <div class="admin-products">
    <h2>商品管理</h2>
    <div class="products-toolbar">
      <button class="btn btn-primary" @click="showAddModal">添加商品</button>
      <div class="search-box">
        <input type="text" placeholder="搜索商品" v-model="searchQuery" />
        <button class="btn btn-search" @click="handleSearch">搜索</button>
      </div>
    </div>
    <div class="products-table-container">
      <table class="products-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名称</th>
            <th>价格</th>
            <th>库存</th>
            <th>分类</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="loading-row">
            <td colspan="7" class="loading-text">加载中...</td>
          </tr>
          <tr v-else-if="products.length === 0" class="empty-row">
            <td colspan="7" class="empty-text">暂无商品数据</td>
          </tr>
          <tr v-else v-for="product in products" :key="product.id">
            <td>{{ product.id }}</td>
            <td>{{ product.name }}</td>
            <td>¥{{ parseFloat(product.price).toFixed(2) }}</td>
            <td>{{ product.stock }}</td>
            <td>{{ product.categoryName }}</td>
            <td>
              <span class="status-badge" :class="{ 'status-active': product.status === 1, 'status-inactive': product.status === 0 }">
                {{ product.status === 1 ? '上架' : '下架' }}
              </span>
            </td>
            <td>
              <button class="btn btn-small btn-edit" @click="showEditModal(product)">编辑</button>
              <button class="btn btn-small btn-delete" @click="deleteProduct(product.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 编辑商品模态框 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑商品</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitEdit">
            <!-- 商品名称 -->
            <div class="form-group">
              <label for="edit-name">商品名称</label>
              <input 
                type="text" 
                id="edit-name" 
                v-model="editForm.name" 
                placeholder="请输入商品名称" 
                required
                :class="{ 'is-invalid': errors.name }"
              />
              <div class="invalid-feedback" v-if="errors.name">{{ errors.name }}</div>
            </div>

            <!-- 价格 -->
            <div class="form-group">
              <label for="edit-price">价格</label>
              <input 
                type="number" 
                id="edit-price" 
                v-model.number="editForm.price" 
                placeholder="请输入价格" 
                min="0" 
                step="0.01"
                required
                :class="{ 'is-invalid': errors.price }"
              />
              <div class="invalid-feedback" v-if="errors.price">{{ errors.price }}</div>
            </div>

            <!-- 库存 -->
            <div class="form-group">
              <label for="edit-stock">库存</label>
              <input 
                type="number" 
                id="edit-stock" 
                v-model.number="editForm.stock" 
                placeholder="请输入库存" 
                min="0"
                required
                :class="{ 'is-invalid': errors.stock }"
              />
              <div class="invalid-feedback" v-if="errors.stock">{{ errors.stock }}</div>
            </div>

            <!-- 分类 -->
            <div class="form-group">
              <label for="edit-categoryId">分类</label>
              <select 
                id="edit-categoryId" 
                v-model.number="editForm.categoryId" 
                required
                :class="{ 'is-invalid': errors.categoryId }"
              >
                <option value="0">请选择分类</option>
                <option v-for="category in displayCategories" :key="category.id" :value="category.id">
                  {{ category.displayName }}
                </option>
              </select>
              <div class="invalid-feedback" v-if="errors.categoryId">{{ errors.categoryId }}</div>
            </div>

            <!-- 状态 -->
            <div class="form-group">
              <label for="edit-status">状态</label>
              <select 
                id="edit-status" 
                v-model.number="editForm.status" 
                required
                :class="{ 'is-invalid': errors.status }"
              >
                <option value="0">下架</option>
                <option value="1">上架</option>
              </select>
              <div class="invalid-feedback" v-if="errors.status">{{ errors.status }}</div>
            </div>

            <!-- 商品描述 -->
            <div class="form-group">
              <label for="edit-description">商品描述</label>
              <textarea 
                id="edit-description" 
                v-model="editForm.description" 
                placeholder="请输入商品描述"
                rows="4"
                :class="{ 'is-invalid': errors.description }"
              ></textarea>
              <div class="invalid-feedback" v-if="errors.description">{{ errors.description }}</div>
            </div>

            <!-- 主图URL -->
            <div class="form-group">
              <label for="edit-mainImage">主图URL</label>
              <input 
                type="url" 
                id="edit-mainImage" 
                v-model="editForm.mainImage" 
                placeholder="请输入主图URL"
                :class="{ 'is-invalid': errors.mainImage }"
              />
              <div class="invalid-feedback" v-if="errors.mainImage">{{ errors.mainImage }}</div>
            </div>

            <!-- 商品图片上传 -->
            <div class="form-group">
              <label>商品图片</label>
              <div class="image-upload-container">
                <!-- 图片预览区域 -->
                <div class="image-preview-grid">
                  <div 
                    v-for="(image, index) in uploadedImages" 
                    :key="index" 
                    class="image-preview-item"
                  >
                    <div class="image-preview" :style="{ backgroundImage: `url(${image.url})` }"></div>
                    <button 
                      type="button" 
                      class="image-delete-btn" 
                      @click="removeImage(index)"
                    >
                      ×
                    </button>
                  </div>
                  
                  <!-- 添加图片按钮 -->
                  <div class="image-add-item" @click="triggerImageUpload">
                    <input 
                      ref="imageUploadInput" 
                      type="file" 
                      multiple 
                      accept="image/*" 
                      @change="handleImageChange" 
                      style="display: none"
                    />
                    <div class="image-add-icon">+</div>
                    <div class="image-add-text">添加图片</div>
                  </div>
                </div>
                
                <!-- 上传状态提示 -->
                <div class="upload-status" v-if="uploadingImages">
                  <span class="loading-text">图片上传中...</span>
                </div>
                <div class="upload-status error" v-if="uploadError">
                  <span class="error-text">{{ uploadError }}</span>
                </div>
              </div>
            </div>

            <!-- 按钮组 -->
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="submitting">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 添加商品模态框 -->
    <div v-if="showAddModalFlag" class="modal-overlay" @click="closeAddModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加商品</h3>
          <button class="close-btn" @click="closeAddModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitAdd">
            <!-- 商品名称 -->
            <div class="form-group">
              <label for="add-name">商品名称</label>
              <input 
                type="text" 
                id="add-name" 
                v-model="addForm.name" 
                placeholder="请输入商品名称" 
                required
                :class="{ 'is-invalid': addErrors.name }"
              />
              <div class="invalid-feedback" v-if="addErrors.name">{{ addErrors.name }}</div>
            </div>

            <!-- 价格 -->
            <div class="form-group">
              <label for="add-price">价格</label>
              <input 
                type="number" 
                id="add-price" 
                v-model.number="addForm.price" 
                placeholder="请输入价格" 
                min="0" 
                step="0.01"
                required
                :class="{ 'is-invalid': addErrors.price }"
              />
              <div class="invalid-feedback" v-if="addErrors.price">{{ addErrors.price }}</div>
            </div>

            <!-- 库存 -->
            <div class="form-group">
              <label for="add-stock">库存</label>
              <input 
                type="number" 
                id="add-stock" 
                v-model.number="addForm.stock" 
                placeholder="请输入库存" 
                min="0"
                required
                :class="{ 'is-invalid': addErrors.stock }"
              />
              <div class="invalid-feedback" v-if="addErrors.stock">{{ addErrors.stock }}</div>
            </div>

            <!-- 分类 -->
            <div class="form-group">
              <label for="add-categoryId">分类</label>
              <select 
                id="add-categoryId" 
                v-model.number="addForm.categoryId" 
                required
                :class="{ 'is-invalid': addErrors.categoryId }"
              >
                <option value="0">请选择分类</option>
                <option v-for="category in displayCategories" :key="category.id" :value="category.id">
                  {{ category.displayName }}
                </option>
              </select>
              <div class="invalid-feedback" v-if="addErrors.categoryId">{{ addErrors.categoryId }}</div>
            </div>

            <!-- 状态 -->
            <div class="form-group">
              <label for="add-status">状态</label>
              <select 
                id="add-status" 
                v-model.number="addForm.status" 
                required
                :class="{ 'is-invalid': addErrors.status }"
              >
                <option value="0">下架</option>
                <option value="1">上架</option>
              </select>
              <div class="invalid-feedback" v-if="addErrors.status">{{ addErrors.status }}</div>
            </div>

            <!-- 商品描述 -->
            <div class="form-group">
              <label for="add-description">商品描述</label>
              <textarea 
                id="add-description" 
                v-model="addForm.description" 
                placeholder="请输入商品描述"
                rows="4"
                :class="{ 'is-invalid': addErrors.description }"
              ></textarea>
              <div class="invalid-feedback" v-if="addErrors.description">{{ addErrors.description }}</div>
            </div>

            <!-- 主图URL -->
            <div class="form-group">
              <label for="add-mainImage">主图URL</label>
              <input 
                type="url" 
                id="add-mainImage" 
                v-model="addForm.mainImage" 
                placeholder="请输入主图URL"
                :class="{ 'is-invalid': addErrors.mainImage }"
              />
              <div class="invalid-feedback" v-if="addErrors.mainImage">{{ addErrors.mainImage }}</div>
            </div>

            <!-- 商品图片上传 -->
            <div class="form-group">
              <label>商品图片</label>
              <div class="image-upload-container">
                <!-- 图片预览区域 -->
                <div class="image-preview-grid">
                  <div 
                    v-for="(image, index) in uploadedImages" 
                    :key="index" 
                    class="image-preview-item"
                  >
                    <div class="image-preview" :style="{ backgroundImage: `url(${image.url})` }"></div>
                    <button 
                      type="button" 
                      class="image-delete-btn" 
                      @click="removeImage(index)"
                    >
                      ×
                    </button>
                  </div>
                  
                  <!-- 添加图片按钮 -->
                  <div class="image-add-item" @click="triggerImageUpload">
                    <input 
                      ref="imageUploadInput" 
                      type="file" 
                      multiple 
                      accept="image/*" 
                      @change="handleImageChange" 
                      style="display: none"
                    />
                    <div class="image-add-icon">+</div>
                    <div class="image-add-text">添加图片</div>
                  </div>
                </div>
                
                <!-- 上传状态提示 -->
                <div class="upload-status" v-if="uploadingImages">
                  <span class="loading-text">图片上传中...</span>
                </div>
                <div class="upload-status error" v-if="uploadError">
                  <span class="error-text">{{ uploadError }}</span>
                </div>
              </div>
            </div>

            <!-- 按钮组 -->
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="closeAddModal">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="submitting">保存</button>
            </div>
          </form>
        </div>
      </div>
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '../../utils/request'

const products = ref([])
const categories = ref([]) // 所有分类（平铺结构）
const searchQuery = ref('')
const loading = ref(false)
const originalProducts = ref([])

// Pagination state
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = ref(0)
const jumpPage = ref(1) // For jump to page functionality

// 编辑模态框相关
const showModal = ref(false)
const submitting = ref(false)
const editForm = ref({
  id: null,
  name: '',
  price: 0,
  stock: 0,
  categoryId: 0,
  status: 0,
  description: '',
  mainImage: ''
})
const errors = ref({})

// 添加模态框相关
const showAddModalFlag = ref(false)
const addForm = ref({
  name: '',
  price: 0,
  stock: 0,
  categoryId: 0,
  status: 0,
  description: '',
  mainImage: ''
})
const addErrors = ref({})

// 图片上传相关状态
const uploadedImages = ref([])
const uploadingImages = ref(false)
const uploadError = ref('')
const imageUploadInput = ref(null)

// 分类数据处理
const categoryTree = ref([]) // 分类树形结构（用于显示层级关系）
const displayCategories = ref([]) // 用于显示的分类列表，包含层级关系

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    console.log('开始获取商品列表，请求URL:', '/api/admin/products/list')
    console.log('当前token:', localStorage.getItem('token'))
    const response = await request.get('/api/admin/products/list')
    console.log('获取商品列表成功，原始响应:', response)
    
    // 处理统一响应格式
    if (response && response.success === true) {
      // 确保data是数组
      if (Array.isArray(response.data)) {
        originalProducts.value = response.data
        console.log('已更新originalProducts，共', originalProducts.value.length, '个商品')
        console.log('originalProducts详情:', originalProducts.value)
      } else {
        console.error('获取商品列表失败: data不是数组', response.data)
        originalProducts.value = []
      }
    } else {
      console.error('获取商品列表失败:', response?.message || '未知错误')
      originalProducts.value = []
    }
    
    // 重置到第一页并应用分页
    currentPage.value = 1
    console.log('准备调用filterProducts，currentPage:', currentPage.value, 'pageSize:', pageSize.value)
    filterProducts()
  } catch (error) {
    console.error('获取商品列表失败:', error)
    console.error('错误名称:', error.name)
    console.error('错误消息:', error.message)
    console.error('错误响应:', error.response)
    console.error('错误响应数据:', error.response?.data)
    console.error('错误响应状态:', error.response?.status)
    console.error('错误请求配置:', error.config)
    originalProducts.value = []
    products.value = []
    totalItems.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
    console.log('fetchProducts完成，loading:', loading.value)
  }
}

// 过滤商品并处理分页
const filterProducts = () => {
  console.log('开始filterProducts，originalProducts.length:', originalProducts.value.length, 'searchQuery:', searchQuery.value)
  
  let filteredProducts = originalProducts.value
  
  // 应用搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filteredProducts = originalProducts.value.filter(product => {
      const match = product.name.toLowerCase().includes(query)
      console.log('商品', product.name, '是否匹配搜索条件:', match)
      return match
    })
    console.log('搜索过滤后，filteredProducts.length:', filteredProducts.length)
  }
  
  // 计算分页信息
  totalItems.value = filteredProducts.length
  totalPages.value = Math.ceil(totalItems.value / pageSize.value)
  console.log('计算分页信息，totalItems:', totalItems.value, 'totalPages:', totalPages.value, 'pageSize:', pageSize.value)
  
  // 确保当前页不超出范围
  if (currentPage.value > totalPages.value) {
    currentPage.value = Math.max(1, totalPages.value)
    console.log('调整currentPage为:', currentPage.value)
  }
  
  // 应用分页
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  console.log('应用分页，startIndex:', startIndex, 'endIndex:', endIndex)
  const paginatedProducts = filteredProducts.slice(startIndex, endIndex)
  console.log('分页后，products.length:', paginatedProducts.length)
  
  // 更新products状态
  products.value = paginatedProducts
  console.log('已更新products，当前页显示', products.value.length, '个商品')
}

// 处理搜索
const handleSearch = () => {
  // 搜索时重置到第一页
  currentPage.value = 1
  filterProducts()
}

// 分页事件处理
const handlePageSizeChange = () => {
  // 改变每页显示数量时重置到第一页
  currentPage.value = 1
  jumpPage.value = 1
  filterProducts()
}

const goToPage = (page) => {
  if (page < 1 || page > totalPages.value || page === currentPage) {
    return
  }
  currentPage.value = page
  jumpPage.value = page
  filterProducts()
}

const jumpToPage = () => {
  let page = jumpPage.value
  // 确保页码在有效范围内
  page = Math.max(1, Math.min(page, totalPages.value))
  jumpPage.value = page
  goToPage(page)
}

// 计算可见页码（最多显示7个页码）
const getVisiblePageNumbers = () => {
  const pageNumbers = []
  const maxVisiblePages = 7
  
  if (totalPages.value <= maxVisiblePages) {
    // 总页数较少，显示所有页码
    for (let i = 1; i <= totalPages.value; i++) {
      pageNumbers.push(i)
    }
  } else {
    // 总页数较多，显示当前页附近的页码
    const halfVisible = Math.floor(maxVisiblePages / 2)
    let startPage = currentPage.value - halfVisible
    let endPage = currentPage.value + halfVisible
    
    // 调整起始和结束页码，确保不超出范围
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

// 获取分类列表（支持二级类目）
const fetchCategories = async () => {
  try {
    // 获取所有分类（平铺结构，用于下拉选择）
    const response = await request.get('/api/categories')
    categories.value = response
    
    // 生成用于显示的分类列表，包含层级关系
    generateDisplayCategories()
  } catch (error) {
    console.error('获取分类列表失败:', error)
    categories.value = []
    displayCategories.value = []
  }
}

// 生成带有层级关系的分类列表
const generateDisplayCategories = () => {
  // 创建分类映射，方便查找
  const categoryMap = {};
  categories.value.forEach(category => {
    categoryMap[category.id] = category;
  });
  
  // 生成显示用的分类列表，包含层级关系
  const displayList = [];
  categories.value.forEach(category => {
    // 只处理二级分类，因为商品应该属于具体的二级分类
    if (category.parentId === null) {
      // 一级分类，添加到列表中
      displayList.push({
        ...category,
        displayName: category.name
      });
      
      // 添加该一级分类下的所有二级分类
      categories.value.forEach(subCategory => {
        if (subCategory.parentId === category.id) {
          displayList.push({
            ...subCategory,
            displayName: '└── ' + subCategory.name
          });
        }
      });
    }
  });
  
  displayCategories.value = displayList;
}

// 图片上传相关方法
// 触发图片上传
const triggerImageUpload = () => {
  imageUploadInput.value?.click()
}

// 处理图片选择
const handleImageChange = async (event) => {
  const files = event.target.files
  if (!files || files.length === 0) return
  
  try {
    uploadingImages.value = true
    uploadError.value = ''
    
    // 处理每个选中的文件
    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      
      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        uploadError.value = '请选择图片文件'
        continue
      }
      
      // 验证文件大小（限制为2MB）
      if (file.size > 2 * 1024 * 1024) {
        uploadError.value = '图片大小不能超过2MB'
        continue
      }
      
      // 创建临时URL用于预览
      const previewUrl = URL.createObjectURL(file)
      
      // 上传图片到服务器
      const formData = new FormData()
      formData.append('file', file)
      
      const response = await request.post('/api/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      
      // 处理上传成功的响应
      if (response && response.success === true) {
        uploadedImages.value.push({
          url: response.data.url,
          file: file
        })
      } else {
        uploadError.value = response?.message || '图片上传失败'
      }
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    uploadError.value = '图片上传失败，请稍后重试'
  } finally {
    uploadingImages.value = false
    // 清空文件输入，允许重复选择同一文件
    event.target.value = ''
  }
}

// 删除图片
const removeImage = async (index) => {
  const image = uploadedImages.value[index]
  
  // 如果是已有图片，调用后端API删除
  if (image.isExisting) {
    try {
      // 只有在编辑模态框中才有已有图片，所以使用editForm.value.id
      await request.post('/api/admin/products/images/delete', {
        productId: editForm.value.id,
        imageUrl: image.url
      })
    } catch (error) {
      console.error('删除图片失败:', error)
      console.error('错误详情:', error.response?.data, error.response?.status)
      alert('删除图片失败，请稍后重试')
      return
    }
  }
  
  // 从前端数组中删除图片
  uploadedImages.value.splice(index, 1)
}

// 清空上传的图片
const clearUploadedImages = () => {
  uploadedImages.value = []
  uploadError.value = ''
}

// 显示编辑模态框
const showEditModal = (product) => {
  // 重置表单和错误信息
  errors.value = {}
  clearUploadedImages()
  
  // 填充表单数据
  editForm.value = {
    id: product.id,
    name: product.name,
    price: parseFloat(product.price),
    stock: product.stock,
    categoryId: product.categoryId,
    status: product.status,
    description: product.description || '',
    mainImage: product.mainImage || ''
  }
  
  // 加载已有的商品图片
  if (product.images && product.images.length > 0) {
    product.images.forEach(imageUrl => {
      uploadedImages.value.push({
        url: imageUrl,
        isExisting: true // 标记为已有图片
      })
    })
  }
  
  // 显示模态框
  showModal.value = true
}

// 关闭编辑模态框
const closeModal = () => {
  showModal.value = false
}

// 显示添加模态框
const showAddModal = () => {
  // 重置表单和错误信息
  addErrors.value = {}
  clearUploadedImages()
  addForm.value = {
    name: '',
    price: 0,
    stock: 0,
    categoryId: 0,
    status: 0,
    description: '',
    mainImage: ''
  }
  showAddModalFlag.value = true
}

// 关闭添加模态框
const closeAddModal = () => {
  showAddModalFlag.value = false
}

// 编辑表单验证
const validateForm = () => {
  const newErrors = {}
  
  // 商品名称验证
  if (!editForm.value.name.trim()) {
    newErrors.name = '商品名称不能为空'
  } else if (editForm.value.name.length > 100) {
    newErrors.name = '商品名称不能超过100个字符'
  }
  
  // 价格验证
  if (isNaN(editForm.value.price) || editForm.value.price < 0) {
    newErrors.price = '价格必须大于等于0'
  }
  
  // 库存验证
  if (isNaN(editForm.value.stock) || editForm.value.stock < 0) {
    newErrors.stock = '库存必须大于等于0'
  }
  
  // 分类ID验证
  if (isNaN(editForm.value.categoryId) || editForm.value.categoryId <= 0) {
    newErrors.categoryId = '请选择分类'
  }
  
  // 状态验证
  if (editForm.value.status !== 0 && editForm.value.status !== 1) {
    newErrors.status = '状态只能是0或1'
  }
  
  errors.value = newErrors
  return Object.keys(newErrors).length === 0
}

// 添加表单验证
const validateAddForm = () => {
  const newErrors = {}
  
  // 商品名称验证
  if (!addForm.value.name.trim()) {
    newErrors.name = '商品名称不能为空'
  } else if (addForm.value.name.length > 100) {
    newErrors.name = '商品名称不能超过100个字符'
  }
  
  // 价格验证
  if (isNaN(addForm.value.price) || addForm.value.price < 0) {
    newErrors.price = '价格必须大于等于0'
  }
  
  // 库存验证
  if (isNaN(addForm.value.stock) || addForm.value.stock < 0) {
    newErrors.stock = '库存必须大于等于0'
  }
  
  // 分类ID验证
  if (isNaN(addForm.value.categoryId) || addForm.value.categoryId <= 0) {
    newErrors.categoryId = '请选择分类'
  }
  
  // 状态验证
  if (addForm.value.status !== 0 && addForm.value.status !== 1) {
    newErrors.status = '状态只能是0或1'
  }
  
  addErrors.value = newErrors
  return Object.keys(newErrors).length === 0
}

// 提交编辑
const submitEdit = async () => {
  // 表单验证
  if (!validateForm()) {
    return
  }
  
  submitting.value = true
  try {
    const response = await request.post('/api/admin/products/update', editForm.value)
    
    // 处理统一响应格式
    if (response && response.success === true) {
      // 如果有上传的图片，将图片与商品关联
      if (uploadedImages.value.length > 0) {
        const productId = editForm.value.id
        const imageUrls = uploadedImages.value.map(img => img.url)
        
        await request.post('/api/admin/products/images', {
          productId,
          imageUrls
        })
      }
      
      // 更新成功，刷新商品列表
      await fetchProducts()
      
      // 关闭模态框
      closeModal()
      
      // 显示成功提示
      alert(response.message || '商品编辑成功')
    } else {
      throw new Error(response?.message || '编辑失败')
    }
  } catch (error) {
    console.error('编辑商品失败:', error)
    alert('编辑商品失败，请稍后重试: ' + (error.message || ''))
  } finally {
    submitting.value = false
  }
}

// 提交添加商品
const submitAdd = async () => {
  // 表单验证
  if (!validateAddForm()) {
    return
  }
  
  submitting.value = true
  try {
    console.log('开始添加商品:', addForm.value)
    const response = await request.post('/api/admin/products/create', addForm.value)
    console.log('添加商品成功，响应:', response)
    
    // 处理统一响应格式
    if (response && response.success === true) {
      // 如果有上传的图片，将图片与商品关联
      if (uploadedImages.value.length > 0) {
        const productId = response.data.id
        const imageUrls = uploadedImages.value.map(img => img.url)
        
        await request.post('/api/admin/products/images', {
          productId,
          imageUrls
        })
      }
      
      // 清除搜索条件，确保新添加的商品能显示出来
      searchQuery.value = ''
      // 添加成功，刷新商品列表
      await fetchProducts()
      
      // 关闭模态框
      closeAddModal()
      
      // 显示成功提示
      alert(response.message || '商品添加成功')
      console.log('商品添加流程完成，已刷新列表')
    } else {
      throw new Error(response?.message || '添加失败')
    }
  } catch (error) {
    console.error('添加商品失败:', error)
    console.error('错误详情:', error.response?.data, error.response?.status)
    alert('添加商品失败，请稍后重试: ' + (error.message || ''))
  } finally {
    submitting.value = false
  }
}

// 删除商品
const deleteProduct = async (productId) => {
  if (confirm('确定要删除该商品吗？')) {
    try {
      const response = await request.post(`/api/admin/products/delete?id=${productId}`)
      
      // 处理统一响应格式
      if (response && response.success === true) {
        // 删除成功后刷新列表
        fetchProducts()
        // 显示成功提示
        alert(response.message || '商品删除成功')
      } else {
        throw new Error(response?.message || '删除失败')
      }
    } catch (error) {
      console.error('删除商品失败:', error)
      alert('删除商品失败，请稍后重试: ' + (error.message || ''))
    }
  }
}

// 监听products变化，调试用
watch(products, (newProducts, oldProducts) => {
  console.log('products变化:', { oldLength: oldProducts?.length || 0, newLength: newProducts.length, newProducts })
}, { deep: true })

// 监听originalProducts变化，调试用
watch(originalProducts, (newOriginal, oldOriginal) => {
  console.log('originalProducts变化:', { oldLength: oldOriginal?.length || 0, newLength: newOriginal.length })
}, { deep: true })

// 组件挂载时获取数据
onMounted(async () => {
  console.log('组件挂载，开始获取初始数据')
  await Promise.all([
    fetchCategories(),
    fetchProducts()
  ])
  console.log('初始数据获取完成，products.length:', products.value.length)
})
</script>

<style scoped>
.admin-products {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.products-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.products-table-container {
  overflow-x: auto;
}

.products-table {
  width: 100%;
  border-collapse: collapse;
}

.products-table th,
.products-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.products-table th {
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

.status-active {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.status-inactive {
  background-color: #ffebee;
  color: #c62828;
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
  background-color: #42b883;
  color: white;
}

.btn-primary:hover {
  background-color: #369f6e;
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

.btn-edit {
  background-color: #17a2b8;
  color: white;
}

.btn-edit:hover {
  background-color: #138496;
}

.btn-delete {
  background-color: #dc3545;
  color: white;
}

.btn-delete:hover {
  background-color: #c82333;
}

/* 加载状态 */
.loading-row {
  text-align: center;
}

.loading-text {
  padding: 30px;
  color: #666;
  font-style: italic;
}

/* 空数据状态 */
.empty-row {
  text-align: center;
}

.empty-text {
  padding: 30px;
  color: #999;
  font-style: italic;
}

/* 模态框样式 - 拟态设计 */
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
  border-radius: 16px;
  box-shadow: 
    8px 8px 16px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalFadeIn 0.3s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(145deg, #ffffff, #f0f0f0);
  border-radius: 16px 16px 0 0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.close-btn:hover {
  background-color: #f0f0f0;
  color: #333;
  box-shadow: 
    3px 3px 6px rgba(0, 0, 0, 0.1),
    -3px -3px 6px rgba(255, 255, 255, 0.8);
}

.modal-body {
  padding: 20px;
}

/* 表单样式 - 拟态设计 */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  background: linear-gradient(145deg, #ffffff, #f0f0f0);
  box-shadow: 
    inset 3px 3px 6px rgba(0, 0, 0, 0.1),
    inset -3px -3px 6px rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  outline: none;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  box-shadow: 
    inset 4px 4px 8px rgba(0, 0, 0, 0.15),
    inset -4px -4px 8px rgba(255, 255, 255, 0.9);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

/* 表单验证错误样式 */
.is-invalid {
  box-shadow: 
    inset 3px 3px 6px rgba(255, 0, 0, 0.1),
    inset -3px -3px 6px rgba(255, 255, 255, 0.8),
    0 0 0 2px rgba(255, 0, 0, 0.2) !important;
}

.invalid-feedback {
  display: block;
  margin-top: 5px;
  color: #dc3545;
  font-size: 12px;
}

/* 表单按钮组 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 按钮样式 - 拟态设计 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 
    4px 4px 8px rgba(0, 0, 0, 0.1),
    -4px -4px 8px rgba(255, 255, 255, 0.8);
}

.btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 
    6px 6px 12px rgba(0, 0, 0, 0.15),
    -6px -6px 12px rgba(255, 255, 255, 0.9);
}

.btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 
    inset 3px 3px 6px rgba(0, 0, 0, 0.15),
    inset -3px -3px 6px rgba(255, 255, 255, 0.9);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-primary {
  background: linear-gradient(145deg, #42b883, #3aa373);
  color: white;
}

.btn-secondary {
  background: linear-gradient(145deg, #f0f0f0, #e0e0e0);
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .modal-header,
  .modal-body {
    padding: 15px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
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
  background-color: #42b883;
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

/* 图片上传样式 */
.image-upload-container {
  margin-top: 10px;
}

.image-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 15px;
  margin-bottom: 15px;
}

.image-preview-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-preview {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.image-delete-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background-color: rgba(255, 0, 0, 0.8);
  color: white;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.image-delete-btn:hover {
  background-color: rgba(255, 0, 0, 1);
  transform: scale(1.1);
}

.image-add-item {
  width: 120px;
  height: 120px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f9f9f9;
}

.image-add-item:hover {
  border-color: #42b883;
  background-color: #f0f9f4;
}

.image-add-icon {
  font-size: 32px;
  color: #999;
  margin-bottom: 8px;
}

.image-add-text {
  font-size: 12px;
  color: #666;
}

.upload-status {
  margin-top: 10px;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.upload-status.loading {
  background-color: #e3f2fd;
  color: #1976d2;
}

.upload-status.error {
  background-color: #ffebee;
  color: #c62828;
}

.loading-text, .error-text {
  display: flex;
  align-items: center;
}

.loading-text::before {
  content: '';
  width: 16px;
  height: 16px;
  border: 2px solid #1976d2;
  border-top: 2px solid transparent;
  border-radius: 50%;
  margin-right: 8px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
  
  .image-preview-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 10px;
  }
  
  .image-preview-item, .image-add-item {
    width: 100px;
    height: 100px;
  }
}
</style>