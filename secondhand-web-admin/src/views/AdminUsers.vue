<template>
  <div class="list-page">
    <!-- 筛选区 -->
    <div class="filter-bar glass-card">
      <div class="filter-row">
        <el-input
          v-model="query.keyword"
          placeholder="搜索用户名、昵称、手机号、邮箱..."
          prefix-icon="Search"
          clearable
          class="filter-input"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="query.status" placeholder="全部状态" clearable class="filter-select">
          <el-option label="全部" value="" />
          <el-option label="正常" :value="1" />
          <el-option label="已禁用" :value="0" />
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
      <p>暂无用户数据</p>
    </div>

    <!-- 用户列表 -->
    <div v-else class="user-list">
      <div v-for="item in list" :key="item.id" class="user-card glass-card">
        <!-- 头像 -->
        <div class="user-avatar">
          <img
            :src="getImageUrl(item.avatar)"
            :alt="item.nickname"
            @error="e => { e.target.style.display = 'none'; e.target.nextElementSibling.style.display = 'flex'; }"
            v-show="item.avatar"
          />
          <el-icon :size="22" :style="{ display: item.avatar ? 'none' : 'flex' }"><UserFilled /></el-icon>
        </div>

        <!-- 信息 -->
        <div class="user-info">
          <div class="user-top">
            <span class="user-id">#{{ item.id }}</span>
            <h3 class="user-name">{{ item.username }}
              <span class="user-nick" v-if="item.nickname">({{ item.nickname }})</span>
            </h3>
            <span class="user-status-badge" :class="item.status === 1 ? 'us-normal' : 'us-disabled'">
              {{ item.status === 1 ? '正常' : '已禁用' }}
            </span>
          </div>

          <div class="user-contact">
            <span v-if="item.phone" class="contact-item">
              <el-icon :size="12"><Phone /></el-icon>
              {{ item.phone }}
            </span>
            <span v-if="item.email" class="contact-item">
              <el-icon :size="12"><Message /></el-icon>
              {{ item.email }}
            </span>
          </div>

          <div class="user-bottom">
            <span class="user-time">注册时间 {{ formatTime(item.createTime) }}</span>
            <div class="action-btns">
              <el-button
                v-if="item.status === 1"
                size="small"
                type="danger"
                @click="handleDisable(item)"
              >
                禁用
              </el-button>
              <el-button
                v-else
                size="small"
                type="success"
                @click="handleEnable(item)"
              >
                启用
              </el-button>
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

    <!-- 禁用确认 -->
    <el-dialog v-model="disableVisible" title="禁用用户" width="400px" class="admin-dialog">
      <p>确定要禁用用户「<strong>{{ currentItem?.username }}</strong>」吗？</p>
      <p class="hint">禁用后该用户将无法登录和使用平台功能。</p>
      <span class="dialog-footer">
        <el-button @click="disableVisible = false">取消</el-button>
        <el-button type="danger" :loading="statusLoading" @click="confirmDisable">确认禁用</el-button>
      </span>
    </el-dialog>

    <!-- 启用确认 -->
    <el-dialog v-model="enableVisible" title="启用用户" width="400px" class="admin-dialog">
      <p>确定要启用用户「<strong>{{ currentItem?.username }}</strong>」吗？</p>
      <p class="hint">启用后该用户将恢复正常使用权限。</p>
      <span class="dialog-footer">
        <el-button @click="enableVisible = false">取消</el-button>
        <el-button type="success" :loading="statusLoading" @click="confirmEnable">确认启用</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Loading, FolderOpened, UserFilled, Phone, Message } from '@element-plus/icons-vue'
import { getAdminUserList, updateUserStatus } from '../api/adminUser'
import { getImageUrl } from '../utils/image'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

const statusLoading = ref(false)
const currentItem = ref(null)
const disableVisible = ref(false)
const enableVisible = ref(false)

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
    if (query.keyword) params.keyword = query.keyword
    if (query.status !== '') params.status = query.status

    const res = await getAdminUserList(params)
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
  fetchList()
}
function handlePageChange(page) { query.pageNum = page; fetchList() }

// ====== 禁用 ======
function handleDisable(item) {
  currentItem.value = item
  disableVisible.value = true
}
async function confirmDisable() {
  statusLoading.value = true
  try {
    await updateUserStatus(currentItem.value.id, { status: 0 })
    ElMessage.success('用户已禁用')
    disableVisible.value = false
    fetchList()
  } catch { /* handled by interceptor */ } finally { statusLoading.value = false }
}

// ====== 启用 ======
function handleEnable(item) {
  currentItem.value = item
  enableVisible.value = true
}
async function confirmEnable() {
  statusLoading.value = true
  try {
    await updateUserStatus(currentItem.value.id, { status: 1 })
    ElMessage.success('用户已启用')
    enableVisible.value = false
    fetchList()
  } catch { /* handled by interceptor */ } finally { statusLoading.value = false }
}

onMounted(() => { fetchList() })
</script>

<style scoped>
.list-page { max-width: 1200px; }

/* ======== 筛选区 ======== */
.filter-bar { padding: 18px 20px; margin-bottom: 20px; }
.filter-row { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.filter-input { width: 260px; }
.filter-select { width: 130px; }

/* ======== 加载 & 空 ======== */
.loading-state, .empty-state {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 80px 24px; color: var(--text-muted); gap: 10px;
}
.loading-icon { animation: spin 1s linear infinite; }
@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

/* ======== 用户卡片 ======== */
.user-list { display: flex; flex-direction: column; gap: 10px; }

.user-card {
  display: flex;
  gap: 16px;
  padding: 16px 20px;
  transition: var(--transition);
}
.user-card:hover { border-color: rgba(99,102,241,0.35); }

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(99,102,241,0.12), rgba(6,182,212,0.06));
  border: 1px solid rgba(99,102,241,0.1);
  overflow: hidden;
  color: var(--text-muted);
}
.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-top {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-id {
  font-size: 11px;
  color: var(--text-muted);
  font-family: monospace;
}
.user-name {
  font-size: 14px;
  font-weight: 600;
}
.user-nick {
  font-size: 12px;
  color: var(--text-secondary);
  font-weight: 400;
}

.user-status-badge {
  font-size: 10px;
  padding: 2px 10px;
  border-radius: 20px;
  font-weight: 500;
}
.us-normal  { background: rgba(16,185,129,0.12); color: #34d399; }
.us-disabled { background: rgba(239,68,68,0.12); color: #f87171; }

.user-contact {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}
.contact-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

.user-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 6px;
  border-top: 1px solid rgba(99,102,241,0.05);
}
.user-time {
  font-size: 11px;
  color: var(--text-muted);
}
.action-btns { display: flex; gap: 6px; }

/* ======== 分页 ======== */
.pagination-wrap { display: flex; justify-content: center; margin-top: 28px; }

/* ======== 弹窗 ======== */
.hint { font-size: 12px; color: var(--text-muted); margin-top: 6px; }
.dialog-footer { display: flex; justify-content: flex-end; gap: 8px; margin-top: 20px; }
</style>
