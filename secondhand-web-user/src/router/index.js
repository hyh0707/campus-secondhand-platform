import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/goods',
    name: 'GoodsList',
    component: () => import('../views/GoodsList.vue')
  },
  {
    path: '/goods/:id',
    name: 'GoodsDetail',
    component: () => import('../views/GoodsDetail.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/publish',
    name: 'Publish',
    component: () => import('../views/Publish.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my-goods',
    name: 'MyGoods',
    component: () => import('../views/MyGoods.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/demand',
    name: 'Demand',
    component: () => import('../views/Demand.vue')
  },
  {
    path: '/demand/:id',
    name: 'DemandDetail',
    component: () => import('../views/DemandDetail.vue')
  },
  {
    path: '/publish-demand',
    name: 'PublishDemand',
    component: () => import('../views/PublishDemand.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my-demands',
    name: 'MyDemands',
    component: () => import('../views/MyDemands.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.guest && token) {
    next('/')
  } else {
    next()
  }
})

export default router