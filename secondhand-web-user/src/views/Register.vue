<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="bg-orb orb-1"></div>
      <div class="bg-orb orb-2"></div>
      <div class="bg-orb orb-3"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="auth-container">
      <div class="auth-left">
        <div class="brand-area">
          <div class="brand-icon">&#8635;</div>
          <h1 class="brand-name">SecondHand Campus</h1>
          <p class="brand-sub">校园二手物品智能匹配平台</p>
          <p class="brand-slogan">加入我们，开启智能二手交易</p>
        </div>
        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>注册即享智能匹配推荐</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>发布商品，轻松找到买家</span>
          </div>
          <div class="feature-item">
            <span class="feature-dot"></span>
            <span>发布求购，让合适商品找到你</span>
          </div>
        </div>
      </div>

      <div class="auth-right">
        <div class="auth-card glass-card">
          <h2 class="auth-title">创建账号</h2>
          <p class="auth-subtitle">注册新账号加入平台</p>

          <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="handleRegister">
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="nickname">
              <el-input v-model="form.nickname" placeholder="昵称" :prefix-icon="UserFilled" />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="form.phone" placeholder="手机号" :prefix-icon="Phone" />
            </el-form-item>
            <el-form-item prop="email">
              <el-input v-model="form.email" placeholder="邮箱" :prefix-icon="Message" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="submit-btn" @click="handleRegister">
                注 册
              </el-button>
            </el-form-item>
          </el-form>

          <div class="auth-footer">
            已有账号？<router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Phone, Message } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.register({ ...form })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  position: relative;
  overflow: hidden;
}
.auth-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
}
.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}
.orb-1 {
  width: 500px;
  height: 500px;
  background: var(--primary);
  top: -150px;
  right: -100px;
  animation: orbFloat 8s ease-in-out infinite;
}
.orb-2 {
  width: 400px;
  height: 400px;
  background: var(--accent);
  bottom: -120px;
  left: -80px;
  animation: orbFloat 10s ease-in-out infinite reverse;
}
.orb-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: orbFloat 12s ease-in-out infinite;
}
@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}
.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(99, 102, 241, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(99, 102, 241, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}
.auth-container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  max-width: 960px;
  min-height: 600px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.4);
}
.auth-left {
  flex: 1;
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.9), rgba(6, 182, 212, 0.85));
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.auth-left::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M30 5 L55 30 L30 55 L5 30Z' fill='none' stroke='rgba(255,255,255,0.06)' stroke-width='1'/%3E%3C/svg%3E");
  background-size: 60px 60px;
}
.brand-area {
  position: relative;
  z-index: 1;
}
.brand-icon {
  font-size: 48px;
  margin-bottom: 16px;
  filter: drop-shadow(0 0 20px rgba(255, 255, 255, 0.3));
  color: white;
}
.brand-name {
  font-size: 28px;
  font-weight: 800;
  color: white;
  margin-bottom: 8px;
}
.brand-sub {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 4px;
}
.brand-slogan {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  font-style: italic;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}
.feature-list {
  position: relative;
  z-index: 1;
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
}
.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.3);
}
.auth-right {
  flex: 1;
  background: rgba(15, 23, 42, 0.95);
  backdrop-filter: blur(20px);
  padding: 40px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-card {
  width: 100%;
  max-width: 360px;
  padding: 36px 32px;
}
.auth-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 6px;
  background: linear-gradient(135deg, var(--text-primary), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.auth-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 28px;
}
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  letter-spacing: 4px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  border: none;
}
.auth-footer {
  text-align: center;
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 8px;
}
.auth-footer a {
  color: var(--accent);
  font-weight: 500;
}

@media (max-width: 768px) {
  .auth-container {
    flex-direction: column;
    max-width: 420px;
  }
  .auth-left {
    padding: 32px 24px;
    min-height: auto;
  }
  .brand-name {
    font-size: 22px;
  }
  .feature-list {
    display: none;
  }
  .auth-right {
    padding: 24px;
  }
  .auth-card {
    padding: 28px 20px;
  }
}
</style>