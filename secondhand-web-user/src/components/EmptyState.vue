<template>
  <div class="empty-state">
    <el-icon :size="48" class="empty-icon"><component :is="icon" /></el-icon>
    <p class="empty-text">{{ description }}</p>
    <slot></slot>
  </div>
</template>

<script setup>
import { FolderOpened } from '@element-plus/icons-vue'

defineProps({
  icon: { type: Object, default: () => FolderOpened },
  description: { type: String, default: '暂无数据' }
})
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  text-align: center;
}
.empty-icon {
  color: var(--text-muted);
  margin-bottom: 16px;
  opacity: 0.5;
}
.empty-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}
</style>