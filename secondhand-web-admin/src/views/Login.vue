<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 头部 -->
      <div class="login-header">
        <div class="login-logo">
          <svg viewBox="0 0 48 48" width="48" height="48">
            <defs>
              <linearGradient id="loginGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#6366f1"/>
                <stop offset="100%" stop-color="#06b6d4"/>
              </linearGradient>
            </defs>
            <rect width="48" height="48" rx="12" fill="url(#loginGrad)"/>
            <text x="24" y="32" text-anchor="middle" font-size="22" font-weight="700" fill="white">A</text>
          </svg>
        </div>
        <h1>管理员登录</h1>
        <p>校园二手物品智能匹配平台</p>
      </div>

      <!-- 表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="管理员账号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="btn-login"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部提示 -->
      <p class="login-footer">默认管理员账号 admin / 123456</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '../stores/admin'

const router = useRouter()
const adminStore = useAdminStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: '123456'
})

const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await adminStore.login({ username: form.username, password: form.password })
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
    // 错误已在 request 拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(ellipse 70% 60% at 50% -20%, rgba(99, 102, 241, 0.12), transparent),
    radial-gradient(ellipse 50% 40% at 80% 80%, rgba(6, 182, 212, 0.06), transparent),
    var(--bg-dark);
}

.login-container {
  width: 400px;
  padding: 40px 36px;
  background: var(--bg-card);
  border: 1px solid var(--border-glass);
  border-radius: var(--radius);
  backdrop-filter: blur(16px);
  box-shadow: var(--shadow-card);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header h1 {
  font-size: 22px;
  font-weight: 700;
  margin: 12px 0 6px;
  background: linear-gradient(135deg, var(--accent-light), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.login-header p {
  font-size: 13px;
  color: var(--text-muted);
}

.login-form :deep(.el-input__wrapper) {
  background: rgba(30, 41, 59, 0.5) !important;
  box-shadow: none !important;
}

.btn-login {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark)) !important;
  border: none !important;
}
.btn-login:hover {
  background: linear-gradient(135deg, var(--primary-dark), var(--primary)) !important;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.35);
}

.login-footer {
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 20px;
}
</style>
