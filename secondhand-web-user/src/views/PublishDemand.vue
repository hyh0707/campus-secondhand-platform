<template>
  <div class="publish-page">
    <div class="page-container-narrow">
      <div class="page-header-section">
        <h1 class="page-title">发布求购</h1>
        <p class="page-subtitle">描述你想要的商品，让同学们帮你找到</p>
      </div>

      <div class="gls-card form-card">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          size="large"
          @submit.prevent="handleSubmit"
        >
          <el-form-item label="求购标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="例如：求购一台 iPad 用于上课记笔记"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="商品分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类" class="full-width">
              <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
            </el-select>
          </el-form-item>

          <el-form-item label="求购描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述你的需求，例如：希望价格合适，成色八成新以上，主要用于网课、PDF 阅读和课堂笔记。"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="最低预算（元）" prop="minPrice">
                <el-input-number
                  v-model="form.minPrice"
                  :min="0"
                  :step="50"
                  :precision="0"
                  placeholder="最低"
                  controls-position="right"
                  class="full-width"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最高预算（元）" prop="maxPrice">
                <el-input-number
                  v-model="form.maxPrice"
                  :min="0"
                  :step="50"
                  :precision="0"
                  placeholder="最高"
                  controls-position="right"
                  class="full-width"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="期望成色" prop="expectedCondition">
            <el-select v-model="form.expectedCondition" placeholder="请选择期望成色" class="full-width">
              <el-option v-for="c in conditionOptions" :key="c.value" :label="c.label" :value="c.value" />
            </el-select>
          </el-form-item>

          <el-form-item label="期望交易地点">
            <el-input
              v-model="form.expectedLocation"
              placeholder="例如：学校图书馆附近"
              maxlength="50"
            />
          </el-form-item>

          <el-form-item label="关键词">
            <el-input
              v-model="form.keywords"
              placeholder="多个关键词用逗号分隔，例如：iPad,平板,网课,笔记"
              maxlength="200"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="submitting"
              @click="handleSubmit"
            >
              发布求购
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addDemand } from '../api/demand'
import { CONDITION_OPTIONS } from '../utils/condition'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  title: '',
  categoryId: null,
  description: '',
  minPrice: null,
  maxPrice: null,
  expectedCondition: '',
  expectedLocation: '',
  keywords: ''
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

const rules = {
  title: [{ required: true, message: '请输入求购标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入求购描述', trigger: 'blur' }],
  minPrice: [{ required: true, message: '请输入最低预算', trigger: 'blur' }],
  maxPrice: [{ required: true, message: '请输入最高预算', trigger: 'blur' }]
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  if (form.minPrice > form.maxPrice) {
    ElMessage.warning('最低预算不能大于最高预算')
    return
  }

  submitting.value = true
  try {
    const data = { ...form }
    data.categoryId = Number(data.categoryId)
    await addDemand(data)
    ElMessage.success('发布成功，等待管理员审核')
    router.push('/my-demands')
  } catch (err) {
    const msg = err?.response?.data?.message || '发布失败，请稍后重试'
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
  max-width: 640px;
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
.form-card {
  padding: 32px;
}
.full-width {
  width: 100%;
}
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  border: none;
  border-radius: 10px;
}
.submit-btn:hover {
  background: linear-gradient(135deg, var(--primary-light), var(--primary));
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.35);
}

@media (max-width: 640px) {
  .form-card {
    padding: 20px 16px;
  }
}
</style>