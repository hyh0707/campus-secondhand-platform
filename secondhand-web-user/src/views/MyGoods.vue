<template>
  <div class="my-goods-page">
    <div class="page-container-wide">
      <div class="page-header-row">
        <div>
          <h1 class="page-title">我的商品</h1>
          <p class="page-subtitle">管理你发布的商品</p>
        </div>
        <router-link to="/publish" class="btn-primary">
          <el-icon><Plus /></el-icon>
          发布商品
        </router-link>
      </div>

      <!-- 状态筛选 -->
      <div class="filter-bar">
        <el-radio-group v-model="query.status" @change="handleStatusChange">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="approved">已通过</el-radio-button>
          <el-radio-button value="rejected">已驳回</el-radio-button>
          <el-radio-button value="sold">已售出</el-radio-button>
          <el-radio-button value="offline">已下架</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!goodsList.length" class="empty-section">
        <el-icon :size="48"><FolderOpened /></el-icon>
        <p>暂无商品</p>
        <router-link to="/publish" class="btn-primary">去发布商品</router-link>
      </div>

      <!-- 商品列表 -->
      <template v-else>
        <div class="goods-grid">
          <div
            v-for="item in goodsList"
            :key="item.id"
            class="gls-card goods-card-item"
          >
            <div class="card-img" @click="goDetail(item.id)">
              <img
                v-if="item.mainImage"
                :src="getImageUrl(item.mainImage)"
                :alt="item.title"
                @error="handleImgError"
              />
              <div v-else class="img-placeholder">
                <el-icon :size="32"><PictureFilled /></el-icon>
              </div>
              <span class="status-tag" :class="'status-' + item.status">{{ statusLabel(item.status) }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title" @click="goDetail(item.id)">{{ item.title }}</h3>
              <div class="card-meta">
                <span class="card-price">&yen;{{ item.price }}</span>
                <span class="card-views">
                  <el-icon :size="13"><View /></el-icon>
                  {{ item.viewCount || 0 }}
                </span>
                <span class="card-fav">
                  <el-icon :size="13"><Star /></el-icon>
                  {{ item.favoriteCount || 0 }}
                </span>
              </div>
              <div class="card-time">{{ formatTime(item.createTime) }}</div>
              <div class="card-actions">
                <el-button size="small" text @click="goDetail(item.id)">查看</el-button>
                <el-button size="small" text type="primary" @click="openEdit(item.id)">修改</el-button>
                <el-popconfirm title="确定要删除这个商品吗？" @confirm="handleDelete(item.id)">
                  <template #reference>
                    <el-button size="small" text type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
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
            background
            @current-change="fetchGoods"
          />
        </div>
      </template>
    </div>

    <!-- 修改弹窗 -->
    <el-dialog
      v-model="editVisible"
      title="修改商品"
      width="640px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-position="top" v-if="editVisible">
        <!-- 图片 -->
        <div class="form-section-dialog">
          <label class="section-label">商品图片</label>
          <div class="upload-area">
            <div
              v-for="(img, idx) in editImageList"
              :key="idx"
              class="upload-item"
            >
              <img :src="getImageUrl(img)" @error="handleEditPreviewError($event, idx)" />
              <div class="upload-item-actions">
                <el-button circle size="small" type="danger" @click="removeEditImage(idx)">
                  <el-icon :size="14"><Close /></el-icon>
                </el-button>
              </div>
            </div>
            <div
              v-if="editImageList.length < 6"
              class="upload-trigger"
              :class="{ uploading: editUploading }"
              @click="triggerEditUpload"
            >
              <el-icon :size="24" v-if="!editUploading"><Plus /></el-icon>
              <el-icon :size="24" class="spinner" v-else><Loading /></el-icon>
              <span>{{ editUploading ? '上传中...' : '添加' }}</span>
            </div>
            <input
              ref="editFileInputRef"
              type="file"
              accept="image/jpg,image/jpeg,image/png,image/webp"
              style="display: none"
              @change="handleEditFileChange"
            />
          </div>
        </div>

        <el-form-item label="商品标题" prop="title">
          <el-input v-model="editForm.title" maxlength="100" show-word-limit />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="成色" prop="conditionLevel">
              <el-select v-model="editForm.conditionLevel" style="width: 100%">
                <el-option v-for="c in conditionOptions" :key="c.value" :label="c.label" :value="c.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="editForm.price" :min="0" :precision="2" :controls="false" style="width: 100%">
                <template #prefix>¥</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="是否可议价">
          <el-switch
            v-model="editForm.negotiable"
            active-text="可议价"
            inactive-text="不议价"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
        <el-form-item label="交易地点">
          <el-input v-model="editForm.tradeLocation" maxlength="100" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="editForm.contactInfo" maxlength="100" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="4" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="handleEditSubmit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Loading, PictureFilled, View, Star, FolderOpened, Close } from '@element-plus/icons-vue'
import { getMyGoods, getGoodsDetail, updateGoods, deleteGoods } from '../api/goods'
import { uploadImage } from '../api/upload'
import { conditionLabel, conditionValue } from '../utils/condition'
import { getImageUrl } from '../utils/image'

const router = useRouter()
const loading = ref(false)
const goodsList = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 12,
  status: ''
})

