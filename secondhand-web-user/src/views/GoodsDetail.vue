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
                <img :src="getImageUrl(img.imageUrl)" :alt="`图片${idx + 1}`" @error="handleThumbError($event, idx)" />
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

            <!-- 操作按钮 -->
            <div class="action-row" v-if="isLoggedIn && !isMyGoods">
              <el-button
                :type="isFavorited ? 'warning' : 'default'"
                size="large"
                :loading="favLoading"
                @click="toggleFavorite"
                class="action-btn"
              >
                <el-icon><StarFilled v-if="isFavorited" /><Star v-else /></el-icon>
                {{ isFavorited ? '取消收藏' : '收藏' }}
              </el-button>
              <el-button
                type="primary"
                size="large"
                @click="openBuyDialog"
                class="action-btn buy-btn"
              >
                <el-icon><ShoppingCart /></el-icon>
                我想要
              </el-button>
            </div>

            <!-- 卖家信息 -->
            <div class="seller-card glass-card">
              <div class="seller-header">
                <span class="seller-avatar">{{ (goods.seller?.nickname || goods.seller?.username || '?')[0] }}</span>
                <div class="seller-info">
                  <span class="seller-name">{{ goods.seller?.nickname || goods.seller?.username }}</span>
                  <span class="seller-username">@{{ goods.seller?.username }}</span>
                  <span class="seller-tag" v-if="isMyGoods">我的商品</span>
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

    <!-- 购买意向弹窗 -->
    <el-dialog
      v-model="buyDialogVisible"
      title="发起购买意向"
      width="460px"
      :close-on-click-modal="false"
    >
      <el-form :model="buyForm" label-position="top" size="large">
        <el-form-item label="商品">
          <span class="buy-goods-title">{{ goods?.title }}</span>
        </el-form-item>
        <el-form-item label="留言">
          <el-input
            v-model="buyForm.message"
            type="textarea"
            :rows="3"
            placeholder="你好，我想购买这个商品，可以线下交易吗？"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="buyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="buySubmitting" @click="handleBuySubmit">提交意向</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Loading, PictureFilled, WarningFilled, Star, StarFilled, ShoppingCart } from '@element-plus/icons-vue'
import { getGoodsDetail } from '../api/goods'
import { addFavorite, deleteFavorite, getFavoriteList } from '../api/favorite'
import { createOrder } from '../api/order'
import { conditionLabel, conditionTagType } from '../utils/condition'
import { getImageUrl } from '../utils/image'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const goods = ref(null)
const loading = ref(true)
const currentImageIdx = ref(0)
const mainImageError = ref(false)

const currentImage = computed(() => {
  if (mainImageError.value) return ''
  const images = goods.value?.images
  if (!images?.length) return ''
  return getImageUrl(images[currentImageIdx.value]?.imageUrl)
})

const conditionText = computed(() => conditionLabel(goods.value?.conditionLevel))
const conditionTagTypeValue = computed(() => conditionTagType(goods.value?.conditionLevel))

const descriptionLines = computed(() => {
  if (!goods.value?.description) return []
  return goods.value.description.split('\n').filter(Boolean)
})

const isLoggedIn = computed(() => userStore.isLoggedIn())
const isMyGoods = computed(() => {
  if (!goods.value?.seller?.id || !userStore.userInfo?.id) return false
  return goods.value.seller.id === userStore.userInfo.id
})

// 收藏
const favLoading = ref(false)
const isFavorited = ref(false)

async function toggleFavorite() {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  if (!goods.value) return
  favLoading.value = true
  try {
    if (isFavorited.value) {
      await deleteFavorite(goods.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(goods.value.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (err) {
    const msg = err?.response?.data?.message || ''
    if (msg.includes('已收藏')) {
      // 后端已收藏，视为收藏成功
      isFavorited.value = true
    } else {
      ElMessage.error(msg || '操作失败')
    }
  } finally {
    favLoading.value = false
  }
}

// 购买意向弹窗
const buyDialogVisible = ref(false)
const buySubmitting = ref(false)
const buyForm = ref({ message: '' })

function openBuyDialog() {
  if (!isLoggedIn.value) {
    router.push('/login')
    return
  }
  buyForm.value.message = ''
  buyDialogVisible.value = true
}

async function handleBuySubmit() {
  if (!buyForm.value.message.trim()) {
    ElMessage.warning('请填写留言')
    return
  }
  buySubmitting.value = true
  try {
    await createOrder({
      goodsId: goods.value.id,
      message: buyForm.value.message.trim()
    })
    ElMessage.success('交易意向已提交，请等待卖家处理')
    buyDialogVisible.value = false
  } catch (err) {
    const msg = err?.response?.data?.message || '提交失败，请稍后重试'
    ElMessage.error(msg)
  } finally {
    buySubmitting.value = false
  }
}

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
    // 登录后检查收藏状态
    await checkFavoriteStatus()
  } catch {
    goods.value = null
  } finally {
    loading.value = false
  }
}

async function checkFavoriteStatus() {
  if (!isLoggedIn.value || !goods.value) return
  try {
    const res = await getFavoriteList({ pageNum: 1, pageSize: 100 })
    const records = res.data?.records || res.data?.list || res.data || []
    isFavorited.value = records.some((item) => item.goodsId === goods.value.id)
  } catch {
    // 无法查询收藏状态时不做处理
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
.seller-tag {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 20px;
  background: rgba(99, 102, 241, 0.15);
  color: var(--primary-light);
  font-weight: 500;
}

/* 操作按钮 */
.action-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
.action-btn {
  flex: 1;
  border-radius: var(--radius-sm) !important;
  font-weight: 600;
  height: 44px;
}
.buy-btn {
  background: linear-gradient(135deg, var(--primary), var(--primary-dark)) !important;
  border: none !important;
}
.buy-btn:hover {
  background: linear-gradient(135deg, var(--primary-light), var(--primary)) !important;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.35);
}
.buy-goods-title {
  font-weight: 600;
  color: var(--text-primary);
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