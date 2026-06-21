import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    component: () => import('../components/AdminLayout.vue'),
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据看板', requiresAuth: true }
      },
      {
        path: 'goods',
        name: 'AdminGoods',
        component: () => import('../views/Placeholder.vue'),
        meta: { title: '商品审核', requiresAuth: true }
      },
      {
        path: 'demands',
        name: 'AdminDemands',
        component: () => import('../views/Placeholder.vue'),
        meta: { title: '求购审核', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/Placeholder.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('../views/Placeholder.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const adminToken = localStorage.getItem('adminToken')
  if (to.meta.requiresAuth && !adminToken) {
    next('/login')
  } else if (to.meta.guest && adminToken) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
