<template>
  <div class="match-page">
    <div class="page-container-narrow">
      <!-- 返回 -->
      <div class="back-row">
        <el-button text @click="router.push('/my-goods')">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <span class="breadcrumb">智能匹配求购</span>
      </div>

      <div class="page-header-section">
        <h1 class="page-title">智能匹配求购</h1>
        <p class="page-subtitle">根据你的商品信息，匹配可能感兴趣的求购用户</p>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-state">
        <el-icon :size="36" class="loading-icon"><Loading /></el-icon>
        <p>智能匹配求购需求中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!matchList.length" class="empty-section">
        <el-icon :size="48"><Search /></el-icon>
        <p>暂未匹配到合适的求购</p>
        <p class="empty-tip">可以尝试调整商品价格或关键词来获得更多匹配结果</p>
        <router-link to="/goods" class="btn-primary">浏览其他商品</router-link>
      </div>

      <!-- 匹配列表 -->
      <div v-else class="match-list">
        <div v-for="item in matchList" :key="item.id || item.demandId" class="gls-card">
          <MatchCard :data="item" mode="demand" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Loading, Search } from '@element-plus/icons-vue'
import { getDemandsForGoods } from '../api/match'
import MatchCard from '../components/MatchCard.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const matchList = ref([])

async function fetchMatch() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getDemandsForGoods(id, { limit: 12 })
    matchList.value = res.data || []
  } catch {
    matchList.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => { fetchMatch() })

watch(() => route.params.id, (n, o) => {
  if (n && n !== o) {
    fetchMatch()
  }
})
</script>

<style scoped>
.match-page {
  min-height: 100vh;
  padding: 40px 24px 80px;
}
.page-container-narrow {
  max-width: 780px;
  margin: 0 auto;
}

.back-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.back-row .el-button { color: var(--text-secondary); }
.back-row .el-button:hover { color: var(--primary-light); }
.breadcrumb { font-size: 13px; color: var(--text-muted); }

.page-header-section {
  text-align: center;
  margin-bottom: 28px;
}
.page-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--accent-light), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.page-subtitle {
  color: var(--text-muted);
  margin-top: 8px;
  font-size: 14px;
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
.loading-icon { animation: spin 1s linear infinite; }
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
  gap: 12px;
}
.empty-tip { font-size: 12px; color: var(--text-muted); }
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
  margin-top: 8px;
}
.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(99,102,241,0.35);
}

.match-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
</style>