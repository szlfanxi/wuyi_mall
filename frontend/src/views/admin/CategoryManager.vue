<template>
  <div class="admin-categories">
    <h2>类目管理</h2>
    <div class="categories-toolbar">
      <button class="btn btn-primary" @click="showAddModal(null)">添加一级类目</button>
    </div>
    
    <div class="categories-container">
      <div v-if="loading">加载中...</div>
      <div v-else-if="categories.length === 0">暂无类目数据</div>
      <div v-else class="categories-tree">
        <!-- 一级类目列表 -->
        <div v-for="category in rootCategories" :key="category.id" class="category-item">
          <div class="category-header">
            <div class="category-info">
              <span class="category-name">{{ category.name }}</span>
              <span class="category-sort">排序: {{ category.sort }}</span>
            </div>
            <div class="category-actions">
              <button class="btn btn-small btn-primary" @click="showAddModal(category.id)">添加子类</button>
              <button class="btn btn-small btn-edit" @click="showEditModal(category)">编辑</button>
              <button class="btn btn-small btn-delete" @click="confirmDelete(category.id)">删除</button>
            </div>
          </div>
          
          <!-- 二级类目列表 -->
          <div class="subcategories-list">
            <div v-for="subCategory in getSubCategories(category.id)" :key="subCategory.id" class="subcategory-item">
              <div class="category-info">
                <span class="subcategory-name">└── {{ subCategory.name }}</span>
                <span class="category-sort">排序: {{ subCategory.sort }}</span>
              </div>
              <div class="category-actions">
                <button class="btn btn-small btn-edit" @click="showEditModal(subCategory)">编辑</button>
                <button class="btn btn-small btn-delete" @click="confirmDelete(subCategory.id)">删除</button>
              </div>
            </div>
            <div v-if="getSubCategories(category.id).length === 0" class="no-subcategories">
              暂无子类
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑模态框 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingCategory.id ? '编辑类目' : (editingCategory.parentId ? '添加子类' : '添加一级类目') }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveCategory">
            <!-- 父类目选择（仅添加子类时显示） -->
            <div class="form-group" v-if="!editingCategory.id">
              <label for="parentId">父类目</label>
              <select id="parentId" v-model="editingCategory.parentId" class="form-control">
                <option value="null">无父类目（一级类目）</option>
                <option v-for="category in rootCategories" :key="category.id" :value="category.id">{{ category.name }}</option>
              </select>
            </div>
            
            <!-- 类目名称 -->
            <div class="form-group">
              <label for="name">类目名称 <span class="required">*</span></label>
              <input 
                type="text" 
                id="name" 
                v-model="editingCategory.name" 
                class="form-control" 
                :class="{ 'is-invalid': errors.name }"
                placeholder="请输入类目名称"
                required
              />
              <div class="invalid-feedback" v-if="errors.name">{{ errors.name }}</div>
            </div>
            
            <!-- 排序 -->
            <div class="form-group">
              <label for="sort">排序 <span class="required">*</span></label>
              <input 
                type="number" 
                id="sort" 
                v-model.number="editingCategory.sort" 
                class="form-control" 
                :class="{ 'is-invalid': errors.sort }"
                placeholder="请输入排序号"
                min="0"
                required
              />
              <div class="invalid-feedback" v-if="errors.sort">{{ errors.sort }}</div>
            </div>
            
            <!-- 按钮组 -->
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="submitting">
                {{ submitting ? '保存中...' : '保存' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="cancelDelete">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>删除确认</h3>
          <button class="close-btn" @click="cancelDelete">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除该类目吗？{{ hasSubCategories ? '该类目下有子类，将一起删除。' : '' }}</p>
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="cancelDelete">取消</button>
            <button type="button" class="btn btn-danger" @click="deleteCategory" :disabled="submitting">
              {{ submitting ? '删除中...' : '删除' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import request from '../../utils/request'

const categories = ref([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const showDeleteConfirm = ref(false)
const editingCategory = ref({ id: null, name: '', parentId: null, sort: 0 })
const deleteCategoryId = ref(null)
const hasSubCategories = ref(false)
const errors = ref({})

// 获取所有一级类目
const rootCategories = computed(() => {
  return categories.value.filter(category => category.parentId === null)
})

// 获取指定一级类目下的所有二级类目
const getSubCategories = (parentId) => {
  return categories.value.filter(category => category.parentId === parentId)
}

// 获取父类目名称
const getParentCategoryName = (parentId) => {
  const parent = categories.value.find(category => category.id === parentId)
  return parent ? parent.name : ''
}

// 获取类目列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/admin/categories/all')
    console.log('获取类目列表响应:', response)
    if (Array.isArray(response)) {
      categories.value = response
    } else if (response && response.success === true) {
      categories.value = response.data || []
    } else {
      categories.value = []
    }
    console.log('处理后类目列表:', categories.value)
  } catch (error) {
    console.error('获取类目列表失败:', error)
    categories.value = []
    alert('获取类目列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 表单验证
const validateForm = () => {
  const newErrors = {}
  
  // 类目名称验证
  if (!editingCategory.value.name.trim()) {
    newErrors.name = '类目名称不能为空'
  } else if (editingCategory.value.name.length > 50) {
    newErrors.name = '类目名称不能超过50个字符'
  }
  
  // 排序验证
  if (isNaN(editingCategory.value.sort) || editingCategory.value.sort < 0) {
    newErrors.sort = '排序必须大于等于0'
    editingCategory.value.sort = 0
  }
  
  errors.value = newErrors
  return Object.keys(newErrors).length === 0
}

// 显示添加模态框
const showAddModal = (parentId) => {
  editingCategory.value = {
    id: null,
    name: '',
    parentId: parentId,
    sort: 0
  }
  errors.value = {}
  showModal.value = true
}

// 显示编辑模态框
const showEditModal = (category) => {
  editingCategory.value = { ...category }
  errors.value = {}
  showModal.value = true
}

// 关闭模态框
const closeModal = () => {
  showModal.value = false
  editingCategory.value = { id: null, name: '', parentId: null, sort: 0 }
  errors.value = {}
}

// 保存类目（添加或编辑）
const saveCategory = async () => {
  if (!validateForm()) {
    return
  }
  
  submitting.value = true
  try {
    let response
    const categoryData = {
      ...editingCategory.value,
      parentId: editingCategory.value.parentId === 'null' ? null : editingCategory.value.parentId
    }
    
    if (editingCategory.value.id) {
      // 更新类目
      response = await request.put(`/api/admin/categories/${editingCategory.value.id}`, categoryData)
    } else {
      // 新增类目
      response = await request.post('/api/admin/categories', categoryData)
    }
    
    if (response && response.success === true) {
      // 刷新类目列表
      await fetchCategories()
      // 关闭模态框
      closeModal()
      // 显示成功提示
      alert(editingCategory.value.id ? '类目更新成功' : '类目添加成功')
    } else {
      throw new Error(response?.message || '操作失败')
    }
  } catch (error) {
    console.error('保存类目失败:', error)
    alert('保存类目失败，请稍后重试: ' + (error.message || ''))
  } finally {
    submitting.value = false
  }
}

// 显示删除确认对话框
const confirmDelete = (categoryId) => {
  deleteCategoryId.value = categoryId
  // 检查是否有子类
  hasSubCategories.value = categories.value.some(category => category.parentId === categoryId)
  showDeleteConfirm.value = true
}

// 取消删除
const cancelDelete = () => {
  showDeleteConfirm.value = false
  deleteCategoryId.value = null
  hasSubCategories.value = false
}

// 删除类目
const deleteCategory = async () => {
  if (!deleteCategoryId.value) return
  
  submitting.value = true
  try {
    const response = await request.delete(`/api/admin/categories/${deleteCategoryId.value}`)
    
    if (response && response.success === true) {
      // 刷新类目列表
      await fetchCategories()
      // 关闭确认对话框
      cancelDelete()
      // 显示成功提示
      alert('类目删除成功')
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除类目失败:', error)
    alert('删除类目失败，请稍后重试: ' + (error.message || ''))
  } finally {
    submitting.value = false
  }
}

// 组件挂载时获取数据
onMounted(async () => {
  await fetchCategories()
})

// 监听editingCategory变化，调试用
watch(editingCategory, (newVal) => {
  console.log('editingCategory变化:', newVal)
}, { deep: true })
</script>

<style scoped>
.admin-categories {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.categories-toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.categories-container {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  min-height: 200px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 50px;
  color: #999;
  font-style: italic;
}

.categories-tree {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.category-item {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.category-name {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.subcategory-name {
  font-weight: 500;
  font-size: 15px;
  color: #555;
}

.category-sort {
  font-size: 12px;
  color: #999;
}

.category-actions {
  display: flex;
  gap: 10px;
}

.subcategories-list {
  margin-top: 15px;
  margin-left: 30px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.subcategory-item {
  background-color: #f5f5f5;
  border-radius: 6px;
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.no-subcategories {
  margin-top: 10px;
  color: #999;
  font-size: 14px;
  font-style: italic;
}

/* 按钮样式 */
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

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
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

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #c82333;
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
}

/* 模态框样式 */
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  background-color: #fafafa;
  border-radius: 8px 8px 0 0;
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
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background-color: #f0f0f0;
  color: #333;
}

.modal-body {
  padding: 20px;
}

/* 表单样式 */
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

.required {
  color: #dc3545;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 0.2rem rgba(66, 184, 131, 0.25);
}

.form-control.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  display: block;
  margin-top: 5px;
  color: #dc3545;
  font-size: 12px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 选择框样式 */
select.form-control {
  height: 40px;
}
</style>