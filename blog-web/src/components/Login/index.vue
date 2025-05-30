<template>
  <el-dialog width="400px" :visible.sync="$store.state.loginVisible" @close="handleClose">

    <template slot="title">
      <div class="login-header">
        <h3>{{ formTitles[currentForm] }}</h3>
        <p class="subtitle">{{ formSubtitles[currentForm] }}</p>
      </div>
    </template>
    <div>
      <!-- 登录表单 -->
      <div class="login-body">
        <div v-show="currentForm === 'login'" class="form-container">
          <el-form :model="loginForm" :rules="rules" ref="ruleFrom">
            <el-form-item class="form-item" prop="username">
              <el-input prefix-icon="el-icon-user-solid" v-model="loginForm.username" placeholder="请输入用户名"
              @keyup.enter.native="handleLogin"/>
            </el-form-item>

            <el-form-item class="form-item" prop="password">
              <el-input prefix-icon="el-icon-lock" v-model="loginForm.password" placeholder="请输入密码"
              @keyup.enter.native="handleLogin" show-password />
            </el-form-item>

            <div class="form-options">
              <a class="forgot-password" @click="switchForm('forgot')">忘记密码？</a>
            </div>

            <el-form-item class="form-item">
              <el-button class="submit-btn ripple" :loading="loading" @click="handleLogin">
                登 录
              </el-button>
            </el-form-item>
          </el-form>


          <div class="divider">
            <el-divider>其他登录方式</el-divider>
          </div>

          <div class="third-party-login">
            <div v-for="(item, type) in loginTypes" :key="type" class="login-icon-wrapper"
              @click="handleThirdPartyLogin(type)">
              <el-tooltip :content="item.title" placement="top">
                <div :class="['login-icon', type]">
                  <i :class="item.icon" v-if="type === 'github'"></i>
                  <i :class="item.icon" v-if="type === 'qq'"></i>
                  <i :class="item.icon" v-if="type === 'wechat'"></i>
                  <i :class="item.icon" v-if="type === 'gitee'"></i>
                  <i :class="item.icon" v-if="type === 'weibo'"></i>
                </div>
              </el-tooltip>

            </div>

          </div>

          <div class="form-switch">
            还没有账号？<a @click="switchForm('register')">立即注册</a>
          </div>
        </div>

        <!-- 注册表单 -->
        <div v-show="currentForm === 'register'" class="form-container">

          <el-form :model="registerForm" :rules="rules" ref="registerForm">

            <el-form-item lable="昵称" prop="nickname">
              <el-input prefix-icon="el-icon-user-solid" v-model="registerForm.nickname" placeholder="请输入昵称" />
            </el-form-item>

            <el-form-item class="form-item" prop="email">
              <el-input prefix-icon="el-icon-message" v-model="registerForm.email" placeholder="请输入邮箱" />
            </el-form-item>

            <el-form-item class="form-item" prop="code">
              <el-input prefix-icon="el-icon-key" v-model="registerForm.code" placeholder="请输入验证码">
                <template slot="append">
                  <el-button @click="sendRegisterCode" :disabled="codeSending">
                    {{ codeButtonText }}
                  </el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item class="form-item" prop="password">
              <el-input prefix-icon="el-icon-lock" v-model="registerForm.password" placeholder="请输入密码" show-password />
            </el-form-item>

            <el-form-item class="form-item">
              <el-button class="submit-btn" :loading="loading" @click="handleRegister">
                注 册
              </el-button>
            </el-form-item>

            <div class="form-switch">
              已有账号？<a @click="switchForm('login')">立即登录</a>
            </div>
          </el-form>

        </div>

        <!-- 忘记密码表单 -->
        <div v-show="currentForm === 'forgot'" class="form-container">
          <el-form :model="forgotForm" :rules="rules" ref="forgotForm">
            <el-form-item class="form-item" prop="email">
              <el-input prefix-icon="el-icon-message" v-model="forgotForm.email" placeholder="请输入注册邮箱" />
            </el-form-item>

            <el-form-item class="form-item" prop="code">
              <el-input prefix-icon="el-icon-key" v-model="forgotForm.code" placeholder="请输入验证码">
                <template slot="append">
                  <el-button @click="sendVerificationCode" :disabled="codeSending">
                    {{ codeButtonText }}
                  </el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item class="form-item" prop="password">
              <el-input prefix-icon="el-icon-lock" v-model="forgotForm.password" placeholder="请输入新密码" show-password />
            </el-form-item>

            <el-form-item class="form-item">
              <el-button class="submit-btn" :loading="loading" @click="handleResetPassword">
                重置密码
              </el-button>
            </el-form-item>

            <div class="form-switch">
              <a @click="switchForm('login')">返回登录</a>
            </div>
          </el-form>

        </div>
      </div>

      <!-- 微信二维码弹窗 -->
      <div class="qrcode-modal" v-if="wechatForm.showQrcode" @click.self="handleCloseWechat">
        <div class="qrcode-container">
          <div class="qrcode-header">
            <span>微信扫码登录</span>
            <span class="close-qrcode" @click="handleCloseWechat">×</span>
          </div>
          <div class="qrcode-content">
            <div class="qrcode-box">
              <!-- 这里放二维码图片 -->
              <img v-lazy="'https://img.shiyit.com/qrcode.jpg'" :key="'https://img.shiyit.com/qrcode.jpg'" alt="微信二维码">
            </div>
            <p class="qrcode-tip">登录验证码：
              <span class="code-text">{{ wechatForm.code }}</span>
              <span class="code-text" v-if="wechatForm.code === '验证码已失效'">
                <i class="fas fa-sync-alt" @click="getWechatLoginCode"></i>
              </span>
            </p>
            <p class="qrcode-tip">微信扫码关注公众号，并发送验证码</p>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { disableScroll, enableScroll } from '@/utils/scroll'
