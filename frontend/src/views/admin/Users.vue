<template>
  <div class="admin-users">
    <h2>用户管理</h2>
    <div class="users-toolbar">
      <div class="filter-box">
        <select v-model="roleFilter">
          <option value="">全部角色</option>
          <option value="user">普通用户</option>
          <option value="admin">管理员</option>
        </select>
        <select v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="active">活跃</option>
          <option value="inactive">禁用</option>
        </select>
        <button class="btn btn-filter" @click="fetchUsers">筛选</button>
      </div>
      <div class="action-box">
        <button class="btn btn-add" @click="showAddModal">添加管理员</button>
      </div>
      <div class="search-box">
        <input type="text" placeholder="搜索用户名" v-model="searchQuery" @keyup.enter="fetchUsers" />
        <button class="btn btn-search" @click="fetchUsers">搜索</button>
      </div>
    </div>
    <div class="users-table-container">
      <table class="users-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>角色</th>
            <th>状态</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="loading-row">
            <td colspan="6" class="loading-text">加载中...</td>
          </tr>
          <tr v-else-if="users.length === 0" class="empty-row">
            <td colspan="6" class="empty-text">暂无用户数据</td>
          </tr>
          <tr v-else v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>
              <span class="role-badge" :class="`role-${user.isAdmin === 1 ? 'admin' : 'user'}`">
                {{ user.isAdmin === 1 ? '管理员' : '普通用户' }}
              </span>
            </td>
            <td>
              <span class="status-badge" :class="`status-${user.status === 0 ? 'active' : 'inactive'}`">
                {{ user.status === 0 ? '活跃' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(user.createTime) }}</td>
            <td>
              <!-- <button class="btn btn-small btn-view" @click="viewUser(user.id)">查看</button> -->
              <button class="btn btn-small btn-edit" @click="editUser(user)">编辑</button>
              <!-- <button class="btn btn-small" :class="user.status === 0 ? 'btn-disable' : 'btn-enable'" @click="toggleUserStatus(user.id, user.status)">
                {{ user.status === 0 ? '禁用' : '启用' }}
              </button> -->
              <button class="btn btn-small btn-delete" @click="deleteUser(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 编辑用户模态框 -->
    <div class="modal" v-if="editModalVisible">
      <div class="modal-overlay" @click="editModalVisible = false"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h3>编辑用户 - {{ currentUser?.username }}</h3>
          <button class="modal-close" @click="editModalVisible = false">&times;</button>
        </div>
        <div class="modal-body">
          <form class="edit-form">
            <div class="form-group">
              <label for="edit-role">角色</label>
              <select id="edit-role" v-model="editIsAdmin" class="form-control">
                <option value="0">普通用户</option>
                <option value="1">管理员</option>
              </select>
            </div>
            <div class="form-group">
              <label for="edit-status">状态</label>
              <select id="edit-status" v-model="editStatus" class="form-control">
                <option value="0">活跃</option>
                <option value="1">禁用</option>
              </select>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="editModalVisible = false">取消</button>
          <button class="btn btn-save" @click="saveUserChanges">保存</button>
        </div>
      </div>
    </div>
    
    <!-- 添加用户模态框 -->
    <div class="modal" v-if="addModalVisible">
      <div class="modal-overlay" @click="addModalVisible = false"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h3>添加管理员</h3>
          <button class="modal-close" @click="addModalVisible = false">&times;</button>
        </div>
        <div class="modal-body">
          <form class="add-form">
            <div class="form-group">
              <label for="add-username">用户名</label>
              <input type="text" id="add-username" v-model="addUsername" class="form-control" placeholder="请输入用户名" required />
            </div>
            <div class="form-group">
              <label for="add-password">密码</label>
              <input type="password" id="add-password" v-model="addPassword" class="form-control" placeholder="请输入密码" required />
            </div>
            <div class="form-group">
              <label for="add-role">角色</label>
              <select id="add-role" v-model="addIsAdmin" class="form-control">
                <option value="0">普通用户</option>
                <option value="1">管理员</option>
              </select>
            </div>
            <div class="form-group">
              <label for="add-status">状态</label>
              <select id="add-status" v-model="addStatus" class="form-control">
                <option value="0">活跃</option>
                <option value="1">禁用</option>
              </select>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button class="btn btn-cancel" @click="addModalVisible = false">取消</button>
          <button class="btn btn-save" @click="addNewUser">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()

const users = ref([])
const loading = ref(false)
const searchQuery = ref('')
const roleFilter = ref('')
const statusFilter = ref('')

// 编辑模态框相关变量
const editModalVisible = ref(false)
const currentUser = ref(null)
const editIsAdmin = ref(0)
const editStatus = ref(0)

