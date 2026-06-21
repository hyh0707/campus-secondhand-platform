<template>
  <div class="match-card">
    <!-- 匹配分数徽章 -->
    <div class="score-orb">
      <div class="score-ring">
        <svg viewBox="0 0 100 100" class="score-svg">
          <circle cx="50" cy="50" r="42" fill="none" stroke="rgba(99,102,241,0.15)" stroke-width="6" />
          <circle
            cx="50" cy="50" r="42"
            fill="none"
            :stroke="`url(#${scoreGradId})`"
            stroke-width="6"
            stroke-linecap="round"
            :stroke-dasharray="Math.PI * 84"
            :stroke-dashoffset="ringOffset"
            transform="rotate(-90 50 50)"
          />
          <defs>
            <linearGradient :id="scoreGradId" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stop-color="#6366f1" />
              <stop offset="100%" stop-color="#06b6d4" />
            </linearGradient>
          </defs>
          <text x="50" y="53" text-anchor="middle" font-size="22" font-weight="700" fill="currentColor">
            {{ matchScore }}
          </text>
          <text x="50" y="71" text-anchor="middle" font-size="10" fill="currentColor" opacity="0.6">
            匹配度
          </text>
        </svg>
      </div>
    </div>

    <!-- 卡片主体 -->
    <div class="card-main">
      <img
        v-if="mainImage"
        :src="getImageUrl(mainImage)"
        :alt="title"
        class="card-image"
        @error="imgErr = true"
        v-show="!imgErr"
      />
      <div v-if="imgErr || !mainImage" class="img-placeholder">
        <el-icon :size="28"><PictureFilled /></el-icon>
      </div>

      <div class="card-info">
        <h3 class="card-title">{{ title }}</h3>

        <div class="card-meta">
          <!-- 商品模式 -->
          <template v-if="mode === 'goods'">
            <span class="price">&yen;{{ data.price }}</span>
            <span class="condition-tag" v-if="data.conditionLevel">{{ conditionLabel(data.conditionLevel) }}</span>
          </template>
          <!-- 求购模式 -->
          <template v-else>
            <span class="price budget">&yen;{{ data.minPrice }} - &yen;{{ data.maxPrice }}</span>
            <span class="condition-tag" v-if="data.expectedCondition">{{ conditionLabel(data.expectedCondition) }}</span>
            <span class="location-tag" v-if="data.expectedLocation">
              <el-icon :size="10"><Location /></el-icon>
              {{ data.expectedLocation }}
            </span>
          </template>
        </div>

        <div class="seller-line">
          <span class="seller-name">{{ data.sellerNickname || data.userNickname || '匿名' }}</span>
        </div>

        <!-- 匹配原因 -->
        <div class="reasons" v-if="reasonItems.length">
          <span v-for="(r, i) in reasonItems" :key="i" class="reason-tag">{{ r }}</span>
        </div>
        <div class="reasons" v-else>
          <span class="reason-tag reason-default">系统根据多特征综合推荐</span>
        </div>

        <!-- 操作 -->
        <div class="card-action">
          <router-link :to="detailLink" class="btn-view">
            {{ mode === 'goods' ? '查看商品' : '查看求购' }}
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { PictureFilled, Location } from '@element-plus/icons-vue'
import { conditionLabel as condLabel } from '../utils/condition'
import { getImageUrl } from '../utils/image'

const props = defineProps({
  data: { type: Object, required: true },
  mode: { type: String, required: true, validator: v => ['goods', 'demand'].includes(v) }
})

const imgErr = ref(false)

const mainImage = computed(() => props.data.mainImage || '')
const title = computed(() => props.data.title || '')

const matchScore = computed(() => {
  const s = props.data.matchScore ?? props.data.score ?? 0
  return Math.max(0, Math.min(100, Math.round(Number(s))))
})

/** 环形进度条 dashoffset：周长 * (1 - score%) */
const ringOffset = computed(() => {
  const circumference = Math.PI * 84
  return circumference * (1 - matchScore.value / 100)
})

const scoreGradId = computed(() => `scoreGrad-${props.data.id || props.data.goodsId || props.data.demandId || Math.random().toString(36).slice(2, 8)}`)

const reasonItems = computed(() => {
  const val = props.data.matchReasons ?? props.data.matchReason ?? props.data.reasons ?? []
  if (Array.isArray(val)) return val.filter(Boolean)
  if (typeof val === 'string') return val.split(/[,，]/).filter(Boolean).map(s => s.trim())
  return []
})

const detailLink = computed(() => {
  const id = props.data.id || props.data.goodsId || props.data.demandId || ''
  if (props.mode === 'goods') return `/goods/${id}`
  return `/demand/${id}`
})

function conditionLabel(val) { return condLabel(val) }
</script>

<style scoped>
.match-card {
  display: flex;
  gap: 16px;
  padding: 20px;
}

.score-orb {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
}
.score-ring {
  width: 100%;
  height: 100%;
  color: var(--text-primary);
}
.score-svg {
  width: 100%;
  height: 100%;
}

.card-main {
  display: flex;
  gap: 16px;
  flex: 1;
  min-width: 0;
}
.card-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 10px;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(99,102,241,0.08), rgba(6,182,212,0.05));
}
.img-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(99,102,241,0.08), rgba(6,182,212,0.05));
  color: var(--text-muted);
}
.card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.price {
  font-size: 18px;
  font-weight: 700;
  color: #f87171;
}
.price.budget {
  color: var(--accent-light);
}
.condition-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 20px;
  background: rgba(99,102,241,0.12);
  color: var(--primary-light);
}
.location-tag {
  font-size: 11px;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: var(--text-muted);
}

.seller-line {
  font-size: 12px;
  color: var(--text-muted);
}
.seller-name {
  color: var(--text-secondary);
}

.reasons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.reason-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(6,182,212,0.08);
  color: var(--accent-light);
  border: 1px solid rgba(6,182,212,0.12);
}
.reason-default {
  background: rgba(107,114,128,0.08);
  color: var(--text-muted);
  border-color: transparent;
}

.card-action {
  margin-top: auto;
}
.btn-view {
  display: inline-flex;
  align-items: center;
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: #fff;
  text-decoration: none;
  transition: var(--transition);
}
.btn-view:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(99,102,241,0.35);
}

@media (max-width: 500px) {
  .match-card { flex-direction: column; align-items: center; }
  .card-main { flex-direction: column; align-items: center; text-align: center; }
  .card-image, .img-placeholder { width: 100%; height: 160px; }
  .card-meta { justify-content: center; }
  .reasons { justify-content: center; }
}
</style>