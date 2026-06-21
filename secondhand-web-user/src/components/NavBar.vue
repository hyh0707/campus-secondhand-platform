<template>
  <header class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">&#8635;</span>
        <span class="logo-text">SecondHand Campus</span>
      </router-link>

      <nav class="nav-links">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/goods" class="nav-link">商品</router-link>
        <router-link to="/demand" class="nav-link">求购</router-link>
      </nav>

      <div class="nav-right">
        <template v-if="userStore.isLoggedIn()">
          <el-dropdown trigger="click">
            <span class="btn-primary btn-sm publish-btn">
              <el-icon><Plus /></el-icon>
              发布
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <router-link to="/publish" class="dropdown-link">发布商品</router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/publish-demand" class="dropdown-link">发布求购</router-link>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <router-link to="/my-goods" class="nav-link">我的商品</router-link>
          <router-link to="/my-demands" class="nav-link">我的求购</router-link>
          <router-link to="/profile" class="user-area">
            <el-avatar :size="34" :src="avatarUrl" class="user-avatar" @error="handleAvatarError">
              {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || 'U')[0] }}
            </el-avatar>
            <span class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
          </router-link>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-outline btn-sm">登录</router-link>
          <router-link to="/register" class="btn-primary btn-sm">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
const userStore = useUserStore()

const avatarLoadError = ref(false)

const avatarUrl = computed(() => {
  if (avatarLoadError.value) return ''
  return userStore.userInfo?.avatar || ''
})

function handleAvatarError() {
  avatarLoadError.value = true
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 64px;
  background: rgba(15, 23, 42, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(99, 102, 241, 0.15);
}
.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 40px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.logo-icon {
  font-size: 26px;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--text-primary), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.nav-links {
  display: flex;
  gap: 8px;
  flex: 1;
}
.nav-link {
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 14px;
  transition: var(--transition);
}
.nav-link:hover,
.nav-link.router-link-active {
  color: var(--text-primary);
  background: rgba(99, 102, 241, 0.1);
}
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}
.btn-sm {
  padding: 7px 18px;
  font-size: 13px;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 20px;
  background: rgba(99, 102, 241, 0.08);
  border: 1px solid transparent;
  transition: var(--transition);
  cursor: pointer;
}
.user-area:hover {
  border-color: var(--border-glass);
  background: rgba(99, 102, 241, 0.15);
}
.user-avatar {
  flex-shrink: 0;
}
.publish-btn {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.arrow-icon {
  font-size: 12px;
  margin-left: -2px;
}
.dropdown-link {
  display: block;
  width: 100%;
  color: inherit;
  text-decoration: none;
}
.user-name {
  font-size: 13px;
  color: var(--text-primary);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
  .logo-text {
    font-size: 15px;
  }
  .navbar-inner {
    padding: 0 16px;
    gap: 16px;
  }
}
</style>