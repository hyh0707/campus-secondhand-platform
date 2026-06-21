<template>
  <div class="admin-layout">
    <!-- 左侧菜单 -->
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 32 32" width="32" height="32">
            <defs>
              <linearGradient id="brandGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#6366f1"/>
                <stop offset="100%" stop-color="#06b6d4"/>
              </linearGradient>
            </defs>
            <rect width="32" height="32" rx="8" fill="url(#brandGrad)"/>
            <text x="16" y="22" text-anchor="middle" font-size="16" font-weight="700" fill="white">A</text>
          </svg>
        </div>
        <span class="brand-text">管理后台</span>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: route.path.startsWith(item.path) }"
        >
          <el-icon :size="18"><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <header class="topbar">
        <h2 class="topbar-title">{{ route.meta.title || '管理后台' }}</h2>
        <div class="topbar-right">
          <div class="admin-info" v-if="adminStore.adminInfo">
            <el-icon :size="16" class="admin-icon"><UserFilled /></el-icon>
            <span>{{ adminStore.adminInfo.nickname || adminStore.adminInfo.username }}</span>
          </div>
          <el-button text class="btn-logout" @click="handleLogout">
            <el-icon :size="16"><SwitchButton /></el-icon>
            退出
          </el-button>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { UserFilled, SwitchButton, DataAnalysis, Goods, Collection, User, Tickets } from '@element-plus/icons-vue'
import { useAdminStore } from '../stores/admin'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

const menuItems = [
  { path: '/dashboard', label: '数据看板', icon: DataAnalysis },
  { path: '/goods', label: '商品审核', icon: Goods },
  { path: '/demands', label: '求购审核', icon: Collection },
  { path: '/users', label: '用户管理', icon: User },
  { path: '/orders', label: '订单管理', icon: Tickets }
]

function handleLogout() {
  adminStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

/* ======== 左侧菜单 ======== */
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: var(--bg-sidebar);
  border-right: 1px solid rgba(99, 102, 241, 0.1);
  display: flex;
  flex-direction: column;
  padding: 0;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px;
  border-bottom: 1px solid rgba(99, 102, 241, 0.1);
}
.brand-text {
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--accent-light), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 12px 8px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 13px;
  color: var(--text-secondary);
  transition: var(--transition);
  text-decoration: none;
}
.nav-item:hover {
  background: rgba(99, 102, 241, 0.08);
  color: var(--text-primary);
}
.nav-item.active {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(6, 182, 212, 0.08));
  color: var(--accent-light);
  font-weight: 500;
}

/* ======== 右侧区域 ======== */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* ======== 顶部栏 ======== */
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  height: 56px;
  background: rgba(15, 23, 42, 0.6);
  border-bottom: 1px solid rgba(99, 102, 241, 0.1);
  backdrop-filter: blur(12px);
  position: sticky;
  top: 0;
  z-index: 10;
}

.topbar-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}
.admin-icon {
  color: var(--accent-light);
}

.btn-logout {
  color: var(--text-muted) !important;
  font-size: 13px;
  padding: 4px 8px;
}
.btn-logout:hover {
  color: var(--danger) !important;
}

/* ======== 内容区 ======== */
.content {
  flex: 1;
  padding: 28px;
  min-height: calc(100vh - 56px);
}
</style>
