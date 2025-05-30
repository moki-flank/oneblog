<template>
    <div class="app-container">
        <!-- 搜索表单 -->
        <div class="search-wrapper">
            <el-form :model="queryParams" ref="queryFormRef" inline>
                <el-form-item label="反馈类型" prop="type">
                    <el-select v-model="queryParams.type" placeholder="请选择反馈类型" clearable @keyup.enter="handleQuery">
                        <el-option v-for="item in feedbackTypes" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="queryParams.status" placeholder="请选择状态" clearable @keyup.enter="handleQuery">
                        <el-option v-for="item in feedbackStatus" :key="item.value" :label="item.label"
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
                <!-- <el-button type="primary" plain icon="Plus" @click="handleAdd">新增
                </el-button> -->
                <el-button type="danger" v-permission="['sys:feedback:delete']" plain icon="Delete" :disabled="selectedIds.length === 0"
                    @click="handleBatchDelete">批量删除
                </el-button>
            </template>

            <!-- 数据表格 -->
            <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column label="反馈人头像" align="center" prop="avatar" width="100">
                    <template #default="scope">
                        <el-avatar :src="scope.row.avatar" alt="Avatar" width="50" height="50" />
                    </template>
                </el-table-column>
                <el-table-column label="反馈人昵称" align="center" prop="nickname" />
                <el-table-column label="反馈类型" align="center" prop="type">
                    <template #default="scope">
                        <span v-for="item in feedbackTypes">
                            <el-tag v-if="item.value === scope.row.type" :key="item.value" :type="item.style">
                                {{ item.label }}
                            </el-tag>
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="反馈内容" align="center" prop="content" show-overflow-tooltip />
                <el-table-column label="联系邮箱" align="center" prop="email" />
                <el-table-column label="回复内容" align="center" prop="replyContent" show-overflow-tooltip/>
                <el-table-column label="状态" align="center" prop="status">
                    <template #default="scope">
                        <span v-for="item in feedbackStatus">
                            <el-tag v-if="item.value === String(scope.row.status)" :key="item.value" :type="item.style">
                                {{ item.label }}
                            </el-tag>
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" align="center" prop="createTime" width="170"/>
                <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                    <template #default="scope">
                        <el-button type="primary" link icon="Edit" v-permission="['sys:feedback:update']"  @click="handleUpdate(scope.row)">修改
                        </el-button>
                        <el-button type="danger" link icon="Delete" v-permission="['sys:feedback:delete']"  @click="handleDelete(scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页工具栏 -->
            <div class="pagination-container">
                <el-pagination
                    background
                    v-model:current-page="queryParams.pageNum"
                    v-model:page-size="queryParams.pageSize"
                    :page-sizes="[10, 20, 30, 50]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>

            <!-- 添加或修改对话框 -->
            <el-dialog v-model="open" :title="title" width="500px" append-to-body>
                <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                    <el-form-item label="回复内容" prop="replyContent">
                        <el-input type="textarea" :rows="5" v-model="form.replyContent" placeholder="请输入回复内容" />
                    </el-form-item>
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="form.status">
                            <el-radio v-for="item in feedbackStatus" :value="Number(item.value)">{{item.label}}</el-radio>
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
    listSysFeedbackApi,
    detailSysFeedbackApi,
    deleteSysFeedbackApi,
    addSysFeedbackApi,
    updateSysFeedbackApi
} from '@/api/message/feedback'

import {
    getDictDataByDictTypesApi
} from '@/api/system/dict'

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
    type: undefined,
    status: undefined,
    source: 'admin'
})

// 表单参数
const form = reactive<any>({})
// 表单校验
const rules = reactive({
    replyContent: [
        { required: false, message: "回复内容不能为空", trigger: "blur" }
    ],
    status: [
        { required: true, message: "状态不能为空", trigger: "blur" }
    ]
})

const queryFormRef = ref()
const formRef = ref()

const feedbackTypes = ref<any[]>([])
const feedbackStatus = ref<any[]>([])



/** 查询列表 */
const getList = () => {
    loading.value = true
    listSysFeedbackApi(queryParams).then(response => {
        dataList.value = response.data.records
        total.value = response.data.total
        loading.value = false
    })
}

const getDicts = () => {
    getDictDataByDictTypesApi(['feedback_type', 'feedback_status']).then(res => {
        feedbackTypes.value = res.data.feedback_type.list
        feedbackStatus.value = res.data.feedback_status.list
    })
}

/** 取消按钮 */
const cancel = () => {
    open.value = false
    reset()
}

/** 表单重置 */
const reset = () => {
    form.value = {
        id: undefined,
        userId: undefined,
        type: undefined,
        content: undefined,
        email: undefined,
        replyContent: undefined,
        status: undefined,
        createTime: undefined
    }
    formRef.value?.resetFields()
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
    title.value = "添加反馈"
}

/** 修改按钮操作 */
const handleUpdate = (row : any) => {
    reset()
    Object.assign(form,row)
    open.value = true
    title.value = "修改反馈"
}

/** 提交按钮 */
const submitForm = () => {
    formRef.value?.validate((valid : any) => {
        if (valid) {
            if (form.id !== undefined) {
                updateSysFeedbackApi(form).then(response => {
                    ElMessage.success("修改成功")
                    open.value = false
                    getList()
                })
            } else {
                addSysFeedbackApi(form).then(response => {
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
    ElMessageBox.confirm(`是否确认删除${selectedIds.value.length}条数据项?`, "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(async () => {
        await deleteSysFeedbackApi(selectedIds.value)
    }).then(() => {
        getList()
        ElMessage.success("删除成功")
    })
}

/** 删除按钮操作 */
const handleDelete = (row : any) =>  {
    ElMessageBox.confirm('是否确认删除内容为"' + row.content + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
    }).then(async () => {
        await deleteSysFeedbackApi(row.id)
    }).then(() => {
        getList()
        ElMessage.success("删除成功")
    })
}


// 添加分页方法
const handleSizeChange = (val : any) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val : any) => {
  queryParams.pageNum = val
  getList()
}

onMounted(() => {
    getList()
    getDicts()
})
</script>