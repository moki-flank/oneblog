<template>
  <el-dialog
    :visible="dialogVisible"
    @update:visible="updateVisible"
    title="开通会员"
    width="800px"
    top="5vh"
    :close-on-click-modal="false"
    custom-class="membership-dialog"
    @close="handleClose"
  >
    <div class="membership-content">
      <!-- 会员特权介绍 -->
      <div class="vip-benefits">
        <h3>会员特权</h3>
        <div class="benefits-list">
          <div class="benefit-item">
            <i class="fas fa-book-reader"></i>
            <div class="benefit-info">
              <h4>专享内容</h4>
              <p>解锁全站会员专享内容</p>
            </div>
          </div>
          <div class="benefit-item">
            <i class="fas fa-download"></i>
            <div class="benefit-info">
              <h4>资源下载</h4>
              <p>享受高速下载通道</p>
            </div>
          </div>
          <div class="benefit-item">
            <i class="fas fa-comments"></i>
            <div class="benefit-info">
              <h4>专属社区</h4>
              <p>加入会员专属交流圈</p>
            </div>
          </div>
          <div class="benefit-item">
            <i class="fas fa-crown"></i>
            <div class="benefit-info">
              <h4>身份标识</h4>
              <p>专属会员标识，彰显身份</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 套餐选择 -->
      <div class="plans-section">
        <h3>选择套餐</h3>
        <div class="plans-list">
          <div
            v-for="plan in membershipPlans"
            :key="plan.id"
            class="plan-item"
            :class="{ active: selectedPlan === plan.id }"
            @click="selectedPlan = plan.id"
          >
            <div class="plan-header">
              <span class="duration">{{ plan.name }}</span>
              <span v-if="plan.discount" class="discount-tag">{{ plan.discount }}折</span>
            </div>
            <div class="plan-price">
              <span class="currency">￥</span>
              <span class="amount">{{ plan.price }}</span>
              <span class="unit">/{{ plan.unit }}</span>
            </div>
            <div v-if="plan.originalPrice" class="original-price">
              原价: ￥{{ plan.originalPrice }}
            </div>
            <div class="average-price">
              日均: ￥{{ (plan.price / plan.days).toFixed(2) }}
            </div>
            <el-button 
              :type="selectedPlan === plan.id ? 'primary' : 'default'" 
              class="select-btn"
              :class="{ selected: selectedPlan === plan.id }"
            >
              {{ selectedPlan === plan.id ? '已选择' : '选择' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <h3>支付方式</h3>
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
      <div v-if="showQRCode" class="qr-section" v-loading="qrLoading">
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
          <div class="amount">支付金额：<span>￥{{ selectedPlanInfo?.price || 0 }}</span></div>
          <div class="order-no">订单号：{{ orderNo }}</div>
        </div>
      </div>
    </div>

    <div slot="footer" class="dialog-footer">
      <template v-if="!showQRCode">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          @click="handlePay" 
          :loading="loading"
          :disabled="!selectedPlan"
        >
          立即开通 {{ selectedPlanInfo ? `￥${selectedPlanInfo.price}` : '' }}
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
import { createMemberOrder, checkOrderStatus } from '@/api/member'

export default {
  name: 'MembershipDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dialogVisible: this.visible,
      membershipPlans: [
        {
          id: 1,
          name: '月度会员',
          price: 30,
          originalPrice: null,
          days: 30,
          unit: '月',
          discount: null
        },
        {
          id: 2,
          name: '季度会员',
          price: 78,
          originalPrice: 90,
          days: 90,
          unit: '季',
          discount: 8.7
        },
        {
          id: 3,
          name: '年度会员',
          price: 258,
          originalPrice: 360,
          days: 365,
          unit: '年',
          discount: 7.2
        }
      ],
      selectedPlan: null,
      paymentMethod: 'alipay',
      loading: false,
      qrLoading: false,
      showQRCode: false,
      qrCodeUrl: this.paymentMethod === 'alipay' ? this.$store.state.webSiteInfo.aliPay : this.$store.state.webSiteInfo.weixinPay,
      orderNo: '',
      checkTimer: null
    }
  },
  computed: {
    selectedPlanInfo() {
      return this.membershipPlans.find(plan => plan.id === this.selectedPlan)
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
    }
  },
  methods: {
    async handlePay() {
      if (!this.selectedPlan) {
        this.$message.warning('请选择会员套餐')
        return
      }

      this.loading = true
      this.qrLoading = true
      try {
        // 创建订单
        const res = await createMemberOrder({
          planId: this.selectedPlan,
          paymentMethod: this.paymentMethod
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
      this.$message.success('开通成功')
      this.$emit('payment-success')
      this.handleClose()
    },
    handleClose() {
      clearInterval(this.checkTimer)
      this.showQRCode = false
      this.qrCodeUrl = ''
      this.orderNo = ''
      this.selectedPlan = null
      this.$emit('update:visible', false)
    },
    updateVisible(value) {
      this.$emit('update:visible', value)
    }
  },
  beforeDestroy() {
    clearInterval(this.checkTimer)
  }
}
</script>

<style lang="scss" scoped>
.membership-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.membership-content {
  padding: $spacing-lg;
}

.vip-benefits {
  margin-bottom: $spacing-xl;

  h3 {
    color: var(--text-primary);
    font-size: 1.3em;
    margin-bottom: $spacing-lg;
  }

  .benefits-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-lg;
  }

  .benefit-item {
    text-align: center;
    padding: $spacing-lg;
    background: var(--hover-bg);
    border-radius: $border-radius-lg;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: $shadow-md;
    }

    i {
      font-size: 2em;
      color: $primary;
      margin-bottom: $spacing-md;
    }

    .benefit-info {
      h4 {
        color: var(--text-primary);
        font-size: 1.1em;
        margin-bottom: $spacing-xs;
      }

      p {
        color: var(--text-secondary);
        font-size: 0.9em;
      }
    }
  }
}

.plans-section {
  margin-bottom: $spacing-xl;

  h3 {
    color: var(--text-primary);
    font-size: 1.3em;
    margin-bottom: $spacing-lg;
  }

  .plans-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-lg;
  }

  .plan-item {
    padding: $spacing-lg;
    border: 2px solid var(--border-color);
    border-radius: $border-radius-lg;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;

    &:hover {
      border-color: rgba($primary, 0.3);
      transform: translateY(-2px);
    }

    &.active {
      border-color: $primary;
      background: rgba($primary, 0.05);
    }

    .plan-header {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-md;

      .duration {
        color: var(--text-primary);
        font-size: 1.2em;
        font-weight: 500;
      }

      .discount-tag {
        padding: 2px 6px;
        background: #E6162D;
        color: white;
        border-radius: 4px;
        font-size: 0.8em;
      }
    }

    .plan-price {
      margin-bottom: $spacing-sm;
      color: #E6162D;

      .currency {
        font-size: 1.2em;
      }

      .amount {
        font-size: 2em;
        font-weight: bold;
      }

      .unit {
        color: var(--text-secondary);
        font-size: 0.9em;
      }
    }

    .original-price {
      color: var(--text-secondary);
      text-decoration: line-through;
      font-size: 0.9em;
      margin-bottom: $spacing-xs;
      min-height: 1.2em;
    }

    .average-price {
      color: var(--text-secondary);
      font-size: 0.9em;
      margin-bottom: $spacing-lg;
    }

    .select-btn {
      width: 100%;
      margin-top: auto;
      
      &.selected {
        background: $primary;
        border-color: $primary;
      }
    }
  }
}

.payment-methods {
  margin-bottom: $spacing-xl;

  h3 {
    color: var(--text-primary);
    font-size: 1.3em;
    margin-bottom: $spacing-lg;
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
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      
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

@media (max-width: 768px) {
  .vip-benefits {
    .benefits-list {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  .plans-section {
    .plans-list {
      grid-template-columns: 1fr;
    }
  }
}
</style> 