<template>
  <div class="list-page">
    <!-- 筛选区 -->
    <div class="filter-bar glass-card">
      <div class="filter-row">
        <el-input
          v-model="query.keyword"
          placeholder="搜索求购标题、描述、关键词..."
          prefix-icon="Search"
          clearable
          class="filter-input"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="query.status" placeholder="全部状态" clearable class="filter-select">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
          <el-option label="已关闭" value="closed" />
        </el-select>
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable class="filter-select">
          <el-option label="全部" :value="null" />
          <el-option label="数码产品" :value="1" />
          <el-option label="图书教材" :value="2" />
          <el-option label="生活用品" :value="3" />
          <el-option label="运动户外" :value="4" />
          <el-option label="箱包服饰" :value="5" />
          <el-option label="其他" :value="6" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <el-icon :size="32" class="loading-icon"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!list.length" class="empty-state">
      <el-icon :size="48"><FolderOpened /></el-icon>
      <p>暂无求购数据</p>
    </div>

    <!-- 求购列表 -->
    <div v-else class="demand-list">
      <div v-for="item in list" :key="item.id" class="demand-card glass-card">
        <div class="demand-info">
          <div class="demand-top">
            <h3 class="demand-title" @click="openDemandDetail(item.id)">{{ item.title }}</h3>
            <span class="status-badge" :class="'s-' + item.status">{{ demandStatusLabel(item.status) }}</span>
          </div>

          <div class="demand-meta">
            <span class="meta-tag">{{ categoryLabel(item.categoryId) }}</span>
            <span class="meta-tag price-range">&yen;{{ item.minPrice }} - &yen;{{ item.maxPrice }}</span>
            <span class="meta-tag cond" v-if="item.expectedCondition">{{ conditionLabel(item.expectedCondition) }}</span>
            <span class="meta-tag location" v-if="item.expectedLocation">{{ item.expectedLocation }}</span>
            <span class="meta-tag seller">{{ item.user?.nickname || item.user?.username || '匿名' }}</span>
          </div>

          <div class="demand-keywords" v-if="item.keywords">
            <span v-for="(k, i) in item.keywords.split(/[,，\s]+/).filter(Boolean)" :key="i" class="kw-tag">{{ k }}</span>
          </div>

          <p class="demand-desc" v-if="item.description">{{ item.description }}</p>

          <div class="demand-bottom">
            <span class="time">{{ formatTime(item.createTime) }}</span>
            <div class="action-btns">
              <el-button size="small" text type="primary" @click="openDemandDetail(item.id)">查看详情</el-button>
              <template v-if="item.status === 'pending'">
                <el-button size="small" type="success" @click="handleApprove(item)">
                  <el-icon :size="14"><Select /></el-icon>
                  通过
                </el-button>
                <el-button size="small" type="danger" @click="handleReject(item)">
                  <el-icon :size="14"><CloseBold /></el-icon>
                  拒绝
                </el-button>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > query.pageSize">
      <el-pagination
        v-model:current-page="query.pageNum"
        :page-size="query.pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 审核通过确认 -->
    <el-dialog v-model="approveVisible" title="审核通过" width="420px" class="admin-dialog">
      <p>确定要通过求购「<strong>{{ currentItem?.title }}</strong>」吗？</p>
      <span class="dialog-footer">
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="success" :loading="auditLoading" @click="confirmApprove">确认通过</el-button>
      </span>
    </el-dialog>

    <!-- 审核拒绝 -->
    <el-dialog v-model="rejectVisible" title="审核拒绝" width="420px" class="admin-dialog">
      <p>拒绝求购「<strong>{{ currentItem?.title }}</strong>」</p>
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="3"
        placeholder="请填写拒绝原因"
        class="reason-input"
      />
      <span class="dialog-footer">
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="auditLoading" :disabled="!rejectReason.trim()" @click="confirmReject">确认拒绝</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search, Refresh, Loading, FolderOpened,
  Select, CloseBold
} from '@element-plus/icons-vue'
import { getAdminDemandList, auditDemand } from '../api/adminDemand'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: '',
  categoryId: null
})

const auditLoading = ref(false)
const currentItem = ref(null)
const approveVisible = ref(false)
const rejectVisible = ref(false)
const rejectReason = ref('')

// ====== 分类映射 ======
const categoryMap = { 1: '数码产品', 2: '图书教材', 3: '生活用品', 4: '运动户外', 5: '箱包服饰', 6: '其他' }
function categoryLabel(val) { return categoryMap[val] || '其他' }

