import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminLogin as loginApi, getAdminProfile } from '../api/adminAuth'

export const useAdminStore = defineStore('admin', () => {
  const adminToken = ref(localStorage.getItem('adminToken') || '')
  const adminInfo = ref(JSON.parse(localStorage.getItem('adminInfo') || 'null'))

  function setToken(val) {
    adminToken.value = val
    localStorage.setItem('adminToken', val)
  }

  function setAdminInfo(val) {
    adminInfo.value = val
    localStorage.setItem('adminInfo', JSON.stringify(val))
  }

  async function login(data) {
    const res = await loginApi(data)
    setToken(res.data.token)
    setAdminInfo(res.data.admin)
    return res.data
  }

  async function fetchProfile() {
    const res = await getAdminProfile()
    setAdminInfo(res.data)
    return res.data
  }

  function logout() {
    adminToken.value = ''
    adminInfo.value = null
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminInfo')
  }

  const isLoggedIn = () => !!adminToken.value

  return { adminToken, adminInfo, login, fetchProfile, logout, isLoggedIn, setToken, setAdminInfo }
})
