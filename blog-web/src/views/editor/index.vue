<template>
    <div class="editor-container">

        <div class="editor-main">
            <el-form :model="articleForm" :rules="rules" ref="articleForm" label-position="top" size="small">
                <div class="editor-content">
                    <div class="content-card">
                        <div class="title-cover-layout">
                            <div class="title-section">
                                <el-form-item label="文章标题" prop="title">
                                    <el-input v-model="articleForm.title" placeholder="请输入文章标题..." />
                                </el-form-item>
                                <el-form-item label="文章描述" prop="summary">
                                    <el-input v-model="articleForm.summary" type="textarea" :rows="4"
                                        placeholder="请输入文章描述..." />
                                </el-form-item>
                            </div>
                            <div class="cover-section">
                                <el-form-item label="封面图片" prop="cover">
                                    <div class="cover-area" @click="triggerCoverUpload">
                                        <div v-if="!articleForm.cover" class="cover-placeholder">
                                            <i class="fas fa-image"></i>
                                            <span>点击上传封面图</span>
                                        </div>
                                        <div v-else class="cover-preview">
                                            <img :src="articleForm.cover" alt="文章封面">
                                            <div class="cover-actions">
                                                <el-button size="small" circle type="danger" @click.stop="removeCover">
                                                    <i class="fas fa-trash"></i>
                                                </el-button>
                                            </div>
                                        </div>
                                    </div>
                                    <input ref="coverInput" type="file" accept="image/*" @change="handleCoverUpload"
                                        style="display: none">
                                </el-form-item>
                            </div>
                        </div>
                    </div>

                    <div class="content-card flex-card">
                        <el-form-item label="文章内容" prop="contentMd" class="mb-20">
                            <mavon-editor
                                placeholder="输入文章内容..."
                                style="height: 500px; width: 100%"
                                ref="mdRef"
                                v-model="articleForm.contentMd"
                                @imgDel="imgDel"
                                @imgAdd="imgAdd"
                                previewBackground="var(--card-bg)"
                                toolbarsBackground="var(--card-bg)"
                                toolbarsBackgroundImage="var(--card-bg)"
                            />
                        </el-form-item>
                    </div>
                </div>

                <div class="editor-sidebar">
                    <div class="sidebar-section">
                        <h3 class="section-title">
                            <i class="fas fa-folder"></i>
                            文章分类
                        </h3>
                        <el-form-item prop="categoryId">
                            <el-select v-model="articleForm.categoryId" placeholder="请选择分类">
                                <el-option v-for="item in categories" :key="item.id" :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </div>

                    <div class="sidebar-section">
                        <h3 class="section-title">
                            <i class="fas fa-tags"></i>
                            文章标签
                        </h3>
                        <el-form-item prop="tagIds">
                            <el-select v-model="articleForm.tagIds" multiple filterable allow-create default-first-option
                                placeholder="请选择标签">
                                <el-option v-for="item in tags" :key="item.id" :label="item.name" :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </div>

                    <div class="sidebar-section">
                        <h3 class="section-title">
                            <i class="fas fa-cog"></i>
                            文章设置
                        </h3>
                        <div class="setting-item">
                            <el-switch v-model="articleForm.isOriginal" :active-value="1" :inactive-value="0" active-text="原创文章">
                            </el-switch>
                        </div>
                        <div class="setting-item" v-if="!articleForm.isOriginal">
                            <div class="setting-label">转载地址：</div>
                            <el-form-item prop="originalUrl">
                                <el-input v-model="articleForm.originalUrl" placeholder="请输入原文地址" size="small" />
                            </el-form-item>
                        </div>
                        <div class="setting-item">
                            <div class="setting-label">关键词：</div>
                            <el-form-item prop="keywords">
                                <el-input v-model="articleForm.keywords" placeholder="请输入关键词（多个用逗号隔开）" size="small" />
                            </el-form-item>
                        </div>
                    </div>
                    <div class="sidebar-section">
                        <div class="setting-item">
                            <el-button size="small" @click="saveDraft">
                                <i class="fas fa-save"></i>
                                保存草稿
                            </el-button>
                            <el-button size="small" type="primary" @click="publishArticle">
                                <i class="fas fa-paper-plane"></i>
                                提交审核
                            </el-button>
                        </div>
                    </div>

                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
import { uploadFileApi, deleteFileApi } from '@/api/file'
import { createArticleApi, updateArticleApi, getArticleInfoApi } from '@/api/article'
import { getTagsApi,getCategoriesApi } from '@/api/tags'
import { getDictDataApi } from '@/api/dict'

