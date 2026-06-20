<template>
  <div class="page-container">
    <div class="placeholder-page glass-card">
      <el-icon :size="48" class="placeholder-icon"><Upload /></el-icon>
      <h2>发布商品</h2>
      <p>发布商品功能开发中，敬请期待...</p>
    </div>
  </div>
</template>

<script setup>
import { Upload } from '@element-plus/icons-vue'
</script>

<style scoped>
.placeholder-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  text-align: center;
}
.placeholder-icon {
  color: var(--primary-light);
  margin-bottom: 20px;
}
.placeholder-page h2 {
  font-size: 22px;
  margin-bottom: 8px;
}
.placeholder-page p {
  color: var(--text-secondary);
  font-size: 14px;
}
</style>