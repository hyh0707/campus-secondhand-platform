<template>
  <div class="my-demands-page">
    <div class="page-container-wide">
      <div class="page-header-section">
        <h1 class="page-title">我的求购</h1>
        <p class="page-subtitle">管理你发布的求购信息</p>
      </div>

      <!-- 状态筛选 -->
      <div class="gls-card filter-bar">
        <div class="filter-tabs">
          <el-radio-group v-model="statusFilter" size="small" @change="handleFilterChange">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="pending">待审核</el-radio-button>
            <el-radio-button value="approved">已通过</el-radio-button>
            <el-radio-button value="rejected">已驳回</el-radio-button>
            <el-radio-button value="closed">已关闭</el-radio-button>
          </el-radio-group>
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
        <p>{{ statusFilter ? '该状态下暂无求购' : '你还没有发布求购' }}</p>
        <router-link to="/publish-demand" class="btn-primary">发布求购</router-link>
      </div>

      <!-- 列表 -->
      <template v-else>
        <div class="demand-grid">
          <div v-for="item in demandList" :key="item.id" class="gls-card demand-card">
            <div class="card-header">
              <span class="category-badge">{{ categoryLabel(item.categoryId) }}</span>
              <span class="status-tag" :class="'status-' + item.status">{{ statusLabel(item.status) }}</span>
            </div>

            <h3 class="card-title">{{ item.title }}</h3>

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
              <span class="tag" v-if="item.keywords">
                <el-icon :size="12"><Collection /></el-icon>
                {{ item.keywords }}
              </span>
            </div>

            <div class="card-footer">
              <span class="card-time">{{ formatTime(item.createTime) }}</span>
              <div class="card-actions">
                <el-button size="small" text @click="goDetail(item.id)">查看</el-button>
                <el-button size="small" text type="primary" @click="openEdit(item)">修改</el-button>
                <el-popconfirm
                  title="确定要删除这条求购吗？"
                  confirm-button-text="确认删除"
                  cancel-button-text="取消"
                  @confirm="handleDelete(item.id)"
                >
                  <template #reference>
                    <el-button size="small" text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
                <a
                  class="link-match"
                  @click.prevent="router.push(`/match/demand/${item.id}`)"
                >智能匹配商品</a>
              </div>
            </div>
          </div>
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

    <!-- 修改弹窗 -->
    <el-dialog
      v-model="editVisible"
      title="修改求购"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        v-loading="editDetailLoading"
        :rules="editRules"
        label-position="top"
        size="large"
      >
        <el-form-item label="求购标题" prop="title">
          <el-input v-model="editForm.title" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="editForm.categoryId" placeholder="请选择分类" class="full-width">
            <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="求购描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最低预算" prop="minPrice">
              <el-input-number
                v-model="editForm.minPrice"
                :min="0"
                :step="50"
                :precision="0"
                controls-position="right"
                class="full-width"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高预算" prop="maxPrice">
              <el-input-number
                v-model="editForm.maxPrice"
                :min="0"
                :step="50"
                :precision="0"
                controls-position="right"
                class="full-width"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="期望成色">
          <el-select v-model="editForm.expectedCondition" placeholder="请选择" class="full-width">
            <el-option v-for="c in conditionOptions" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="期望交易地点">
          <el-input v-model="editForm.expectedLocation" maxlength="50" />
        </el-form-item>

        <el-form-item label="关键词">
          <el-input v-model="editForm.keywords" maxlength="200" placeholder="多个关键词用逗号分隔" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="handleEditSubmit">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Location, Collection, Loading, FolderOpened } from '@element-plus/icons-vue'
import { getMyDemands, getDemandDetail, updateDemand, deleteDemand } from '../api/demand'
import { conditionLabel, conditionValue, CONDITION_OPTIONS } from '../utils/condition'

const router = useRouter()
const loading = ref(false)
const demandList = ref([])
const total = ref(0)
const statusFilter = ref('')

const query = reactive({
  pageNum: 1,
  pageSize: 10
})

const categories = [
  { value: 1, label: '数码产品' },
  { value: 2, label: '图书教材' },
  { value: 3, label: '生活用品' },
  { value: 4, label: '运动户外' },
  { value: 5, label: '箱包服饰' },
  { value: 6, label: '其他' }
]