export default {
    name: 'Editor',
    data() {
        return {
            articleForm: {
                title: '',
                summary: '',
                content: '',
                contentMd: '',
                cover: '',
                keywords: '',
                isOriginal: 1,
                originalUrl: '',
                categoryId: '',
                tagIds: [],
                status: ''
            },
            rules: {
                title: [
                    { required: true, message: '请输入文章标题', trigger: 'blur' },
                    { min: 5, max: 100, message: '标题长度应在5-100个字符之间', trigger: 'blur' }
                ],
                summary: [
                    { required: true, message: '请输入文章描述', trigger: 'blur' },
                    { min: 10, max: 500, message: '描述长度应在10-500个字符之间', trigger: 'blur' }
                ],
                contentMd: [
                    { required: true, message: '请输入文章内容', trigger: 'blur' },
                    { 
                        validator: (rule, value, callback) => {
                            if (!value) {
                                callback();
                                return;
                            }
                            
                            // 检查最小长度
                            if (value.length < 50) {
                                callback(new Error('文章内容至少需要50个字符'));
                                return;
                            }

                            // 检查重复字符
                            const repeatedCharPattern = /(.)\1{4,}/;

                            // 检查内容是否有意义
                            const meaninglessPattern = /^[a-zA-Z]{10,}$/;
                            if (meaninglessPattern.test(value)) {
                                callback(new Error('文章内容似乎没有实际意义，请认真编写'));
                                return;
                            }


                            callback();
                        }, 
                        trigger: ['blur', 'change'] 
                    }
                ],
                cover: [
                    { required: true, message: '请上传封面图片', trigger: 'change' }
                ],
                categoryId: [
                    { required: true, message: '请选择文章分类', trigger: 'change' }
                ],
                tagIds: [
                    { required: true, message: '请选择文章标签', trigger: 'change' },
                    { 
                        validator: (rule, value, callback) => {
                            if (value.length > 3) {
                                callback(new Error('最多只能选择3个标签'));
                            } else {
                                callback();
                            }
                        }, 
                        trigger: 'change' 
                    }
                ],
                originalUrl: [
                    { required: true, message: '请输入原文地址', trigger: 'blur' },
                    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
                ],
                keywords: [
                    { 
                        validator: (rule, value, callback) => {
                            if (value && value.split(',').length > 5) {
                                callback(new Error('关键词最多不超过5个'));
                            } else {
                                callback();
                            }
                        }, 
                        trigger: 'blur' 
                    }
                ]
            },
            categories: [],
            tags: [],
            isSubmitting: false,
            statusList: []
        }
    },
    created() {
        getCategoriesApi().then(res => {
            this.categories = res.data
        })
        getTagsApi().then(res => {
            this.tags = res.data
        })
        getDictDataApi(['article_status']).then(res => {
            this.statusList = res.data.article_status.list
        })
        this.articleForm.id = this.$route.query.id
        if (this.articleForm.id) {
            getArticleInfoApi(this.articleForm.id).then(res => {
                this.articleForm = res.data
            })
        }
    },
    methods: {
        /**
         * 保存草稿
         */
        async saveDraft() {
            const status = this.statusList.filter(item => {
                if (item.label === '草稿') {
                    return item.value
                }
            })
            this.articleForm.status = status[0].value
            await this.submitArticle()
        },
        /**
         * 发布文章
         */
        async publishArticle() {
            const status = this.statusList.filter(item => {
                if (item.label === '审核') {
                    return item
                }
            })
            this.articleForm.status = status[0].value
            await this.submitArticle()
        },
        /**
         * 提交文章
         */
        submitArticle() {
            if (this.isSubmitting) return
            this.$refs.articleForm.validate(valid => {
                if (!valid) return
                this.isSubmitting = true
                this.articleForm.content = this.$refs.mdRef.d_render;
                const api = this.articleForm.id ? updateArticleApi : createArticleApi
                api(this.articleForm).then(res => {
                    this.$message.success('保存成功')
                    this.$router.push('/user/profile')
                }).catch(err => {
                    this.$message.error(err.message)
                }).finally(() => {
                    this.isSubmitting = false
                })
            })
        },
        /**
         * 删除图片
         */
        imgDel(pos, $file) {
            console.log(pos)
            deleteFileApi(pos[0]).then(res => {
                this.$refs.mdRef.$img2Url(pos, '');
            });
        },
        /**
         * 添加图片
         */
        imgAdd(pos, $file) {
            const formdata = new FormData();
            formdata.append("file", $file);
            uploadFileApi(formdata,'article-content').then(res => {
                this.$refs.mdRef.$img2Url(pos, res.data);
            });
        },
        /**
         * 触发封面上传
         */
        triggerCoverUpload() {
            this.$refs.coverInput.click()
        },
        /**
         * 封面上传
         */
        handleCoverUpload(e) {
            const file = e.target.files[0]
            if (!file) return

            const formData = new FormData()
            formData.append('file', file)

            uploadFileApi(formData,'article-cover').then(res => {
                if (res.code === 200) {
                    this.articleForm.cover = res.data
                    this.$message.success('上传成功')
                } else {
                    this.$message.error(res.message || '上传失败')
                }
            }).catch(error => {
                this.$message.error('上传失败，请重试')
            })

            // 清空 input 的值，这样可以重复上传同一个文件
            e.target.value = ''
        },
        /**
         * 删除封面
         */
        removeCover() {
            this.articleForm.cover = ''
        }
    }
}
</script>

