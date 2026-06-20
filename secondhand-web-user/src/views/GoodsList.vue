<template>
  <div class="goods-list-page">
    <div class="page-container-wide">
      <!-- 搜索栏 -->
      <div class="search-bar glass-card">
        <div class="search-row">
          <el-input
            v-model="query.keyword"
            placeholder="搜索商品..."
            :prefix-icon="Search"
            size="large"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
          <el-button type="primary" size="large" class="search-btn" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>

        <!-- 筛选条件 -->
        <div class="filter-row">
          <div class="filter-item">
            <span class="filter-label">分类：</span>
            <el-select v-model="query.categoryId" placeholder="全部分类" clearable size="default" @change="handleSearch">
              <el-option v-for="cat in categories" :key="cat.value" :label="cat.label" :value="cat.value" />
            </el-select>
          </div>
          <div class="filter-item">
            <span class="filter-label">成色：</span>
            <el-select v-model="query.conditionLevel" placeholder="全部成色" clearable size="default" @change="handleSearch">
              <el-option v-for="c in conditions" :key="c.value" :label="c.label" :value="c.value" />
            </el-select>
          </div>
          <div class="filter-item price-range">
            <span class="filter-label">价格：</span>
            <el-input-number v-model="query.minPrice" :min="0" :step="10" placeholder="最低" size="default" controls-position="right" @change="handleSearch" />
            <span class="price-sep">-</span>
            <el-input-number v-model="query.maxPrice" :min="0" :step="10" placeholder="最高" size="default" controls-position="right" @change="handleSearch" />
          </div>
        </div>
      </div>

      <!-- 结果区域 -->
      <div class="results-area">
        <!-- 加载中 -->
        <div v-if="loading" class="loading-state">
          <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
          <p>正在加载商品...</p>
        </div>

        <!-- 空数据 -->
        <EmptyState v-else-if="!loading && goodsList.length === 0" description="暂无符合条件的商品">
          <el-button type="primary" @click="resetFilters">重置筛选</el-button>
        </EmptyState>

        <!-- 商品列表 -->
        <template v-else>
          <div class="results-info">
            <span>共 <strong>{{ total }}</strong> 件商品</span>
          </div>
          <div class="goods-grid">
            <GoodsCard v-for="item in goodsList" :key="item.id" :goods="item" />
          </div>
          <div class="pagination-wrap" v-if="total > query.pageSize">
            <el-pagination
              v-model:current-page="query.pageNum"
              :page-size="query.pageSize"
              :total="total"
              layout="prev, pager, next"
              background
              @current-change="handlePageChange"
            />
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Loading } from '@element-plus/icons-vue'
import { getGoodsList } from '../api/goods'
import GoodsCard from '../components/GoodsCard.vue'
import EmptyState from '../components/EmptyState.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const goodsList = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 12,
  keyword: '',
  categoryId: null,
  minPrice: null,
  maxPrice: null,
  conditionLevel: ''
})

const categories = [
  { value: 1, label: '数码产品' },
  { value: 2, label: '图书教材' },
  { value: 3, label: '生活用品' },
  { value: 4, label: '运动户外' },
  { value: 5, label: '箱包服饰' },
  { value: 6, label: '其他' }
]

const conditions = [
  { value: 'brand_new', label: '全新' },
  { value: 'like_new', label: '几乎全新' },
  { value: 'good', label: '良好' },
  { value: 'fair', label: '一般' },
  { value: 'old', label: '老旧' }
]

async function fetchGoods() {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.categoryId) params.categoryId = query.categoryId
    if (query.conditionLevel) params.conditionLevel = query.conditionLevel
    if (query.minPrice !== null && query.minPrice !== undefined) params.minPrice = query.minPrice
    if (query.maxPrice !== null && query.maxPrice !== undefined) params.maxPrice = query.maxPrice

    const res = await getGoodsList(params)
    goodsList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    goodsList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.pageNum = 1
  fetchGoods()
}

function handlePageChange(page) {
  query.pageNum = page
  fetchGoods()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function resetFilters() {
  query.keyword = ''
  query.categoryId = null
  query.conditionLevel = ''
  query.minPrice = null
  query.maxPrice = null
  query.pageNum = 1
  fetchGoods()
}

// 从 URL query 中读取 keyword
onMounted(() => {
  if (route.query.keyword) {
    query.keyword = route.query.keyword
  }
  fetchGoods()
})
</script>

<style scoped>
.goods-list-page {
  min-height: 100vh;
  padding-bottom: 60px;
}
.page-container-wide {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 搜索栏 */
.search-bar {
  padding: 24px;
  margin-bottom: 24px;
}
.search-row {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.search-input {
  flex: 1;
}
.search-btn {
  height: 40px;
  padding: 0 24px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  border: none;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}
.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.filter-label {
  font-size: 13px;
  color: var(--text-secondary);
  white-space: nowrap;
}
.filter-item :deep(.el-select) {
  width: 140px;
}
.price-range {
  display: flex;
  align-items: center;
}
.price-range :deep(.el-input-number) {
  width: 110px;
}
.price-sep {
  margin: 0 6px;
  color: var(--text-muted);
}

/* 结果 */
.results-area {
  min-height: 300px;
}
.results-info {
  margin-bottom: 16px;
  font-size: 13px;
  color: var(--text-secondary);
}
.results-info strong {
  color: var(--primary-light);
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: var(--text-muted);
  font-size: 14px;
  gap: 12px;
}
.loading-icon {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  .filter-row {
    gap: 12px;
  }
  .filter-item :deep(.el-select) {
    width: 120px;
  }
  .price-range :deep(.el-input-number) {
    width: 90px;
  }
}
</style>