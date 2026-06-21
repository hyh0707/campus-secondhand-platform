<template>
  <div class="demand-list-page">
    <div class="page-container-wide">
      <div class="page-header-section">
        <h1 class="page-title">求购广场</h1>
        <p class="page-subtitle">浏览同学们的需求，看看有没有你正好能提供的</p>
      </div>

      <!-- 搜索和筛选 -->
      <div class="gls-card filter-card">
        <div class="filter-row">
          <div class="search-box">
            <el-input
              v-model="query.keyword"
              placeholder="搜索求购信息..."
              size="large"
              clearable
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
          </div>
        </div>
        <div class="filter-row">
          <div class="filter-item">
            <span class="filter-label">分类：</span>
            <el-select v-model="query.categoryId" placeholder="全部分类" clearable @change="handleSearch">
              <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
            </el-select>
          </div>
          <div class="filter-item price-range">
            <span class="filter-label">预算：</span>
            <el-input-number v-model="query.minPrice" :min="0" :step="50" placeholder="最低" controls-position="right" @change="handleSearch" />
            <span class="price-sep">-</span>
            <el-input-number v-model="query.maxPrice" :min="0" :step="50" placeholder="最高" controls-position="right" @change="handleSearch" />
          </div>
          <el-button text @click="handleReset" class="reset-btn">重置筛选</el-button>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!demandList.length" class="empty-section">
        <el-icon :size="48"><FolderOpened /></el-icon>
        <p>{{ query.keyword ? '没有找到匹配的求购' : '暂无求购信息' }}</p>
        <router-link v-if="!query.keyword" to="/publish-demand" class="btn-primary">发布求购</router-link>
      </div>

      <!-- 求购列表 -->
      <template v-else>
        <div class="demand-grid">
          <router-link
            v-for="item in demandList"
            :key="item.id"
            :to="`/demand/${item.id}`"
            class="gls-card demand-card"
          >
            <div class="card-header">
              <span class="demand-icon">&#9747;</span>
              <span class="category-badge">{{ categoryLabel(item.categoryId) }}</span>
            </div>
            <h3 class="card-title">{{ item.title }}</h3>
            <div class="card-body">
              <div class="budget-range">
                <span class="budget-label">预算</span>
                <span class="budget-value">&yen;{{ item.minPrice }} - &yen;{{ item.maxPrice }}</span>
              </div>
              <div class="card-tags">
                <span class="tag" v-if="item.expectedCondition">
                  <el-icon :size="12"><Star /></el-icon>
                  {{ conditionLabelText(item.expectedCondition) }}
                </span>
                <span class="tag" v-if="item.expectedLocation">
                  <el-icon :size="12"><Location /></el-icon>
                  {{ item.expectedLocation }}
                </span>
              </div>
            </div>
            <div class="card-footer">
              <span class="card-user">
                <span class="user-avatar">{{ (item.user?.nickname || item.user?.username || '?')[0] }}</span>
                {{ item.user?.nickname || item.user?.username }}
              </span>
              <span class="card-time">{{ formatTime(item.createTime) }}</span>
            </div>
          </router-link>
        </div>

        <div class="pagination-wrap" v-if="total > query.pageSize">
          <el-pagination
            v-model:current-page="query.pageNum"
            :page-size="query.pageSize"
            :total="total"
            layout="prev, pager, next"
            background
            @current-change="fetchDemands"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Loading, FolderOpened, Star, Location } from '@element-plus/icons-vue'
import { getDemandList } from '../api/demand'
import { conditionLabel } from '../utils/condition'

const loading = ref(false)
const demandList = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 12,
  keyword: '',
  categoryId: null,
  minPrice: null,
  maxPrice: null
})

const categories = [
  { value: 1, label: '数码产品' },
  { value: 2, label: '图书教材' },
  { value: 3, label: '生活用品' },
  { value: 4, label: '运动户外' },
  { value: 5, label: '箱包服饰' },
  { value: 6, label: '其他' }
]

function categoryLabel(id) {
  return categories.find(c => c.value === id)?.label || '其他'
}

function conditionLabelText(val) {
  return conditionLabel(val)
}

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function handleSearch() {
  query.pageNum = 1
  fetchDemands()
}

function handleReset() {
  query.keyword = ''
  query.categoryId = null
  query.minPrice = null
  query.maxPrice = null
  query.pageNum = 1
  fetchDemands()
}

async function fetchDemands() {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.categoryId) params.categoryId = query.categoryId
    if (query.minPrice !== null) params.minPrice = query.minPrice
    if (query.maxPrice !== null) params.maxPrice = query.maxPrice

    const res = await getDemandList(params)
    demandList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    demandList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDemands()
})
</script>

<style scoped>
.demand-list-page {
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

.filter-card {
  padding: 20px 24px;
  margin-bottom: 24px;
}
.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.filter-row + .filter-row {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px solid rgba(99, 102, 241, 0.06);
}
.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
  min-width: 280px;
}
.search-box .el-input {
  max-width: 420px;
}
.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.filter-label {
  font-size: 13px;
  color: var(--text-muted);
  white-space: nowrap;
}
.price-sep {
  color: var(--text-muted);
  margin: 0 4px;
}
.reset-btn {
  color: var(--text-muted);
  font-size: 13px;
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

.demand-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.demand-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  text-decoration: none;
  color: inherit;
  transition: var(--transition);
}
.demand-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35);
  border-color: rgba(99, 102, 241, 0.3);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.demand-icon {
  font-size: 22px;
  color: var(--accent);
}
.category-badge {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 20px;
  background: rgba(99, 102, 241, 0.12);
  color: var(--primary-light);
  font-weight: 500;
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
.budget-range {
  display: flex;
  align-items: center;
  gap: 8px;
}
.budget-label {
  font-size: 12px;
  color: var(--text-muted);
}
.budget-value {
  font-size: 16px;
  font-weight: 700;
  color: #f87171;
}
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 6px;
}
.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 20px;
  background: rgba(6, 182, 212, 0.08);
  color: var(--accent-light);
  border: 1px solid rgba(6, 182, 212, 0.12);
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(99, 102, 241, 0.06);
  margin-top: auto;
}
.card-user {
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 6px;
}
.user-avatar {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}
.card-time {
  font-size: 11px;
  color: var(--text-extra);
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 1024px) {
  .demand-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 640px) {
  .demand-grid {
    grid-template-columns: 1fr;
  }
  .search-box {
    flex-direction: column;
  }
  .search-box .el-input {
    max-width: 100%;
  }
}
</style>