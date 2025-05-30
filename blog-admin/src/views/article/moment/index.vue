<template>
  <div class="app-container">

    <!-- 操作按钮区域 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <ButtonGroup>
            <el-button v-permission="['sys:moment:add']" type="primary" icon="Plus" @click="handleAdd">新增</el-button>
            <el-button v-permission="['sys:moment:delete']" type="danger" icon="Delete"
              :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
          </ButtonGroup>
        </div>
      </template>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="momentList" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="内容" align="center" prop="content" show-overflow-tooltip />
        <el-table-column label="图片" align="center" prop="content">
          <template #default="scope">
            <el-image v-for="item in parseImage(scope.row.images)" :src="item" style="width: 50px; height: 50px" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="280" fixed="right">
          <template #default="scope">
            <el-button v-permission="['sys:moment:update']" type="primary" link icon="Edit"
              @click="handleUpdate(scope.row)">修改</el-button>
            <el-button v-permission="['sys:moment:delete']" type="danger" link icon="Delete"
              @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]" :total="total" :background="true"
          layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body destroy-on-close
      class="custom-dialog">
      <el-form ref="momentFormRef" :model="momentForm" :rules="rules" label-width="80px" class="custom-form">
        <el-form-item label="内容" prop="content">
            <div style="border: 1px solid #ccc;">
                <Toolbar style="border-bottom: 1px solid #ccc;" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
                <Editor style=" overflow-y: hidden;min-height: 300px;" v-model="momentForm.content" :defaultConfig="editorConfig" :mode="mode"
                @onCreated="handleCreated"/>
            </div>
        </el-form-item>
        <el-form-item label="图片" prop="images">
          <UploadImage v-model="momentForm.images" :source="'moment'" :limit="9" :multiple="true" />
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
  getSysMomentListApi,
  addSysMomentApi,
  updateSysMomentApi,
  deleteSysMomentApi
} from '@/api/article/moment'
import UploadImage from '@/components/Upload/Image.vue'


import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
const editorRef = shallowRef()
const mode = 'default'
const toolbarConfig = {}
const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {
    codeSelectLang: {
      // 代码语言
      codeLangs: [
        { text: "CSS", value: "css" },
        { text: "HTML", value: "html" },
        { text: "XML", value: "xml" },
        { text: "Java", value: "java" },
      ],
    },
  },
}

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
})

const loading = ref(false)
const total = ref(0)
const momentList = ref([])
const momentFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 选中项数组
const selectedIds = ref<number[]>([])

// 弹窗控制
const dialog = reactive({
  title: '',
  visible: false,
  type: 'add'
})

// 表单数据
const momentForm = reactive<any>({
  id: undefined,
  content: '',
  images: '',
})

// 表单校验规则
const rules = reactive<FormRules>({
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ],
})

const parseImage = (images: string) => {
  return images.split(',')
}

// 获取标签列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getSysMomentListApi(queryParams)
    momentList.value = data.records
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

  ElMessageBox.confirm(`是否确认删除 ${selectedIds.value.length} 个说说?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSysMomentApi(selectedIds.value)
      ElMessage.success('批量删除成功')
      getList()
      selectedIds.value = []
    } catch (error) {
    }
  })
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`是否确认删除 ${row.content} 这个说说?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSysMomentApi(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
    }
  })
}


// 新增说说
const handleAdd = () => {
  dialog.type = 'add'
  dialog.title = '新增说说'
  dialog.visible = true
  momentForm.id = undefined
  momentForm.content = ''
  momentForm.images = ''
}

// 修改说说
const handleUpdate = (row: any) => {
  dialog.type = 'edit'
  dialog.title = '修改说说'
  dialog.visible = true
  Object.assign(momentForm, row)
  momentForm.images = momentForm.images.split(',')
}

// 富文本编辑器创建完成
const handleCreated = (editor:any) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

// 提交表单
const submitForm = async () => {
  if (!momentFormRef.value) return

  await momentFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (momentForm.images && momentForm.images.length > 0) { 
          momentForm.images = momentForm.images.join(',')
        }
        if (dialog.type === 'add') {
          await addSysMomentApi(momentForm)
          ElMessage.success('新增成功')
        } else {
          await updateSysMomentApi(momentForm)
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
  momentFormRef.value?.resetFields()
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