const statusLabels = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已驳回',
  sold: '已售出',
  offline: '已下架'
}

function statusLabel(s) {
  return statusLabels[s] || s
}

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function goDetail(id) {
  router.push(`/goods/${id}`)
}

function handleStatusChange() {
  query.pageNum = 1
  fetchGoods()
}

function handleImgError(e) {
  e.target.style.display = 'none'
}

async function fetchGoods() {
  loading.value = true
  try {
    const params = {
      pageNum: query.pageNum,
      pageSize: query.pageSize
    }
    if (query.status) params.status = query.status
    const res = await getMyGoods(params)
    goodsList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (err) {
    goodsList.value = []
    total.value = 0
    const msg = err?.response?.data?.message || '加载失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

// 删除
async function handleDelete(id) {
  try {
    await deleteGoods(id)
    ElMessage.success('删除成功')
    fetchGoods()
  } catch (err) {
    const msg = err?.response?.data?.message || '删除失败'
    ElMessage.error(msg)
  }
}

// 修改弹窗
const editVisible = ref(false)
const editFormRef = ref(null)
const editSubmitting = ref(false)
const editUploading = ref(false)
const editFileInputRef = ref(null)
const editingId = ref(null)
const editImageList = ref([])

const editForm = reactive({
  title: '',
  conditionLevel: '',
  price: null,
  tradeLocation: '',
  contactInfo: '',
  description: '',
  negotiable: 0
})

const editRules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  description: [{ required: true, message: '请输入商品描述', trigger: 'blur' }],
  conditionLevel: [{ required: true, message: '请选择成色', trigger: 'change' }]
}

const categoryOptions = [
  { label: '数码产品', value: 1 },
  { label: '图书教材', value: 2 },
  { label: '生活用品', value: 3 },
  { label: '运动户外', value: 4 },
  { label: '箱包服饰', value: 5 },
  { label: '其他', value: 6 }
]

const conditionOptions = [
  { label: '全新', value: '10' },
  { label: '九成新', value: '9' },
  { label: '八成新', value: '8' },
  { label: '七成新', value: '7' },
  { label: '六成新及以下', value: '6' }
]

async function openEdit(id) {
  editingId.value = id
  try {
    const res = await getGoodsDetail(id)
    const d = res.data
    editForm.title = d.title || ''
    editForm.conditionLevel = conditionValue(d.conditionLevel)
    editForm.price = d.price ?? null
    editForm.tradeLocation = d.tradeLocation || ''
    editForm.contactInfo = d.contactInfo || ''
    editForm.description = d.description || ''
    editForm.negotiable = d.negotiable ?? 0
    editImageList.value = (d.images || []).map(img => getImageUrl(img.imageUrl))
    editVisible.value = true
  } catch (err) {
    const msg = err?.response?.data?.message || '获取商品信息失败'
    ElMessage.error(msg)
  }
}

function triggerEditUpload() {
  editFileInputRef.value?.click()
}

async function handleEditFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  e.target.value = ''

  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.warning('仅支持 jpg、jpeg、png、webp 格式')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }

  editUploading.value = true
  try {
    const res = await uploadImage(file)
    const url = res.data?.url
    if (url) {
      editImageList.value.push(url)
    }
  } catch (err) {
    const msg = err?.response?.data?.message || '上传失败'
    ElMessage.error(msg)
  } finally {
    editUploading.value = false
  }
}

