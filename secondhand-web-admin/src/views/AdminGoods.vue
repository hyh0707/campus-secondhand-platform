<template>
  <div class="list-page">
    <!-- 筛选区 -->
    <div class="filter-bar glass-card">
      <div class="filter-row">
        <el-input
          v-model="query.keyword"
          placeholder="搜索商品标题、描述..."
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
          <el-option label="已下架" value="offline" />
          <el-option label="已售出" value="sold" />
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
      <p>暂无商品数据</p>
    </div>

    <!-- 商品列表 -->
    <div v-else class="goods-list">
      <div v-for="item in list" :key="item.id" class="goods-card glass-card">
        <!-- 图片 -->
        <div class="goods-img" @click="openDetail(item)">
          <img
            v-if="item.mainImage"
            :src="getImageUrl(item.mainImage)"
            :alt="item.title"
            @error="handleImgError"
          />
          <div v-else class="img-ph">
            <el-icon :size="28"><PictureFilled /></el-icon>
          </div>
          <span class="status-badge" :class="'s-' + item.status">{{ goodsStatusLabel(item.status) }}</span>
        </div>

        <!-- 信息 -->
        <div class="goods-info">
          <h3 class="goods-title" @click="openDetail(item)">{{ item.title }}</h3>
          <div class="goods-meta">
            <span class="meta-tag">{{ categoryLabel(item.categoryId) }}</span>
            <span class="meta-tag cond">{{ conditionLabel(item.conditionLevel) }}</span>
            <span class="meta-tag seller">{{ item.seller?.nickname || item.seller?.username || '匿名' }}</span>
          </div>
          <div class="goods-stats">
            <span><el-icon :size="13"><View /></el-icon> {{ item.viewCount || 0 }}</span>
            <span><el-icon :size="13"><StarFilled /></el-icon> {{ item.favoriteCount || 0 }}</span>
            <span class="time">{{ formatTime(item.createTime) }}</span>
          </div>
          <p class="goods-desc" v-if="item.description">{{ item.description }}</p>
        </div>

        <!-- 价格 + 操作 -->
        <div class="goods-actions">
          <span class="goods-price">&yen;{{ item.price }}</span>
          <div class="action-btns">
            <el-button size="small" text type="primary" @click="openDetail(item)">
              查看
            </el-button>
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
            <template v-if="item.status === 'approved'">
              <el-button size="small" type="warning" @click="handleOffline(item)">
                <el-icon :size="14"><Remove /></el-icon>
                下架
              </el-button>
            </template>
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

    <!-- 商品详情弹窗 -->
    <el-dialog v-model="detailVisible" width="680px" class="tech-detail-dialog" @closed="detailItem = null">
      <template #header>
        <div class="detail-dialog-header">
          <span class="detail-dialog-title">商品详情</span>
          <span v-if="detailItem" class="detail-status-badge" :class="'ds-' + detailItem.status">
            {{ goodsStatusLabel(detailItem.status) }}
          </span>
        </div>
      </template>

      <template v-if="detailItem">
        <div class="detail-body">
          <!-- 图片 -->
          <div class="detail-img">
            <img
              v-if="detailItem.mainImage"
              :src="getImageUrl(detailItem.mainImage)"
              :alt="detailItem.title"
              @error="e => { e.target.style.display = 'none' }"
            />
            <div v-else class="detail-img-ph">
              <el-icon :size="48"><PictureFilled /></el-icon>
              <span>暂无图片</span>
            </div>
          </div>

          <!-- 信息 -->
          <div class="detail-info">
            <div class="detail-info-top">
              <h2 class="detail-title">{{ detailItem.title }}</h2>
              <span class="detail-price">&yen;{{ detailItem.price }}</span>
            </div>

            <div class="detail-meta">
              <span class="meta-tag">{{ categoryLabel(detailItem.categoryId) }}</span>
              <span class="meta-tag cond" v-if="detailItem.conditionLevel">{{ conditionLabel(detailItem.conditionLevel) }}</span>
            </div>

            <div class="detail-fields">
              <div class="field-row">
                <span class="field-label">发布者</span>
                <span class="field-value">{{ detailItem.seller?.nickname || detailItem.seller?.username || '匿名' }}</span>
              </div>
              <div class="field-row">
                <span class="field-label">交易地点</span>
                <span class="field-value">{{ detailItem.tradeLocation || '未设置' }}</span>
              </div>
              <div class="field-row">
                <span class="field-label">联系方式</span>
                <span class="field-value">{{ detailItem.contactInfo || '未设置' }}</span>
              </div>
              <div class="field-row">
                <span class="field-label">浏览量</span>
                <span class="field-value">{{ detailItem.viewCount || 0 }}</span>
              </div>
              <div class="field-row">
                <span class="field-label">收藏数</span>
                <span class="field-value">{{ detailItem.favoriteCount || 0 }}</span>
              </div>
              <div class="field-row">
                <span class="field-label">发布时间</span>
                <span class="field-value">{{ formatTime(detailItem.createTime) }}</span>
              </div>
            </div>

            <div class="detail-desc" v-if="detailItem.description">
              <span class="desc-label">商品描述</span>
              <p>{{ detailItem.description }}</p>
            </div>
          </div>
        </div>

        <!-- 底部操作 -->
        <div class="detail-footer">
          <el-button class="btn-cancel" @click="detailVisible = false">关闭</el-button>
          <template v-if="detailItem.status === 'pending'">
            <el-button type="success" @click="detailVisible = false; handleApprove(detailItem)">
              <el-icon :size="14"><Select /></el-icon>
              审核通过
            </el-button>
            <el-button type="danger" @click="detailVisible = false; handleReject(detailItem)">
              <el-icon :size="14"><CloseBold /></el-icon>
              审核拒绝
            </el-button>
          </template>
          <template v-if="detailItem.status === 'approved'">
            <el-button type="warning" @click="detailVisible = false; handleOffline(detailItem)">
              <el-icon :size="14"><Remove /></el-icon>
              下架
            </el-button>
          </template>
        </div>
      </template>
    </el-dialog>

    <!-- 审核通过确认 -->
    <el-dialog v-model="approveVisible" title="审核通过" width="420px" class="admin-dialog">
      <p>确定要通过商品「<strong>{{ currentItem?.title }}</strong>」吗？</p>
      <span class="dialog-footer">
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="success" :loading="auditLoading" @click="confirmApprove">确认通过</el-button>
      </span>
    </el-dialog>

    <!-- 审核拒绝 -->
    <el-dialog v-model="rejectVisible" title="审核拒绝" width="420px" class="admin-dialog">
      <p>拒绝商品「<strong>{{ currentItem?.title }}</strong>」</p>
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

    <!-- 下架 -->
    <el-dialog v-model="offlineVisible" title="下架商品" width="420px" class="admin-dialog">
      <p>下架商品「<strong>{{ currentItem?.title }}</strong>」</p>
      <el-input
        v-model="offlineReason"
        type="textarea"
        :rows="3"
        placeholder="请填写下架原因"
        class="reason-input"
      />
      <span class="dialog-footer">
        <el-button @click="offlineVisible = false">取消</el-button>
        <el-button type="warning" :loading="auditLoading" :disabled="!offlineReason.trim()" @click="confirmOffline">确认下架</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search, Refresh, Loading, FolderOpened, PictureFilled,
  View, StarFilled, Select, CloseBold, Remove
} from '@element-plus/icons-vue'
import { getAdminGoodsList, auditGoods, offlineGoods } from '../api/adminGoods'
import { getImageUrl } from '../utils/image'

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

