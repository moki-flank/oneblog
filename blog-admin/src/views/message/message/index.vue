<template>
    <div class="app-container">
 
  
      <!-- 操作按钮区域 -->
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <ButtonGroup>
              <el-button
                v-permission="['sys:message:delete']"
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
          :data="messageList"
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection"  width="55" align="center" />
          <el-table-column label="用户头像" align="center" prop="name" show-overflow-tooltip>
            <template #default="scope">
                <el-image :src="scope.row.avatar" style="width: 30px; height: 30px; border-radius: 50%;" />
            </template>
          </el-table-column>
          <el-table-column label="IP" align="center" prop="ip" show-overflow-tooltip />
          <el-table-column label="IP来源" align="center" prop="source" show-overflow-tooltip />
          <el-table-column label="留言内容" width="300" align="center" prop="content" show-overflow-tooltip>
            <template #default="scope">
                <span v-html="scope.row.content"></span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
          <el-table-column label="操作" align="center" width="280" fixed="right">
            <template #default="scope">
              <el-button
                v-permission="['sys:message:delete']"
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
    </div>
  </template>
  
  <script setup lang="ts">
  import { ElMessage, ElMessageBox } from 'element-plus'
  import {
    getMessageListApi,
    deleteMessageApi
  } from '@/api/message/message'
  
  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    name: null
  })
  
  const loading = ref(false)
  const total = ref(0)
  const messageList = ref([])
  
  // 选中项数组
  const selectedIds = ref<string[]>([])
  
  // 获取标签列表
  const getList = async () => {
    loading.value = true
    try {
      const { data } = await getMessageListApi(queryParams)
      messageList.value = data.records
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
    
    ElMessageBox.confirm(`是否确认删除 ${selectedIds.value.length} 条留言?`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteMessageApi(selectedIds.value)
        ElMessage.success('批量删除成功')
        getList()
        selectedIds.value = []
      } catch (error) {
      }
    })
  }
  
  // 删除
  const handleDelete = (row: any) => {
    ElMessageBox.confirm(`是否确认删除 ${row.nickname} 的留言?`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await deleteMessageApi(row.id)
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
  