function removeEditImage(idx) {
  editImageList.value.splice(idx, 1)
}

function handleEditPreviewError(event, idx) {
  event.target.style.display = 'none'
}

async function handleEditSubmit() {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  editSubmitting.value = true
  try {
    const data = {
      ...editForm,
      imageUrls: editImageList.value
    }
    await updateGoods(editingId.value, data)
    ElMessage.success('修改成功')
    editVisible.value = false
    fetchGoods()
  } catch (err) {
    const msg = err?.response?.data?.message || '修改失败'
    ElMessage.error(msg)
  } finally {
    editSubmitting.value = false
  }
}

onMounted(() => {
  fetchGoods()
})
</script>

<style scoped>
.my-goods-page {
  min-height: 100vh;
  padding: 40px 24px 80px;
}
.page-container-wide {
  max-width: 1100px;
  margin: 0 auto;
}
.page-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}
.page-title {
  font-size: 26px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-light), var(--accent-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.page-subtitle {
  color: var(--text-muted);
  font-size: 14px;
  margin-top: 4px;
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

.filter-bar {
  margin-bottom: 24px;
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

.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.goods-card-item {
  overflow: hidden;
  transition: var(--transition);
}
.goods-card-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35);
}
.card-img {
  position: relative;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  cursor: pointer;
}
.card-img img {
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
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.06), rgba(6, 182, 212, 0.04));
  color: var(--text-muted);
}
.status-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  backdrop-filter: blur(4px);
}
.status-pending {
  background: rgba(234, 179, 8, 0.2);
  color: #eab308;
  border: 1px solid rgba(234, 179, 8, 0.25);
}
.status-approved {
  background: rgba(34, 197, 94, 0.2);
  color: #22c55e;
  border: 1px solid rgba(34, 197, 94, 0.25);
}
.status-rejected {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
  border: 1px solid rgba(239, 68, 68, 0.25);
}
.status-sold {
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
  border: 1px solid rgba(59, 130, 246, 0.25);
}
.status-offline {
  background: rgba(107, 114, 128, 0.2);
  color: #9ca3af;
  border: 1px solid rgba(107, 114, 128, 0.25);
}
.card-body {
  padding: 14px;
}
.card-title {
  font-size: 14px;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 8px;
  cursor: pointer;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color var(--transition);
}
.card-title:hover {
  color: var(--primary-light);
}
.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.card-price {
  font-size: 18px;
  font-weight: 700;
  color: #f87171;
}
.card-views,
.card-fav {
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 3px;
}
.card-time {
  font-size: 12px;
  color: var(--text-extra);
  margin-bottom: 10px;
}
.card-actions {
  display: flex;
  gap: 4px;
  padding-top: 10px;
  border-top: 1px solid rgba(99, 102, 241, 0.08);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* --- 弹窗内上传 --- */
.form-section-dialog {
  margin-bottom: 16px;
}
.section-label {
  display: block;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.upload-item {
  width: 88px;
  height: 88px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  border: 1px solid var(--border-glass);
}
.upload-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.upload-item-actions {
  position: absolute;
  top: 2px;
  right: 2px;
  opacity: 0;
  transition: var(--transition);
}
.upload-item:hover .upload-item-actions {
  opacity: 1;
}
.upload-trigger {
  width: 88px;
  height: 88px;
  border-radius: 8px;
  border: 2px dashed rgba(99, 102, 241, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;
  color: var(--text-muted);
  transition: var(--transition);
  font-size: 11px;
}
.upload-trigger:hover {
  border-color: var(--primary);
  color: var(--primary-light);
  background: rgba(99, 102, 241, 0.05);
}
.upload-trigger.uploading {
  cursor: not-allowed;
  opacity: 0.6;
}
.spinner {
  animation: spin 1s linear infinite;
}

@media (max-width: 1024px) {
  .goods-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 768px) {
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .page-header-row {
    flex-direction: column;
  }
}
@media (max-width: 480px) {
  .goods-grid {
    grid-template-columns: 1fr;
  }
}
</style>