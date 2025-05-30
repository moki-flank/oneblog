<template>
  <el-dialog
      :visible.sync="visible"
      title="相册密码验证"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
  >
    <div class="password-dialog">
      <div class="dialog-icon">
        <i class="fas fa-lock"></i>
      </div>
      <p class="dialog-tip">这是一个加密相册，请输入密码访问</p>
      <el-form :model="form" ref="form" :rules="rules" @submit.native.prevent="handleSubmit">
        <el-form-item prop="password">
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入相册密码"
          >
            <template slot="prefix">
              <i class="fas fa-key"></i>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <div class="dialog-footer">
        <el-button @click="handleCancel">返回</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确认
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'AlbumPasswordDialog',
  data() {
    return {
      visible: false,
      loading: false,
      form: {
        password: ''
      },
      rules: {
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    show() {
      this.visible = true;
      this.form.password = '';
      if (this.$refs.form) {
        this.$refs.form.clearValidate();
      }
    },
    handleCancel() {
      this.visible = false;
      this.$emit('cancel');
    },
    handleSubmit(event) {
      // 阻止默认行为，防止页面刷新
      event.preventDefault();

      this.$refs.form.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$emit('submit', this.form.password, () => {
            this.loading = false;
            this.visible = false; // 提交后关闭弹窗
          });
        }
      });
    }
  }

}
</script>

<style lang="scss" scoped>
.password-dialog {
  text-align: center;
  padding: 20px 0;

  .dialog-icon {
    font-size: 48px;
    color: #e6a23c;
    margin-bottom: 20px;

    i {
      animation: shake 0.5s ease-in-out;
    }
  }

  .dialog-tip {
    color: var(--text-secondary);
    margin-bottom: 25px;
    font-size: 14px;
  }

  .el-input {
    width: 100%;
  }

  .dialog-footer {
    margin-top: 30px;
    display: flex;
    justify-content: center;
    gap: 15px;
  }
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}
</style>