<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 核心规则：所有非输入元素光标均为默认箭头 */
*:not(input):not(textarea):not([contenteditable="true"]) {
  cursor: default;
}

/* 但保留可点击元素的手型光标（提升交互体验） */
button,
a,
[role="button"],
.el-button,
.el-menu-item,
.el-sub-menu__title,
.el-pagination button,
.el-table__expand-icon,
.el-radio,
.el-checkbox,
.el-switch,
.el-tabs__item,
.el-dialog__headerbtn,
.el-select .el-input__wrapper,
.el-tree-node__content,
.el-link {
  cursor: pointer;
}

/* 输入框内保持文本光标（方便输入） */
input, textarea, [contenteditable="true"] {
  cursor: text;
}
</style>