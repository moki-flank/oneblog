<template>
  <div class="friends-page">
    <el-card class="content-card">
      <div class="page-header">
        <h1>友情链接</h1>
        <div class="header-divider">
          <span class="line"></span>
          <i class="fas fa-link"></i>
          <span class="line"></span>
        </div>
        <p>与优秀的人同行，分享技术与生活</p>
        <div class="site-info">
          <div class="site-avatar">
            <div class="avatar-wrapper">
              <el-avatar class="avatar" :src="$store.state.webSiteInfo.logo" />
              <div class="copy-overlay" @click="copyLogoUrl">
                <i class="fas fa-copy"></i>
                <span>复制图片链接</span>
                <input type="text" :value="$store.state.webSiteInfo.logo" readonly ref="logoInput" style="position: absolute; opacity: 0;">
              </div>
            </div>
          </div>
          <div class="site-details">
            <h2>{{ $store.state.webSiteInfo.name }}</h2>
            <p>{{ $store.state.webSiteInfo.summary }}</p>
            <div class="site-url">
              <i class="fas fa-link"></i>
              <input type="text" :value="$store.state.webSiteInfo.webUrl" readonly ref="urlInput" @click="copyUrl">
              <button class="copy-btn" @click="copyUrl">
                <i class="fas fa-copy"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="copy-tip">
          <i class="fas fa-info-circle"></i>
          申请友链前请先添加本站链接
          <span class="tip-highlight">「 点击上方链接可快速复制 」</span>
        </div>
        <button class="apply-btn" @click="handleApply">
          <i class="fas fa-plus"></i>
          申请友链
        </button>
      </div>

      <div class="friends-container">
        <div class="section-title">
          <h2>友链列表</h2>
          <span class="count">{{ friends.length }} 个伙伴</span>
        </div>
        <div class="friends-grid">
          <div v-for="friend in friends" :key="friend.id" class="friend-card" @click="visitFriend(friend.url)">
            <div class="friend-avatar">
              <img v-lazy="friend.avatar" :key="friend.avatar" :alt="friend.name">
              <div class="status" :class="{ online: friend.online }"></div>
            </div>
            <div class="friend-info">
              <h3>{{ friend.name }}</h3>
              <p>{{ friend.info }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 申请表单弹窗 -->
      <el-dialog 
        :visible.sync="showApplyForm" 
        title="申请友链" 
        width="500px" 
        top="3vh"
        :append-to-body="true"
      >
        <div class="apply-form">
          <el-form 
            size="small" 
            :model="form" 
            :rules="rules" 
            ref="ruleForm" 
            label-width="100px"
          >
          <div class="form-group">
              <el-form-item prop="name">
                <template v-slot:label>
                  <i class="fas fa-signature"></i> 网站名称
                </template>
              </el-form-item>
              <el-input type="text" v-model="form.name" placeholder="请输入您的网站名称" />
            </div>
            <div class="form-group">
              <el-form-item prop="url">
                <template v-slot:label>
                  <i class="fas fa-link"></i> 网站链接
                </template>
              </el-form-item>
              <el-input type="url" v-model="form.url" placeholder="请输入您的网站链接" />
            </div>
            <div class="form-group">
              <el-form-item prop="info">
                <template v-slot:label>
                  <i class="fas fa-quote-left"></i> 网站描述
                </template>
              </el-form-item>
              <el-input type="url" v-model="form.info" placeholder="一句话描述您的网站" />
            </div>
            <div class="form-group">
              <el-form-item prop="avatar">
                <template v-slot:label>
                  <i class="fas fa-image"></i> 头像链接
                </template>
              </el-form-item>
              <el-input type="url" v-model="form.avatar" placeholder="请输入您的头像链接" />
            </div>
            <div class="form-group">
              <el-form-item prop="email">
                <template v-slot:label>
                  <i class="fas fa-envelope"></i> 联系邮箱
                </template>
              </el-form-item>
              <el-input type="url" v-model="form.email" placeholder="邮箱用于联系" />
            </div>


            <div class="form-footer">
              <el-button class="submit-btn" type="primary" @click="submitApplication">
                <i class="fas fa-paper-plane"></i>
                提交申请
              </el-button>
            </div>
          </el-form>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getFriendsApi, applyFriendApi } from '@/api/friends'

