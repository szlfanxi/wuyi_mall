import { createRouter, createWebHistory } from 'vue-router'
import ProductList from '../views/User/ProductList.vue'

const routes = [
  {
    path: '/',
    name: 'ProductList',
    component: ProductList
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    // 懒加载组件
    component: () => import('../views/User/ProductDetail.vue')
  },
  {
    path: '/login',
    name: 'Login',
    // 懒加载登录组件
    component: () => import('../views/User/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    // 懒加载注册组件
    component: () => import('../views/User/Register.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    // 懒加载购物车组件
    component: () => import('../views/User/Cart.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    // 懒加载订单列表组件
    component: () => import('../views/User/Orders.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    // 懒加载个人中心组件
    component: () => import('../views/User/Profile.vue')
  },
  {
    path: '/favorites',
    name: 'Favorites',
    // 懒加载收藏列表组件
    component: () => import('../views/User/Favorites.vue')
  },
  // 后台管理路由
    {
      path: '/admin',
      name: 'Admin',
      // 懒加载后台管理布局组件
      component: () => import('../views/admin/AdminLayout.vue'),
      meta: {
        requiresAuth: true,
        requiresAdmin: true
      },
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          // 懒加载后台首页组件
          component: () => import('../views/admin/Dashboard.vue')
        },
        {
          path: 'products',
          name: 'AdminProducts',
          // 懒加载商品管理组件
          component: () => import('../views/admin/Products.vue')
        },
        {
          path: 'categories',
          name: 'AdminCategories',
          // 懒加载类目管理组件
          component: () => import('../views/admin/CategoryManager.vue')
        },
        {
          path: 'orders',
          name: 'AdminOrders',
          // 懒加载订单管理组件
          component: () => import('../views/admin/Orders.vue')
        },
        {
          path: 'orders/:id',
          name: 'AdminOrderDetail',
          // 懒加载订单详情组件
          component: () => import('../views/admin/OrderDetail.vue')
        },
        {
          path: 'users',
          name: 'AdminUsers',
          // 懒加载用户管理组件
          component: () => import('../views/admin/Users.vue')
        }
      ]
    }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      // 未登录，跳转到登录页
      next({ name: 'Login' })
      return
    }
    
    // 检查是否需要管理员权限
    if (to.meta.requiresAdmin) {
      const isAdmin = localStorage.getItem('isAdmin')
      if (isAdmin !== '1') {
        // 不是管理员，跳转到首页
        next({ name: 'ProductList' })
        return
      }
    }
  }
  
  // 继续路由
  next()
})

export default router