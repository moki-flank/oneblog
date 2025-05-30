<template>
  <div>
    <!-- banner -->
    <div class="message-banner" :style="cover">
      <!-- 弹幕输入框 -->
      <div class="message-container">
        <h1 class="message-title">留言板</h1>
        <div class="message-input-wrapper">
          <el-input class="input" v-model="content" placeholder="说点什么吧" @keyup.enter.native="addToList"
            @focus="show = true"></el-input>
          <el-button style="opacity: .6;" class="ml-3" round @click="addToList" v-show="show">发送</el-button>
        </div>
      </div>
      <!-- 弹幕列表 -->
      <div class="barrage-container">
        <vue-danmaku
        class="danmaku"
          :danmus="barrageList"
          style="height: 100%; width: 100%"
          useSlot
          :speeds="150"
          :channels="15"
        >
          <template v-slot:dm="{ danmu }">
            <span class="barrage-items">
              <img
                :src="danmu.avatar"
                width="30"
                height="30"
                style="border-radius: 50%"
              />
              {{ danmu.nickname }}:{{ danmu.content }}</span
            >
          </template>
        </vue-danmaku>
      </div>
    </div>
  </div>
</template>

<script>
import { getMessagesApi, addMessageApi } from "@/api/message";
import VueDanmaku from 'vue-danmaku';
export default {
  components: {
    VueDanmaku
  },

  data() {
    return {
      show: false,
      content: "",
      count: null,
      timer: null,
      barrageList: [],
      user: this.$store.state.userInfo,
    };
  },
  mounted() {
    this.listMessage();
  },
  methods: {
    /**
     * 添加留言
     */
    addToList() {
      if (this.count) {
        this.$message.error("30秒后才能再次留言");
        return false;
      }
      if (this.content.trim() === "") {

        this.$message.error("留言不能为空");
        return false;
      }
      var message = {
        avatar: this.user ? this.user.avatar : this.$store.state.webSiteInfo.touristAvatar,
        status: 1,
        nickname: this.user ? this.user.nickname : "游客",
        content: this.content
      };

      this.content = "";
      addMessageApi(message).then(res => {
        this.barrageList.push(message);
        this.$message.success("留言成功");
      }).catch(err => {
        this.$message.error("留言失败");
      });
      const TIME_COUNT = 30;
      if (!this.timer) {
        this.count = TIME_COUNT;
        this.timer = setInterval(() => {
          if (this.count > 0 && this.count <= 30) {
            this.count--;
          } else {
            clearInterval(this.timer);
            this.timer = null;
          }
        }, 1000);
      }
    },
    /**
     * 获取留言列表
     */
    listMessage() {
      getMessagesApi().then(res => {
        this.barrageList = res.data;
      });
    }
  },
  computed: {
    cover() {
      var cover = "https://img.shiyit.com/1642481294001.png";
      return "background: url(" + cover + ") center center / cover no-repeat";
    }
  }
};
</script>

<style lang="scss" scoped>
:deep(.el-input__inner) {
  border-radius: 50px;
  opacity: .6;
}

.message-banner {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: $primary;
  animation: header-effect 1s;


  .message-container {
    position: absolute;
    width: 360px;
    top: 35%;
    left: 0;
    right: 0;
    text-align: center;
    z-index: 5;
    margin: 0 auto;
    color: #fff;

    .message-title {
      color: #eee;
      animation: title-scale 1s;
    }

    .message-input-wrapper {
      display: flex;
      justify-content: center;
      height: 2.5rem;
      margin-top: 2rem;

      .ml-3 {
        animation: left-in 1s ease;

        @keyframes left-in {
          0% {
            transform: translateY(-500%);
          }

          100% {
            transform: translateX(0);
          }
        }
      }
    }
  }

  .barrage-container {
    position: absolute;
    top: 80px;
    left: 0;
    right: 0;
    bottom: 0;
    width: 100%;

    .barrage-items {
      background: #000;
      border-radius: 100px;
      color: #fff;
      padding: 5px 10px 5px 5px;
      align-items: center;
      display: flex;
      margin-top: 10PX;
    }
  }

}
</style>