// 审核状态
const auditLoading = ref(false)
const currentItem = ref(null)
const approveVisible = ref(false)
const rejectVisible = ref(false)
const rejectReason = ref('')
const offlineVisible = ref(false)
const offlineReason = ref('')

// 商品详情弹窗
const detailVisible = ref(false)
const detailItem = ref(null)

// ====== 分类映射 ======
const categoryMap = { 1: '数码产品', 2: '图书教材', 3: '生活用品', 4: '运动户外', 5: '箱包服饰', 6: '其他' }
function categoryLabel(val) { return categoryMap[val] || '其他' }

// ====== 状态映射 ======
const goodsStatusMap = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已拒绝',
  offline: '已下架',
  sold: '已售出'
}
function goodsStatusLabel(val) { return goodsStatusMap[val] || val }

// ====== 成色映射 ======
const condMap = { brand_new: '全新', like_new: '几乎全新', minor_flaws: '轻微瑕疵', obvious_flaws: '明显瑕疵' }
function conditionLabel(val) { return condMap[val] || val || '' }

// ====== 时间格式化 ======
function formatTime(val) {
  if (!val) return ''
  const s = String(val).replace('T', ' ').substring(0, 19)
  return s
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

    const res = await getAdminGoodsList(params)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

// ====== 事件 ======
function handleSearch() {
  query.pageNum = 1
  fetchList()
}
function handleReset() {
  query.pageNum = 1
  query.keyword = ''
  query.status = ''
  query.categoryId = null
  fetchList()
}
function handlePageChange(page) {
  query.pageNum = page
  fetchList()
}

function handleImgError(e) {
  e.target.style.display = 'none'
}

function openDetail(item) {
  detailItem.value = item
  detailVisible.value = true
}

// ====== 审核通过 ======
function handleApprove(item) {
  currentItem.value = item
  approveVisible.value = true
}
async function confirmApprove() {
  auditLoading.value = true
  try {
    await auditGoods(currentItem.value.id, { status: 'approved', reason: '审核通过' })
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
    await auditGoods(currentItem.value.id, { status: 'rejected', reason: rejectReason.value })
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    fetchList()
  } catch { /* handled by interceptor */ } finally { auditLoading.value = false }
}

// ====== 下架 ======
function handleOffline(item) {
  currentItem.value = item
  offlineReason.value = ''
  offlineVisible.value = true
}
async function confirmOffline() {
  if (!offlineReason.value.trim()) return
  auditLoading.value = true
  try {
    await offlineGoods(currentItem.value.id, { reason: offlineReason.value })
    ElMessage.success('已下架')
    offlineVisible.value = false
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

/* ======== 商品卡片 ======== */
.goods-list { display: flex; flex-direction: column; gap: 12px; }

.goods-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  transition: var(--transition);
}
.goods-card:hover { border-color: rgba(99,102,241,0.35); }

.goods-img {
  width: 160px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
  cursor: pointer;
  background: rgba(99,102,241,0.06);
}
.goods-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.img-ph {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
}

.status-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 20px;
  backdrop-filter: blur(6px);
  font-weight: 500;
}
.s-pending { background: rgba(245,158,11,0.2); color: #fbbf24; }
.s-approved { background: rgba(16,185,129,0.2); color: #34d399; }
.s-rejected { background: rgba(239,68,68,0.2); color: #f87171; }
.s-offline  { background: rgba(107,114,128,0.2); color: #9ca3af; }
.s-sold     { background: rgba(99,102,241,0.2); color: #a5b4fc; }

.goods-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.goods-title {
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}
.goods-title:hover { color: var(--accent-light); }

.goods-meta { display: flex; gap: 6px; flex-wrap: wrap; }
.meta-tag {
  font-size: 11px;
  padding: 1px 8px;
  border-radius: 12px;
  background: rgba(99,102,241,0.08);
  color: var(--text-secondary);
}
.meta-tag.seller { color: var(--accent-light); }

.goods-stats {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: var(--text-muted);
  align-items: center;
}
.goods-stats span { display: flex; align-items: center; gap: 3px; }
.time { margin-left: auto; }

.goods-desc {
  font-size: 12px;
  color: var(--text-muted);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  flex-shrink: 0;
  gap: 8px;
}
.goods-price {
  font-size: 20px;
  font-weight: 700;
  color: #f87171;
}
.action-btns { display: flex; gap: 4px; flex-wrap: wrap; justify-content: flex-end; }

/* ======== 分页 ======== */
.pagination-wrap { display: flex; justify-content: center; margin-top: 28px; }

/* ======== 弹窗 ======== */
.reason-input { margin-top: 12px; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 8px; margin-top: 20px; }

/* ======== 商品详情弹窗（深色科技风） ======== */
:deep(.tech-detail-dialog) {
  --el-dialog-bg-color: #0f172a;
  --el-dialog-border-radius: 16px;
}
:deep(.tech-detail-dialog .el-dialog) {
  border: 1px solid rgba(99,102,241,0.18);
  box-shadow: 0 0 40px rgba(99,102,241,0.1), 0 8px 32px rgba(0,0,0,0.5);
}
:deep(.tech-detail-dialog .el-dialog__header) {
  padding: 20px 24px 0;
  border-bottom: none;
  margin-right: 0;
}
:deep(.tech-detail-dialog .el-dialog__body) {
  padding: 20px 24px 24px;
  color: var(--text-secondary);
}
:deep(.tech-detail-dialog .el-dialog__headerbtn) {
  top: 20px;
  right: 20px;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(99,102,241,0.08);
  border: 1px solid rgba(99,102,241,0.1);
}
:deep(.tech-detail-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: var(--text-muted);
}
:deep(.tech-detail-dialog .el-dialog__headerbtn:hover) {
  background: rgba(239,68,68,0.12);
}
:deep(.tech-detail-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: var(--danger);
}

.detail-dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.detail-dialog-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
}

/* 状态徽章（弹窗内专用，非 absolute） */
.detail-status-badge {
  font-size: 12px;
  padding: 4px 14px;
  border-radius: 20px;
  font-weight: 600;
  flex-shrink: 0;
}
.ds-pending  { background: rgba(245,158,11,0.15); color: #fbbf24; }
.ds-approved { background: rgba(16,185,129,0.15); color: #34d399; }
.ds-rejected { background: rgba(239,68,68,0.15); color: #f87171; }
.ds-offline  { background: rgba(107,114,128,0.15); color: #9ca3af; }
.ds-sold     { background: rgba(99,102,241,0.15); color: #a5b4fc; }

.detail-body {
  display: flex;
  gap: 24px;
}
.detail-img {
  width: 280px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(99,102,241,0.06), rgba(6,182,212,0.04));
  border: 1px solid rgba(99,102,241,0.08);
  min-height: 200px;
}
.detail-img img {
  width: 100%;
  max-height: 360px;
  object-fit: cover;
  display: block;
}
.detail-img-ph {
  width: 100%;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  gap: 8px;
}
.detail-img-ph span {
  font-size: 12px;
}

.detail-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.detail-info-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}
.detail-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
  flex: 1;
  min-width: 0;
}
.detail-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.detail-price {
  font-size: 22px;
  font-weight: 700;
  color: #f87171;
  flex-shrink: 0;
}
.detail-fields {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: rgba(15,23,42,0.5);
  border: 1px solid rgba(99,102,241,0.06);
  border-radius: 10px;
  padding: 14px;
}
.field-row {
  display: flex;
  gap: 12px;
  font-size: 13px;
}
.field-label {
  width: 70px;
  flex-shrink: 0;
  color: var(--text-muted);
  font-size: 12px;
}
.field-value {
  color: var(--text-secondary);
  word-break: break-all;
}
.detail-desc {
  background: rgba(99,102,241,0.04);
  border: 1px solid rgba(99,102,241,0.07);
  border-radius: 10px;
  padding: 14px;
}
.desc-label {
  font-size: 12px;
  color: var(--text-muted);
  display: block;
  margin-bottom: 6px;
}
.detail-desc p {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
}
.detail-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(99,102,241,0.08);
}
.btn-cancel {
  background: rgba(107,114,128,0.1) !important;
  border: 1px solid rgba(107,114,128,0.2) !important;
  color: var(--text-muted) !important;
}
.btn-cancel:hover {
  background: rgba(107,114,128,0.2) !important;
  color: var(--text-secondary) !important;
}

@media (max-width: 600px) {
  .detail-body { flex-direction: column; }
  .detail-img { width: 100%; }
  .detail-info-top { flex-direction: column; }
}
</style>
