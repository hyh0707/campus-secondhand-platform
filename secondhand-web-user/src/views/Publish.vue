<template>
  <div class="publish-page">
    <div class="page-container-narrow">
      <div class="page-header-section">
        <h1 class="page-title">发布商品</h1>
        <p class="page-subtitle">填写商品信息，让更多人看到你的闲置</p>
      </div>

      <div class="gls-card">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="publish-form">
          <!-- 商品图片 -->
          <div class="form-section">
            <h3 class="section-title">商品图片</h3>
            <div class="upload-area">
              <div
                v-for="(img, idx) in imageList"
                :key="idx"
                class="upload-item"
              >
                <img :src="img" @error="handlePreviewError($event, idx)" />
                <div class="upload-item-actions">
                  <el-button circle size="small" type="danger" @click="removeImage(idx)">
                    <el-icon :size="14"><Close /></el-icon>
                  </el-button>
                </div>
              </div>
              <div
                v-if="imageList.length < 6"
                class="upload-trigger"
                :class="{ uploading: uploading }"
                @click="triggerUpload"
              >
                <el-icon :size="28" v-if="!uploading"><Plus /></el-icon>
                <el-icon :size="28" class="spinner" v-else><Loading /></el-icon>
                <span>{{ uploading ? '上传中...' : '添加图片' }}</span>
                <span class="upload-hint">最多6张</span>
              </div>
              <input
                ref="fileInputRef"
                type="file"
                accept="image/jpg,image/jpeg,image/png,image/webp"
                style="display: none"
                @change="handleFileChange"
              />
            </div>
          </div>

          <!-- 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            <el-form-item label="商品标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入商品标题，如：iPad 9代 64G 校园自用" maxlength="100" show-word-limit />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="分类" prop="categoryId">
                  <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                    <el-option
                      v-for="cat in categoryOptions"
                      :key="cat.value"
                      :label="cat.label"
                      :value="cat.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="成色" prop="conditionLevel">
                  <el-select v-model="form.conditionLevel" placeholder="请选择成色" style="width: 100%">
                    <el-option
                      v-for="c in conditionOptions"
                      :key="c.value"
                      :label="c.label"
                      :value="c.value"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="价格" prop="price">
                  <el-input-number
                    v-model="form.price"
                    :min="0"
                    :precision="2"
                    :controls="false"
                    placeholder="请输入价格"
                    style="width: 100%"
                  >
                    <template #prefix>¥</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否可议价">
                  <el-switch
                    v-model="negotiable"
                    active-text="可议价"
                    inactive-text="不议价"
                    :active-value="1"
                    :inactive-value="0"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 交易信息 -->
          <div class="form-section">
            <h3 class="section-title">交易信息</h3>
            <el-form-item label="交易地点" prop="tradeLocation">
              <el-input v-model="form.tradeLocation" placeholder="如：学校图书馆门口、二食堂门口" maxlength="100" />
            </el-form-item>
            <el-form-item label="联系方式" prop="contactInfo">
              <el-input v-model="form.contactInfo" placeholder="如：QQ:123456789、微信:xxx" maxlength="100" />
            </el-form-item>
          </div>

          <!-- 商品描述 -->
          <div class="form-section">
            <h3 class="section-title">商品描述</h3>
            <el-form-item prop="description">
              <el-input
                v-model="form.description"
                type="textarea"
                :rows="5"
                placeholder="详细描述商品的使用情况、外观成色、功能是否正常、购买时间等..."
                maxlength="2000"
                show-word-limit
              />
            </el-form-item>
          </div>

          <!-- 提交 -->
          <div class="form-actions">
            <el-button size="large" @click="$router.back()">取消</el-button>
            <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
              发布商品
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Close, Loading } from '@element-plus/icons-vue'
import { addGoods } from '../api/goods'
import { uploadImage } from '../api/upload'

const router = useRouter()
const formRef = ref(null)
const fileInputRef = ref(null)
const submitting = ref(false)
const uploading = ref(false)

const imageList = ref([])

const negotiable = ref(0)

const form = reactive({
  title: '',
  categoryId: null,
  conditionLevel: '',
  price: null,
  tradeLocation: '',
  contactInfo: '',
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
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

function triggerUpload() {
  fileInputRef.value?.click()
}

async function handleFileChange(e) {
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

  uploading.value = true
  try {
    const res = await uploadImage(file)
    const url = res.data?.url
    if (url) {
      imageList.value.push(url)
    }
  } catch (err) {
    const msg = err?.response?.data?.message || '上传失败'
    ElMessage.error(msg)
  } finally {
    uploading.value = false
  }
}

function removeImage(idx) {
  imageList.value.splice(idx, 1)
}

function handlePreviewError(event, idx) {
  event.target.style.display = 'none'
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = {
      ...form,
      negotiable: negotiable.value,
      imageUrls: imageList.value
    }
    await addGoods(data)
    ElMessage.success('发布成功，等待管理员审核')
    router.push('/my-goods')
  } catch (err) {
    const msg = err?.response?.data?.message || '发布失败'
    ElMessage.error(msg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  padding: 40px 24px 80px;
}
.page-container-narrow {
  max-width: 720px;
  margin: 0 auto;
}
.page-header-section {
  text-align: center;
  margin-bottom: 32px;
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

.gls-card {
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.7), rgba(15, 23, 42, 0.8));
  border: 1px solid var(--border-glass);
  border-radius: var(--radius);
  padding: 32px;
  backdrop-filter: blur(8px);
}

.form-section {
  margin-bottom: 28px;
  padding-bottom: 28px;
  border-bottom: 1px solid rgba(99, 102, 241, 0.08);
}
.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--primary-light);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.section-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, var(--primary), var(--accent));
  border-radius: 2px;
}

/* 上传区域 */
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.upload-item {
  width: 120px;
  height: 120px;
  border-radius: 10px;
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
  top: 4px;
  right: 4px;
  opacity: 0;
  transition: var(--transition);
}
.upload-item:hover .upload-item-actions {
  opacity: 1;
}
.upload-trigger {
  width: 120px;
  height: 120px;
  border-radius: 10px;
  border: 2px dashed rgba(99, 102, 241, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  color: var(--text-muted);
  transition: var(--transition);
  font-size: 12px;
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
.upload-hint {
  font-size: 11px;
  color: var(--text-extra);
}
.spinner {
  animation: spin 1s linear infinite;
}
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(99, 102, 241, 0.08);
}

@media (max-width: 768px) {
  .gls-card {
    padding: 20px;
  }
  .upload-item,
  .upload-trigger {
    width: 96px;
    height: 96px;
  }
}
</style>