export default {
  name: 'Friends',

  data() {
    return {
      showApplyForm: false,
      form: {
        name: '',
        url: '',
        info: '',
        avatar: '',
        email: ''
      },
      friends: [],
      rules: {
        name: [
          { required: true, message: '请输入网站名称', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入网站链接', trigger: 'blur' },
          { type: 'url', message: '请输入正确的链接格式', trigger: 'blur' }
        ],
        info: [
          { required: true, message: '请输入网站描述', trigger: 'blur' }
        ],
        avatar: [
          { required: true, message: '请输入头像链接', trigger: 'blur' },
          { type: 'url', message: '请输入正确的链接格式', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入联系邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      }
    }
  },
  async created() {
    const res = await getFriendsApi()
    this.friends = res.data
  },
  methods: {
    visitFriend(url) {
      window.open(url, '_blank')
    },
    handleApply() {
      this.form = {
        name: '',
        url: '',
        info: '',
        avatar: '',
        email: ''
      },
        this.showApplyForm = true
    },
    handleClose() {
      this.showApplyForm = false
    },
    submitApplication() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          try {
            const res = applyFriendApi(this.form)
            this.showApplyForm = false
            this.$message.success('申请已提交，请等待审核')
            this.$refs['ruleForm'].resetFields();
          } catch (error) {
            this.$message.error(error.message)
          }
        } else {
          console.log('error submit!!')
        }
      })

    },
    copyLogoUrl() {
      const input = this.$refs.logoInput
      input.select()
      document.execCommand('copy')
      this.$message.success('Logo链接已复制到剪贴板')
    },
    copyUrl() {
      const input = this.$refs.urlInput
      input.select()
      document.execCommand('copy')
      this.$message.success('链接已复制到剪贴板')
    }
  }
}
</script>

<style lang="scss" scoped>

:deep(.el-form-item__content){
  margin-left: 0 !important;
}
.friends-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: $spacing-lg;
  min-height: calc(100vh - 200px);
  animation: fadeIn 0.8s ease-out;
}

.content-card {
  padding: $spacing-sm;

}

.page-header {
  text-align: center;
  margin-bottom: $spacing-xl;
  position: relative;

  h1 {
    font-size: 2em;
    margin-bottom: $spacing-md;
    font-weight: 800;
    background: linear-gradient(135deg, $primary, $secondary);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: 2px;
    text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.1);
  }

  .header-divider {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-sm;
    margin-bottom: $spacing-md;
    opacity: 0.8;

    .line {
      width: 60px;
      height: 2px;
      background: linear-gradient(90deg,
          transparent,
          var(--text-secondary),
          transparent);
    }

    i {
      color: $primary;
      font-size: 1.4em;
      transform: rotate(45deg);
      animation: pulse 2s infinite;
    }
  }

  p {
    color: var(--text-secondary);
    font-size: 1.2em;
    margin-bottom: $spacing-lg;
    font-weight: 300;
    letter-spacing: 0.5px;
  }
}

.friends-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: $spacing-md;
  padding: $spacing-md;
}

.friend-card {
  @include card;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  padding: $spacing-lg;
  display: flex;
  align-items: center;
  gap: $spacing-md;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 4px;
    background: linear-gradient(90deg, $primary, $secondary);
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &:hover {
    transform: translateY(-5px) scale(1.02);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    border-color: $primary;

    &::before {
      opacity: 1;
    }

    .friend-avatar img {
      transform: scale(1.1) rotate(5deg);
    }
  }

  .friend-avatar {
    position: relative;
    width: 60px;
    height: 60px;
    flex-shrink: 0;
    border-radius: 50%;
    padding: 3px;

    img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      object-fit: cover;
      transition: transform 0.5s ease;
      border: 3px solid var(--card-bg);
    }

    .status {
      position: absolute;
      bottom: 5px;
      right: 5px;
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: #9ca3af;
      border: 2px solid var(--card-bg);
      box-shadow: 0 0 0 2px var(--card-bg);

      &.online {
        background: #10b981;
        animation: pulse 2s infinite;
      }
    }
  }

  .friend-info {
    flex: 1;
    min-width: 0;

    h3 {
      color: var(--text-primary);
      font-size: 1.2em;
      margin-bottom: $spacing-xs;
      font-weight: 600;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    p {
      color: var(--text-secondary);
      font-size: 0.95em;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}

.site-info {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: $border-radius-lg;
  padding: $spacing-lg;
  margin: $spacing-lg auto;
  max-width: 700px;
  display: flex;
  align-items: center;
  gap: $spacing-lg;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);

    .site-avatar .avatar {
      transform: scale(1.05);
    }
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, $primary, $secondary);
  }

  .site-avatar {
    width: 100px;
    height: 100px;
    flex-shrink: 0;
    position: relative;

    .avatar-wrapper {
      width: 100%;
      height: 100%;
      position: relative;
      cursor: pointer;
      border-radius: 50%;
      overflow: hidden;

      .avatar {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid var(--border-color);
        background: var(--card-bg);
        transition: transform 0.5s ease;
      }

      .copy-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.6);
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s ease;
        color: white;
        border-radius: 50%;

        i {
          font-size: 1.5em;
          margin-bottom: 5px;
        }

        span {
          font-size: 0.8em;
        }
      }

      &:hover {
        .copy-overlay {
          opacity: 1;
        }

        .avatar {
          transform: scale(1.05);
        }
      }
    }
  }

  .site-details {
    flex: 1;
    min-width: 0;

    h2 {
      color: var(--text-primary);
      font-size: 1.5em;
      margin-bottom: $spacing-xs;
      font-weight: 700;
      background: linear-gradient(135deg, $primary, $secondary);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    p {
      color: var(--text-secondary);
      margin-bottom: $spacing-sm;
      font-size: 1em;
      line-height: 1.5;
    }

    .site-url {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      padding: $spacing-sm $spacing-md;
      background: var(--input-bg);
      border-radius: $border-radius-lg;
      border: 1px solid var(--border-color);
      transition: all 0.3s ease;

      &:hover {
        border-color: $primary;
        box-shadow: 0 0 0 2px rgba($primary, 0.1);
      }

      i {
        font-size: 1em;
        color: $primary;
      }

      input {
        background: none;
        border: none;
        color: var(--text-primary);
        font-size: 1em;
        flex: 1;
        min-width: 0;
        cursor: pointer;
        padding: $spacing-xs;

        &:focus {
          outline: none;
        }
      }

      .copy-btn {
        background: none;
        border: none;
        color: var(--text-secondary);
        cursor: pointer;
        padding: $spacing-xs $spacing-sm;
        transition: all 0.3s ease;
        border-radius: $border-radius-sm;

        &:hover {
          color: $primary;
          background: rgba($primary, 0.1);
        }
      }
    }
  }
}

