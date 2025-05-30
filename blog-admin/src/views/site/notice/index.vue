<template>
    <div class="app-container">
        <!-- 搜索表单 -->
        <div class="search-wrapper">
            <el-form :model="queryParams" ref="queryFormRef" inline>
                <el-form-item label="公告内容" prop="content">
                    <el-input v-model="queryParams.content"  placeholder="请输入公告内容" clearable
                        @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="是否展示" prop="isShow">
                    <el-select v-model="queryParams.isShow" placeholder="请选择是否展示"  style="width: 240px">
                        <el-option label="否" :value="0" />
                        <el-option label="是" :value="1" />
                    </el-select>
                </el-form-item>
                <el-form-item label="显示位置" prop="position">
                    <el-select v-model="queryParams.position" placeholder="请选择显示位置" style="width: 240px">
                        <el-option v-for="item in positionOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                    <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

        </div>
        <el-card class="box-card">
            <!-- 操作工具栏 -->
            <template #header>
                <el-button type="primary"   v-permission="['sys:notice:add']" icon="Plus" @click="handleAdd">新增
                </el-button>
                <el-button type="danger"   v-permission="['sys:notice:delete']" icon="Delete" :disabled="selectedIds.length === 0"
                    @click="handleBatchDelete">批量删除
                </el-button>
            </template>

            <!-- 数据表格 -->
            <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column label="内容" align="left" width="800" prop="content" show-overflow-tooltip>
                    <template #default="scope">
                        <div v-html="scope.row.content"></div>
                    </template>
                </el-table-column>
                <el-table-column label="是否展示" align="center" prop="isShow">
                    <template #default="scope">
                        <el-switch v-model="scope.row.isShow" @change="handleChange(scope.row)"
                            style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" :active-value="1"
                            :inactive-value="0" />
                    </template>
                </el-table-column>
                <el-table-column label="显示位置" align="center" prop="position">
                    <template #default="scope">
                        <span v-for="item in positionOptions">
                            <el-tag :type="item.style" v-if="scope.row.position === item.value">
                                {{ item.label }}
                            </el-tag>
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" align="center" prop="createTime" width="180"/>
                <el-table-column label="操作" align="center" width="150">
                    <template #default="scope">
                        <el-button type="primary" link icon="Edit"  v-permission="['sys:notice:update']" @click="handleUpdate(scope.row)">修改
                        </el-button>
                        <el-button type="danger" link icon="Delete"  v-permission="['sys:notice:delete']" @click="handleDelete(scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页工具栏 -->
            <div class="pagination-container">
                <el-pagination background v-model:current-page="queryParams.pageNum"
                    v-model:page-size="queryParams.pageSize" :page-sizes="[10, 20, 30, 50]" :total="total"
                    layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" />
            </div>

            <!-- 添加或修改对话框 -->
            <el-dialog v-model="open" :title="title" width="500px" append-to-body>
                <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                    <el-form-item label="公告内容" prop="content">
                        <div style="border: 1px solid #ccc">
                            <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
                            <Editor style="height: 300px; overflow-y: hidden;" v-model="form.content" :defaultConfig="editorConfig" :mode="mode"
                            @onCreated="handleCreated"/>
                        </div>

                    </el-form-item>
                    <el-form-item label="是否展示" prop="isShow">
                        <el-switch v-model="form.isShow"
                            style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949" :active-value="1"
                            :inactive-value="0" />
                    </el-form-item>
                    <el-form-item label="显示位置" prop="position">
                        <el-radio-group v-model="form.position">
                            <el-radio v-for="dict in positionOptions" :value="dict.value">{{ dict.label }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button type="primary" @click="submitForm">确 定</el-button>
                        <el-button @click="cancel">取 消</el-button>
                    </div>
                </template>
            </el-dialog>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    listSysNoticeApi,
    deleteSysNoticeApi,
    addSysNoticeApi,
    updateSysNoticeApi
} from '@/api/site/notice'

