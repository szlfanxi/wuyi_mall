<script setup>
import NavBar from './components/NavBar.vue'
import { useRoute } from 'vue-router'
// App.vue 作为应用的根组件，使用 router-view 显示当前路由对应的组件

const route = useRoute()
</script>

<template>
  <div id="app">
    <!-- 非后台管理页面显示导航栏 -->
    <NavBar v-if="!route.path.startsWith('/admin')" />
    <main class="main-content">
      <transition name="page-transition" mode="out-in">
        <router-view />
      </transition>
    </main>
  </div>
</template>

<style scoped>
/* 可以在这里添加全局样式 */
#app {
  margin: 0 ;
  padding: 0;
}

.main-content {
  padding: 20px;
}

/* 为admin页面移除默认padding，因为admin layout已经有自己的padding */
.main-content:has(.admin-layout) {
  padding: 0;
}
</style>

<style>
/* 页面切换动画 */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: all 0.3s ease;
}

.page-transition-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.page-transition-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>
