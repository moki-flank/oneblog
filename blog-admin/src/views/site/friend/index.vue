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
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态">
              <el-option v-for="item in statusList" :label="item.label" :value="item.value" />
            </el-select>
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
                v-permission="['sys:friend:add']"
                type="primary"
                icon="Plus"
                @click="handleAdd"
              >新增</el-button>
              <el-button
                v-permission="['sys:friend:delete']"
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
          :data="tableData"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection"  width="55" align="center" />
          <el-table-column label="头像" align="center" prop="avatar" show-overflow-tooltip>
            <template #default="scope"> 
              <el-image :src="scope.row.avatar" style="width: 50px; height: 50px;" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column label="名称" align="center" prop="name" show-overflow-tooltip />
          <el-table-column label="地址" align="center" prop="url" show-overflow-tooltip />
          <el-table-column label="描述" align="center" prop="info" show-overflow-tooltip />
          <el-table-column label="状态" align="center" prop="status" show-overflow-tooltip>
            <template #default="scope">
              <el-tag :type="statusList[scope.row.status].type">{{ statusList[scope.row.status].label }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="排序" align="center" prop="sort" show-overflow-tooltip />
          <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
          <el-table-column label="操作" align="center" width="280" fixed="right">
            <template #default="scope">
              <el-button
                v-permission="['sys:friend:update']"
                type="primary"
                link
                icon="Edit"
                @click="handleUpdate(scope.row)"
              >修改</el-button>
              <el-button
                v-permission="['sys:friend:delete']"
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
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          class="custom-form"
        >
            <el-form-item label="站点名称" prop="name">
                <el-input 
                    v-model="form.name" 
                    placeholder="请输入站点名称" 
                    clearable
                />
            </el-form-item>
            <el-form-item label="站点地址" prop="url">
                <el-input 
                    v-model="form.url" 
                    placeholder="请输入站点地址" 
                    clearable
                />
            </el-form-item>
            <el-form-item label="站点头像" prop="avatar">
                <el-input 
                    v-model="form.avatar" 
                    placeholder="请输入站点头像" 
                    clearable
                />
            </el-form-item>
            <el-form-item label="站点描述" prop="info">
                <el-input 
                    v-model="form.info" 
                    placeholder="请输入站点描述" 
                    clearable
                />
            </el-form-item>
            <el-form-item label="联系邮箱" prop="email">
              <el-input 
                    v-model="form.email" 
                    placeholder="请输入联系邮箱" 
                    clearable
                />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="(item) in statusList" :label="item.value">{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
                <el-input-number
                    :min="1"
                    v-model="form.sort" 
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
    getFriendListApi,
    addFriendApi,
    updateFriendApi,
    deleteFriendApi
  } from '@/api/site/friend'

  
  // 查询参数
  const queryParams = reactive<any>({
    pageNum: 1,
    pageSize: 10,
    name: null,
    status: null
  })
  
  const loading = ref(false)
  const total = ref(0)
  const tableData = ref<any>([])
  const queryFormRef = ref<FormInstance>()
  const formRef = ref<FormInstance>()
  const submitLoading = ref(false)
  const statusList = ref<any>([
    { label: '下架', value: 0, type: 'danger' as const },
    { label: '申请', value: 1, type: 'warning' as const },
    { label: '上架', value: 2, type: 'success' as const }
  ])
  // 选中项数组
  const selectedIds = ref<string[]>([])
  
  // 弹窗控制
  const dialog = reactive({
    title: '',
    visible: false,
    type: 'add'
  })
  
  // 表单数据
  const form = reactive({
    id: undefined,
    name: '',
    sort: 0,
    url: '',
    avatar: '',
    info: '',
    status: 1,
    email: ''
  })
  
  // 表单校验规则
  const rules = reactive<FormRules>({
    name: [
      { required: true, message: '请输入名称', trigger: 'blur' },
      { min: 3, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    sort: [
      { required: true, message: '请输入排序', trigger: 'blur' }
    ],
    url: [
      { required: true, message: '请输入友链地址', trigger: 'blur' }
    ],
    avatar: [
      { required: true, message: '请输入友链头像', trigger: 'blur' }
    ],
    info: [
      { required: true, message: '请输入友链描述', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择友链状态', trigger: 'blur' }
    ],
    email: [
      { required: false, message: '请输入联系邮箱', trigger: 'blur' }
    ]
  })
  
  // 获取友链列表
  const getList = async () => {
    loading.value = true
    try {
      const { data } = await getFriendListApi(queryParams)
      tableData.value = data.records
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
    if (selectedIds.value.length === 0) {
      ElMessage.warning('请选择要删除的记录')
      return
    }
    ElMessageBox.confirm(`是否确认删除 ${selectedIds.value.length} 个友链?`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteFriendApi(selectedIds.value)
        ElMessage.success('删除成功')
        getList()
      } catch (error) {
      }
    })
  }
  
  // 删除
  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`确定要删除 ${row.name} 这个友链吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteFriendApi(row.id)
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
  
  // 新增友链
  const handleAdd = () => {
    dialog.type = 'add'
    dialog.title = '新增友链'
    dialog.visible = true
    form.id = undefined
    form.name = ''
    form.sort = 0
  }
  
  // 修改友链
  const handleUpdate = (row: any) => {
    dialog.type = 'edit'
    dialog.title = '修改友链'
    dialog.visible = true
    Object.assign(form, row)
  }
  
  // 提交表单
  const submitForm = async () => {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid) => {
      if (valid) {
        submitLoading.value = true
        try {
          if (dialog.type === 'add') {
            await addFriendApi(form)
            ElMessage.success('新增成功')
          } else {
            await updateFriendApi(form)
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
    formRef.value?.resetFields()
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
  