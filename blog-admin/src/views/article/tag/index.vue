<template>
    <div class="app-container">
      <!-- 搜索表单 -->
      <div class="search-wrapper">
        <el-form ref="queryFormRef" :model="queryParams" :inline="true">
          <el-form-item label="名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入名称"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
  
      <!-- 操作按钮区域 -->
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <ButtonGroup>
              <el-button
                v-permission="['sys:tag:add']"
                type="primary"
                icon="Plus"
                @click="handleAdd"
              >新增</el-button>
              <el-button
                v-permission="['sys:tag:delete']"
                type="danger"
                icon="Delete"
                :disabled="selectedIds.length === 0"
                @click="handleBatchDelete"
              >批量删除</el-button>
            </ButtonGroup>
          </div>
        </template>
  
        <!-- 数据表格 -->
        <el-table
          v-loading="loading"
          :data="tagList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection"  width="55" align="center" />
          <el-table-column label="名称" align="center" prop="name" show-overflow-tooltip />
          <el-table-column label="排序" align="center" prop="sort" show-overflow-tooltip />
          <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
          <el-table-column label="操作" align="center" width="280" fixed="right">
            <template #default="scope">
              <el-button
                v-permission="['sys:tag:update']"
                type="primary"
                link
                icon="Edit"
                @click="handleUpdate(scope.row)"
              >修改</el-button>
              <el-button
                v-permission="['sys:tag:delete']"
                type="danger"
                link
                icon="Delete"
                @click="handleDelete(scope.row)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
  
        <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 30, 50]"
            :total="total"
            :background="true"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
  
      <!-- 添加或修改对话框 -->
      <el-dialog
        :title="dialog.title"
        v-model="dialog.visible"
        width="600px"
        append-to-body
        destroy-on-close
        class="custom-dialog"
      >
        <el-form
          ref="tagFormRef"
          :model="tagForm"
          :rules="rules"
          label-width="80px"
          class="custom-form"
        >
            <el-form-item label="名称" prop="name">
                <el-input 
                    v-model="tagForm.name" 
                    placeholder="请输入名称" 
                    clearable
                />
            </el-form-item>
            <el-form-item label="排序" prop="sort">
                <el-input-number
                    :min="1"
                    v-model="tagForm.sort" 
                    placeholder="请输入排序"
                    clearable 
                />
            </el-form-item>
        </el-form>
  
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ElMessage, ElMessageBox } from 'element-plus'
  import type { FormInstance, FormRules } from 'element-plus'
  import {
    getTagListApi,
    addTagApi,
    updateTagApi,
    deleteTagApi
  } from '@/api/article/tag'
  
  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    name: null
  })
  
  const loading = ref(false)
  const total = ref(0)
  const tagList = ref([])
  const queryFormRef = ref<FormInstance>()
  const tagFormRef = ref<FormInstance>()
  const submitLoading = ref(false)
  
  // 选中项数组
  const selectedIds = ref<string[]>([])
  
  // 弹窗控制
  const dialog = reactive({
    title: '',
    visible: false,
    type: 'add'
  })
  
  // 表单数据
  const tagForm = reactive({
    id: undefined,
    name: '',
    sort: 0,
  })
  
  // 表单校验规则
  const rules = reactive<FormRules>({
    name: [
      { required: true, message: '请输入名称', trigger: 'blur' },
      { min: 3, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    sort: [
      { required: true, message: '请输入排序', trigger: 'blur' }
    ]
  })
  
  // 获取标签列表
  const getList = async () => {
    loading.value = true
    try {
      const { data } = await getTagListApi(queryParams)
      tagList.value = data.records
      total.value = data.total
    } catch (error) {
    }
    loading.value = false
  }
  
  // 表格选择项变化
  const handleSelectionChange = (selection: any[]) => {
    selectedIds.value = selection.map(item => item.id)
  }
  
  // 批量删除
  const handleBatchDelete = () => {
    if (selectedIds.value.length === 0) return
    
    ElMessageBox.confirm(`是否确认删除 ${selectedIds.value.length} 个标签?`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteTagApi(selectedIds.value)
        ElMessage.success('批量删除成功')
        getList()
        selectedIds.value = []
      } catch (error) {
      }
    })
  }
  
  // 删除
  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`是否确认删除 ${row.name} 这个标签?`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteTagApi(row.id)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
      }
    })
  }

  // 搜索
  const handleQuery = () => {
    queryParams.pageNum = 1
    getList()
  }
  
  // 重置查询
  const resetQuery = () => {
    queryFormRef.value?.resetFields()
    handleQuery()
  }
  
  // 新增标签
  const handleAdd = () => {
    dialog.type = 'add'
    dialog.title = '新增标签'
    dialog.visible = true
    tagForm.id = undefined
    tagForm.name = ''
    tagForm.sort = 0
  }
  
  // 修改标签
  const handleUpdate = (row: any) => {
    dialog.type = 'edit'
    dialog.title = '修改标签'
    dialog.visible = true
    Object.assign(tagForm, row)
  }
  
  // 提交表单
  const submitForm = async () => {
    if (!tagFormRef.value) return
    
    await tagFormRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true
        try {
          if (dialog.type === 'add') {
            await addTagApi(tagForm)
            ElMessage.success('新增成功')
          } else {
            await updateTagApi(tagForm)
            ElMessage.success('修改成功')
          }
          getList()
          dialog.visible = false
        } catch (error) {
        } finally {
          submitLoading.value = false
        }
      }
    })
  }
  
  // 取消按钮
  const cancel = () => {
    dialog.visible = false
    tagFormRef.value?.resetFields()
  }
  
  // 分页大小改变
  const handleSizeChange = (val: number) => {
    queryParams.pageSize = val
    getList()
  }
  
  // 页码改变
  const handleCurrentChange = (val: number) => {
    queryParams.pageNum = val
    getList()
  }
  
  // 初始化
  onMounted(() => {
    getList()
  })
  </script>
