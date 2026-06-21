<template>
  <div class="dashboard">
    <!-- 顶部统计卡片 -->
    <div class="stat-section">
      <h3 class="section-title">今日概况</h3>
      <div class="stat-grid">
        <div class="stat-card highlight">
          <div class="stat-icon" style="background: rgba(99,102,241,0.15)">
            <el-icon :size="20" color="#818cf8"><User /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">今日新增用户</span>
            <span class="stat-value">{{ stats.todayUserCount }}</span>
          </div>
        </div>
        <div class="stat-card highlight">
          <div class="stat-icon" style="background: rgba(6,182,212,0.15)">
            <el-icon :size="20" color="#22d3ee"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">今日新增商品</span>
            <span class="stat-value">{{ stats.todayGoodsCount }}</span>
          </div>
        </div>
        <div class="stat-card highlight">
          <div class="stat-icon" style="background: rgba(16,185,129,0.15)">
            <el-icon :size="20" color="#34d399"><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">今日新增求购</span>
            <span class="stat-value">{{ stats.todayDemandCount }}</span>
          </div>
        </div>
        <div class="stat-card highlight">
          <div class="stat-icon" style="background: rgba(245,158,11,0.15)">
            <el-icon :size="20" color="#fbbf24"><Tickets /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">今日新增交易</span>
            <span class="stat-value">{{ stats.todayOrderCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 平台概览 -->
    <div class="stat-section">
      <h3 class="section-title">平台概览</h3>
      <div class="stat-grid">
        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(99,102,241,0.1)">
            <el-icon :size="20" color="#818cf8"><User /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">用户总数</span>
            <span class="stat-value">{{ stats.userCount }}</span>
            <span class="stat-sub" v-if="stats.disabledUserCount">禁用 {{ stats.disabledUserCount }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(6,182,212,0.1)">
            <el-icon :size="20" color="#22d3ee"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">商品总数</span>
            <span class="stat-value">{{ stats.goodsCount }}</span>
            <span class="stat-sub">
              上架 {{ stats.approvedGoodsCount }} / 待审 {{ stats.pendingGoodsCount }} / 已售 {{ stats.soldGoodsCount }}
            </span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(16,185,129,0.1)">
            <el-icon :size="20" color="#34d399"><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">求购总数</span>
            <span class="stat-value">{{ stats.demandCount }}</span>
            <span class="stat-sub">待审 {{ stats.pendingDemandCount }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(245,158,11,0.1)">
            <el-icon :size="20" color="#fbbf24"><Tickets /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">交易总数</span>
            <span class="stat-value">{{ stats.orderCount }}</span>
            <span class="stat-sub">已完成 {{ stats.completedOrderCount }}</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(239,68,68,0.1)">
            <el-icon :size="20" color="#f87171"><StarFilled /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">收藏总数</span>
            <span class="stat-value">{{ stats.favoriteCount }}</span>
          </div>
        </div>
        <!-- 待确认交易 -->
        <div class="stat-card" v-if="stats.pendingOrderCount">
          <div class="stat-icon" style="background: rgba(139,92,246,0.1)">
            <el-icon :size="20" color="#c084fc"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">待确认交易</span>
            <span class="stat-value" style="color: #c084fc">{{ stats.pendingOrderCount }}</span>
          </div>
        </div>
        <!-- 已通过求购 -->
        <div class="stat-card" v-if="stats.approvedDemandCount">
          <div class="stat-icon" style="background: rgba(34,197,94,0.1)">
            <el-icon :size="20" color="#4ade80"><CircleCheckFilled /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">已通过求购</span>
            <span class="stat-value" style="color: #4ade80">{{ stats.approvedDemandCount }}</span>
          </div>
        </div>
        <!-- 禁用用户 -->
        <div class="stat-card" v-if="stats.disabledUserCount">
          <div class="stat-icon" style="background: rgba(239,68,68,0.1)">
            <el-icon :size="20" color="#f87171"><RemoveFilled /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-label">禁用用户</span>
            <span class="stat-value" style="color: #f87171">{{ stats.disabledUserCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatisticsOverview } from '../api/adminStatistics'
import {
  User, Goods, Collection, Tickets, StarFilled,
  Clock, CircleCheckFilled, RemoveFilled
} from '@element-plus/icons-vue'

const stats = ref({
  userCount: 0, disabledUserCount: 0,
  goodsCount: 0, pendingGoodsCount: 0, approvedGoodsCount: 0, soldGoodsCount: 0,
  demandCount: 0, pendingDemandCount: 0, approvedDemandCount: 0,
  orderCount: 0, pendingOrderCount: 0, completedOrderCount: 0,
  favoriteCount: 0,
  todayUserCount: 0, todayGoodsCount: 0, todayDemandCount: 0, todayOrderCount: 0
})

onMounted(async () => {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      Object.assign(stats.value, res.data)
    }
  } catch {
    // 已经过 request 拦截器处理
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.section-title::before {
  content: '';
  width: 3px;
  height: 16px;
  background: linear-gradient(135deg, var(--primary), var(--accent-light));
  border-radius: 2px;
}

.stat-section {
  margin-bottom: 36px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-glass);
  border-radius: var(--radius);
  backdrop-filter: blur(8px);
  transition: var(--transition);
}
.stat-card:hover {
  border-color: rgba(99, 102, 241, 0.35);
  box-shadow: 0 2px 16px rgba(99, 102, 241, 0.08);
}
.stat-card.highlight {
  border-color: rgba(6, 182, 212, 0.15);
}
.stat-card.highlight:hover {
  border-color: rgba(6, 182, 212, 0.35);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.stat-label {
  font-size: 12px;
  color: var(--text-muted);
}
.stat-value {
  font-size: 26px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--text-primary), var(--text-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.2;
}
.stat-sub {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
