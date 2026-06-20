<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-orb h-orb-1"></div>
        <div class="hero-orb h-orb-2"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="gradient-text">校园二手物品</span>
          <br />
          <span class="gradient-text-2">智能匹配平台</span>
        </h1>
        <p class="hero-slogan">让闲置物品找到更合适的人</p>
        <div class="search-box">
          <el-input
            v-model="keyword"
            size="large"
            placeholder="搜索你想要的商品..."
            :prefix-icon="Search"
            class="hero-search"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" class="search-btn" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
        <div class="hero-tags">
          <span class="hero-tag" v-for="tag in hotTags" :key="tag" @click="keyword = tag; handleSearch()">
            {{ tag }}
          </span>
        </div>
      </div>
    </section>

    <!-- 功能入口 -->
    <section class="features">
      <div class="section-header">
        <h2 class="section-title">核心功能</h2>
        <p class="section-desc">智能匹配，让交易更高效</p>
      </div>
      <div class="feature-cards">
        <div class="feature-card glass-card">
          <div class="fc-icon fc-icon-1">
            <el-icon :size="32"><Goods /></el-icon>
          </div>
          <h3>商品浏览</h3>
          <p>浏览校园二手商品，发现心仪好物</p>
        </div>
        <div class="feature-card glass-card">
          <div class="fc-icon fc-icon-2">
            <el-icon :size="32"><Search /></el-icon>
          </div>
          <h3>求购发布</h3>
          <p>发布求购需求，智能匹配合适商品</p>
        </div>
        <div class="feature-card glass-card">
          <div class="fc-icon fc-icon-3">
            <el-icon :size="32"><Connection /></el-icon>
          </div>
          <h3>智能匹配</h3>
          <p>多特征融合算法，精准推荐匹配</p>
        </div>
        <div class="feature-card glass-card">
          <div class="fc-icon fc-icon-4">
            <el-icon :size="32"><ChatDotRound /></el-icon>
          </div>
          <h3>安全交易</h3>
          <p>校内交易，安全可靠有保障</p>
        </div>
      </div>
    </section>

    <!-- 商品推荐占位 -->
    <section class="recommend">
      <div class="section-header">
        <h2 class="section-title">热门推荐</h2>
        <p class="section-desc">为你精选校园好物</p>
      </div>
      <div class="goods-grid">
        <div class="goods-card glass-card" v-for="i in 8" :key="i">
          <div class="goods-img-placeholder">
            <el-icon :size="40"><PictureFilled /></el-icon>
          </div>
          <div class="goods-info">
            <div class="goods-title-placeholder"></div>
            <div class="goods-price-placeholder"></div>
          </div>
        </div>
      </div>
      <div class="section-more">
        <router-link to="/goods" class="btn-outline">查看更多商品</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Goods, Connection, ChatDotRound, PictureFilled } from '@element-plus/icons-vue'

const router = useRouter()
const keyword = ref('')

const hotTags = ['自行车', '教材', '电子产品', '运动器材', '生活用品']

function handleSearch() {
  if (keyword.value.trim()) {
    router.push({ path: '/goods', query: { keyword: keyword.value } })
  }
}
</script>

<style scoped>
.home-page {
  padding-bottom: 60px;
}

/* Hero */
.hero {
  position: relative;
  padding: 80px 24px 60px;
  text-align: center;
  overflow: hidden;
}
.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.hero-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.25;
}
.h-orb-1 {
  width: 400px;
  height: 400px;
  background: var(--primary);
  top: -100px;
  left: 20%;
  animation: orbFloat 8s ease-in-out infinite;
}
.h-orb-2 {
  width: 350px;
  height: 350px;
  background: var(--accent);
  bottom: -80px;
  right: 15%;
  animation: orbFloat 10s ease-in-out infinite reverse;
}
@keyframes orbFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}
.hero-content {
  position: relative;
  z-index: 1;
  max-width: 700px;
  margin: 0 auto;
}
.hero-title {
  font-size: 40px;
  font-weight: 800;
  line-height: 1.3;
  margin-bottom: 16px;
}
.gradient-text {
  background: linear-gradient(135deg, var(--text-primary), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.gradient-text-2 {
  background: linear-gradient(135deg, var(--primary-light), var(--accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hero-slogan {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 36px;
}
.search-box {
  max-width: 560px;
  margin: 0 auto 24px;
}
.hero-search :deep(.el-input__wrapper) {
  background: rgba(30, 41, 59, 0.8) !important;
  border-radius: 12px 0 0 12px !important;
  height: 50px;
}
.hero-search :deep(.el-input-group__append) {
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  border: none;
  border-radius: 0 12px 12px 0;
}
.search-btn {
  height: 50px;
  border: none;
  background: transparent;
  padding: 0 24px;
  font-size: 15px;
}
.hero-tags {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}
.hero-tag {
  padding: 6px 16px;
  border-radius: 20px;
  background: rgba(99, 102, 241, 0.1);
  border: 1px solid rgba(99, 102, 241, 0.2);
  color: var(--text-secondary);
  font-size: 13px;
  cursor: pointer;
  transition: var(--transition);
}
.hero-tag:hover {
  background: rgba(99, 102, 241, 0.2);
  border-color: var(--primary);
  color: var(--text-primary);
}

/* Features */
.features {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}
.section-header {
  text-align: center;
  margin-bottom: 36px;
}
.section-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(135deg, var(--text-primary), var(--primary-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.section-desc {
  font-size: 14px;
  color: var(--text-secondary);
}
.feature-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.feature-card {
  padding: 32px 24px;
  text-align: center;
  cursor: default;
}
.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 16px 0 8px;
}
.feature-card p {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}
.fc-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.fc-icon-1 { background: rgba(99, 102, 241, 0.15); color: var(--primary-light); }
.fc-icon-2 { background: rgba(6, 182, 212, 0.15); color: var(--accent-light); }
.fc-icon-3 { background: rgba(139, 92, 246, 0.15); color: #a78bfa; }
.fc-icon-4 { background: rgba(34, 197, 94, 0.15); color: #4ade80; }

/* 推荐 */
.recommend {
  max-width: 1200px;
  margin: 60px auto 0;
  padding: 0 24px;
}
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.goods-card {
  padding: 0;
  overflow: hidden;
}
.goods-img-placeholder {
  height: 160px;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.08), rgba(6, 182, 212, 0.05));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
}
.goods-info {
  padding: 16px;
}
.goods-title-placeholder {
  height: 14px;
  width: 80%;
  background: rgba(148, 163, 184, 0.15);
  border-radius: 4px;
  margin-bottom: 10px;
}
.goods-price-placeholder {
  height: 14px;
  width: 40%;
  background: rgba(99, 102, 241, 0.15);
  border-radius: 4px;
}
.section-more {
  text-align: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 28px;
  }
  .hero {
    padding: 60px 16px 40px;
  }
  .feature-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .goods-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>