import {
  sendEmailCodeApi, registerApi, forgotPasswordApi,
  getWechatLoginCodeApi, getWechatIsLoginApi, getAuthRenderApi
} from '@/api/auth'
import { setCookie } from '@/utils/cookie'

export default {
  name: 'Login',
  data() {
    return {
      currentForm: 'login',
      loading: false,
      wechatForm: {
        code: '',
        showQrcode: false,
      },
      countdown: 0,
      formTitles: {
        login: '欢迎回来',
        register: '创建账号',
        forgot: '重置密码'
      },
      formSubtitles: {
        login: '登录您的账号继续访问',
        register: '填写信息创建您的账号',
        forgot: '通过邮箱重置您的密码'
      },
      loginForm: {
        username: '',
        password: '',
        source: 'PC'
      },
      registerForm: {
        nickname: '',
        email: '',
        password: '',
        code: ''
      },
      forgotForm: {
        email: '',
        code: '',
        password: ''
      },
      loginTypes: {
        github: {
          title: 'GitHub账号登录',
          icon: 'fab fa-github'
        },
        qq: {
          title: 'QQ账号登录',
          icon: 'fab fa-qq'
        },
        wechat: {
          title: '微信扫码登录',
          icon: 'fab fa-weixin'
        },
        gitee: {
          title: '码云账号登录',
          icon: 'fab fa-git-alt'
        },
        weibo: {
          title: '微博账号登录',
          icon: 'fab fa-weibo'
        }
      },
      codeSending: false,
      codeButtonText: '发送验证码',
      codeTimer: null,
      pollingTimer: null,
      rules: {
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      }

    }
  },
  watch: {
    '$store.state.loginVisible': {
      handler(newVal) {
        if (newVal) {
          disableScroll()
          Object.keys(this.loginTypes).forEach(key => {
            if (!this.$store.state.webSiteInfo.loginTypeList.includes(key)) {
              delete this.loginTypes[key]
            }
          })
        } else {
          enableScroll()
        }
      },
      immediate: true
    }
  },

  methods: {
    /**
     * 
     * @param form 
     */
    switchForm(form) {
      this.currentForm = form
      this.loading = false
    },
    /**
     * 关闭登录弹窗
     */
    handleClose() {
      this.$store.commit('SET_LOGIN_VISIBLE', false)
      this.resetForm()
      this.clearTimer()
    },
    /**
     * 重置表单
     */
    resetForm() {
      this.currentForm = 'login'
      this.wechatForm.showQrcode = false
      this.wechatForm.code = ''
      this.loginForm = { nickname: '', password: '', source: 'PC' }
      this.registerForm = { nickname: '', email: '', password: '', code: '' }
      this.forgotForm = { email: '', code: '', password: '' }
    },
    /**
     * 登录
     */
    async handleLogin() {
      this.$refs['ruleFrom'].validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            await this.$store.dispatch('loginAction', this.loginForm)
            this.$message.success('登录成功')
            this.handleClose()
          } catch (error) {
            this.$message.error(error.message || '登录失败，请重试')
          } finally {
            this.loading = false
          }
        } else {
          console.log('error submit!!')
          return false;
        }
      })
    },
    /**
     * 注册
     */
    async handleRegister() {
      this.$refs['registerForm'].validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            await registerApi(this.registerForm)
            this.$message.success('注册成功')
            this.switchForm('login')
          } catch (error) {
            this.$message.error(error.message || '注册失败，请重试')
          } finally {
            this.loading = false
          }
        } else {
          console.log('error submit!!')
          return false;
        }
      })
    },
    /**
     * 忘记密码
     */
    async handleResetPassword() {
      this.$refs['forgotForm'].validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            // 调用重置密码接口
            await forgotPasswordApi(this.forgotForm)
            this.$message.success('密码重置成功')
            this.switchForm('login')
          } catch (error) {
            this.$message.error(error.message || '重置失败，请重试')
          } finally {
            this.loading = false
          }
        } else {
          console.log('error submit!!')
          return false;
        }
      })

    },
    /**
     * 发送忘记密码邮箱验证码
     */
    async sendVerificationCode() {
      if (this.codeSending) return

      if (!this.forgotForm.email) {
        this.$message.error('请先输入邮箱')
        return
      }

      this.codeSending = true
      this.sendEmailCode(this.forgotForm.email)

    },

    /**
     * 第三方登录
     */
    handleThirdPartyLogin(type) {
      if (type === 'wechat') {
        this.wechatForm.showQrcode = true
        this.getWechatLoginCode()
        return
      }
      getAuthRenderApi(type).then(res => {
        //将当前地址存到cookie中
        setCookie('redirectUrl', window.location.href)
        window.open(res.data, "_self")
      })
    },
    /**
     * 关闭微信二维码弹窗
     */
    handleCloseWechat() {
      this.wechatForm.showQrcode = false
      this.wechatForm.code = ''
      this.clearTimer()
    },

    /**
     * 获取微信登录验证码
     */
    getWechatLoginCode() {
      getWechatLoginCodeApi().then(res => {
        this.wechatForm.code = res.data
        this.pollingWechatIsLogin()
        // 开始倒计时
        let countdown = 60
        this.codeTimer = setInterval(() => {
          countdown--
          if (countdown <= 0) {
            clearInterval(this.codeTimer)
            clearInterval(this.pollingTimer)
            this.wechatForm.code = '验证码已失效'
          }
        }, 1000)
      })
    },
    /**
     * 定时轮询获取微信登录状态
     */
    pollingWechatIsLogin() {
      this.pollingTimer = setInterval(() => {
        getWechatIsLoginApi(this.wechatForm.code).then(res => {
          if (res.code === 200) {
            this.$store.commit('SET_TOKEN', res.data.token)
            this.$store.commit('SET_USER_INFO', res.data)
            clearInterval(this.pollingTimer)
            this.$message.success('登录成功')
            this.handleClose()
          }
        })
      }, 1000)
    },


    /**
     * 发送邮箱验证码
     */
    sendRegisterCode() {
      if (this.codeSending) return

      if (!this.registerForm.email) {
        this.$message.error('请先输入邮箱')
        return
      }
      this.codeSending = true
      this.sendEmailCode(this.registerForm.email)
    },

    /**
     * 发送邮箱验证码
     */
    sendEmailCode(email) {
      sendEmailCodeApi(email).then(res => {
        this.$message.success("发送成功，请前往邮箱查看验证码")
        // 开始倒计时
        let countdown = 60
        this.codeButtonText = `${countdown}秒后重试`

        this.codeTimer = setInterval(() => {
          countdown--
          if (countdown <= 0) {
            clearInterval(this.codeTimer)
            this.codeSending = false
            this.codeButtonText = '发送验证码'
          } else {
            this.codeButtonText = `${countdown}秒后重试`
          }
        }, 1000)
      }).catch(err => {
        this.$message.error(err.message || "发送失败")
        this.codeSending = false
      })
    },

    /**
     * 清理定时器
     */
    clearTimer() {
      if (this.codeTimer) {
        clearInterval(this.codeTimer)
      }
      if (this.pollingTimer) {
        clearInterval(this.pollingTimer)
      }
    }

  },
  beforeDestroy() {
    this.clearTimer()
  }
}
</script>
<style scoped lang="scss">
:deep(.el-dialog__header) {
  padding: 0 !important;
}

