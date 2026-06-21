<template>
  <div class="list-page">
    <!-- 筛选区 -->
    <div class="filter-bar glass-card">
      <div class="filter-row">
        <el-select v-model="query.status" placeholder="全部状态" clearable class="filter-select">
          <el-option label="全部" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="已接受" value="accepted" />
          <el-option label="已拒绝" value="rejected" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-input
          v-model="query.buyerId"
          placeholder="买家ID"
          clearable
          class="filter-num"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="query.sellerId"
          placeholder="卖家ID"
          clearable
          class="filter-num"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="query.goodsId"
          placeholder="商品ID"
          clearable
          class="filter-num"
          @keyup.enter="handleSearch"
        />
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
      <p>暂无订单数据</p>
    </div>

    <!-- 订单列表 -->
    <div v-else class="order-list">
      <div v-for="item in list" :key="item.id" class="order-card glass-card">
        <div class="order-header">
          <span class="order-id">#{{ item.id }}</span>
          <span class="order-status-badge" :class="'os-' + item.status">{{ orderStatusLabel(item.status) }}</span>
        </div>

        <div class="order-body">
          <!-- 商品信息 -->
          <div class="order-section">
            <span class="section-label">商品</span>
            <span class="section-value">{{ item.goodsTitle || '-' }}</span>
            <span class="order-price">&yen;{{ item.goodsPrice || '-' }}</span>
          </div>

          <!-- 人员 -->
          <div class="order-section">
            <span class="section-label">买家</span>
            <span class="section-value">{{ item.buyerNickname || item.buyerUsername || '-' }}</span>
            <span class="section-id" v-if="item.buyerId">ID: {{ item.buyerId }}</span>
          </div>
          <div class="order-section">
            <span class="section-label">卖家</span>
            <span class="section-value">{{ item.sellerNickname || item.sellerUsername || '-' }}</span>
            <span class="section-id" v-if="item.sellerId">ID: {{ item.sellerId }}</span>
          </div>

          <!-- 留言 -->
          <div class="order-section message" v-if="item.message">
            <span class="section-label">留言</span>
            <span class="section-value msg-text">{{ item.message }}</span>
          </div>

          <!-- 时间 -->
          <div class="order-section time-row">
            <span>
              <span class="section-label">创建</span>
              <span class="time-val">{{ formatTime(item.createTime) }}</span>
            </span>
            <span>
              <span class="section-label">更新</span>
              <span class="time-val">{{ formatTime(item.updateTime) }}</span>
            </span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Loading, FolderOpened } from '@element-plus/icons-vue'
import { getAdminOrderList } from '../api/adminOrder'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  status: '',
  buyerId: '',
  sellerId: '',
  goodsId: ''
})

// ====== 状态映射 ======
const statusMap = {
  pending: '待处理',
  accepted: '已接受',
  rejected: '已拒绝',
  completed: '已完成',
  cancelled: '已取消'
}
function orderStatusLabel(val) { return statusMap[val] || val }

// ====== 时间格式化 ======
function formatTime(val) {
  if (!val) return '-'
  return String(val).replace('T', ' ').substring(0, 19) || '-'
}

// ====== 加载列表 ======
async function fetchList() {
  loading.value = true
  try {
    const params = { pageNum: query.pageNum, pageSize: query.pageSize }
    if (query.status) params.status = query.status
    if (query.buyerId) params.buyerId = query.buyerId
    if (query.sellerId) params.sellerId = query.sellerId
    if (query.goodsId) params.goodsId = query.goodsId

    const res = await getAdminOrderList(params)
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
  query.status = ''
  query.buyerId = ''
  query.sellerId = ''
  query.goodsId = ''
  fetchList()
}
function handlePageChange(page) { query.pageNum = page; fetchList() }

onMounted(() => { fetchList() })
</script>

<style scoped>
.list-page { max-width: 1200px; }

/* ======== 筛选区 ======== */
.filter-bar { padding: 18px 20px; margin-bottom: 20px; }
.filter-row { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.filter-select { width: 130px; }
.filter-num { width: 110px; }

/* ======== 加载 & 空 ======== */
.loading-state, .empty-state {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 80px 24px; color: var(--text-muted); gap: 10px;
}
.loading-icon { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* ======== 订单卡片 ======== */
.order-list { display: flex; flex-direction: column; gap: 10px; }

.order-card {
  padding: 16px 20px;
  transition: var(--transition);
}
.order-card:hover { border-color: rgba(99,102,241,0.35); }

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(99,102,241,0.06);
}
.order-id {
  font-size: 12px;
  color: var(--text-muted);
  font-family: monospace;
}

/* ====== 订单状态 ====== */
.order-status-badge {
  font-size: 11px;
  padding: 3px 12px;
  border-radius: 20px;
  font-weight: 600;
}
.os-pending   { background: rgba(245,158,11,0.12); color: #fbbf24; }
.os-accepted  { background: rgba(59,130,246,0.12); color: #60a5fa; }
.os-rejected  { background: rgba(239,68,68,0.12); color: #f87171; }
.os-completed { background: rgba(16,185,129,0.12); color: #34d399; }
.os-cancelled { background: rgba(107,114,128,0.12); color: #9ca3af; }

.order-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-section {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}
.order-section.message {
  align-items: flex-start;
}
.section-label {
  width: 44px;
  flex-shrink: 0;
  color: var(--text-muted);
  font-size: 12px;
}
.section-value {
  color: var(--text-secondary);
  min-width: 0;
  word-break: break-all;
}
.msg-text {
  color: var(--text-muted);
  font-size: 12px;
  font-style: italic;
}
.section-id {
  font-size: 11px;
  color: var(--text-muted);
  font-family: monospace;
}
.order-price {
  font-size: 14px;
  font-weight: 700;
  color: #f87171;
  margin-left: auto;
}

.order-section.time-row {
  display: flex;
  gap: 32px;
  padding-top: 6px;
  border-top: 1px solid rgba(99,102,241,0.04);
}
.time-row span {
  display: flex;
  align-items: center;
  gap: 6px;
}
.time-val {
  font-size: 12px;
  color: var(--text-muted);
  font-family: monospace;
}

/* ======== 分页 ======== */
.pagination-wrap { display: flex; justify-content: center; margin-top: 28px; }
</style>
