<template>
  <div class="favorites-page">
    <div class="page-container-wide">
      <div class="page-header-section">
        <h1 class="page-title">我的收藏</h1>
        <p class="page-subtitle">你收藏的商品都在这里</p>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!favoriteList.length" class="empty-section">
        <el-icon :size="48"><FolderOpened /></el-icon>
        <p>还没有收藏任何商品</p>
        <router-link to="/goods" class="btn-primary">去逛逛</router-link>
      </div>

      <!-- 收藏列表 -->
      <template v-else>
        <div class="goods-grid">
          <div v-for="item in favoriteList" :key="item.favoriteId" class="gls-card goods-card">
            <router-link :to="`/goods/${item.goodsId}`" class="card-cover">
              <img
                v-if="item.mainImage"
                :src="getImageUrl(item.mainImage)"
                :alt="item.title"
                @error="imgErrList[item.favoriteId] = true"
              />
              <div v-else class="img-placeholder">
                <el-icon :size="32"><PictureFilled /></el-icon>
              </div>
            </router-link>
            <div class="card-body">
              <router-link :to="`/goods/${item.goodsId}`" class="card-title-link">
                <h3 class="card-title">{{ item.title }}</h3>
              </router-link>
              <div class="card-price">&yen;{{ item.price }}</div>
              <div class="card-meta">
                <span class="meta-item" v-if="item.conditionLevel">
                  <el-tag size="small">{{ conditionLabelText(item.conditionLevel) }}</el-tag>
                </span>
                <span class="meta-item" v-if="item.status === 'off_shelf' || item.status === 'sold'">
                  <el-tag size="small" type="danger">已下架</el-tag>
                </span>
              </div>
              <div class="card-footer">
                <span class="fav-time">{{ formatTime(item.createTime) }}</span>
                <el-button size="small" text type="danger" @click="handleDelete(item.goodsId)">
                  取消收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrap" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            background
            @current-change="fetchFavorites"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, FolderOpened, PictureFilled } from '@element-plus/icons-vue'
import { getFavoriteList, deleteFavorite } from '../api/favorite'
import { conditionLabel } from '../utils/condition'
import { getImageUrl } from '../utils/image'

const loading = ref(false)
const favoriteList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const imgErrList = reactive({})

function conditionLabelText(val) {
  return conditionLabel(val)
}

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

async function fetchFavorites() {
  loading.value = true
  try {
    const res = await getFavoriteList({ pageNum: pageNum.value, pageSize: pageSize.value })
    favoriteList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    favoriteList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function handleDelete(goodsId) {
  try {
    await deleteFavorite(goodsId)
    ElMessage.success('已取消收藏')
    // 如果当前页只剩一条且不是第一页，回退一页
    if (favoriteList.value.length === 1 && pageNum.value > 1) {
      pageNum.value--
    }
    fetchFavorites()
  } catch (err) {
    const msg = err?.response?.data?.message || '操作失败'
    ElMessage.error(msg)
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  padding: 40px 24px 80px;
}
.page-container-wide {
  max-width: 1100px;
  margin: 0 auto;
}
.page-header-section {
  text-align: center;
  margin-bottom: 28px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-light), var(--accent-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.page-subtitle {
  color: var(--text-muted);
  margin-top: 8px;
  font-size: 14px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
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
.empty-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: var(--text-secondary);
  gap: 16px;
}
.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 22px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  border-radius: 8px;
  color: #fff;
  font-weight: 600;
  font-size: 14px;
  text-decoration: none;
  transition: var(--transition);
}
.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.35);
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.goods-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.card-cover {
  width: 100%;
  aspect-ratio: 1;
  display: block;
  overflow: hidden;
  background: rgba(30, 41, 59, 0.5);
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
}
.card-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}
.card-title-link {
  text-decoration: none;
  color: inherit;
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
.card-price {
  font-size: 18px;
  font-weight: 700;
  color: #f87171;
}
.card-meta {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid rgba(99, 102, 241, 0.06);
  margin-top: auto;
}
.fav-time {
  font-size: 11px;
  color: var(--text-extra);
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 1024px) {
  .goods-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .goods-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 480px) {
  .goods-grid { grid-template-columns: 1fr; }
}
</style>