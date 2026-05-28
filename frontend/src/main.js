import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import axios from './utils/axios'

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.config.globalProperties.$axios = axios

app.mount('#app')

// 阻止点击 label 时自动聚焦到关联的输入框（避免非输入框区域点击出现光标）
// 在 main.js 末尾添加（在 app.mount 之前或之后均可）

// ========== 禁止非输入区域出现编辑光标 ==========
// 1. 移除所有现有非输入元素上的 contenteditable 属性
function removeNonInputContentEditable() {
    document.querySelectorAll('[contenteditable="true"]').forEach(el => {
        if (el.tagName !== 'INPUT' && el.tagName !== 'TEXTAREA') {
            el.removeAttribute('contenteditable');
            // 可选：确保元素不可获得焦点
            el.setAttribute('tabindex', '-1');
        }
    });
}

// 2. 监听动态添加的元素，防止后来插入的可编辑元素
const observer = new MutationObserver(mutations => {
    mutations.forEach(mutation => {
        mutation.addedNodes.forEach(node => {
            if (node.nodeType === 1) {
                if (node.hasAttribute && node.hasAttribute('contenteditable') &&
                    node.getAttribute('contenteditable') === 'true' &&
                    node.tagName !== 'INPUT' && node.tagName !== 'TEXTAREA') {
                    node.removeAttribute('contenteditable');
                }
                // 同样处理其子元素
                if (node.querySelectorAll) {
                    node.querySelectorAll('[contenteditable="true"]').forEach(child => {
                        if (child.tagName !== 'INPUT' && child.tagName !== 'TEXTAREA') {
                            child.removeAttribute('contenteditable');
                        }
                    });
                }
            }
        });
    });
});

// 3. 阻止任何非输入元素获得焦点（彻底杜绝光标）
document.addEventListener('focusin', (e) => {
    const target = e.target;
    const isInput = target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable;
    if (!isInput && target !== document.body) {
        target.blur();
    }
});

// 4. 页面加载时执行
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', removeNonInputContentEditable);
} else {
    removeNonInputContentEditable();
}
observer.observe(document.body, { childList: true, subtree: true });