// 添加用户模态框相关变量
const addModalVisible = ref(false)
const addUsername = ref('')
const addPassword = ref('')
const addIsAdmin = ref(1) // 默认管理员角色
const addStatus = ref(0) // 默认活跃状态

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = new URLSearchParams()
    if (searchQuery.value) {
      params.append('username', searchQuery.value)
    }
    if (roleFilter.value) {
      params.append('role', roleFilter.value)
    }
    if (statusFilter.value) {
      params.append('status', statusFilter.value)
    }
    
    const response = await request.get(`/api/admin/users/list?${params.toString()}`)
    users.value = response
  } catch (error) {
    console.error('获取用户列表失败:', error)
    users.value = []
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!(date instanceof Date)) {
    date = new Date(date)
  }
  return date.toLocaleString('zh-CN')
}

// 查看用户
// const viewUser = (userId) => {
//   // 跳转到用户详情页
//   router.push(`/admin/users/${userId}`)
// }

// 编辑用户
const editUser = (user) => {
  // 打开编辑模态框并填充用户数据
  currentUser.value = user
  editIsAdmin.value = user.isAdmin
  editStatus.value = user.status
  editModalVisible.value = true
}

// 切换用户状态
const toggleUserStatus = async (userId, currentStatus) => {
  // 切换用户状态逻辑
  try {
    // 0: 活跃, 1: 禁用
    const newStatus = currentStatus === 0 ? 1 : 0
    await request.post('/api/admin/users/changeStatus', { userId, status: newStatus })
    // 刷新用户列表
    fetchUsers()
  } catch (error) {
    console.error('切换用户状态失败:', error)
    alert('切换用户状态失败，请稍后重试')
  }
}

// 保存用户修改
const saveUserChanges = async () => {
  if (!currentUser.value) return
  
  try {
    await request.post('/api/admin/users/update', {
      userId: currentUser.value.id,
      isAdmin: editIsAdmin.value,
      status: editStatus.value
    })
    // 关闭模态框
    editModalVisible.value = false
    // 刷新用户列表
    fetchUsers()
    alert('用户信息更新成功')
  } catch (error) {
    console.error('更新用户失败:', error)
    alert('更新用户失败，请稍后重试')
  }
}

// 显示添加用户模态框
const showAddModal = () => {
  // 重置表单
  addUsername.value = ''
  addPassword.value = ''
  addIsAdmin.value = 1 // 默认管理员角色
  addStatus.value = 0 // 默认活跃状态
  // 打开模态框
  addModalVisible.value = true
}

// 添加新用户
const addNewUser = async () => {
  // 表单验证
  if (!addUsername.value.trim() || !addPassword.value.trim()) {
    alert('用户名和密码不能为空')
    return
  }
  
  try {
    await request.post('/api/admin/users/add', {
      username: addUsername.value,
      password: addPassword.value,
      isAdmin: addIsAdmin.value,
      status: addStatus.value
    })
    // 关闭模态框
    addModalVisible.value = false
    // 刷新用户列表
    fetchUsers()
    alert('用户添加成功')
  } catch (error) {
    console.error('添加用户失败:', error)
    alert('添加用户失败，请稍后重试')
  }
}

// 删除用户
const deleteUser = async (userId) => {
  // 删除用户逻辑
  if (confirm('确定要删除该用户吗？')) {
    try {
      await request.post('/api/admin/users/delete', { userId })
      // 刷新用户列表
      fetchUsers()
    } catch (error) {
      console.error('删除用户失败:', error)
      alert('删除用户失败，请稍后重试')
    }
  }
}

// 组件挂载时获取用户列表
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-users {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.users-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.action-box {
  display: flex;
  gap: 10px;
}

.filter-box {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-box select {
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

.users-table-container {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th,
.users-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.users-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #333;
}

.role-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.role-admin {
  background-color: #e1f5fe;
  color: #0277bd;
}

.role-user {
  background-color: #e8f5e8;
  color: #2e7d32;
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

.btn-add {
  background-color: #28a745;
  color: white;
}

.btn-add:hover {
  background-color: #218838;
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

.btn-disable {
  background-color: #6c757d;
  color: white;
}

.btn-disable:hover {
  background-color: #5a6268;
}

.btn-enable {
  background-color: #28a745;
  color: white;
}

.btn-enable:hover {
  background-color: #218838;
}

.btn-delete {
  background-color: #dc3545;
  color: white;
}

.btn-delete:hover {
  background-color: #c82333;
}

/* 模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  position: relative;
  z-index: 1001;
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
  padding: 16px 20px;
  border-bottom: 1px solid #e9ecef;
  background-color: #f8f9fa;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #6c757d;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.modal-close:hover {
  color: #495057;
}

.modal-body {
  padding: 20px;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #495057;
  font-size: 14px;
}

.form-control {
  padding: 10px 12px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-control:focus {
  outline: none;
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e9ecef;
  background-color: #f8f9fa;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

.btn-cancel {
  background-color: #6c757d;
  color: white;
}

.btn-cancel:hover {
  background-color: #5a6268;
}

.btn-save {
  background-color: #28a745;
  color: white;
}

.btn-save:hover {
  background-color: #218838;
}
</style>