<template>
  <div class="category-list">
    <div class="search-bar">
      <el-radio-group v-model="categoryType" @change="loadCategories">
        <el-radio :label="1">药理分类</el-radio>
        <el-radio :label="2">管理分类</el-radio>
      </el-radio-group>
    </div>

    <el-tree
        :data="categoryTree"
        :props="{ label: 'categoryName', children: 'children' }"
        :expand-on-click-node="false"
    >
      <template #default="{ node, data }">
        <span class="tree-node">
          <span>{{ node.label }}</span>
          <span class="tree-actions">
            <el-button type="text" size="small" @click="editCategory(data)">编辑</el-button>
            <el-button type="text" size="small" @click="deleteCategory(data)">删除</el-button>
          </span>
        </span>
      </template>
    </el-tree>

    <el-dialog title="编辑分类" v-model="showAddModal" width="500px">
      <el-form :model="formData" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName"></el-input>
        </el-form-item>
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input v-model="formData.categoryCode"></el-input>
        </el-form-item>
        <el-form-item label="上级分类" prop="parentId">
          <el-select v-model="formData.parentId">
            <el-option label="无" :value="0"></el-option>
            <el-option v-for="cat in parentOptions" :key="cat.id" :label="cat.categoryName" :value="cat.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input v-model="formData.sortOrder" type="number"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="formData.remark"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const categoryType = ref(1)
const categoryTree = ref([])
const parentOptions = ref([])
const showAddModal = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  categoryName: '',
  categoryCode: '',
  parentId: 0,
  type: 1,
  sortOrder: 0,
  remark: ''
})

const loadCategories = async () => {
  formData.type = categoryType.value
  try {
    const response = await axios.get('/drugs/categories', { params: { type: categoryType.value } })
    if (response.code === 200) {
      categoryTree.value = buildTree(response.data)
      parentOptions.value = response.data.filter(c => !c.parentId || c.parentId === 0)
    }
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

const buildTree = (list) => {
  const map = new Map()
  const roots = []

  list.forEach(item => {
    map.set(item.id, { ...item, children: [] })
  })

  list.forEach(item => {
    const node = map.get(item.id)
    if (item.parentId && item.parentId !== 0 && map.has(item.parentId)) {
      map.get(item.parentId).children.push(node)
    } else {
      roots.push(node)
    }
  })

  return roots
}

const editCategory = (data) => {
  Object.assign(formData, data)
  showAddModal.value = true
}

const deleteCategory = async (data) => {
  if (data.children && data.children.length > 0) {
    ElMessage.warning('请先删除子分类')
    return
  }
  if (confirm(`确定要删除分类 ${data.categoryName} 吗？`)) {
    try {
      await axios.delete(`/drugs/categories/${data.id}`)
      ElMessage.success('删除成功')
      loadCategories()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }
}

const saveCategory = async () => {
  // 仅允许编辑已有分类，不允许新增
  if (!formData.id) {
    ElMessage.warning('暂不支持新增分类，只能编辑现有分类')
    return
  }
  formData.type = categoryType.value
  try {
    await axios.put(`/drugs/categories/${formData.id}`, formData)
    ElMessage.success('保存成功')
    showAddModal.value = false
    loadCategories()
    resetForm()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const resetForm = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = null
  })
  formData.type = categoryType.value
  formData.parentId = 0
  formData.sortOrder = 0
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-list {
  background: white;
  border-radius: 10px;
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.tree-actions {
  display: flex;
  gap: 10px;
}
</style>