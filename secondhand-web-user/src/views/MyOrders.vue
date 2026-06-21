<template>
  <div class="orders-page">
    <div class="page-container-wide">
      <div class="page-header-section">
        <h1 class="page-title">我的交易</h1>
        <p class="page-subtitle">管理购买与出售的交易意向</p>
      </div>

      <div class="gls-card tab-card">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="我的购买" name="buy" />
          <el-tab-pane label="我的出售" name="sell" />
        </el-tabs>

        <!-- 状态筛选 -->
        <div class="status-filter" v-if="orderList.length || statusFilter">
          <el-radio-group v-model="statusFilter" size="small" @change="fetchOrders">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="pending">待处理</el-radio-button>
            <el-radio-button value="accepted">已接受</el-radio-button>
            <el-radio-button value="rejected">已拒绝</el-radio-button>
            <el-radio-button value="completed">已完成</el-radio-button>
            <el-radio-button value="cancelled">已取消</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!orderList.length" class="empty-section">
        <el-icon :size="48"><FolderOpened /></el-icon>
        <p>{{ activeTab === 'buy' ? '还没有购买记录' : '还没有出售记录' }}</p>
        <router-link to="/goods" class="btn-primary">去逛逛</router-link>
      </div>

      <!-- 订单列表 -->
      <template v-else>
        <div class="order-list">
          <div v-for="item in orderList" :key="item.id" class="gls-card order-card">
            <div class="order-header">
              <router-link :to="`/goods/${item.goodsId}`" class="order-goods-title">
                {{ item.goodsTitle }}
              </router-link>
              <span class="order-status-tag" :class="'status-' + item.status">
                {{ statusLabel(item.status) }}
              </span>
            </div>

            <div class="order-body">
              <div class="order-info">
                <div class="info-item">
                  <span class="info-label">价格</span>
                  <span class="info-value price">&yen;{{ item.goodsPrice }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">{{ activeTab === 'buy' ? '卖家' : '买家' }}</span>
                  <span class="info-value">
                    {{ activeTab === 'buy' ? item.sellerName : item.buyerName }}
                  </span>
                </div>
                <div class="info-item">
                  <span class="info-label">留言</span>
                  <span class="info-value message-text">{{ item.message }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">创建时间</span>
                  <span class="info-value">{{ formatTime(item.createTime) }}</span>
                </div>
              </div>
            </div>

            <div class="order-actions" v-if="item.status === 'pending' || item.status === 'accepted'">
              <!-- 买家操作 -->
              <template v-if="activeTab === 'buy'">
                <el-popconfirm
                  v-if="item.status === 'pending'"
                  title="确定要取消这个交易意向吗？"
                  confirm-button-text="确认取消"
                  cancel-button-text="返回"
                  @confirm="handleStatus(item.id, 'cancelled')"
                >
                  <template #reference>
                    <el-button size="small" text type="danger">取消</el-button>
                  </template>
                </el-popconfirm>
                <el-popconfirm
                  v-if="item.status === 'accepted'"
                  title="确认交易已完成？"
                  confirm-button-text="确认完成"
                  cancel-button-text="返回"
                  @confirm="handleStatus(item.id, 'completed')"
                >
                  <template #reference>
                    <el-button size="small" type="success">确认完成</el-button>
                  </template>
                </el-popconfirm>
              </template>

              <!-- 卖家操作 -->
              <template v-if="activeTab === 'sell'">
                <template v-if="item.status === 'pending'">
                  <el-button size="small" type="primary" @click="handleStatus(item.id, 'accepted')">
                    接受
                  </el-button>
                  <el-popconfirm
                    title="确定要拒绝这个交易意向吗？"
                    confirm-button-text="确认拒绝"
                    cancel-button-text="返回"
                    @confirm="handleStatus(item.id, 'rejected')"
                  >
                    <template #reference>
                      <el-button size="small" text type="danger">拒绝</el-button>
                    </template>
                  </el-popconfirm>
                </template>
                <el-popconfirm
                  v-if="item.status === 'accepted'"
                  title="确认交易已完成？"
                  confirm-button-text="确认完成"
                  cancel-button-text="返回"
                  @confirm="handleStatus(item.id, 'completed')"
                >
                  <template #reference>
                    <el-button size="small" type="success">确认完成</el-button>
                  </template>
                </el-popconfirm>
              </template>
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
            @current-change="fetchOrders"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, FolderOpened } from '@element-plus/icons-vue'
import { getMyBuyOrders, getMySellOrders, updateOrderStatus } from '../api/order'

const activeTab = ref('buy')
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref('')

const statusLabels = {
  pending: '待处理',
  accepted: '已接受',
  rejected: '已拒绝',
  completed: '已完成',
  cancelled: '已取消'
}

function statusLabel(s) {
  return statusLabels[s] || s
}

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function handleTabChange() {
  statusFilter.value = ''
  pageNum.value = 1
  fetchOrders()
}

async function fetchOrders() {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (statusFilter.value) params.status = statusFilter.value

    const res = activeTab.value === 'buy'
      ? await getMyBuyOrders(params)
      : await getMySellOrders(params)

    orderList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    orderList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function handleStatus(id, status) {
  try {
    await updateOrderStatus(id, status)
    ElMessage.success('操作成功')
    fetchOrders()
  } catch (err) {
    const msg = err?.response?.data?.message || '操作失败'
    ElMessage.error(msg)
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  padding: 40px 24px 80px;
}
.page-container-wide {
  max-width: 900px;
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

.tab-card {
  margin-bottom: 24px;
  padding: 0 20px 16px;
}
.status-filter {
  display: flex;
  justify-content: center;
  margin-top: 8px;
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.order-card {
  padding: 20px 24px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
.order-goods-title {
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  color: var(--text-primary);
  flex: 1;
  margin-right: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.order-goods-title:hover {
  color: var(--primary-light);
}

/* 状态标签 */
.order-status-tag {
  font-size: 12px;
  padding: 3px 12px;
  border-radius: 20px;
  font-weight: 600;
  white-space: nowrap;
}
.status-pending {
  background: rgba(234, 179, 8, 0.15);
  color: #eab308;
}
.status-accepted {
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
}
.status-rejected {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}
.status-completed {
  background: rgba(99, 102, 241, 0.15);
  color: var(--primary-light);
}
.status-cancelled {
  background: rgba(107, 114, 128, 0.15);
  color: #9ca3af;
}

.order-body {
  margin-bottom: 14px;
}
.order-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px 32px;
}
.info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.info-label {
  font-size: 11px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.info-value {
  font-size: 14px;
  color: var(--text-primary);
}
.info-value.price {
  font-weight: 700;
  color: #f87171;
}
.message-text {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.order-actions {
  display: flex;
  gap: 8px;
  padding-top: 14px;
  border-top: 1px solid rgba(99, 102, 241, 0.06);
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .order-info {
    flex-direction: column;
    gap: 10px;
  }
  .message-text {
    max-width: 200px;
  }
}
</style>