<template>
  <div class="profile-page">
    <div class="page-container">
      <div class="profile-header">
        <div class="profile-avatar-section">
          <el-avatar :size="80" :src="avatarUrl" class="profile-avatar" @error="handleAvatarError">
            {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || 'U')[0] }}
          </el-avatar>
          <div class="profile-header-text">
            <h2>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h2>
            <p>@{{ userStore.userInfo?.username }}</p>
          </div>
        </div>
        <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
      </div>

      <div class="profile-content">
        <div class="profile-card glass-card">
          <h3 class="card-title">
            <el-icon><User /></el-icon>
            基本信息
          </h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ userStore.userInfo?.username }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">昵称</span>
              <span class="info-value">{{ userStore.userInfo?.nickname }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">手机号</span>
              <span class="info-value">{{ userStore.userInfo?.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱</span>
              <span class="info-value">{{ userStore.userInfo?.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">注册时间</span>
              <span class="info-value">{{ formatTime(userStore.userInfo?.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">账号状态</span>
              <span class="info-value">
                <el-tag :type="userStore.userInfo?.status === 1 ? 'success' : 'danger'" size="small">
                  {{ userStore.userInfo?.status === 1 ? '正常' : '已禁用' }}
                </el-tag>
              </span>
            </div>
          </div>
        </div>

        <div class="profile-card glass-card">
          <h3 class="card-title">
            <el-icon><List /></el-icon>
            我的交易
          </h3>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-num">{{ goodsCount }}</span>
              <span class="stat-label">我的商品</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ demandCount }}</span>
              <span class="stat-label">我的求购</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ favoriteCount }}</span>
              <span class="stat-label">我的收藏</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ orderCount }}</span>
              <span class="stat-label">交易意向</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, List } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { getImageUrl } from '../utils/image'
import { getMyGoods } from '../api/goods'
import { getMyDemands } from '../api/demand'
import { getFavoriteList } from '../api/favorite'
import { getMyBuyOrders, getMySellOrders } from '../api/order'

const router = useRouter()
const userStore = useUserStore()
const avatarLoadError = ref(false)

/** 统计数据 —— 4 个独立 ref，简单可靠 */
const goodsCount    = ref(0)
const demandCount   = ref(0)
const favoriteCount = ref(0)
const orderCount    = ref(0)

const avatarUrl = computed(() => {
  if (avatarLoadError.value) return ''
  return getImageUrl(userStore.userInfo?.avatar)
})

function handleAvatarError() {
  avatarLoadError.value = true
}

/** 从响应中安全提取数据总量 —— 数组长度优先，total 兜底 */
function getTotal(res) {
  console.log('[Profile] getTotal raw input:', JSON.parse(JSON.stringify(res ?? null)))
  if (res == null) return 0

  const candidates = [
    res,
    res?.data,
    res?.data?.data,
    res?.value,
    res?.value?.data,
    res?.value?.data?.data
  ]

  // 第一轮：优先检查数组长度（数组不会说谎，total=0 会骗人）
  for (let i = 0; i < candidates.length; i++) {
    const item = candidates[i]
    if (!item) continue

    console.log('[Profile] getTotal candidate[' + i + ']:', Object.keys(item))

    if (Array.isArray(item)) {
      console.log('[Profile] getTotal found direct array, length =', item.length)
      return item.length
    }

    if (Array.isArray(item.records) && item.records.length > 0) {
      console.log('[Profile] getTotal found .records.length =', item.records.length)
      return item.records.length
    }

    if (Array.isArray(item.list) && item.list.length > 0) {
      console.log('[Profile] getTotal found .list.length =', item.list.length)
      return item.list.length
    }

    if (Array.isArray(item.data) && item.data.length > 0) {
      console.log('[Profile] getTotal found .data.length =', item.data.length)
      return item.data.length
    }
  }

  // 第二轮：没有数组数据时，才用 total 兜底
  for (let i = 0; i < candidates.length; i++) {
    const item = candidates[i]
    if (!item) continue

    if (typeof item.total === 'number' && item.total > 0) {
      console.log('[Profile] getTotal found .total =', item.total)
      return item.total
    }

    if (typeof item.total === 'string' && item.total !== '') {
      const n = Number(item.total)
      if (n > 0) {
        console.log('[Profile] getTotal found .total string =', n)
        return n
      }
    }
  }

  console.log('[Profile] getTotal found nothing — returning 0')
  return 0
}

async function loadStats() {
  console.log('[Profile] loadStats() started')
  let goodsRes, demandRes, favoriteRes, buyRes, sellRes

  try {
    const results = await Promise.allSettled([
      getMyGoods({ pageNum: 1, pageSize: 1000 }),
      getMyDemands({ pageNum: 1, pageSize: 1000 }),
      getFavoriteList({ pageNum: 1, pageSize: 1000 }),
      getMyBuyOrders({ pageNum: 1, pageSize: 1000 }),
      getMySellOrders({ pageNum: 1, pageSize: 1000 })
    ])

    console.log('[Profile] allSettled results keys:', results.map(r => r.status))

    goodsRes    = results[0].status === 'fulfilled' ? results[0].value : (console.warn('[Profile] goods/my failed:', results[0].reason), null)
    demandRes   = results[1].status === 'fulfilled' ? results[1].value : (console.warn('[Profile] demand/my failed:', results[1].reason), null)
    favoriteRes = results[2].status === 'fulfilled' ? results[2].value : (console.warn('[Profile] favorite/list failed:', results[2].reason), null)
    buyRes      = results[3].status === 'fulfilled' ? results[3].value : (console.warn('[Profile] order/my-buy failed:', results[3].reason), null)
    sellRes     = results[4].status === 'fulfilled' ? results[4].value : (console.warn('[Profile] order/my-sell failed:', results[4].reason), null)
  } catch (e) {
    console.error('[Profile] loadStats unexpected error:', e)
    return
  }

  console.log('[Profile] goodsRes:', goodsRes)
  console.log('[Profile] demandRes:', demandRes)
  console.log('[Profile] favoriteRes:', favoriteRes)
  console.log('[Profile] buyRes:', buyRes)
  console.log('[Profile] sellRes:', sellRes)

  goodsCount.value    = getTotal(goodsRes)
  demandCount.value   = getTotal(demandRes)
  favoriteCount.value = getTotal(favoriteRes)
  orderCount.value    = getTotal(buyRes) + getTotal(sellRes)

  console.log('[Profile] final stats:', JSON.parse(JSON.stringify({
    goodsCount: goodsCount.value,
    demandCount: demandCount.value,
    favoriteCount: favoriteCount.value,
    orderCount: orderCount.value
  })))
}

// 调试：监听所有 ref 变化
watchEffect(() => {
  console.log('[Profile] WATCH — goodsCount:', goodsCount.value, 'demandCount:', demandCount.value, 'favoriteCount:', favoriteCount.value, 'orderCount:', orderCount.value)
})

onMounted(async () => {
  try {
    await userStore.fetchProfile()
  } catch {
    // error handled
  }
  loadStats()
})

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    goodsCount.value    = 0
    demandCount.value   = 0
    favoriteCount.value = 0
    orderCount.value    = 0
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
}
.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(6, 182, 212, 0.08));
  border-radius: var(--radius);
  border: 1px solid var(--border-glass);
  margin-bottom: 24px;
}
.profile-avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}
.profile-avatar {
  border: 3px solid var(--primary);
  box-shadow: 0 0 20px rgba(99, 102, 241, 0.3);
}
.profile-header-text h2 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
}
.profile-header-text p {
  font-size: 14px;
  color: var(--text-secondary);
}
.profile-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}
.profile-card {
  padding: 28px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--primary-light);
}
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.info-label {
  font-size: 12px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.info-value {
  font-size: 15px;
  color: var(--text-primary);
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.stat-item {
  text-align: center;
  padding: 20px 12px;
  background: rgba(99, 102, 241, 0.06);
  border-radius: var(--radius-sm);
  border: 1px solid rgba(99, 102, 241, 0.1);
}
.stat-num {
  display: block;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-light), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
  display: block;
}

@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .profile-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  .profile-avatar-section {
    flex-direction: column;
  }
}
</style>