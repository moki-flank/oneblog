<template>
  <el-dialog
    title="支付"
    :visible.sync="visible"
    width="500px"
    :close-on-click-modal="false"
    custom-class="payment-dialog"
    @close="handleClose"
  >
    <div class="payment-content">
      <!-- 商品信息 -->
      <div class="product-info">
        <h3>{{ title }}</h3>
        <div class="price">￥{{ price }}</div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <div class="method-title">选择支付方式</div>
        <div class="method-list">
          <div
            class="method-item"
            :class="{ active: paymentMethod === 'alipay' }"
            @click="paymentMethod = 'alipay'"
          >
            <i class="fab fa-alipay method-icon"></i>
            <span class="method-name">支付宝</span>
            <i class="fas fa-check-circle check-icon"></i>
          </div>
          <div
            class="method-item"
            :class="{ active: paymentMethod === 'wechat' }"
            @click="paymentMethod = 'wechat'"
          >
            <i class="fab fa-weixin method-icon"></i>
            <span class="method-name">微信支付</span>
            <i class="fas fa-check-circle check-icon"></i>
          </div>
        </div>
      </div>

      <!-- 支付二维码区域 -->
      <div class="qr-section" v-loading="qrLoading">
        <template v-if="showQRCode">
          <div class="qr-wrapper">
            <div class="qr-code">
              <img :src="qrCodeUrl" alt="支付二维码">
            </div>
            <div class="qr-tip">
              <i class="fas" :class="paymentMethod === 'alipay' ? 'fa-alipay' : 'fa-weixin'"></i>
              <span>{{ paymentMethod === 'alipay' ? '请使用支付宝扫码支付' : '请使用微信扫码支付' }}</span>
            </div>
          </div>
          <div class="payment-status">
            <div class="amount">支付金额：<span>￥{{ price }}</span></div>
            <div class="order-no">订单号：{{ orderNo }}</div>
          </div>
        </template>
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <template v-if="!showQRCode">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handlePay" :loading="loading">
          立即支付
        </el-button>
      </template>
      <template v-else>
        <el-button @click="handleClose">取消支付</el-button>
        <el-button type="primary" @click="checkPaymentStatus">
          已完成支付
        </el-button>
      </template>
    </div>
  </el-dialog>
</template>

<script>
import { createOrder, checkOrderStatus } from '@/api/payment'

export default {
  name: 'PaymentDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      required: true
    },
    price: {
      type: Number,
      required: true
    },
    articleId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      paymentMethod: 'alipay',
      loading: false,
      qrLoading: false,
      showQRCode: false,
      qrCodeUrl: this.paymentMethod === 'alipay' ? this.$store.state.webSiteInfo.aliPay : this.$store.state.webSiteInfo.weixinPay,
      orderNo: '',
      checkTimer: null
    }
  },
  methods: {
    async handlePay() {
      this.loading = true
      this.qrLoading = true
      try {
        // 创建订单
        const res = await createOrder({
          articleId: this.articleId,
          paymentMethod: this.paymentMethod,
          amount: this.price
        })
        
        this.orderNo = res.data.orderNo
        this.qrCodeUrl = res.data.qrCodeUrl
        this.showQRCode = true
        
        // 开始轮询支付状态
        this.startCheckingPayment()
      } catch (error) {
        this.$message.error('创建订单失败，请重试')
      } finally {
        this.loading = false
        this.qrLoading = false
      }
    },
    startCheckingPayment() {
      this.checkTimer = setInterval(async () => {
        const status = await this.checkPaymentStatus(false)
        if (status === 'SUCCESS') {
          this.handlePaymentSuccess()
        }
      }, 3000)
    },
    async checkPaymentStatus(showMessage = true) {
      try {
        const res = await checkOrderStatus(this.orderNo)
        if (res.data.status === 'SUCCESS') {
          this.handlePaymentSuccess()
          return 'SUCCESS'
        } else if (showMessage) {
          this.$message.warning('未检测到支付完成，请确认支付状态')
        }
        return res.data.status
      } catch (error) {
        if (showMessage) {
          this.$message.error('检查支付状态失败，请刷新页面重试')
        }
        return 'ERROR'
      }
    },
    handlePaymentSuccess() {
      clearInterval(this.checkTimer)
      this.$message.success('支付成功')
      this.$emit('payment-success')
      this.handleClose()
    },
    handleClose() {
      clearInterval(this.checkTimer)
      this.showQRCode = false
      this.qrCodeUrl = ''
      this.orderNo = ''
      this.$emit('update:visible', false)
    }
  },
  beforeDestroy() {
    clearInterval(this.checkTimer)
  }
}
</script>

<style lang="scss" scoped>
.payment-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.payment-content {
  padding: $spacing-lg;
}

.product-info {
  text-align: center;
  margin-bottom: $spacing-xl;
  padding-bottom: $spacing-lg;
  border-bottom: 1px solid var(--border-color);

  h3 {
    color: var(--text-primary);
    font-size: 1.2em;
    margin-bottom: $spacing-md;
  }

  .price {
    color: #E6162D;
    font-size: 1.8em;
    font-weight: bold;
  }
}

.payment-methods {
  margin-bottom: $spacing-xl;

  .method-title {
    font-size: 1.1em;
    color: var(--text-primary);
    margin-bottom: $spacing-md;
  }

  .method-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: $spacing-md;
  }

  .method-item {
    position: relative;
    padding: $spacing-md;
    border: 2px solid var(--border-color);
    border-radius: $border-radius-lg;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: $spacing-md;
    transition: all 0.3s ease;

    .method-icon {
      font-size: 24px;
      color: var(--text-primary);
      
      &.fa-alipay {
        color: #1677FF;
      }
      
      &.fa-weixin {
        color: #07C160;
      }
    }

    .method-name {
      color: var(--text-primary);
      font-size: 1.1em;
    }

    .check-icon {
      position: absolute;
      right: $spacing-md;
      color: $primary;
      opacity: 0;
      transform: scale(0.8);
      transition: all 0.3s ease;
    }

    &:hover {
      border-color: rgba($primary, 0.3);
      background: rgba($primary, 0.02);
    }

    &.active {
      border-color: $primary;
      background: rgba($primary, 0.05);

      .check-icon {
        opacity: 1;
        transform: scale(1);
      }
    }
  }
}

.qr-section {
  min-height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $spacing-xl;
  padding: $spacing-lg;
  background: var(--hover-bg);
  border-radius: $border-radius-lg;

  .qr-wrapper {
    text-align: center;

    .qr-code {
      width: 200px;
      height: 200px;
      margin: 0 auto $spacing-md;
      padding: $spacing-md;
      background: white;
      border-radius: $border-radius-md;
      box-shadow: $shadow-md;

      img {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }
    }

    .qr-tip {
      color: var(--text-secondary);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: $spacing-sm;

      i {
        font-size: 1.2em;
        
        &.fa-alipay {
          color: #1677FF;
        }
        
        &.fa-weixin {
          color: #07C160;
        }
      }
    }
  }

  .payment-status {
    text-align: center;
    color: var(--text-secondary);
    font-size: 0.9em;

    .amount {
      margin-bottom: $spacing-sm;
      
      span {
        color: #E6162D;
        font-weight: bold;
        font-size: 1.2em;
      }
    }

    .order-no {
      opacity: 0.8;
    }
  }
}
</style> 