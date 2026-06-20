<template>
  <router-link :to="`/goods/${goods.id}`" class="goods-card glass-card">
    <div class="card-img">
      <img
        v-if="goods.mainImage"
        :src="goods.mainImage"
        :alt="goods.title"
        @error="handleImageError"
        v-show="!imgError"
      />
      <div v-if="imgError || !goods.mainImage" class="img-placeholder">
        <el-icon :size="36"><PictureFilled /></el-icon>
      </div>
      <span class="condition-tag">{{ conditionText }}</span>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ goods.title }}</h3>
      <div class="card-meta">
        <span class="card-price">&yen;{{ goods.price }}</span>
        <span class="card-views">
          <el-icon :size="14"><View /></el-icon>
          {{ goods.viewCount || 0 }}
        </span>
      </div>
      <div class="card-footer">
        <span class="card-seller">
          <span class="seller-avatar">{{ (goods.seller?.nickname || goods.seller?.username || '?')[0] }}</span>
          {{ goods.seller?.nickname || goods.seller?.username }}
        </span>
        <span class="card-time">{{ formatTime(goods.createTime) }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { ref, computed } from 'vue'
import { PictureFilled, View } from '@element-plus/icons-vue'
import { conditionLabel } from '../utils/condition'

const props = defineProps({
  goods: { type: Object, required: true }
})

const imgError = ref(false)

const conditionText = computed(() => {
  return conditionLabel(props.goods.conditionLevel)
})

function handleImageError() {
  imgError.value = true
}

function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return d.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.goods-card {
  display: block;
  padding: 0;
  overflow: hidden;
  color: inherit;
}

.card-img {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.08), rgba(6, 182, 212, 0.05));
}
.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}
.goods-card:hover .card-img img {
  transform: scale(1.05);
}
.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
}
.condition-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 11px;
  background: rgba(99, 102, 241, 0.8);
  color: white;
  backdrop-filter: blur(4px);
}

.card-body {
  padding: 14px 16px;
}
.card-title {
  font-size: 14px;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: var(--text-primary);
}
.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.card-price {
  font-size: 18px;
  font-weight: 700;
  color: #f87171;
}
.card-views {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: var(--text-muted);
}
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid rgba(99, 102, 241, 0.1);
}
.card-seller {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}
.seller-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.card-time {
  font-size: 11px;
  color: var(--text-muted);
}
</style>