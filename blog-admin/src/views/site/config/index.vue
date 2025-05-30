<template>
  <div class="app-container">
    <el-card>
      <el-tabs v-model="activeTab">
        <!-- 基本信息 Tab -->
        <el-tab-pane name="basic">
          <template #label>
            <el-icon>
              <Setting />
            </el-icon>
            <span class="tab-label">基本信息</span>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="网站Logo" prop="logo">
                  <upload-image v-model="form.logo" :limit="1" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="网站名称" prop="name">
                  <el-input v-model="form.name" placeholder="请输入网站名称" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="网站介绍" prop="summary">
                  <el-input v-model="form.summary" type="textarea" placeholder="请输入网站介绍" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="备案号" prop="recordNum">
                  <el-input v-model="form.recordNum" placeholder="请输入备案号" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="网站地址" prop="webUrl">
                  <el-input v-model="form.webUrl" placeholder="请输入网站地址" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 作者信息 Tab -->
        <el-tab-pane name="author">
          <template #label>
            <el-icon>
              <User />
            </el-icon>
            <span class="tab-label">作者信息</span>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="作者头像" prop="authorAvatar">
                  <upload-image v-model="form.authorAvatar" :limit="1" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="作者名称" prop="author">
                  <el-input v-model="form.author" placeholder="请输入作者名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="个性签名" prop="authorInfo">
                  <el-input v-model="form.authorInfo" placeholder="请输入个性签名" />
                </el-form-item>
              </el-col>
            </el-row>
                <el-form-item label="关于我" prop="aboutMe">
                  <div style="border: 1px solid #ccc;">
                      <Toolbar style="border-bottom: 1px solid #ccc;width: 1200px;" :editor="editorRef" :defaultConfig="toolbarConfig" :mode="mode" />
                      <Editor style=" overflow-y: hidden;width: 1200px" v-model="form.aboutMe" :defaultConfig="editorConfig" :mode="mode"
                      @onCreated="handleCreated"/>
                  </div>
                </el-form-item>

          </el-form>
        </el-tab-pane>

        <!-- 社交信息 Tab -->
        <el-tab-pane name="social">
          <template #label>
            <el-icon>
              <Share />
            </el-icon>
            <span class="tab-label">社交信息</span>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="Github地址" prop="github">
              <el-input v-model="form.github" placeholder="请输入Github地址">
                <template #prefix>
                  <el-icon>
                    <ElementPlus />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="Gitee地址" prop="gitee">
              <el-input v-model="form.gitee" placeholder="请输入Gitee地址">
                <template #prefix>
                  <el-icon>
                    <Platform />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="QQ号" prop="qqNumber">
              <el-input v-model="form.qqNumber" placeholder="请输入QQ号">
                <template #prefix>
                  <el-icon>
                    <ChatDotRound />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="QQ群" prop="qqGroup">
              <el-input v-model="form.qqGroup" placeholder="请输入QQ群">
                <template #prefix>
                  <el-icon>
                    <User />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="微信" prop="wechat">
              <el-input v-model="form.wechat" placeholder="请输入微信号">
                <template #prefix>
                  <el-icon>
                    <ChatLineRound />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱地址">
                <template #prefix>
                  <el-icon>
                    <Message />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 收款信息 Tab -->
        <el-tab-pane name="payment">
          <template #label>
            <el-icon>
              <Money />
            </el-icon>
            <span class="tab-label">收款信息</span>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="支付宝收款码" prop="aliPay">
                  <upload-image v-model="form.aliPay" :limit="1" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="微信收款码" prop="weixinPay">
                  <upload-image v-model="form.weixinPay" :limit="1" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <!-- 网站设置 Tab -->
        <el-tab-pane name="settings">
          <template #label>
            <el-icon>
              <Tools />
            </el-icon>
            <span class="tab-label">网站设置</span>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="游客头像" prop="touristAvatar">
                  <upload-image v-model="form.touristAvatar" :limit="1" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="显示的社交信息" prop="showList">
                  <el-select v-model="showList" multiple placeholder="请选择要显示的社交信息">
                    <el-option label="邮箱" value="email" />
                    <el-option label="QQ" value="qq" />
                    <el-option label="QQ群" value="qqGroup" />
                    <el-option label="Github" value="github" />
                    <el-option label="Gitee" value="gitee" />
                    <el-option label="微信" value="wechat" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="登录方式" prop="loginTypeList">
                  <el-select v-model="loginTypeList" multiple placeholder="请选择登录方式">
                    <el-option v-for="item in loginTypes" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="开启评论">
                  <el-switch v-model="form.openComment" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="开启赞赏">
                  <el-switch v-model="form.openAdmiration" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="开启灯笼">
                  <el-switch v-model="form.openLantern" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
            </el-row>
             <!--<el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="公告" prop="bulletin">
                  <el-input v-model="form.bulletin" type="textarea" :rows="3" placeholder="请输入公告内容" />
                </el-form-item>
              </el-col>
            </el-row>-->
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <!-- 底部按钮 -->
      <div class="bottom-buttons">
        <el-button icon="Refresh" type="primary" v-permission="['sys:web:update']" @click="submitForm">保存配置</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import UploadImage from '@/components/Upload/Image.vue'