// ====== 状态映射 ======
const demandStatusMap = { pending: '待审核', approved: '已通过', rejected: '已拒绝', closed: '已关闭' }
function demandStatusLabel(val) { return demandStatusMap[val] || val }

// ====== 成色映射 ======
const condMap = { brand_new: '全新', like_new: '几乎全新', minor_flaws: '轻微瑕疵', obvious_flaws: '明显瑕疵' }
function conditionLabel(val) { return condMap[val] || val || '' }

// ====== 时间格式化 ======
function formatTime(val) {
  if (!val) return ''
  return String(val).replace('T', ' ').substring(0, 19)
}

// ====== 加载列表 ======
async function fetchList() {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.status) params.status = query.status
    if (query.categoryId) params.categoryId = query.categoryId

    const res = await getAdminDemandList(params)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

// ====== 事件 ======
function handleSearch() { query.pageNum = 1; fetchList() }
function handleReset() {
  query.pageNum = 1
  query.keyword = ''
  query.status = ''
  query.categoryId = null
  fetchList()
}
function handlePageChange(page) { query.pageNum = page; fetchList() }

function openDemandDetail(id) {
  window.open(`/demand/${id}`, '_blank')
}

// ====== 审核通过 ======
function handleApprove(item) {
  currentItem.value = item
  approveVisible.value = true
}
async function confirmApprove() {
  auditLoading.value = true
  try {
    await auditDemand(currentItem.value.id, { status: 'approved', reason: '审核通过' })
    ElMessage.success('审核通过')
    approveVisible.value = false
    fetchList()
  } catch { /* handled by interceptor */ } finally { auditLoading.value = false }
}

// ====== 审核拒绝 ======
function handleReject(item) {
  currentItem.value = item
  rejectReason.value = ''
  rejectVisible.value = true
}
async function confirmReject() {
  if (!rejectReason.value.trim()) return
  auditLoading.value = true
  try {
    await auditDemand(currentItem.value.id, { status: 'rejected', reason: rejectReason.value })
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    fetchList()
  } catch { /* handled by interceptor */ } finally { auditLoading.value = false }
}

onMounted(() => { fetchList() })
</script>

<style scoped>
.list-page { max-width: 1200px; }

/* ======== 筛选区 ======== */
.filter-bar { padding: 18px 20px; margin-bottom: 20px; }
.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.filter-input { width: 220px; }
.filter-select { width: 130px; }

/* ======== 加载 & 空 ======== */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: var(--text-muted);
  gap: 10px;
}
.loading-icon { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* ======== 求购卡片 ======== */
.demand-list { display: flex; flex-direction: column; gap: 12px; }

.demand-card {
  padding: 18px 20px;
  transition: var(--transition);
}
.demand-card:hover { border-color: rgba(99,102,241,0.35); }

.demand-info { display: flex; flex-direction: column; gap: 10px; }

.demand-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}
.demand-title {
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.demand-title:hover { color: var(--accent-light); }

.status-badge {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 20px;
  font-weight: 500;
  flex-shrink: 0;
}
.s-pending { background: rgba(245,158,11,0.15); color: #fbbf24; }
.s-approved { background: rgba(16,185,129,0.15); color: #34d399; }
.s-rejected { background: rgba(239,68,68,0.15); color: #f87171; }
.s-closed  { background: rgba(107,114,128,0.15); color: #9ca3af; }

.demand-meta { display: flex; gap: 6px; flex-wrap: wrap; }
.meta-tag {
  font-size: 11px;
  padding: 1px 8px;
  border-radius: 12px;
  background: rgba(99,102,241,0.08);
  color: var(--text-secondary);
}
.meta-tag.price-range { color: var(--accent-light); font-weight: 500; }
.meta-tag.seller { color: var(--accent-light); }

.demand-keywords { display: flex; gap: 4px; flex-wrap: wrap; }
.kw-tag {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  background: rgba(6,182,212,0.08);
  color: var(--accent-light);
  border: 1px solid rgba(6,182,212,0.12);
}

.demand-desc {
  font-size: 12px;
  color: var(--text-muted);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.demand-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 8px;
  border-top: 1px solid rgba(99,102,241,0.06);
}
.time { font-size: 12px; color: var(--text-muted); }
.action-btns { display: flex; gap: 4px; }

/* ======== 分页 ======== */
.pagination-wrap { display: flex; justify-content: center; margin-top: 28px; }

/* ======== 弹窗 ======== */
.reason-input { margin-top: 12px; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 8px; margin-top: 20px; }
</style>