const conditionOptions = CONDITION_OPTIONS
const statusLabels = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已驳回',
  closed: '已关闭'
}

function categoryLabel(id) {
  return categories.find(c => c.value === id)?.label || '其他'
}
function statusLabel(s) {
  return statusLabels[s] || s
}
function conditionLabelText(val) {
  return conditionLabel(val)
}
function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function handleFilterChange() {
  query.pageNum = 1
  fetchDemands()
}

function goDetail(id) {
  router.push(`/demand/${id}`)
}

async function fetchDemands() {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize
    }
    if (statusFilter.value) params.status = statusFilter.value

    const res = await getMyDemands(params)
    demandList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    demandList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 删除
async function handleDelete(id) {
  try {
    await deleteDemand(id)
    ElMessage.success('删除成功')
    fetchDemands()
  } catch (err) {
    const msg = err?.response?.data?.message || '删除失败'
    ElMessage.error(msg)
  }
}

// 修改弹窗
const editVisible = ref(false)
const editSubmitting = ref(false)
const editFormRef = ref(null)
const editId = ref(null)

const editForm = reactive({
  title: '',
  categoryId: null,
  description: '',
  minPrice: null,
  maxPrice: null,
  expectedCondition: '',
  expectedLocation: '',
  keywords: ''
})

const editRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  minPrice: [{ required: true, message: '请输入最低预算', trigger: 'blur' }],
  maxPrice: [{ required: true, message: '请输入最高预算', trigger: 'blur' }]
}

const editDetailLoading = ref(false)

async function openEdit(item) {
  editId.value = item.id
  editVisible.value = true
  editDetailLoading.value = true
  try {
    const res = await getDemandDetail(item.id)
    const detail = res.data
    editForm.title = detail.title || ''
    editForm.categoryId = detail.categoryId
    editForm.description = detail.description || ''
    editForm.minPrice = Number(detail.minPrice) || 0
    editForm.maxPrice = Number(detail.maxPrice) || 0
    editForm.expectedCondition = conditionValue(detail.expectedCondition)
    editForm.expectedLocation = detail.expectedLocation || ''
    editForm.keywords = detail.keywords || ''
  } catch (err) {
    ElMessage.error('获取求购详情失败')
    editVisible.value = false
  } finally {
    editDetailLoading.value = false
  }
}

async function handleEditSubmit() {
  if (!editFormRef.value) return
  try {
    await editFormRef.value.validate()
  } catch {
    return
  }

  if (editForm.minPrice > editForm.maxPrice) {
    ElMessage.warning('最低预算不能大于最高预算')
    return
  }

  editSubmitting.value = true
  try {
    const data = { ...editForm }
    data.categoryId = Number(data.categoryId)
    await updateDemand(editId.value, data)
    ElMessage.success('修改成功')
    editVisible.value = false
    fetchDemands()
  } catch (err) {
    const msg = err?.response?.data?.message || '修改失败'
    ElMessage.error(msg)
  } finally {
    editSubmitting.value = false
  }
}

onMounted(() => {
  fetchDemands()
})
</script>

<style scoped>
.my-demands-page {
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

.filter-bar {
  padding: 14px 20px;
  margin-bottom: 24px;
}
.filter-tabs {
  display: flex;
  justify-content: center;
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
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.demand-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.category-badge {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 20px;
  background: rgba(99, 102, 241, 0.12);
  color: var(--primary-light);
  font-weight: 500;
}
.status-tag {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 20px;
  font-weight: 600;
}
.status-pending {
  background: rgba(234, 179, 8, 0.15);
  color: #eab308;
}
.status-approved {
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
}
.status-rejected {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}
.status-closed {
  background: rgba(107, 114, 128, 0.15);
  color: #9ca3af;
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
.card-time {
  font-size: 11px;
  color: var(--text-extra);
}
.card-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}
.link-match {
  font-size: 12px;
  color: var(--accent-light);
  text-decoration: none;
  padding: 4px 8px;
  border-radius: 4px;
  background: rgba(6,182,212,0.08);
  border: 1px solid rgba(6,182,212,0.12);
  margin-left: auto;
  transition: var(--transition);
}
.link-match:hover {
  background: rgba(6,182,212,0.15);
  color: var(--accent-light);
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .demand-grid {
    grid-template-columns: 1fr;
  }
}
</style>