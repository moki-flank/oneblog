<template>
    <div class="app-container">

        <!-- 操作按钮区域 -->
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <ButtonGroup>
                        <el-button v-permission="['sys:album:add']" type="primary" icon="Plus"
                            @click="handleAdd">新增</el-button>
                        <el-button v-permission="['sys:album:delete']" type="danger" icon="Delete"
                            :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>

                        <el-button type="success" icon="check" @click="handleAllSelect">全/反选</el-button>
                    </ButtonGroup>
                </div>
            </template>

            <!-- 数据表格 -->
            <div class="album-list" v-loading="loading">
                <el-checkbox-group v-model="selectedIds" v-for="item in albumList" :key="item.id">
                    <div class="album-item">
                        <span class="album-checkbox">
                            <el-checkbox :value="item.id" />
                        </span>
                        <el-image class="album-cover" :src="item.cover" />
                        <div class="album-lock" v-if="item.isLock">
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </div>
                        <div class="album-info">
                            <div class="album-name">{{ item.name }}</div>
                            <div class="album-description">{{ item.description }}</div>
                        </div>
                        <div class="album-action">
                            <el-button link type="success" size="small" icon="Edit"
                                @click="handlePreviewPhotos(item)">查看照片</el-button>
                            <el-button link v-permission="['sys:album:update']" type="primary" size="small" icon="Edit"
                                @click="handleUpdate(item)">编辑</el-button>
                            <el-button link v-permission="['sys:album:delete']" type="danger" size="small" icon="Delete"
                                @click="handleDelete(item)">删除</el-button>
                        </div>
                    </div>
                </el-checkbox-group>
            </div>
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
            <el-form ref="albumFormRef" :model="albumForm" :rules="rules" label-width="80px" class="custom-form">
                <el-form-item label="封面" prop="cover">
                    <UploadImage v-model="albumForm.cover" :source="'album-cover'" :limit="1" />
                </el-form-item>
                <el-form-item label="名称" prop="name">
                    <el-input v-model="albumForm.name" placeholder="请输入名称" clearable />
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="albumForm.description" type="textarea" :rows="4" show-word-limit
                        placeholder="请输入描述" clearable />
                </el-form-item>
                <el-form-item label="是否加密" prop="isLock">
                    <el-switch v-model="albumForm.isLock" :active-value="1" :inactive-value="0" />
                </el-form-item>
                <el-form-item label="密码" prop="password" v-if="albumForm.isLock === 1">
                    <el-input v-model="albumForm.password" placeholder="请输入密码" clearable />
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="cancel">取 消</el-button>
                    <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
                </div>
            </template>
        </el-dialog>

        <Photos v-model:openPhotos="openPhotos" :albumId="albumForm.id" />
    </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import UploadImage from '@/components/Upload/Image.vue'
import { listAlbumApi, addAlbumApi, updateAlbumApi, deleteAlbumApi, verifyAlbumPasswordApi } from '@/api/site/album'
import Photos from './Photos.vue'

// 查询参数
const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
})

const loading = ref(false)
const total = ref(0)
const albumList = ref<any[]>([])
const albumFormRef = ref<FormInstance>()
const submitLoading = ref(false)
const openPhotos = ref(false)
// 选中项数组
const selectedIds = ref<number[]>([])

// 弹窗控制
const dialog = reactive({
    title: '',
    visible: false,
    type: 'add'
})

// 表单数据
const albumForm = reactive<any>({
    id: undefined,
    name: '',
    description: '',
    cover: '',
    isLock: 0,
    password: '',
})

// 表单校验规则
const rules = reactive<FormRules>({
    name: [
        { required: true, message: '请输入名称', trigger: 'blur' }
    ],
    isLock: [
        { required: true, message: '请选择是否加密', trigger: 'blur' }
    ],

})

// 获取标签列表
const getList = async () => {
    loading.value = true
    try {
        const { data } = await listAlbumApi(queryParams)
        albumList.value = data.records
        total.value = data.total
    } catch (error) {
    }
    loading.value = false
}

// 全选
const handleAllSelect = () => {
    if (albumList.value.length === selectedIds.value.length) {
        selectedIds.value = []
    } else {
        selectedIds.value = albumList.value.map(item => item.id)
    }
}

// 批量删除
const handleBatchDelete = () => {
    if (selectedIds.value.length === 0) return

    ElMessageBox.confirm(`是否确认删除 ${selectedIds.value.length} 个相册?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await deleteAlbumApi(selectedIds.value)
            ElMessage.success('批量删除成功')
            getList()
            selectedIds.value = []
        } catch (error) {
        }
    })
}

// 删除
const handleDelete = (row: any) => {
    ElMessageBox.confirm(`是否确认删除 ${row.name} 这个相册?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await deleteAlbumApi(row.id)
            ElMessage.success('删除成功')
            getList()
        } catch (error) {
        }
    })
}


// 新增相册
const handleAdd = () => {
    dialog.type = 'add'
    dialog.title = '新增相册'
    dialog.visible = true
    albumForm.id = undefined
    albumForm.name = ''
    albumForm.description = ''
    albumForm.cover = ''
    albumForm.isLock = 0
    albumForm.password = ''
}

// 修改说说
const handleUpdate = (row: any) => {
    dialog.type = 'edit'
    dialog.title = '修改相册'
    dialog.visible = true
    Object.assign(albumForm, row)
    albumForm.createTime = undefined
}

// 查看照片
const handlePreviewPhotos = (row: any) => {
    if (row.isLock === 1) {
        ElMessageBox.prompt('', '提示', {
            inputPlaceholder: '请输入密码',
            inputType: 'password',
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            beforeClose: (action, instance, done) => {
                const password = instance.inputValue;
                if (action === 'confirm') {
                    if (!password) {
                        ElMessage.warning('请输入密码');
                        return false;
                    }
                    verifyAlbumPasswordApi(row.id, password).then(() => {
                        openPhotos.value = true
                        albumForm.id = row.id
                        done();
                    })
                } else {
                    // 点击取消，直接关闭弹框
                    done();
                }
            },
        });
    } else {
        openPhotos.value = true
        albumForm.id = row.id
    }

}

// 提交表单
const submitForm = async () => {
    if (!albumFormRef.value) return

    await albumFormRef.value.validate(async (valid) => {
        if (valid) {
            submitLoading.value = true
            try {
                if (dialog.type === 'add') {
                    await addAlbumApi(albumForm)
                    ElMessage.success('新增成功')
                } else {
                    await updateAlbumApi(albumForm)
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
    albumFormRef.value?.resetFields()
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

<style scoped lang="scss">
.album-list {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;

    .album-item {
        border: 1px solid #e6e6e6;
        margin-bottom: 10px;
        border-radius: 10px;
        position: relative;

        .album-checkbox {
            position: absolute;
            top: 10px;
            left: 10px;
        }

        .album-cover {
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            width: 300px;
            height: 150px;
            margin-bottom: 10px;
        }

        .album-lock {
            position: absolute;
            top: 10px;
            right: 10px;
            color: #fff;
            background-color: rgba(0, 0, 0, 0.3);
            border-radius: 50%;
            padding: 8px;

            i {
                font-size: 16px;
            }

        }

        .album-info {
            display: flex;
            flex-direction: column;
            padding: 0 10px;
            margin: 10px 0 20px 0;
            gap: 20px;

            .album-name {
                font-size: 16px;
                font-weight: bold;
                margin-bottom: 10px;
            }

            .album-description {
                font-size: 14px;
                color: #999;
            }
        }

        .album-action {
            display: flex;
            padding: 0 10px;
            margin: 10px 0 20px 0;
        }

    }
}
</style>
