import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

request.interceptors.response.use(response => {
  const res = response.data
  if (res.code !== 200) {
    ElMessage.error(res.message || '请求失败')
    if (res.code === 403) {
      ElMessage.warning('无权限访问')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  }
  return res
}, error => {
  if (error.response) {
    const status = error.response.status
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      const currentPath = router.currentRoute?.value?.path
      if (currentPath !== '/login' && currentPath !== '/register') {
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
      }
    } else if (status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error(error.response.data?.message || '服务器错误')
    }
  } else {
    ElMessage.error('网络异常，请检查网络连接')
  }
  return Promise.reject(error)
})

export default request