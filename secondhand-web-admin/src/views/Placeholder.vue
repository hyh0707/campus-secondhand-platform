<template>
  <div class="placeholder-page">
    <el-icon :size="48" color="var(--text-muted)"><Clock /></el-icon>
    <h2>{{ title }}</h2>
    <p>功能开发中，敬请期待</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Clock } from '@element-plus/icons-vue'

const route = useRoute()
const title = computed(() => route.meta.title || '页面')
</script>

<style scoped>
.placeholder-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  color: var(--text-muted);
  gap: 12px;
}
.placeholder-page h2 {
  font-size: 18px;
  color: var(--text-secondary);
  font-weight: 500;
}
.placeholder-page p {
  font-size: 13px;
}
</style>