import { getDictDataByDictTypesApi } from '@/api/system/dict'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'

const editorRef = shallowRef()
const mode = 'default'
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...' }


// 遮罩层
const loading = ref(true)
// 选中数组
const selectedIds = ref<any[]>([])
// 总条数
const total = ref(0)
// 表格数据
const dataList = ref([])
// 弹出层标题
const title = ref('')
// 是否显示弹出层
const open = ref(false)
// 查询参数
const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    id: undefined,
    content: undefined,
    isShow: undefined,
    position: undefined,
    createTime: undefined,
})

// 表单参数
const form = reactive<any>({})
// 表单校验
const rules = reactive({
    content: [
        { required: true, message: "内容不能为空", trigger: "blur" }
    ],
    isShow: [
        { required: true, message: "是否展示不能为空", trigger: "blur" }
    ],
    position: [
        { required: true, message: "显示位置不能为空", trigger: "blur" }
    ],
})

const queryFormRef = ref()
const formRef = ref()

// 字典数据
const positionOptions = ref<any[]>([])

/** 查询列表 */
const getList = () => {
    loading.value = true
    listSysNoticeApi(queryParams).then(response => {
        dataList.value = response.data.records
        total.value = response.data.total
        loading.value = false
    })
}

/** 取消按钮 */
const cancel = () => {
    open.value = false
    reset()
}

/** 表单重置 */
const reset = () => {
    form.id = undefined
    form.isShow = 1
    form.position = 'right'
    form.content = ''
}

/** 搜索按钮操作 */
const handleQuery = () => {
    queryParams.pageNum = 1
    getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
    queryFormRef.value?.resetFields()
    handleQuery()
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: { id: any }[]) => {
    selectedIds.value = selection.map(item => item.id)
}

/** 新增按钮操作 */
const handleAdd = () => {
    reset()
    open.value = true
    title.value = "添加公告"
}

/** 修改按钮操作 */
const handleUpdate = (row: any) => {
    reset()
    Object.assign(form, row)
    open.value = true
    title.value = "修改公告"

}

/** 提交按钮 */
const submitForm = () => {
    formRef.value?.validate((valid: any) => {
        if (valid) {
            if (form.id !== undefined) {
                updateSysNoticeApi(form).then(response => {
                    ElMessage.success("修改成功")
                    open.value = false
                    getList()
                })
            } else {
                addSysNoticeApi(form).then(response => {
                    ElMessage.success("新增成功")
                    open.value = false
                    getList()
                })
            }
        }
    })
}

/** 批量删除按钮操作 */
const handleBatchDelete = () => {
    if (!selectedIds.value.length) {
        return
    }
    ElMessageBox.confirm('是否确认删除"' + selectedIds.value.length + '"条数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        deleteSysNoticeApi(selectedIds.value)
    }).then(() => {
        getList()
        ElMessage.success("删除成功")
    })
}

/** 删除按钮操作 */
const handleDelete = (row: any) => {
    ElMessageBox.confirm('是否确认删除编号为"' + row.id + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(() => {
        deleteSysNoticeApi(row.id)
    }).then(() => {
        getList()
        ElMessage.success("删除成功")
    })
}


// 添加分页方法
const handleSizeChange = (val: any) => {
    queryParams.pageSize = val
    getList()
}

const handleCurrentChange = (val: any) => {
    queryParams.pageNum = val
    getList()
}
// 切换状态
const handleChange = (row: any) => {
    updateSysNoticeApi({ id: row.id, isShow: row.isShow,position:row.position }).then(response => {
        ElMessage.success("修改成功")
    }).catch(error => {
        row.isShow = !row.isShow
    })
}

const getDicts = async () => {
    try {
        const { data } = await getDictDataByDictTypesApi(['notice_position'])
        positionOptions.value = data.notice_position.list
    } catch (error) {
    }
}

const handleCreated = (editor:any) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

onMounted(() => {
    getList()
    getDicts()
})
</script>