.login-header {
  border-top-left-radius: $border-radius-lg;
  border-top-right-radius: $border-radius-lg;
  text-align: center;
  position: relative;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);

  h3 {
    margin: 0;
    font-size: 24px;
    letter-spacing: -0.5px;
    color: #fff;
    font-weight: 500;
  }
}


.login-body {
  background: var(--card-bg);
}

.welcome-text {
  text-align: center;
  color: #666;
  margin-bottom: 25px;
  font-size: 14px;
}


.third-party-login {
  display: flex;
  justify-content: center;
  gap: 25px;
  margin-top: 25px;
}

.login-icon-wrapper {
  position: relative;
}

.login-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba($text-secondary, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 20px;
}

.login-icon:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.github {
  color: #24292e;
}

.github:hover {
  background: #24292e;
  color: #fff;
}

.qq {
  color: #12B7F5;
}

.qq:hover {
  background: #12B7F5;
  color: #fff;
}

.wechat {
  color: #07C160;
}

.wechat:hover {
  background: #07C160;
  color: #fff;
}

.gitee {
  color: #C71D23;
}

.gitee:hover {
  background: #C71D23;
  color: #fff;
}

.weibo {
  color: #C71D23;
}

.weibo:hover {
  background: #C71D23;
  color: #fff;
}

