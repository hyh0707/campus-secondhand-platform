<template>
  <router-link :to="`/goods/${goodsId}`" class="rec-card glass-card">
    <div class="card-img">
      <img
        v-if="goods.mainImage"
        :src="getImageUrl(goods.mainImage)"
        :alt="goods.title"
        @error="handleImageError"
        v-show="!imgError"
      />
      <div v-if="imgError || !goods.mainImage" class="img-placeholder">
        <el-icon :size="36"><PictureFilled /></el-icon>
      </div>
      <span class="condition-tag">{{ conditionText }}</span>
      <span v-if="goods.recommendScore" class="score-badge">
        <el-icon :size="12"><TrendCharts /></el-icon>
        {{ goods.recommendScore }}分
      </span>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ goods.title }}</h3>
      <div class="card-meta">
        <span class="card-price">&yen;{{ goods.price }}</span>
      </div>
      <div v-if="goods.recommendReasons?.length" class="card-reasons">
        <span v-for="(r, i) in goods.recommendReasons.slice(0, 2)" :key="i" class="reason-tag">{{ r }}</span>
      </div>
      <div class="card-footer">
        <span class="card-seller">{{ goods.sellerNickname || '匿名' }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { ref, computed } from 'vue'
import { PictureFilled, TrendCharts } from '@element-plus/icons-vue'
import { conditionLabel } from '../utils/condition'
import { getImageUrl } from '../utils/image'

const props = defineProps({
  goods: { type: Object, required: true }
})

const imgError = ref(false)

const goodsId = computed(() => props.goods.id || props.goods.goodsId || '')

const conditionText = computed(() => conditionLabel(props.goods.conditionLevel))

function handleImageError() {
  imgError.value = true
}
</script>

<style scoped>
.rec-card {
  display: block;
  padding: 0;
  overflow: hidden;
  color: inherit;
  border-color: rgba(99, 102, 241, 0.25);
}
.rec-card:hover {
  border-color: rgba(6, 182, 212, 0.5);
  box-shadow: 0 0 30px rgba(6, 182, 212, 0.12);
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
.rec-card:hover .card-img img {
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
  left: 10px;
  padding: 3px 10px;
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.75);
  backdrop-filter: blur(8px);
  font-size: 11px;
  color: var(--text-primary);
  border: 1px solid rgba(255, 255, 255, 0.1);
}
.score-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 3px 10px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.85), rgba(6, 182, 212, 0.85));
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.15);
}
.card-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.card-title {
  font-size: 14px;
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
  justify-content: space-between;
}
.card-price {
  font-size: 18px;
  font-weight: 700;
  color: #f87171;
}
.card-reasons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.reason-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(6, 182, 212, 0.1);
  color: var(--accent-light);
  border: 1px solid rgba(6, 182, 212, 0.15);
}
.card-footer {
  padding-top: 10px;
  border-top: 1px solid rgba(99, 102, 241, 0.06);
  margin-top: auto;
}
.card-seller {
  font-size: 12px;
  color: var(--text-muted);
}
</style>