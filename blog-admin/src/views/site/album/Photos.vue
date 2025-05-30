<template>
    <el-dialog title="相册照片" :model-value="props.openPhotos" width="80%" top="5vh" append-to-body destroy-on-close 
    :close-on-click-modal="false" @update:model-value="handleDialogClose">

        <!-- 操作按钮区域 -->
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <ButtonGroup>
                        <el-button v-permission="['sys:photo:add']" type="primary" icon="Plus"
                            @click="handleAdd">新增</el-button>
                        <el-button v-permission="['sys:photo:delete']" type="danger" icon="Delete"
                            :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
                        <el-button v-permission="['sys:photo:move']" type="info" icon="Notification"
                            :disabled="selectedIds.length === 0" @click="handleBatchMove">批量移动</el-button>
                        <el-button type="success" icon="check" @click="handleAllSelect">全/反选</el-button>
                    </ButtonGroup>
                </div>
            </template>

            <!-- 数据表格 -->
            <div class="photo-list" v-loading="loading">
                <el-checkbox-group v-model="selectedIds" v-for="item in photoList" :key="item.id">
                    <div class="photo-card" >
                        <span class="photo-select">
                            <el-checkbox :value="item.id" />
                        </span>
                        <el-image class="photo-image" :src="item.url" />
                        <div class="photo-overlay">
                            <div class="photo-content">
                                <div class="photo-time">
                                    <i class="el-icon-time" />
                                    <span class="time-text">{{ item.recordTime }}</span>
                                </div>
                                <div class="photo-desc">{{ item.description }}</div>
                            </div>
                            <div class="photo-actions">
                                <el-button link type="success" size="small" icon="View" @click="handlePreviewPhotos(item)">查看</el-button>
                                <el-button link v-permission="['sys:photo:update']" type="primary" size="small" icon="Edit" @click="handleUpdate(item)">编辑</el-button>
                                <el-button link v-permission="['sys:photo:delete']" type="danger" size="small" icon="Delete" @click="handleDelete(item)">删除</el-button>
                            </div>
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
            <el-form ref="photoFormRef" :model="photoForm" :rules="rules" label-width="80px" class="custom-form">
                <el-form-item label="封面" prop="url">
                    <UploadImage v-model="photoForm.url" :source="'photo'" :limit="1" />
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="photoForm.description" type="textarea" :rows="4" show-word-limit placeholder="请输入描述"
                        clearable />
                </el-form-item>
                <el-form-item label="记录时间" prop="recordTime">
                    <el-date-picker
                        v-model="photoForm.recordTime"
                        type="date"
                        value-format="YYYY-MM-DD"
                        placeholder="请选择记录的日期..."
                    />
                </el-form-item>
                <el-form-item label="排序" prop="sort">
                    <el-input-number v-model="photoForm.sort" :min="1" />
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="cancel">取 消</el-button>
                    <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 批量移动对话框 -->
        <el-dialog
            title="移动照片"
            v-model="moveDialog.visible"
            width="400px"
            append-to-body
            destroy-on-close
        >
            <el-form label-width="80px">
                <el-form-item label="目标相册">
                    <el-select v-model="moveDialog.targetAlbumId" placeholder="请选择目标相册" style="width: 100%">
                        <el-option
                            v-for="album in albumList"
                            :key="album.id"
                            :label="album.name"
                            :value="album.id"
                            :disabled="album.id === props.albumId"
                        />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="cancelMove">取 消</el-button>
                    <el-button type="primary" @click="confirmMove">确 定</el-button>
                </div>
            </template>
        </el-dialog>

        <el-image-viewer v-if="openPreview" @close="closeViewer" :url-list="previewList" />
    </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import UploadImage from '@/components/Upload/Image.vue'
import { listPhotoApi, addPhotoApi, updatePhotoApi, deletePhotoApi, movePhotoApi } from '@/api/site/photo'
import { listAlbumAllApi } from '@/api/site/album'

// 查询参数
const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    albumId: 0
})

const loading = ref(false)
const total = ref(0)
const photoList = ref<any[]>([])
const previewList = ref<string[]>([])
const openPreview = ref(false)
const photoFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 所有相册
const albumList = ref<any[]>([])

const props = defineProps({
    openPhotos: {
        type: Boolean,
        default: false
    },
    albumId: {
        type: Number,
        default: 0
    }
})

const emit = defineEmits(['update:openPhotos', 'close'])

// 选中项数组
const selectedIds = ref<number[]>([])

// 弹窗控制
const dialog = reactive({
    title: '',
    visible: false,
    type: 'add'
})

// 表单数据
const photoForm = reactive<any>({
    id: undefined,
    name: '',
    description: '',
    url: '',
    recordTime: '',
    sort: 1,
})

// 表单校验规则
const rules = reactive<FormRules>({
    url: [
        { required: true, message: '请上传封面', trigger: 'blur' }
    ],
    description: [
        { required: true, message: '请输入描述', trigger: 'blur' }
    ],
    recordTime: [
        { required: false, message: '请选择记录的日期', trigger: 'blur' }
    ],
    sort: [
        { required: false, message: '请输入排序', trigger: 'blur' }
    ],

})

watch(() => props.openPhotos, (newVal) => {
    if (newVal) {
        selectedIds.value = []
        getList()
        getAlbumList()
    }
})

// 获取标签列表
const getList = async () => {
    loading.value = true
    try {
        queryParams.albumId = props.albumId
        const { data } = await listPhotoApi(queryParams)
        photoList.value = data.records
        total.value = data.total
    } catch (error) {
    }
    loading.value = false
}

