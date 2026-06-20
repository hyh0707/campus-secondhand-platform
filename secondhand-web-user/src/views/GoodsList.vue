<template>
  <div class="page-container">
    <div class="placeholder-page glass-card">
      <el-icon :size="48" class="placeholder-icon"><Goods /></el-icon>
      <h2>商品列表</h2>
      <p>商品列表功能开发中，敬请期待...</p>
    </div>
  </div>
</template>

<script setup>
import { Goods } from '@element-plus/icons-vue'
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