<template>
  <div class="detail-page">
    <div class="page-container-wide">
      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 商品不存在 -->
      <div v-else-if="!goods" class="empty-state">
        <el-icon :size="48"><WarningFilled /></el-icon>
        <p>商品不存在或已下架</p>
        <el-button type="primary" @click="$router.push('/goods')">返回商品列表</el-button>
      </div>

      <!-- 商品详情 -->
      <template v-else>
        <!-- 返回按钮 -->
        <div class="back-row">
          <el-button text @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <span class="breadcrumb">商品详情</span>
        </div>

        <div class="detail-main">
          <!-- 左侧：图片 -->
          <div class="detail-gallery">
            <div class="main-image">
              <img
                v-if="currentImage"
                :src="currentImage"
                :alt="goods.title"
                @error="handleMainImageError"
              />
              <div v-else class="img-placeholder">
                <el-icon :size="56"><PictureFilled /></el-icon>
                <p>暂无图片</p>
              </div>
            </div>
            <div class="thumb-list" v-if="goods.images?.length > 1">
              <div
                v-for="(img, idx) in goods.images"
                :key="idx"
                class="thumb-item"
                :class="{ active: currentImageIdx === idx }"
                @click="currentImageIdx = idx"
              >
                <img :src="img.imageUrl" :alt="`图片${idx + 1}`" @error="handleThumbError($event, idx)" />
              </div>
            </div>
          </div>

          <!-- 右侧：信息 -->
          <div class="detail-info">
            <h1 class="goods-title">{{ goods.title }}</h1>

            <div class="price-area">
              <span class="price">&yen;{{ goods.price }}</span>
              <span class="negotiable" v-if="goods.negotiable === 1">可议价</span>
            </div>

            <div class="meta-grid">
              <div class="meta-item">
                <span class="meta-label">成色</span>
                <el-tag :type="conditionTagTypeValue" size="small">{{ conditionText }}</el-tag>
              </div>
              <div class="meta-item">
                <span class="meta-label">发布时间</span>
                <span class="meta-value">{{ formatTime(goods.createTime) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">浏览量</span>
                <span class="meta-value">{{ goods.viewCount || 0 }} 次</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">收藏数</span>
                <span class="meta-value">{{ goods.favoriteCount || 0 }} 次</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">交易地点</span>
                <span class="meta-value">{{ goods.tradeLocation || '未设置' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">联系方式</span>
                <span class="meta-value contact">{{ goods.contactInfo || '未设置' }}</span>
              </div>
            </div>

            <!-- 卖家信息 -->
            <div class="seller-card glass-card">
              <div class="seller-header">
                <span class="seller-avatar">{{ (goods.seller?.nickname || goods.seller?.username || '?')[0] }}</span>
                <div class="seller-info">
                  <span class="seller-name">{{ goods.seller?.nickname || goods.seller?.username }}</span>
                  <span class="seller-username">@{{ goods.seller?.username }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品描述 -->
        <div class="detail-description glass-card">
          <h3 class="desc-title">商品描述</h3>
          <div class="desc-content" v-if="goods.description">
            <p v-for="(line, idx) in descriptionLines" :key="idx">{{ line }}</p>
          </div>
          <div class="desc-content desc-empty" v-else>
            <p>卖家没有填写描述</p>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, Loading, PictureFilled, WarningFilled } from '@element-plus/icons-vue'
import { getGoodsDetail } from '../api/goods'
import { conditionLabel, conditionTagType } from '../utils/condition'

const route = useRoute()
const goods = ref(null)
const loading = ref(true)
const currentImageIdx = ref(0)
const mainImageError = ref(false)

const currentImage = computed(() => {
  if (mainImageError.value) return ''
  const images = goods.value?.images
  if (!images?.length) return ''
  return images[currentImageIdx.value]?.imageUrl || ''
})

const conditionText = computed(() => conditionLabel(goods.value?.conditionLevel))
const conditionTagTypeValue = computed(() => conditionTagType(goods.value?.conditionLevel))

const descriptionLines = computed(() => {
  if (!goods.value?.description) return []
  return goods.value.description.split('\n').filter(Boolean)
})

function handleMainImageError() {
  mainImageError.value = true
}

function handleThumbError(event, idx) {
  event.target.style.display = 'none'
}

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

async function fetchDetail() {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getGoodsDetail(id)
    goods.value = res.data
  } catch {
    goods.value = null
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
  padding-bottom: 60px;
}
.page-container-wide {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px;
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

/* 返回 */
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

/* 主体 */
.detail-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  margin-bottom: 32px;
}

/* 图库 */
.detail-gallery {
  position: sticky;
  top: 88px;
  align-self: start;
}
.main-image {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: var(--radius);
  overflow: hidden;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.06), rgba(6, 182, 212, 0.04));
  border: 1px solid var(--border-glass);
  margin-bottom: 12px;
}
.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  gap: 12px;
  font-size: 14px;
}
.thumb-list {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}
.thumb-item {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  flex-shrink: 0;
  transition: var(--transition);
  background: rgba(30, 41, 59, 0.5);
}
.thumb-item.active {
  border-color: var(--primary);
  box-shadow: 0 0 12px rgba(99, 102, 241, 0.3);
}
.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 信息 */
.detail-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.goods-title {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.4;
}
.price-area {
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.price {
  font-size: 32px;
  font-weight: 800;
  color: #f87171;
}
.negotiable {
  font-size: 13px;
  padding: 2px 10px;
  border-radius: 20px;
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.meta-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
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
.meta-value.contact {
  color: var(--accent-light);
}

/* 卖家 */
.seller-card {
  padding: 16px 20px;
}
.seller-header {
  display: flex;
  align-items: center;
  gap: 12px;
}
.seller-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: white;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.seller-info {
  display: flex;
  flex-direction: column;
}
.seller-name {
  font-size: 15px;
  font-weight: 600;
}
.seller-username {
  font-size: 12px;
  color: var(--text-muted);
}

/* 描述 */
.detail-description {
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
.desc-content.desc-empty {
  color: var(--text-muted);
  font-style: italic;
}

@media (max-width: 768px) {
  .detail-main {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .detail-gallery {
    position: static;
  }
  .goods-title {
    font-size: 20px;
  }
  .price {
    font-size: 26px;
  }
  .meta-grid {
    grid-template-columns: 1fr;
  }
}
</style>