// 全选
const handleAllSelect = () => {
    if(photoList.value.length === selectedIds.value.length){
        selectedIds.value = []
    }else{
        selectedIds.value = photoList.value.map(item => item.id)
    }
}

// 批量移动
const handleBatchMove = () => {
    if (selectedIds.value.length === 0) return
    moveDialog.visible = true
}

// 移动对话框数据
const moveDialog = reactive({
    visible: false,
    targetAlbumId: undefined
})

// 确认移动
const confirmMove = async () => {
    if (!moveDialog.targetAlbumId) {
        ElMessage.warning('请选择目标相册')
        return
    }

    try {
        await movePhotoApi({
            photoIds: selectedIds.value,
            albumId: moveDialog.targetAlbumId
        })
        ElMessage.success('移动成功')
        moveDialog.visible = false
        moveDialog.targetAlbumId = undefined
        selectedIds.value = []
        getList()
    } catch (error) {
        console.error('移动失败:', error)
    }
}

// 取消移动
const cancelMove = () => {
    moveDialog.visible = false
    moveDialog.targetAlbumId = undefined
}

// 获取所有相册
const getAlbumList = async () => {
    const { data } = await listAlbumAllApi()
    albumList.value = data
}

// 批量删除
const handleBatchDelete = () => {
    if (selectedIds.value.length === 0) return

    ElMessageBox.confirm(`是否确认删除 ${selectedIds.value.length} 个照片?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await deletePhotoApi(selectedIds.value)
            ElMessage.success('批量删除成功')
            getList()
            selectedIds.value = []
        } catch (error) {
        }
    })
}

// 删除
const handleDelete = (row: any) => {
    ElMessageBox.confirm(`是否确认删除 ${row.description} 这个照片?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            await deletePhotoApi(row.id)
            ElMessage.success('删除成功')
            getList()
        } catch (error) {
        }
    })
}


// 新增相册
const handleAdd = () => {
    dialog.type = 'add'
    dialog.title = '新增照片'
    dialog.visible = true
    photoForm.id = undefined
    photoForm.albumId = props.albumId
    photoForm.name = ''
    photoForm.description = ''
    photoForm.url = ''
    photoForm.recordTime = ''
    photoForm.sort = 1
}

// 修改说说
const handleUpdate = (row: any) => {
    dialog.type = 'edit'
    dialog.title = '修改照片'
    dialog.visible = true
    Object.assign(photoForm, row)
    photoForm.createTime = undefined
}

// 关闭预览
const closeViewer = () => {
    openPreview.value = false
    previewList.value = []
}

// 查看照片
const handlePreviewPhotos = (item: any) => {
    previewList.value = [item.url]
    openPreview.value = true
}

// 提交表单
const submitForm = async () => {
    if (!photoFormRef.value) return

    await photoFormRef.value.validate(async (valid) => {
        if (valid) {
            submitLoading.value = true
            try {
                if (dialog.type === 'add') {
                    await addPhotoApi(photoForm)
                    ElMessage.success('新增成功')
                } else {
                    await updatePhotoApi(photoForm)
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
    photoFormRef.value?.resetFields()
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

// 处理弹框关闭
const handleDialogClose = (val: boolean) => {
    emit('update:openPhotos', val)
}
</script>

<style scoped lang="scss">

.photo-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    padding: 10px;

    .photo-card {
        border: 1px solid #e6e6e6;
        border-radius: 12px;
        position: relative;
        overflow: hidden;
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        
        &:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
            
            .photo-overlay {
                opacity: 1;
                
                .photo-content {
                    transform: translateY(0);
                    opacity: 1;
                }
                
                .photo-actions {
                    transform: translateY(0);
                    opacity: 1;
                }
            }
        }

        .photo-select {
            position: absolute;
            top: 12px;
            right: 12px;
            z-index: 2;
        }

        .photo-image {
            border-radius: 12px;
            width: 300px;
            height: 200px;
            object-fit: cover;
            display: block;
        }

        .photo-overlay {
            position: absolute;
            inset: 0;
            background: linear-gradient(to bottom, 
                rgba(0, 0, 0, 0.2) 0%,
                rgba(0, 0, 0, 0.7) 100%);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            opacity: 0;
            transition: opacity 0.4s ease;
            border-radius: 12px;
        }

        .photo-content {
            padding: 20px 15px;
            color: #fff;
            transform: translateY(-20px);
            opacity: 0;
            transition: all 0.4s ease;

            .photo-time {
                font-size: 14px;
                margin-bottom: 12px;
                display: flex;
                align-items: center;
                gap: 5px;
                opacity: 0.9;
                
                .time-text {
                    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
                }
            }

            .photo-desc {
                font-size: 14px;
                line-height: 1.5;
                opacity: 0.95;
                display: -webkit-box;
                -webkit-line-clamp: 3;
                -webkit-box-orient: vertical;
                overflow: hidden;
                text-overflow: ellipsis;
                text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
            }
        }

        .photo-actions {
            display: flex;
            justify-content: center;
            gap: 15px;
            padding: 15px;
            background: rgba(0, 0, 0, 0.6);
            backdrop-filter: blur(4px);
            transform: translateY(20px);
            opacity: 0;
            transition: all 0.4s ease;

            :deep(.el-button) {
                font-weight: 500;
                transition: all 0.3s ease;
                
                &:hover {
                    transform: scale(1.05);
                    text-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
                }
            }
        }
    }
}
</style>
