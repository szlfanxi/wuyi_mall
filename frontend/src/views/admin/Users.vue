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
              <button class="btn btn-small btn-view" @click="viewUser(user.id)">查看</button>
              <button class="btn btn-small btn-edit" @click="editUser(user.id)">编辑</button>
              <button class="btn btn-small" :class="user.status === 0 ? 'btn-disable' : 'btn-enable'" @click="toggleUserStatus(user.id, user.status)">
                {{ user.status === 0 ? '禁用' : '启用' }}
              </button>
              <button class="btn btn-small btn-delete" @click="deleteUser(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
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
const viewUser = (userId) => {
  // 跳转到用户详情页
  router.push(`/admin/users/${userId}`)
}

// 编辑用户
const editUser = (userId) => {
  // 编辑用户逻辑
  console.log('编辑用户:', userId)
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
</style>