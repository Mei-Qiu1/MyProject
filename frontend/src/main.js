import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'   // 导入所有图标
import App from './App.vue'
import router from './router'
import axios from './utils/axios'

const app = createApp(App)

// 全局注册所有 Element Plus 图标组件（关键！）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.config.globalProperties.$axios = axios

app.mount('#app')