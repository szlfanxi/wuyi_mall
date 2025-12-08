import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: '', // API基础路径，根据实际情况配置
  timeout: 10000, // 请求超时时间
  headers: {
    // 移除默认的Content-Type，让axios根据请求自动设置
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage中获取token
    const token = localStorage.getItem('token')
    // 如果token存在，将token添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 处理请求错误
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 处理响应数据
    return response.data
  },
  error => {
    // 处理响应错误
    if (error.response) {
      // 响应状态码处理
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          // 拒绝访问
          console.error('拒绝访问')
          break
        case 404:
          // 请求资源不存在
          console.error('请求资源不存在')
          break
        case 500:
          // 服务器错误
          console.error('服务器错误')
          break
        default:
          console.error(`请求错误: ${error.response.status}`)
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      console.error('网络错误，请检查网络连接')
    } else {
      // 请求配置错误
      console.error('请求错误:', error.message)
    }
    return Promise.reject(error)
  }
)

export default request