import { getWebConfigApi, updateWebConfigApi } from '@/api/site/config'
import { getDictDataByDictTypesApi } from '@/api/system/dict'
import { uploadApi } from '@/api/file'

import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
const editorRef = shallowRef()
const mode = 'default'
const toolbarConfig = {}
const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {
    // 配置上传图片
    uploadImage: {
      customUpload: contentUpload,
    },

    codeSelectLang: {
      // 代码语言
      codeLangs: [
        { text: "CSS", value: "css" },
        { text: "HTML", value: "html" },
        { text: "XML", value: "xml" },
        { text: "Java", value: "java" },
        // 其他
      ],
    },
  },
}


const activeTab = ref('basic')
const formRef = ref<FormInstance>()
const form = ref({
  logo: '',
  name: '',
  summary: '',
  recordNum: '',
  webUrl: '',
  author: '',
  authorInfo: '',
  authorAvatar: '',
  github: '',
  gitee: '',
  qqNumber: '',
  qqGroup: '',
  wechat: '',
  email: '',
  aliPay: '',
  weixinPay: '',
  showList: '',
  loginTypeList: '',
  openComment: 1,
  openAdmiration: 1,
  touristAvatar: '',
  bulletin: '',
  aboutMe: '',
  openLantern: 0
})
const showList = ref([])
const loginTypeList = ref([])
const loginTypes = ref<any>([])

const files = ref();

const rules = {
  name: [{ required: true, message: '请输入网站名称', trigger: 'blur' }],
  logo: [{ required: true, message: '请上传网站Logo', trigger: 'change' }],
  summary: [{ required: true, message: '请输入网站介绍', trigger: 'blur' }],
  recordNum: [{ required: true, message: '请输入备案号', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者名称', trigger: 'blur' }]
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      form.value.showList = JSON.stringify(showList.value)
      form.value.loginTypeList = JSON.stringify(loginTypeList.value)
      updateWebConfigApi(form.value).then(() => {
        ElMessage.success('保存成功')
      })
    }
  })
}
// 获取字典数据
const getDictDataByDictTypes = async () => {
  const res = await getDictDataByDictTypesApi(['login_type'])
  loginTypes.value = res.data.login_type.list
}

const handleCreated = (editor:any) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

//编辑器上传图片
function contentUpload(file: any, insertFn: any) {
  files.value = file;
  // FormData 对象
  var formData = new FormData();
  // 文件对象
  formData.append("file", files.value);
  uploadApi(formData).then((res: any) => {
    insertFn(res.data, "", res.data);
  });
}

onMounted(() => {
  getWebConfigApi().then((res) => {
    form.value = res.data
    if (form.value.showList) {
      showList.value = JSON.parse(form.value.showList)
    }
    if (form.value.loginTypeList) {
      loginTypeList.value = JSON.parse(form.value.loginTypeList)
    }
  })

  getDictDataByDictTypes();
})
</script>

<style scoped>
.app-container {
  padding: 10px;
}

.bottom-buttons {
  margin-top: 20px;
  text-align: center;
}

.tab-label {
  margin-left: 4px;
  vertical-align: middle;
}

:deep(.el-tabs__item) {
  display: flex !important;
  align-items: center;
  justify-content: center;
}

:deep(.el-input-group__prepend) {
  padding: 0 10px;
}

.el-form-item {
  max-width: 600px;
}
</style>
