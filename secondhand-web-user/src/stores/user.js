import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getProfile } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  function setToken(val) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setUserInfo(val) {
    userInfo.value = val
    localStorage.setItem('userInfo', JSON.stringify(val))
  }

  async function login(data) {
    const res = await loginApi(data)
    setToken(res.data.token)
    setUserInfo(res.data.user)
    return res.data
  }

  async function register(data) {
    return await registerApi(data)
  }

  async function fetchProfile() {
    const res = await getProfile()
    setUserInfo(res.data)
    return res.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const isLoggedIn = () => !!token.value

  return { token, userInfo, login, register, fetchProfile, logout, isLoggedIn, setToken, setUserInfo }
})