<style lang="scss" scoped>
.editor-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
}


.editor-main {
    flex: 1;
    max-width: 1400px;
    margin: 0 auto;
    padding: 24px 40px;
    width: 100%;

    .el-form {
        display: flex;
        gap: 24px;
    }
}

.editor-content {
    flex: 1;
    min-width: 0;

    .content-card {
        background: var(--card-bg);
        border-radius: 8px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
        padding: 24px;
        margin-bottom: 24px;

        &:last-child {
            margin-bottom: 0;
        }

        &.flex-card {
            flex: 1;
            display: flex;
            flex-direction: column;

            :deep(.el-form-item) {
                flex: 1;
                display: flex;
                flex-direction: column;
                margin-bottom: 0;

                .el-form-item__content {
                    flex: 1;
                    display: flex;
                    flex-direction: column;

                    .el-textarea {
                        flex: 1;
                        display: flex;

                        textarea {
                            flex: 1;
                            resize: none;
                        }
                    }
                }
            }
        }
    }

    :deep(.el-form-item) {
        margin-bottom: 0;

        .el-form-item__label {
            padding-bottom: 12px;
            font-size: 13px;
            color: #999;
            font-weight: normal;
        }
    }

    .title-cover-layout {
        display: flex;
        gap: 24px;
        align-items: flex-start;

        .title-section {
            flex: 1;
        }

        .cover-section {
            width: 240px;
            flex-shrink: 0;
        }
    }

    .cover-area {
        width: 100%;
        height: 135px;
        border-radius: 4px;
        overflow: hidden;
        cursor: pointer;
        position: relative;
        background: var(--card-bg);
        border: 1px dashed var(--border-color);
        transition: all 0.15s;

        &:hover {
            border-color: #409EFF;
        }
    }

    .cover-placeholder {
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 8px;
        color: #999;

        i {
            font-size: 24px;
        }

        span {
            font-size: 13px;
        }
    }

    .cover-preview {
        height: 100%;

        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .cover-actions {
            position: absolute;
            top: 12px;
            right: 12px;
            opacity: 0;
            transition: all 0.15s;
        }

        &:hover .cover-actions {
            opacity: 1;
        }
    }
}

.editor-sidebar {
    width: 320px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    gap: 24px;

    .sidebar-section {
        background: var(--card-bg);
        border-radius: 8px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
        padding: 24px;

        .section-title {
            font-size: 13px;
            font-weight: 500;
            color: #999;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            gap: 6px;

            i {
                font-size: 14px;
                color: #409EFF;
            }
        }

        .setting-item {
            padding: 12px 0;
            border-bottom: 1px solid var(--border-color);

            &:last-child {
                border-bottom: none;
                padding-bottom: 0;
            }

            &:first-child {
                padding-top: 0;
            }

            .setting-label {
                font-size: 13px;
                color: #999;
                margin-bottom: 8px;
            }

            :deep(.el-form-item) {
                margin-bottom: 0;
            }
        }
    }
}

@media screen and (max-width: 1200px) {


    .editor-main {
        padding: 24px;

        .el-form {
            flex-direction: column;
        }
    }

    .editor-sidebar {
        width: 100%;
    }
}

@media screen and (max-width: 768px) {


    .editor-main {
        padding: 16px;

        .el-form {
            gap: 16px;
        }
    }

    .editor-content {
        .content-card {
            padding: 20px;
            margin-bottom: 16px;
        }

        .cover-area {
            height: 120px;
        }
    }

    .editor-sidebar {
        gap: 16px;

        .sidebar-section {
            padding: 20px;
        }
    }

    .title-cover-layout {
        flex-direction: column;
        gap: 16px;

        .cover-section {
            width: 100%;
        }
    }
}

.v-note-wrapper{
    background: var(--card-bg)!important;
    color: var(--text-secondary)!important;
}
:deep(.content-input-wrapper){
    background: var(--card-bg)!important;
    .auto-textarea-input,.no-border,.no-resize{
        color: var(--text-secondary)!important;
        background: var(--card-bg)!important;
    }
}

</style>