.login-decoration {
  position: absolute;
  top: 10px;
  left: 20px;
  display: flex;
  gap: 8px;
}

.decoration-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.decoration-circle:nth-child(1) {
  background: #ff5f57;
}

.decoration-circle:nth-child(2) {
  background: #ffbd2e;
}

.decoration-circle:nth-child(3) {
  background: #28c940;
}

.login-header {
  padding: 30px 24px;
  text-align: center;
  position: relative;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
}

.subtitle {
  margin: 8px 0 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}


.form-options {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 20px;
}


.forgot-password {
  font-size: 13px;
  color: #6366f1;
  text-decoration: none;
  transition: color 0.3s;
  cursor: pointer;
}

.forgot-password:hover {
  color: #4f46e5;
}

.qrcode-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
}

.qrcode-container {
  background: #fff;
  width: 300px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.qrcode-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;
}

.close-qrcode {
  cursor: pointer;
  font-size: 20px;
  color: #999;
  transition: color 0.3s;
}

.close-qrcode:hover {
  color: #666;
}

.qrcode-content {
  padding: 20px;
  text-align: center;
}

.qrcode-box {
  width: 200px;
  height: 200px;
  margin: 0 auto;
  background: #f5f5f5;
  border: 1px solid #eee;
  border-radius: 4px;

  img {
    width: 100%;
    height: 100%;
  }
}

.qrcode-tip {
  margin-top: 15px;
  color: #666;
  font-size: 14px;

  i {
    cursor: pointer;
    color: #b71828;
    font-size: 12px;
  }
}

.code-text {
  color: #6366f1;
  font-weight: 600;
  margin-right: 5px;
}


.code-btn {
  padding: 0 15px;
  height: 42px;
  border: none;
  border-radius: 8px;
  background: #6366f1;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.code-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.form-switch {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #666;
}

.form-switch a {
  color: #6366f1;
  text-decoration: none;
  cursor: pointer;
  margin-left: 5px;
  transition: color 0.3s;
}

.form-switch a:hover {
  color: #4f46e5;
}

.submit-btn {
  width: 100%;
  color: #fff;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  transition: all 0.3s;

  :hover {
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes shake {

  0%,
  100% {
    transform: translateX(0);
  }

  25% {
    transform: translateX(-5px);
  }

  75% {
    transform: translateX(5px);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 优化按钮样式 */
.submit-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  outline: none;
  transition: all 0.2s ease;
}

.submit-btn:focus {
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.3);
}


/* 优化输入框动画 */
.input-wrapper {
  position: relative;
  overflow: hidden;
}

/* 优化过渡动画 */
.login-wrapper,
.submit-btn,
.login-icon,
.form-switch a,
.forgot-password {
  transition: all 0.2s ease;
}

/* 其他样式保持不变 */
</style>