.copy-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  color: var(--text-secondary);
  margin: $spacing-lg 0;
  font-size: 1em;
  padding: $spacing-sm;
  background: rgba($primary, 0.05);
  border-radius: $border-radius-lg;

  i {
    color: $primary;
    animation: bounce 2s infinite;
  }

  .tip-highlight {
    color: $primary;
    margin-left: $spacing-sm;
    font-weight: 500;
  }
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-sm;
  border-bottom: 2px solid var(--border-color);
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 100px;
    height: 2px;
    background: linear-gradient(90deg, $primary, $secondary);
  }

  h2 {
    color: var(--text-primary);
    font-size: 1.8em;
    font-weight: 700;
  }

  .count {
    color: var(--text-secondary);
    font-size: 1.2em;
    padding: $spacing-xs $spacing-sm;
    background: rgba($primary, 0.1);
    border-radius: $border-radius-lg;
    font-weight: 500;
  }
}

.apply-btn {
  padding: $spacing-sm $spacing-lg;
  background: linear-gradient(135deg, $primary, $secondary);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 1em;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: $spacing-sm;
  box-shadow: 0 4px 15px rgba($primary, 0.3);

  i {
    font-size: 1.1em;
    transition: transform 0.3s ease;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba($primary, 0.4);

    i {
      transform: rotate(180deg);
    }
  }

  &:active {
    transform: translateY(1px);
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba($primary, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba($primary, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba($primary, 0);
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-5px);
  }
  60% {
    transform: translateY(-3px);
  }
}

.apply-form {
  .el-form-item {
    margin-bottom: $spacing-md;
    opacity: 1;
    transform: none;

    &__label {
      font-weight: 500;
      color: var(--text-primary);
    }
  }

  .el-input {
    .el-input__inner {
      border-radius: $border-radius-md;
      border: 1px solid var(--border-color);
      background: var(--input-bg);
      color: var(--text-primary);
      
      &:focus {
        border-color: $primary;
        box-shadow: 0 0 0 2px rgba($primary, 0.1);
      }

      &::placeholder {
        color: var(--text-secondary);
      }
    }
  }

  .form-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: $spacing-lg;

    .submit-btn {
      background: linear-gradient(135deg, $primary, $secondary);
      border: none;
      padding: $spacing-sm $spacing-xl;
      font-size: 1em;
      border-radius: 25px;
      
      i {
        margin-right: $spacing-xs;
      }

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba($primary, 0.3);
      }

      &:active {
        transform: translateY(1px);
      }
    }
  }
}

.form-group {
  margin-bottom: $spacing-md;
}

@for $i from 1 through 5 {
  .form-group:nth-child(#{$i}) {
    animation: none;
  }
}

</style>
