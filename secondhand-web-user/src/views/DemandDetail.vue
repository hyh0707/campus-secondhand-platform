<template>
  <div class="detail-page">
    <div class="page-container-narrow">
      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 不存在 -->
      <div v-else-if="!demand" class="empty-state">
        <el-icon :size="48"><WarningFilled /></el-icon>
        <p>求购信息不存在或已关闭</p>
        <el-button type="primary" @click="$router.push('/demand')">返回求购列表</el-button>
      </div>

      <template v-else>
        <div class="back-row">
          <el-button text @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <span class="breadcrumb">求购详情</span>
        </div>

        <div class="gls-card detail-card">
          <div class="detail-header">
            <span class="category-badge">{{ categoryLabel(demand.categoryId) }}</span>
            <span class="status-tag" :class="'status-' + demand.status">{{ statusLabel(demand.status) }}</span>
          </div>

          <h1 class="demand-title">{{ demand.title }}</h1>

          <div class="price-area">
            <span class="price-label">预算范围</span>
            <span class="price-value">&yen;{{ demand.minPrice }} - &yen;{{ demand.maxPrice }}</span>
          </div>

          <div class="meta-grid">
            <div class="meta-item">
              <span class="meta-label">期望成色</span>
              <el-tag :type="conditionTagTypeValue" size="small">{{ conditionLabelText(demand.expectedCondition) }}</el-tag>
            </div>
            <div class="meta-item">
              <span class="meta-label">期望交易地点</span>
              <span class="meta-value">{{ demand.expectedLocation || '未设置' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">发布时间</span>
              <span class="meta-value">{{ formatTime(demand.createTime) }}</span>
            </div>
            <div class="meta-item" v-if="demand.keywords">
              <span class="meta-label">关键词</span>
              <div class="keywords-wrap">
                <span v-for="(kw, idx) in keywordList" :key="idx" class="keyword-tag">{{ kw }}</span>
              </div>
            </div>
          </div>

          <!-- 发布者 -->
          <div class="user-card glass-card">
            <div class="user-header">
              <span class="user-avatar">{{ (demand.user?.nickname || demand.user?.username || '?')[0] }}</span>
              <div class="user-info">
                <span class="user-name">{{ demand.user?.nickname || demand.user?.username }}</span>
                <span class="user-username">@{{ demand.user?.username }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="gls-card description-card">
          <h3 class="desc-title">详细描述</h3>
          <div class="desc-content" v-if="demand.description">
            <p v-for="(line, idx) in descriptionLines" :key="idx">{{ line }}</p>
          </div>
          <div class="desc-content desc-empty" v-else>
            <p>发布者没有填写描述</p>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, Loading, WarningFilled } from '@element-plus/icons-vue'
import { getDemandDetail } from '../api/demand'
import { conditionLabel, conditionTagType } from '../utils/condition'

const route = useRoute()
const demand = ref(null)
const loading = ref(true)

const categoryLabels = {
  1: '数码产品', 2: '图书教材', 3: '生活用品',
  4: '运动户外', 5: '箱包服饰', 6: '其他'
}

const statusLabels = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已驳回',
  closed: '已关闭'
}

function categoryLabel(id) {
  return categoryLabels[id] || '其他'
}

function statusLabel(s) {
  return statusLabels[s] || s
}

function conditionLabelText(val) {
  return conditionLabel(val)
}

const conditionTagTypeValue = computed(() => conditionTagType(demand.value?.expectedCondition))

const keywordList = computed(() => {
  if (!demand.value?.keywords) return []
  return demand.value.keywords.split(/[,，]/).filter(Boolean).map(k => k.trim())
})

const descriptionLines = computed(() => {
  if (!demand.value?.description) return []
  return demand.value.description.split('\n').filter(Boolean)
})

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getDemandDetail(route.params.id)
    demand.value = res.data
  } catch {
    demand.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 40px 24px 80px;
}
.page-container-narrow {
  max-width: 720px;
  margin: 0 auto;
}
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 24px;
  color: var(--text-muted);
  gap: 12px;
}
.loading-icon {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: var(--text-secondary);
  gap: 16px;
}
.back-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.back-row .el-button {
  color: var(--text-secondary);
}
.back-row .el-button:hover {
  color: var(--primary-light);
}
.breadcrumb {
  font-size: 13px;
  color: var(--text-muted);
}

.detail-card {
  padding: 28px;
  margin-bottom: 20px;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.category-badge {
  font-size: 12px;
  padding: 3px 12px;
  border-radius: 20px;
  background: rgba(99, 102, 241, 0.12);
  color: var(--primary-light);
  font-weight: 500;
}
.status-tag {
  font-size: 12px;
  padding: 3px 12px;
  border-radius: 20px;
  font-weight: 600;
}
.status-pending {
  background: rgba(234, 179, 8, 0.15);
  color: #eab308;
}
.status-approved {
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
}
.status-rejected {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}
.status-closed {
  background: rgba(107, 114, 128, 0.15);
  color: #9ca3af;
}

.demand-title {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.4;
  margin-bottom: 16px;
}
.price-area {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 20px;
}
.price-label {
  font-size: 13px;
  color: var(--text-muted);
}
.price-value {
  font-size: 26px;
  font-weight: 800;
  color: #f87171;
}

.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 20px;
}
.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.meta-label {
  font-size: 12px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.meta-value {
  font-size: 14px;
  color: var(--text-primary);
}
.keywords-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.keyword-tag {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 20px;
  background: rgba(6, 182, 212, 0.08);
  color: var(--accent-light);
  border: 1px solid rgba(6, 182, 212, 0.12);
}

.user-card {
  padding: 16px 20px;
}
.user-header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.user-info {
  display: flex;
  flex-direction: column;
}
.user-name {
  font-size: 15px;
  font-weight: 600;
}
.user-username {
  font-size: 12px;
  color: var(--text-muted);
}

.description-card {
  padding: 28px;
}
.desc-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--primary-light);
}
.desc-content {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-secondary);
}
.desc-content p {
  margin-bottom: 8px;
}
.desc-empty {
  color: var(--text-muted);
  font-style: italic;
}

@media (max-width: 768px) {
  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>