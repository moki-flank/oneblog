<template>
  <div class="chat-container">
    <div class="nav-sidebar">
      <div class="nav-header">
        <div class="user-avatar">
          <img :src="$store.state.userInfo.avatar" />
        </div>
      </div>
      <div class="nav-menu">
        <div
          class="nav-item chat-icon"
          :class="{ active: currentNav === 'chat' }"
          @click="switchNav('chat')"
        >
          <i class="fas fa-comment"></i>
          <span class="nav-text">聊天</span>
        </div>
        <div
          class="nav-item friends-icon"
          :class="{ active: currentNav === 'friends' }"
          @click="switchNav('friends')"
        >
          <i class="fas fa-user-friends"></i>
          <span class="nav-text">好友</span>
        </div>
        <div
          class="nav-item groups-icon"
          :class="{ active: currentNav === 'groups' }"
          @click="switchNav('groups')"
        >
          <i class="fas fa-users"></i>
          <span class="nav-text">群聊</span>
        </div>
      </div>
      <div class="nav-bottom">
        <a
          class="nav-item"
          href="https://gitee.com/quequnlong/shiyi-blog"
          target="_blank"
          title="查看源码"
        >
          <i class="fab fa-github"></i>
          <span class="nav-text">源码</span>
        </a>
      </div>
    </div>

    <!-- 遮罩层 -->
    <div
      v-if="isMobile && !isListHidden"
      class="mobile-overlay"
      @click="toggleList"
    ></div>

    <div
      class="chat-list-container"
      :class="{ 'mobile-hidden': isMobile && isListHidden }"
    >
      <div class="search-box">
        <input type="text" placeholder="搜索" />
      </div>
      <div class="chat-list">
        <template v-if="currentNav === 'chat'">
          <div
            v-for="chat in chatList"
            :key="chat.id"
            class="chat-item"
            :class="{ active: currentChat.id === chat.id }"
          >
            <div class="avatar">
              <img :src="chat.avatar" />
            </div>
            <div class="chat-info">
              <div class="name">{{ chat.name }}</div>
              <div class="last-message">{{ currentChat.lastMessage }}</div>
            </div>
            <div class="meta">
              <div class="time">{{ currentChat.lastTime }}</div>
            </div>
          </div>
        </template>
        <template v-else-if="currentNav === 'friends'">
          <div
            v-for="friend in friendsList"
            :key="friend.id"
            class="friend-item"
            :class="{
              active: selectedFriend && selectedFriend.id === friend.id,
            }"
            @click="selectFriend(friend)"
          >
            <div class="avatar">
              <img :src="friend.avatar" />
            </div>
            <div class="friend-info">
              <div class="name">{{ friend.name }}</div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <div class="chat-main" v-if="currentNav === 'chat'">
      <div class="chat-header">
        <div class="user-info">
          <div class="toggle-list-btn" @click="toggleList" v-if="isMobile">
            <i class="fas fa-bars"></i>
          </div>
          <h3>{{ currentChat.name }}</h3>
        </div>
      </div>

      <div class="messages" ref="messageContainer">
        <div v-if="loading" class="loading-messages">
          <i class="fas fa-spinner fa-spin"></i>
          加载中...
        </div>

        <div
          v-for="(msg, index) in currentChat.messages"
          :key="generateUniqueKey(msg, index)"
          :class="[
            'message',
            { 'message-self': msg.userId === $store.state.userInfo.id },
            { 'active-message': msg.id === currentChat.id }
          ]"
        >
     
          <div
            class="avatar"
            @contextmenu.prevent="handleAvatarContextMenu(msg, $event)"
          >
            <img :src="msg.avatar" />
          </div>
          <div class="message-content">
            <div class="message-header">
              <div
                v-if="
                  currentChat.isGroup && msg.userId !== $store.state.userInfo.id
                "
                class="sender-name"
              >
                <span :class="msg.userId === 1 ? 'user-name': msg.userId === 2 ? 'assistant-name': ''" >{{ msg.name }}</span>
                <span v-if="msg.userId === 1" class="author-tag">作者</span>
                <span v-if="msg.userId === 2" class="assistant-tag">A I</span>
                <span class="message-time">{{ msg.time }}</span>
                <span class="location">{{ splitIpAddress(msg.location) }}</span>
              </div>
            </div>
            <div v-if="msg.isRecalled" class="message-recalled">
              <span>消息已撤回</span>
            </div>
            <template v-else>
              <div
                :data-message-id="msg.id"
                v-if="msg.type === 'text'"
                class="message-text"
                v-html="formatMessageContent(msg.content)"
                @contextmenu.prevent="showMessageActions(msg, $event)"
              ></div>
              
              <img
                v-else-if="msg.type === 'image'"
                v-lazy="msg.content"
                :key="'img-' + (msg.id || index)"
                class="message-image"
                @click="previewImage(msg.content)"
                @load="handleImageLoad"
                @contextmenu.prevent="showMessageActions(msg, $event)"
              />
              <div
                v-else-if="msg.type === 'file'"
                class="message-file"
                @contextmenu.prevent="showMessageActions(msg, $event)"
              >
                <i class="fas fa-file-download"></i>
                <div class="file-info">
                  <a href="javascript:void(0)" >{{
                    msg.fileName
                  }}</a>
                  <span class="file-size">{{
                    formatFileSize(msg.fileSize)
                  }}</span>
                </div>
              </div>
              <div
                v-else-if="msg.type === 'audio'"
                class="message-audio"
                @contextmenu.prevent="showMessageActions(msg, $event)"
              >
                <div
                  class="audio-bubble"
                  @click="toggleAudioPlayback(msg, $event)"
                >
                  <i
                    :class="['fas', msg.isPlaying ? 'fa-pause' : 'fa-play']"
                  ></i>
                  <span>{{ msg.duration }}秒</span>
                </div>
              </div>
            </template>
             <!-- 新增引用消息显示 -->
             <div v-if="msg.replyId && !msg.isRecalled" class="reply-message" @click="scrollToMessage(msg.replyId)">
              <span class="reply-content">
                <i class="el-icon-top"></i>{{msg.replyUserName}}：{{msg.replyContent}}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input" style="position: relative;">
        <!-- 新增被回复消息显示 -->
        <div v-if="selectedReplyMessage" class="reply-preview">
          <span>回复: {{ selectedReplyMessage.content }}</span>
          <i class="fas fa-times" @click="cancelReply"></i>
        </div>
        <div class="input-toolbar">
          <mj-emoji size="1.1rem" class="emoji-picker" @select="insertEmoji" />

          <label
            class="toolbar-btn image-upload-btn"
            for="image-upload"
            title="上传图片"
          >
            <i class="fas fa-image"></i>
          </label>
          <input
            id="image-upload"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleImageUpload"
          />

          <!-- 新增文件上传按钮 -->
          <label
            class="toolbar-btn file-upload-btn"
            for="file-upload"
            title="上传文件"
          >
            <i class="fas fa-folder"></i>
          </label>
          <input
            id="file-upload"
            type="file"
            style="display: none"
            @change="handleFileUpload"
          />

          <!-- 新增语音录制按钮 -->
          <label
            @click="toggleVoiceMode"
            class="toolbar-btn voice-toggle-btn"
            title="语音输入"
          >
            <i class="fas fa-microphone"></i>
          </label>
          <!-- <button
            class="toolbar-btn voice-toggle-btn"
            @click="toggleVoiceMode"
            title="语音输入"
          >
            <i class="fas fa-microphone"></i>
          </button> -->
        </div>

        <div class="input-area">
          <template v-if="isVoiceMode">
            <!-- 改进的语音录制按钮 -->
            <button
              :class="['voice-record-btn', { recording: isRecording }]"
              @mousedown="startRecording"
              @mouseup="stopRecording"
              @mouseleave="cancelRecording"
              @touchstart="startRecording"
              @touchend="stopRecording"
            >
              {{ isRecording ? "正在说话中..." : "按住说话" }}
            </button>
          </template>
          <template v-else>
            <textarea
              ref="messageTextarea"
              v-model="messageText"
              @keydown.enter.prevent="handleEnterKey"
              @input="handleInput"
              @keydown="handleMentionKeydown"
              placeholder="输入消息..."
            ></textarea>
          </template>

          <button
            v-if="!isVoiceMode"
            @click="sendMessage"
            :disabled="!messageText.trim() || countdown > 0"
          >
            <template v-if="countdown > 0"> {{ countdown }}s </template>
            <i v-else class="fas fa-paper-plane"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- 图片预览 -->
    <mj-image-preview ref="imagePreview" />

    <div
      v-if="showActionsMenu"
      class="message-actions-menu"
      :style="actionMenuPosition"
    >
      <template v-if="selectedMessage">
        <!-- 新增回复选项 -->
        <div class="action-item" v-if="selectedMessage.userId !== $store.state.userInfo.id"" @click="replyToMessage">
          <i class="fas fa-reply"></i>
          回复
        </div>
        <div
          v-if="selectedMessage.userId !== $store.state.userInfo.id"
          class="action-item"
          @click="mentionUser"
        >
          <i class="fas fa-at"></i>
          TA
        </div>
        <div
          v-if="
            !selectedMessage.isRecalled &&
            selectedMessage.userId === $store.state.userInfo.id
          "
          class="action-item"
          @click="recallMessage"
        >
          <i class="fas fa-undo"></i>
          撤回
        </div>
        <div class="action-item" @click="handleSearch">
          <i class="fas fa-search"></i>
          搜索
        </div>
        <div class="action-item" @click="handleDownloadFile" v-if="selectedMessage.type === 'file'">
          <i class="fas fa-download"></i>
          下载文件
        </div>
      </template>
    </div>

    <!-- 好友信息显示区域 -->
    <div v-if="selectedFriend" class="friend-details">
      <h3>{{ selectedFriend.name }}</h3>
      <p class="signature">
        签名：{{ selectedFriend.signature || "这个人很懒，什么都没写。" }}
      </p>
      <p class="gender">性别：{{ selectedFriend.gender || "未知" }}</p>
      <el-button
        type="primary"
        size="small"
        icon="el-icon-message"
        @click="sendMessageToFriend"
        >发送信息</el-button
      >
      <el-button
        type="danger"
        size="small"
        icon="el-icon-delete"
        @click="deleteFriend"
        >删除好友</el-button
      >
    </div>
  </div>
</template>

<script>
import { sendMsg, getChatMsgListApi, recallMsgApi } from "@/api/chat";
import { formatTime } from "@/utils/time";
import { uploadFileApi } from "@/api/file";
import { disableScroll, enableScroll } from "@/utils/scroll";
import { showLoading, hideLoading } from "@/utils/loading";
import { marked } from 'marked'; // 使用命名导入
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'


export default {
  name: "Chat",
  data() {
    return {
      currentNav: "chat",
      chatList: [
        {
          id: 1,
          name: "博客交流群",
          avatar: this.$store.state.webSiteInfo.logo,
          lastMessage: "",
          lastTime: "",
        },
      ],
      friendsList: [
        {
          id: 1,
          name: "张三",
          avatar:
            "https://foruda.gitee.com/avatar/1677004143848886034/2106773_hhf1237_1647845148.png",
          signature: "热爱生活，热爱编程。",
          gender: "男",
        },
        {
          id: 2,
          name: "李四",
          avatar:
            "https://foruda.gitee.com/avatar/1677079463351115261/7467101_unique_perfect_1638710768.png",
          signature: "旅行是我的灵魂。",
          gender: "女",
        },
      ],
      messageText: "",
      ws: null, // WebSocket实例
      currentChat: {
        id: 1,
        name: "博客交流群",
        avatar: this.$store.state.webSiteInfo.logo,
        lastMessage: "",
        lastTime: "12:30",
        isGroup: true,
        members: [],
        messages: [
          {
            id: 1,
            type: "audio",
            content: "audio_url_here",
            isPlaying: false, // 确保初始化时存在
          },
        ],
      },
      showActionsMenu: false,
      selectedMessage: null,
      actionMenuPosition: {
        top: "0px",
        left: "0px",
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      loading: false, // 是否正在加载更多消息
      hasMore: true, // 是否还有更多消息
      countdown: 0, // 倒计时秒数
      countdownTimer: null, // 计时器实例
      heartbeatTimer: null, // 心跳定时器
      heartbeatInterval: 30000, // 心跳间隔，30秒
      reconnectAttempts: 0, // 重连次数
      maxReconnectAttempts: 5, // 最大重连次数
      shouldReconnect: true, // 是否需要重连的标志
      shouldScrollToBottom: true, // 添加新标志来控制是否滚动到底部
      isMobile: false,
      isListHidden: false,
      selectedFriend: null, // 用于存储选中的好友信息
      isVoiceMode: false, // 新增语音模式状态
      mediaRecorder: null,
      audioChunks: [],
      isRecording: false, // 新增录音状态
      audioStartTime: null, // 新增音频开始时间
      audioEndTime: null, // 新增音频结束时间
      audio: null,
      selectedReplyMessage: null, // 新增字段，用于存储回复的消息
    };
  },
  //监听this.$store.state.userInfo的变化
  watch: {
    "$store.state.userInfo": {
      handler(newVal, oldVal) {
        // 有值就连接ws, 没有就移除
        if (newVal) {
          this.init();
        } else {
          this.$store.commit("SET_LOGIN_VISIBLE", true);
          if (this.ws) {
            this.ws.close();
          }
        }
      },
      deep: true,
    },
  },
  created() {
    // 如果用户信息存在，则连接WebSocket
    if (!this.$store.state.userInfo) {
      this.$store.commit("SET_LOGIN_VISIBLE", true);
      return;
    }
    this.init();
    //禁止滚动
    disableScroll();
    this.checkMobile();
    window.addEventListener("resize", this.checkMobile);
  },
  beforeDestroy() {
    this.shouldReconnect = false; // 设置不再重连
    this.stopHeartbeat(); // 清理心跳定时器
    if (this.ws) {
      this.ws.close();
    }
    // 移除滚动事件监听
    const container = this.$refs.messageContainer;
    if (container) {
      container.removeEventListener("scroll", this.handleScroll);
    }
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
    //开启滚动
    enableScroll();
    window.removeEventListener("resize", this.checkMobile);
  },
  methods: {
    switchNav(nav) {
      this.currentNav = nav;
      this.selectedFriend = null;
      if (nav === "chat") {
        this.$nextTick(() => {
          this.scrollToBottom();
          // 重新添加滚动事件监听器
          const container = this.$refs.messageContainer;
          if (container) {
            container.addEventListener("scroll", this.handleScroll);
          }
          // 重置加载状态
          this.hasMore = true;
          this.params.pageNum = 1;
        });
      }
    },
    /**
     * 初始化
     */
    async init() {
      this.connectWebSocket();
      await this.getChatList();

      // 添加滚动事件监听
      this.$nextTick(() => {
        const container = this.$refs.messageContainer;
        if (container) {
          container.addEventListener("scroll", this.handleScroll);
        }

        setTimeout(() => {
          this.scrollToBottom();
        }, 300);
      });
    },
    /**
     * 获取聊天列表
     */
    async getChatList() {
      const res = await getChatMsgListApi(this.params);
      this.currentChat.messages = res.data.records;
      if (this.params.pageNum === 1 && this.currentChat.messages.length > 0) {
        this.currentChat.lastTime = formatTime(
          this.currentChat.messages[0].time
        );
        this.currentChat.lastMessage =
          this.currentChat.messages[0].type === "text"
            ? this.currentChat.messages[0].content
            : this.currentChat.messages[0].type === "image"
            ? "[图片]"
            : this.currentChat.messages[0].type === "audio"
            ? "[语音] " + this.currentChat.messages[0].duration + "秒"
            : "[文件]";
      }
      //获取到的数据是最新时间的，应该要把这个顺序反过来
      this.currentChat.messages.reverse();
      this.currentChat.messages.forEach((msg) => {
        msg.time = formatTime(msg.time);
      });

     this.handlePreCode();
    },
    /**
     * 连接WebSocket
     */
    connectWebSocket() {
      try {
        const wsUrl = import.meta.env.VITE_APP_WEBSOCKET_API;
        this.ws = new WebSocket(wsUrl + this.$store.state.userInfo.id);

        this.ws.onopen = () => {
          console.log("WebSocket连接已建立");
          this.reconnectAttempts = 0;
          this.startHeartbeat();
        };

        this.ws.onmessage = (event) => {
          console.log("收到原始消息:", event.data);
          try {
            const message = JSON.parse(event.data);
            // 如果是心跳响应，则不处理
            if (message.type === "ping") {
              return;
            }
            this.handleIncomingMessage(message);
          } catch (error) {
            console.error("解析消息失败:", error);
          }
        };

        this.ws.onclose = () => {
          console.log("WebSocket连接已关闭");
          this.stopHeartbeat(); // 停止心跳
          this.reconnect(); // 尝试重连
        };

        this.ws.onerror = (error) => {
          console.error("WebSocket错误:", error);
          this.stopHeartbeat(); // 停止心跳
        };
      } catch (error) {
        console.error("建立WebSocket连接失败:", error);
      }
    },

    /**
     * 处理接收到的消息
     */
    handleIncomingMessage(message) {
      console.log("收到消息:", message);

      if (typeof message === "string") {
        try {
          message = JSON.parse(message);
        } catch (e) {
          console.error("消息格式错误:", e);
          return;
        }
      }
      if (message.isRecalled) {
        const index = this.currentChat.messages.findIndex(
          (msg) => msg.id === message.id
        );
        if (index !== -1) {
          this.currentChat.messages[index].content = message.content;
          this.currentChat.messages[index].isRecalled = message.isRecalled;
        }
        return;
      }
      if (message.type === "text") {
        message.content = message.content.replace(
          /@(\S+)\s/g,
          "<mention>@$1</mention> "
        );
      }

      const newMessage = {
        id: message.id || Date.now() + Math.random(), // 确保每条消息都有唯一ID
        type: message.type || "text",
        content: message.content,
        time: formatTime(new Date()),
        userId: message.userId,
        name: message.name,
        avatar: message.avatar,
        location: message.location,
        fileSize: message.fileSize,
        fileName: message.fileName,
        duration: message.duration,
        isPlaying: false, // 确保初始化时存在
        replyId: message.replyId || null, 
        replyContent: message.replyContent || null,
        replyUserName: message.replyUserName || null,
      };

      this.currentChat.messages.push(newMessage);
      this.currentChat.lastMessage =
        message.type === "text"
          ? message.content
          : message.type === "image"
          ? "[图片]"
          : message.type === "audio"
          ? "[语音] " + message.duration + "秒"
          : "[文件]";
      this.currentChat.lastTime = "刚刚";
      this.shouldScrollToBottom = true;

      this.$nextTick(() => {
        this.scrollToBottom();
      });
      setTimeout(() => {
        this.handlePreCode();
      }, 100);
    },
    /**
     * 处理代码块
     */
    async handlePreCode(){
      await this.$nextTick()
      document.querySelectorAll('pre code').forEach((block) => {
        hljs.highlightBlock(block)
      })

      this.addCopyButtons();
    },

     /**
     * 添加复制按钮
     */
     addCopyButtons() {
      const codeBlocks = document.querySelectorAll('.message-content pre')
      if (!codeBlocks.length) return

      codeBlocks.forEach(pre => {
        // 检查是否已经添加过复制按钮
        if (pre.querySelector('.code-header')) return

        // 创建复制按钮容器
        const buttonWrapper = document.createElement('div')
        buttonWrapper.className = 'code-header'

        // 创建复制按钮
        const copyButton = document.createElement('button')
        copyButton.className = 'copy-button'
        copyButton.innerHTML = '<i class="fas fa-copy"></i> 复制'
        copyButton.title = '复制代码'

        // 添加点击事件
        copyButton.addEventListener('click', async () => {
          try {
            const code = pre.querySelector('code')
            await navigator.clipboard.writeText(code.textContent)
            copyButton.innerHTML = '<i class="fas fa-check"></i> 已复制'
            copyButton.classList.add('copied')
            setTimeout(() => {
              copyButton.innerHTML = '<i class="fas fa-copy"></i> 复制'
              copyButton.classList.remove('copied')
            }, 2000)
            this.$message.success('复制成功')
          } catch (err) {
            this.$message.error('复制失败，请手动复制')
          }
        })

        // 将按钮添加到代码块
        buttonWrapper.appendChild(copyButton)
        pre.appendChild(buttonWrapper)
      })
    },

    /**
     * 发送文本消息
     */
    async sendMessage() {
      if (
        !this.messageText.trim() ||
        !this.$store.state.userInfo ||
        this.countdown > 0
      )
        return;

      const mentionedContent = this.messageText.replace(
        /@(\S+)\s/g,
        "<mention>@$1</mention> "
      );

      const message = {
        type: "text",
        content: mentionedContent,
        name: this.$store.state.userInfo.nickname,
        userId: this.$store.state.userInfo.id,
        avatar: this.$store.state.userInfo.avatar,
        replyId: this.selectedReplyMessage?.id || null,
        replyContent: this.selectedReplyMessage?.content || null,
        replyUserId: this.selectedReplyMessage?.userId || null,
        replyUserName: this.selectedReplyMessage?.name || null,
      };

      try {
        const response = await sendMsg(message);
        this.startCountdown();
        this.shouldScrollToBottom = true; // 发送消息时设置为true
        this.selectedReplyMessage = null;
      } catch (error) {
        console.error("发送消息失败:", error);
        this.$message.error("发送失败，请重试");
      }
      this.messageText = "";
    },

    /**
     * 发送图片消息
     */
    async sendImage(imageData) {
      if (!this.$store.state.userInfo) return;

      const message = {
        type: "image",
        content: imageData,
        self: true,
        name: this.$store.state.userInfo.nickname,
        userId: this.$store.state.userInfo.id,
        avatar: this.$store.state.userInfo.avatar,
      };

      try {
        const response = await sendMsg(message);
        this.shouldScrollToBottom = true; // 发送图片时设置为true
      } catch (error) {
        console.error("发送图片失败:", error);
        this.$message.error("发送失败，请重试");
      }
    },

    /**
     * 滚动到底部
     */
    scrollToBottom() {
      const container = this.$refs.messageContainer;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },

    /**
     * 插入表情
     */
    insertEmoji(emoji) {
      const match = emoji.match(/\((.*?)\)/);
      if (match && match.length > 1) {
        const imgUrl = match[1];
        this.sendImage(imgUrl);
        return;
      }
      const textarea = this.$refs.messageTextarea;
      if (!textarea) return;
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      this.messageText =
        this.messageText.substring(0, start) +
        emoji +
        this.messageText.substring(end);

      // 将光标移动到插入的表情符号后面
      this.$nextTick(() => {
        textarea.focus();
        textarea.setSelectionRange(start + emoji.length, start + emoji.length);
      });
    },
    /**
     * 上传图片
     * @param event
     */
    async handleImageUpload(event) {
      const file = event.target.files[0];
      if (!file) return;

      if (!file.type.startsWith("image/")) {
        this.$message.error("请选择图片文件");
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        this.$message.error("图片大小不能超过5MB");
        return;
      }

      try {
        // 创建 FormData 对象
        const formData = new FormData();
        formData.append("file", file);

        // 调用上传接口
        const response = await uploadFileApi(formData, "chat");

        // 发送图片消息
        if (response.data) {
          await this.sendImage(response.data);
        }
      } catch (error) {
        console.error("图片上传失败:", error);
        this.$message.error("图片上传失败");
      }

      // 清空 input 的值，允许重复选择同一文件
      event.target.value = "";
    },
    /**
     * 预览图片
     * @param src
     */
    previewImage(src) {
      this.$refs.imagePreview.show(src);
    },
    /**
     * 显示消息操作菜单
     * @param {Object} message 消息对象
     * @param {Event} event 鼠标事件
     */
    showMessageActions(message, event) {
      this.selectedMessage = message;
      this.showActionsMenu = true;

      // 计算菜单位置
      this.actionMenuPosition = {
        top: `${event.clientY}px`,
        left: `${event.clientX}px`,
      };

      // 添加点击事件监听
      setTimeout(() => {
        document.addEventListener("click", this.closeActionsMenu);
      }, 0);
    },

    /**
     * 关闭消息操作菜单
     */
    closeActionsMenu() {
      this.showActionsMenu = false;
      this.selectedMessage = null;
      document.removeEventListener("click", this.closeActionsMenu);
    },

    /**
     * 撤回消息
     */
    async recallMessage() {
      if (!this.selectedMessage) return;

      try {
        const data = { id: this.selectedMessage.id };
        // 调用撤回消息接口
        const response = await recallMsgApi(data);
      } catch (error) {
        this.$message.error(error.message || "撤回失败，请重试");
      }
      this.closeActionsMenu();
    },

    /**
     * 处理搜索
     */
    handleSearch() {
      this.$message.info("搜索功能暂未实现");
    },

    /**
     * 处理下载文件
     */
    async handleDownloadFile() {
      try {
        let fileName = this.selectedMessage.fileName;
        let response = await fetch(this.selectedMessage.content);
        let blob = await response.blob();
        let blobUrl = URL.createObjectURL(blob);
        const link = document.createElement("a"); // 创建a标签
        link.href = blobUrl; // 下载链接
        link.download = fileName; // 下载的文件名
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link); // 下载完成后移除a标签
        URL.revokeObjectURL(blobUrl); // 释放URL对象
      } catch (error) {
        this.$message.error("下载失败，请重试");
      }
    },

    /**
     * 处理图片加载
     */
    handleImageLoad() {
      // 只有在需要滚动到底部时才执行
      if (this.shouldScrollToBottom) {
        this.scrollToBottom();
      }
    },

    /**
     * 监听消息容器的滚动
     */
    handleScroll() {
      const container = this.$refs.messageContainer;
      // 当滚动到接近顶部时加载更多消息
      if (container.scrollTop <= 100 && !this.loading && this.hasMore) {
        this.loadMoreMessages();
      }
    },

    /**
     * 加载更多历史消息
     */
    async loadMoreMessages() {
      if (this.loading || !this.hasMore) return;

      this.loading = true;
      this.shouldScrollToBottom = false; // 加载历史消息时设置为false
      try {
        const container = this.$refs.messageContainer;
        const oldScrollHeight = container.scrollHeight;

        this.params.pageNum++;

        const response = await getChatMsgListApi(this.params);

        if (
          response.data &&
          response.data.records &&
          response.data.records.length > 0
        ) {
          const formattedMessages = response.data.records.map((msg) => ({
            ...msg,
            time: formatTime(msg.time),
          }));

          this.currentChat.messages.unshift(...formattedMessages.reverse());

          if (response.data.records.length < this.params.pageSize) {
            this.hasMore = false;
          }

          this.$nextTick(() => {
            const newScrollHeight = container.scrollHeight;
            container.scrollTop = newScrollHeight - oldScrollHeight;
          });
        } else {
          this.hasMore = false;
        }
      } catch (error) {
        console.error("加载历史消息失败:", error);
        this.$message.error("加载历史消息失败");
      } finally {
        this.loading = false;
      }
    },

    /**
     * 开始倒计时
     */
    startCountdown() {
      this.countdown = 3;
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer);
      }
      this.countdownTimer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          clearInterval(this.countdownTimer);
        }
      }, 1000);
    },

    /**
     * 开始心跳
     */
    startHeartbeat() {
      this.stopHeartbeat(); // 确保之前的心跳已停止
      this.heartbeatTimer = setInterval(() => {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          // 发送心跳消息
          this.ws.send(
            JSON.stringify({
              type: "ping",
              timestamp: new Date().getTime(),
            })
          );
        } else {
          this.stopHeartbeat();
          this.reconnect();
        }
      }, this.heartbeatInterval);
    },

    /**
     * 停止心跳
     */
    stopHeartbeat() {
      if (this.heartbeatTimer) {
        clearInterval(this.heartbeatTimer);
        this.heartbeatTimer = null;
      }
    },

    /**
     * 重连WebSocket
     */
    reconnect() {
      // 如果不需要重连，直接返回
      if (!this.shouldReconnect) {
        return;
      }

      if (this.reconnectAttempts >= this.maxReconnectAttempts) {
        console.log("达到最大重连次数，停止重连");
        this.$message.error("网络连接异常，请刷新页面重试");
        return;
      }

      this.reconnectAttempts++;
      console.log(`第 ${this.reconnectAttempts} 次尝试重连...`);

      // 使用指数退避算法计算重连延迟
      const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);

      setTimeout(() => {
        // 再次检查是否需要重连
        if (this.shouldReconnect && this.$store.state.userInfo) {
          this.connectWebSocket();
        }
      }, delay);
    },

    //截取地址
    splitIpAddress(address) {
      return address ? address.split("|")[1] : "未知";
    },

    /**
     * 处理Enter键
     * @param {KeyboardEvent} event
     */
    handleEnterKey(event) {
      // 如果按下了Ctrl键，则插入换行
      if (event.ctrlKey) {
        const textarea = this.$refs.messageTextarea;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        this.messageText =
          this.messageText.substring(0, start) +
          "\n" +
          this.messageText.substring(end);

        // 将光标移动到换行后的位置
        this.$nextTick(() => {
          textarea.selectionStart = textarea.selectionEnd = start + 1;
        });
      } else {
        // 否则发送消息
        this.sendMessage();
      }
    },

    /**
     * 处理@输入
     * @param {Event} event
     */
    handleInput(event) {
      const textarea = this.$refs.messageTextarea;
      const text = textarea.value;
      const position = textarea.selectionStart;

      // 检查是否刚输入了@符号
      if (text[position - 1] === "@") {
        this.showMentionList = true;
        this.mentionFilter = "";
        this.selectedMentionIndex = -1;

        // 计算@列表的位置
        const coords = this.getCaretCoordinates(textarea, position);
        this.mentionListPosition = {
          top: `${coords.top + 20}px`,
          left: `${coords.left}px`,
        };
      } else if (this.showMentionList) {
        // 更新过滤文本
        const lastAtIndex = text.lastIndexOf("@", position - 1);
        if (lastAtIndex !== -1) {
          this.mentionFilter = text.substring(lastAtIndex + 1, position);
        }
      }
    },

    /**
     * 处理@列表键盘事件
     * @param {KeyboardEvent} event
     */
    handleMentionKeydown(event) {
      if (!this.showMentionList) return;

      const filteredUsers = this.getFilteredUsers();

      switch (event.key) {
        case "ArrowDown":
          event.preventDefault();
          this.selectedMentionIndex = Math.min(
            this.selectedMentionIndex + 1,
            filteredUsers.length - 1
          );
          break;
        case "ArrowUp":
          event.preventDefault();
          this.selectedMentionIndex = Math.max(
            this.selectedMentionIndex - 1,
            0
          );
          break;
        case "Enter":
          event.preventDefault();
          if (this.selectedMentionIndex >= 0) {
            this.selectMentionUser(filteredUsers[this.selectedMentionIndex]);
          }
          break;
        case "Escape":
          event.preventDefault();
          this.showMentionList = false;
          break;
      }
    },

    /**
     * 选择要@的用户
     * @param {Object} user
     */
    selectMentionUser(user) {
      const textarea = this.$refs.messageTextarea;
      const text = textarea.value;
      const position = textarea.selectionStart;
      const lastAtIndex = text.lastIndexOf("@", position - 1);

      if (lastAtIndex !== -1) {
        this.messageText =
          text.substring(0, lastAtIndex) +
          `@${user.name} ` +
          text.substring(position);

        // 将光标移动到插入的@用户名后面
        this.$nextTick(() => {
          const newPosition = lastAtIndex + user.nickname.length + 2;
          textarea.focus();
          textarea.setSelectionRange(newPosition, newPosition);
        });
      }

      this.showMentionList = false;
    },

    /**
     * 获取过滤后的用户列表
     */
    getFilteredUsers() {
      return this.mentionUsers.filter((user) =>
        user.nickname.toLowerCase().includes(this.mentionFilter.toLowerCase())
      );
    },

    /**
     * 获取光标在textarea中的坐标
     */
    getCaretCoordinates(element, position) {
      const div = document.createElement("div");
      const styles = window.getComputedStyle(element);
      const properties = [
        "direction",
        "boxSizing",
        "width",
        "height",
        "overflowX",
        "overflowY",
        "borderTopWidth",
        "borderRightWidth",
        "borderBottomWidth",
        "borderLeftWidth",
        "paddingTop",
        "paddingRight",
        "paddingBottom",
        "paddingLeft",
        "fontStyle",
        "fontVariant",
        "fontWeight",
        "fontStretch",
        "fontSize",
        "fontSizeAdjust",
        "lineHeight",
        "fontFamily",
        "textAlign",
        "textTransform",
        "textIndent",
        "textDecoration",
        "letterSpacing",
        "wordSpacing",
      ];

      div.style.position = "absolute";
      div.style.visibility = "hidden";
      properties.forEach((prop) => {
        div.style[prop] = styles[prop];
      });

      div.textContent = element.value.substring(0, position);
      const span = document.createElement("span");
      span.textContent = element.value.substring(position) || ".";
      div.appendChild(span);
      document.body.appendChild(div);

      const coordinates = {
        top:
          span.offsetTop +
          parseInt(styles.borderTopWidth) +
          parseInt(styles.paddingTop),
        left:
          span.offsetLeft +
          parseInt(styles.borderLeftWidth) +
          parseInt(styles.paddingLeft),
      };

      document.body.removeChild(div);
      return coordinates;
    },

    /**
     * 在头像上右键时显示@选项
     */
    handleAvatarContextMenu(message, event) {
      event.preventDefault();
      // 不能@自己
      if (message.userId === this.$store.state.userInfo.id) {
        return;
      }

      this.selectedMessage = message;
      this.showActionsMenu = true;

      // 计算菜单位置
      this.actionMenuPosition = {
        top: `${event.clientY}px`,
        left: `${event.clientX}px`,
      };

      // 添加点击事件监听
      setTimeout(() => {
        document.addEventListener("click", this.closeActionsMenu);
      }, 0);
    },

    /**
     * @用户
     */
    mentionUser() {
      if (!this.selectedMessage) return;
      const user = {
        nickname: this.selectedMessage.name,
      };

      // 在输入框末尾添加@用户
      this.messageText += `@${user.nickname} `;

      // 关闭菜单
      this.closeActionsMenu();

      // 聚焦输入框
      this.$nextTick(() => {
        const textarea = this.$refs.messageTextarea;
        textarea.focus();
        textarea.setSelectionRange(
          this.messageText.length,
          this.messageText.length
        );
      });
    },

    /**
     * 格式化消息内容
     */
    formatMessageContent(content) {
      // 使用 marked 解析 Markdown 内容
      const htmlContent = marked(content);

      // 如果内容已经包含<mention>标签，说明是发送时已经处理过的
      if (htmlContent.includes("<mention>")) {
        return htmlContent;
      }

      // 否则处理普通文本中的@格式
      return htmlContent.replace(/@(\S+)\s/g, "<mention>@$1</mention> ");
    },

    toggleList() {
      if (!this.isMobile) return;
      this.isListHidden = !this.isListHidden;
    },

    /**
     * 检查是否为移动设备
     */
    checkMobile() {
      const width = window.innerWidth;
      this.isMobile = width <= 768;
      // 在移动端时默认隐藏列表
      if (this.isMobile) {
        this.isListHidden = true;
      } else {
        this.isListHidden = false;
      }
    },

    selectFriend(friend) {
      this.selectedFriend = friend;
    },

    sendMessageToFriend() {
      // 实现发送信息的逻辑
      this.$message.info(`发送信息给 ${this.selectedFriend.name}`);
    },

    deleteFriend() {
      // 实现删除好友的逻辑
      this.$message.info(`删除好友 ${this.selectedFriend.name}`);
    },

    /**
     * 处理文件上传
     * @param event
     */
    async handleFileUpload(event) {
      showLoading();
      const file = event.target.files[0];
      if (!file) return;

      try {
        // 创建 FormData 对象
        const formData = new FormData();
        formData.append("file", file);

        // 调用上传接口
        const response = await uploadFileApi(formData, "chat");

        // 发送文件消息
        if (response.data) {
          await this.sendFile(response.data, file.name, file.size);
        }
      } catch (error) {
        console.error("文件上传失败:", error);
        this.$message.error("文件上传失败");
      } finally {
        hideLoading();
      }

      // 清空 input 的值，允许重复选择同一文件
      event.target.value = "";
    },

    /**
     * 发送文件消息
     */
    async sendFile(fileData, fileName, fileSize) {
      if (!this.$store.state.userInfo) return;

      const message = {
        type: "file",
        content: fileData,
        fileName: fileName,
        fileSize: fileSize,
        name: this.$store.state.userInfo.nickname,
        userId: this.$store.state.userInfo.id,
        avatar: this.$store.state.userInfo.avatar,
      };

      try {
        const response = await sendMsg(message);
        this.shouldScrollToBottom = true; // 发送文件时设置为true
      } catch (error) {
        console.error("发送文件失败:", error);
        this.$message.error("发送失败，请重试");
      }
    },

    /**
     * 格式化文件大小
     * @param {Number} size 文件大小（字节）
     * @returns {String} 格式化后的文件大小
     */
    formatFileSize(size) {
      if (size < 1024) return size + " B";
      if (size < 1024 * 1024) return (size / 1024).toFixed(2) + " KB";
      if (size < 1024 * 1024 * 1024)
        return (size / (1024 * 1024)).toFixed(2) + " MB";
      return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
    },

    /**
     * 切换语音模式
     */
    toggleVoiceMode() {
      this.isVoiceMode = !this.isVoiceMode;
    },

    /**
     * 开始录音
     */
    startRecording() {
      if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
        this.$message.error("当前浏览器不支持录音功能");
        return;
      }

      navigator.mediaDevices
        .getUserMedia({ audio: true })
        .then((stream) => {
          this.audioStartTime = new Date();
          this.mediaRecorder = new MediaRecorder(stream);
          this.mediaRecorder.start();
          this.audioChunks = [];

          this.mediaRecorder.ondataavailable = (event) => {
            this.audioChunks.push(event.data);
          };

          // 添加录音中的视觉反馈
          this.isRecording = true;
        })
        .catch((error) => {
          console.error("录音失败:", error);
          this.$message.error("录音失败，请检查麦克风权限");
        });
    },

    /**
     * 停止录音并上传语音
     */
    stopRecording() {
      if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
        this.audioEndTime = new Date();
        let recordingDuration = Math.ceil(
          (this.audioEndTime - this.audioStartTime) / 1000
        );
        if (recordingDuration < 3) {
          this.$message.error("录音时间太短，请重新录音");
          return;
        }
        this.mediaRecorder.stop();
        this.mediaRecorder.onstop = async () => {
          const audioBlob = new Blob(this.audioChunks, { type: "audio/wav" });
          const formData = new FormData();
          formData.append("file", audioBlob, new Date().getTime() + ".wav");

          try {
            const response = await uploadFileApi(formData, "chat");
            if (response.data) {
              await this.sendAudio(response.data, recordingDuration);
            }
          } catch (error) {
            console.error("语音上传失败:", error);
            this.$message.error("语音上传失败");
          }

          this.isRecording = false;
          if (this.mediaRecorder.stream) {
            this.mediaRecorder.stream
              .getTracks()
              .forEach((track) => track.stop());
          }
        };
      }
    },

    /**
     * 取消录音
     */
    cancelRecording() {
      if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
        this.mediaRecorder.stop();
        this.audioChunks = [];
        this.$message.info("录音已取消");
        this.isRecording = false; // 移除录音中的视觉反馈
        if (this.mediaRecorder.stream) {
          this.mediaRecorder.stream
            .getTracks()
            .forEach((track) => track.stop());
        }
      }
    },

    /**
     * 发送语音消息
     */
    async sendAudio(audioUrl, duration) {
      if (!this.$store.state.userInfo) return;

      const message = {
        type: "audio",
        content: audioUrl,
        duration: duration,
        name: this.$store.state.userInfo.nickname,
        userId: this.$store.state.userInfo.id,
        avatar: this.$store.state.userInfo.avatar,
      };

      try {
        const response = await sendMsg(message);
        this.shouldScrollToBottom = true; // 发送语音时设置为true
      } catch (error) {
        console.error("发送语音失败:", error);
        this.$message.error("发送失败，请重试");
      }
    },

    /**
     * 切换语音播放状态
     */
    toggleAudioPlayback(msg, event) {
      if (this.audio) {
        this.audio.pause();
        this.$set(msg, "isPlaying", false);
        this.audio = null;
        return;
      }
      this.audio = new Audio(msg.content);
      this.audio
        .play()
        .then(() => {
          this.$set(msg, "isPlaying", true);
          this.audio.onended = () => {
            this.$set(msg, "isPlaying", false);
            this.audio = null;
          };
        })
        .catch((error) => {
          this.audio = null;
          console.error("音频播放失败:", error);
          this.$message.error("音频播放失败，请检查音频格式或网络连接");
        });
    },

    /**
     * 格式化音频时长
     */
    formatAudioDuration(duration) {
      const minutes = Math.floor(duration / 60);
      const seconds = duration % 60;
      return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
    },

    /**
     * 滚动到指定消息
     */
    async scrollToMessage(messageId) {
        const container = this.$refs.messageContainer;
        let messageElement = container.querySelector(`[data-message-id="${messageId}"]`);

        // 如果消息元素不存在，尝试加载更多消息
        while (!messageElement && this.hasMore) {
            await this.loadMoreMessages(); // 确保等待加载完成
            messageElement = container.querySelector(`[data-message-id="${messageId}"]`);
        }

        // 如果找到消息元素，滚动到该位置
        if (messageElement) {
            const offset = 100; // 设置偏移量，单位为像素
            const topPosition = messageElement.offsetTop - offset;
            container.scrollTo({ top: topPosition, behavior: 'smooth' });

            // 添加激活效果
            messageElement.classList.add('active-message');
            setTimeout(() => {
                messageElement.classList.remove('active-message');
            }, 2000); // 3秒后移除激活效果
        } else {
            this.$message.error("未能找到该消息，请尝试手动加载更多历史消息。");
        }
    },
    /**
     * 回复菜单
     */
    replyToMessage() {
      this.selectedReplyMessage = this.selectedMessage;
      this.closeActionsMenu();
      this.$nextTick(() => {
        const textarea = this.$refs.messageTextarea;
        textarea.focus();
      });
    },
    /**
     * 取消回复
     */
    cancelReply() {
      this.selectedReplyMessage = null;
    },
    // 生成唯一的key
    generateUniqueKey(msg, index) {
      return msg.id || `msg-${msg.userId}-${index}-${Date.now()}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.chat-container {
  height: calc(100vh - 140px);
  display: grid;
  grid-template-columns: 80px 280px 1fr;
  background: var(--card-bg);
  border-radius: 12px;
  box-shadow: $shadow-lg;
  margin: $spacing-lg auto;
  overflow: hidden;
  max-width: 1200px;
  position: relative;

  @media screen and (max-width: 768px) {
    height: 100vh;
    margin: 0;
    border-radius: 0;
    grid-template-columns: 80px 1fr;
    padding-top: 0; // 移除顶部padding
  }
}

.nav-sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #3498db 100%);
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
  z-index: 200;

  @media screen and (max-width: 768px) {
    position: fixed;
    left: 0;
    top: 70px;
    bottom: 0;
    width: 80px;
  }

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(
      135deg,
      rgba(52, 152, 219, 0.1) 0%,
      rgba(44, 62, 80, 0.1) 100%
    );
    z-index: 1;
  }

  .nav-header {
    padding: $spacing-md;
    display: flex;
    justify-content: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    margin-bottom: $spacing-md;
    background: rgba(255, 255, 255, 0.05);
    position: relative;
    z-index: 2;

    .user-avatar {
      width: 48px;
      height: 48px;
      border-radius: 16px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 2px solid transparent;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);

      &:hover {
        border-color: rgba(255, 255, 255, 0.8);
        transform: scale(1.05) translateY(-2px);
        box-shadow: 0 8px 16px rgba(52, 152, 219, 0.3);
      }

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .nav-menu {
    padding: $spacing-sm 0;
    display: flex;
    flex-direction: column;
    gap: $spacing-md;
    position: relative;
    z-index: 2;
    height: calc(100% - 100px); // 为底部源码按钮留出空间
    justify-content: flex-start;

    .nav-item {
      height: 64px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 6px;
      cursor: pointer;
      color: rgba(255, 255, 255, 0.7);
      transition: all 0.3s ease;
      position: relative;
      margin: 0 $spacing-sm;
      border-radius: 16px;
      backdrop-filter: blur(8px);
      &:hover {
        color: white;
        background: rgba(255, 255, 255, 0.1);
        transform: translateY(-1px);

        .nav-text {
          opacity: 1;
          transform: scale(1.02);
        }

        i {
          transform: scale(1.05);
        }

        &.chat-icon i {
          color: #2ecc71;
        }

        &.friends-icon i {
          color: #e74c3c;
        }

        &.groups-icon i {
          color: #f1c40f;
        }
      }

      &.active {
        color: white;
        background: rgba(255, 255, 255, 0.15);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

        &::before {
          content: "";
          position: absolute;
          left: -$spacing-sm;
          top: 50%;
          transform: translateY(-50%);
          width: 3px;
          height: 24px;
          background: white;
          border-radius: 0 4px 4px 0;
          box-shadow: 0 0 10px rgba(255, 255, 255, 0.4);
        }

        .nav-text {
          opacity: 1;
          color: white;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }

        &.chat-icon i {
          color: #2ecc71;
        }

        &.friends-icon i {
          color: #e74c3c;
        }

        &.groups-icon i {
          color: #f1c40f;
        }
      }

      i {
        font-size: 1.4em;
        transition: all 0.3s ease;
      }

      .nav-text {
        font-size: 0.85em;
        opacity: 0.8;
        transition: all 0.3s ease;
        font-weight: 500;
      }

      &::after {
        content: "";
        position: absolute;
        left: 50%;
        bottom: -$spacing-sm;
        transform: translateX(-50%);
        width: 3px;
        height: 3px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.2);
      }

      &:last-child::after {
        display: none;
      }
    }
  }

  // 添加底部源码按钮样式
  .nav-bottom {
    position: absolute;
    bottom: $spacing-md;
    left: 0;
    right: 0;
    padding: 0 $spacing-sm;
    z-index: 2;

    .nav-item {
      height: 64px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 6px;
      cursor: pointer;
      color: rgba(255, 255, 255, 0.7);
      transition: all 0.3s ease;
      border-radius: 16px;
      backdrop-filter: blur(8px);
      text-decoration: none !important;

      &:hover {
        color: white;
        background: rgba(255, 255, 255, 0.1);
        transform: translateY(-1px);

        .nav-text {
          opacity: 1;
        }

        i {
          color: #f1c40f;
          transform: scale(1.1);
        }
      }

      i {
        font-size: 1.4em;
        transition: all 0.3s ease;
      }

      .nav-text {
        font-size: 0.85em;
        opacity: 0.8;
        transition: all 0.3s ease;
        font-weight: 500;
      }
    }
  }
}

.chat-list-container {
  background: var(--card-bg);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  overflow: hidden;

  @media screen and (max-width: 768px) {
    position: absolute;
    left: 80px;
    top: 0;
    bottom: 0;
    width: 280px;
    z-index: 150;
    background: var(--card-bg);
    transform: translateX(0);
    transition: transform 0.3s ease;
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);

    &.mobile-hidden {
      transform: translateX(-280px);
    }
  }

  .search-box {
    padding: $spacing-md;
    border-bottom: 1px solid var(--border-color);
    background: var(--card-bg);
    position: relative;

    &::before {
      content: "\f002";
      font-family: "Font Awesome 5 Free";
      font-weight: 900;
      position: absolute;
      left: 24px;
      top: 50%;
      transform: translateY(-50%);
      color: var(--text-secondary);
      font-size: 0.9em;
      opacity: 0.7;
    }

    input {
      width: 100%;
      padding: $spacing-sm $spacing-xl $spacing-sm 42px;
      border: 1px solid var(--border-color);
      border-radius: 24px;
      background: var(--input-bg);
      color: var(--text-primary);
      font-size: 0.9em;
      transition: all 0.3s ease;

      &:focus {
        outline: none;
        border-color: $primary;
        box-shadow: 0 0 0 3px rgba($primary, 0.1);

        & + .search-icon {
          color: $primary;
        }
      }

      &::placeholder {
        color: var(--text-secondary);
        opacity: 0.7;
      }
    }
  }

  .chat-list {
    flex: 1;
    overflow-y: auto;
    padding: $spacing-sm 0;
  }

  .friend-item {
    padding: $spacing-md;
    display: flex;
    align-items: center;
    gap: $spacing-md;
    margin: $spacing-sm $spacing-xs;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover,
    &.active {
      background: var(--hover-bg);
    }

    .avatar {
      width: 42px;
      height: 42px;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
      }
    }

    .friend-info {
      .name {
        font-size: 0.95em;
        font-weight: 500;
        color: var(--text-primary);
      }
    }
  }
}

.chat-item {
  padding: $spacing-md;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: $spacing-md;
  margin: 0 $spacing-xs;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: var(--hover-bg);
  }

  &.active {
    background: var(--hover-bg);
  }

  .avatar {
    width: 42px;
    height: 42px;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      border-radius: 12px;
      object-fit: cover;
    }
  }

  .chat-info {
    min-width: 0; // 确保文本可以正确截断

    .name {
      margin-bottom: 4px;
      font-size: 0.95em;
      font-weight: 500;
      color: var(--text-primary);
    }

    .last-message {
      font-size: 0.85em;
      color: var(--text-secondary);
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      width: 170px;
    }
  }

  .meta {
    flex-shrink: 0;

    .time {
      font-size: 0.8em;
      color: var(--text-secondary);
    }
  }
}

.empty-tip {
  padding: $spacing-lg;
  text-align: center;
  color: var(--text-secondary);
  font-size: 0.9em;
}

.chat-main {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--card-bg);
  border-left: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;

  @media screen and (max-width: 768px) {
    grid-column: 2;
    padding-top: 60px; // 为header留出空间
    height: 100vh; // 设置全屏高度
    position: fixed; // 固定定位
    right: 0; // 固定在右侧
    left: 80px; // 留出左侧导航栏的空间
  }

  .chat-header {
    height: 70px;
    flex-shrink: 0;
    padding: $spacing-md $spacing-lg;
    border-bottom: 1px solid var(--border-color);
    display: flex;
    align-items: center;
    background: var(--card-bg);
    z-index: 10;

    .user-info {
      display: flex;
      align-items: center;
      gap: $spacing-md;
      width: 100%;

      h3 {
        font-size: 1.1em;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
      }

      .toggle-list-btn {
        display: none;
        align-items: center;
        justify-content: center;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: var(--hover-bg);
        cursor: pointer;
        margin-right: $spacing-sm;

        @media screen and (max-width: 768px) {
          display: flex;
        }

        &:hover {
          background: var(--border-color);
        }

        i {
          color: var(--text-primary);
          font-size: 1.1em;
        }
      }
    }
  }

  .messages {
    flex: 1;
    overflow-y: auto;
    padding: $spacing-md $spacing-lg;
    background: var(--bg);
    display: flex;
    flex-direction: column;
    gap: $spacing-md;

    .message {
      display: flex;
      gap: $spacing-sm;
      align-items: flex-start;

      &.message-self {
        flex-direction: row-reverse;

        .message-content {
          align-items: flex-end;

          .message-text {
            background: $primary;
            color: white;
            border-radius: 16px 16px 4px 16px;
          }

          .message-header {
            flex-direction: row-reverse;
          }

          .sender-name {
            text-align: right;
            user-select: none;
          }
        }
      }

      .avatar {
        width: 36px;
        height: 36px;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          border-radius: 50%;
          object-fit: cover;
        }
      }

      .message-content {
        display: flex;
        flex-direction: column;
        gap: 4px;
        max-width: 70%;

        .message-header {
          display: flex;
          align-items: center;
          gap: $spacing-sm;

          .sender-name {
            font-size: 0.85em;
            user-select: none;
            color: var(--text-secondary);
            .user-name {
              color: red;
            }
            .assistant-name {
              color: #b32ce9;
            }
          }
        }

        .message-text {
          background: var(--hover-bg);
          padding: $spacing-sm $spacing-md;
          border-radius: 16px 16px 16px 4px;
          color: var(--text-primary);
          word-break: break-word;

          :deep(mention) {
            color: rgb(84, 214, 231);
            font-weight: 500;
            cursor: pointer;

            &:hover {
              text-decoration: underline;
            }
          }
        }
      }
    }
  }

  .chat-input {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: $spacing-md;
    border-top: 1px solid var(--border-color);
    background: var(--card-bg);
    display: flex;
    gap: $spacing-md;
    z-index: 10;

    @media screen and (max-width: 768px) {
      position: fixed; // 固定定位
      left: 80px; // 与左侧导航栏对齐
    }

    .input-toolbar {
      display: flex;
      gap: $spacing-sm;
      align-items: center;

      :deep(.emoji-picker i) {
        color: #f1c40f !important;
      }
      .image-upload-btn {
        color: $primary;
      }
      .file-upload-btn {
        color: #2daba5;
      }
      .voice-toggle-btn {
        color: #e67e22;
      }
      .toolbar-btn {
        padding: $spacing-sm;
        border: none;
        background: none;
        cursor: pointer;
        border-radius: 4px;

        &:hover {
          background: var(--hover-bg);
        }
      }
    }
    .input-area {
      flex: 1;
      display: flex;
      gap: $spacing-sm;
    }

    textarea {
      flex: 1;
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: $spacing-md;
      resize: none;
      height: 60px;
      background: var(--input-bg);
      color: var(--text-primary);

      &:focus {
        outline: none;
        border-color: $primary;
      }
    }

    button {
      width: 60px;
      border: none;
      border-radius: 8px;
      background: $primary;
      color: white;
      cursor: pointer;
      transition: all 0.3s ease;
      font-size: 1em;

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
  }
}

.message-text {
  background: var(--hover-bg);
  padding: $spacing-sm $spacing-md;
  border-radius: 12px 12px 12px 4px;
  color: var(--text-primary);
  max-width: 600px;

  // 添加深度选择器来处理v-html内容的样式
  :deep(mention) {
    color: rgb(84, 214, 231) !important;
    font-weight: 500;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

.no-chat-selected {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);

  i {
    font-size: 4em;
    margin-bottom: $spacing-md;
  }
}

.message-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;

  &:hover {
    transform: scale(1.05);
  }
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-actions {
  opacity: 0;
  transition: opacity 0.2s;
  padding: 4px;
  cursor: pointer;
  color: var(--text-secondary);

  &:hover {
    color: var(--text-primary);
  }
}

.message:hover .message-actions {
  opacity: 1;
}

.message-recalled {
  text-align: center;
  padding: $spacing-md;
  margin: 0 auto;

  span {
    background: var(--hover-bg);
    color: var(--text-secondary);
    font-size: 0.9em;
    padding: $spacing-sm $spacing-md;
    border-radius: 16px;
    display: inline-block;
  }
}

.message-time {
  margin-left: $spacing-sm;
  color: var(--text-secondary);
  &::after {
    content: '·';
  }
}

.message-actions-menu {
  position: fixed;
  background: var(--card-bg);
  border-radius: 8px;
  box-shadow: $shadow-lg;
  z-index: 1000;
  padding: $spacing-sm;
  min-width: 120px;

  .action-item {
    padding: $spacing-sm $spacing-md;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    color: var(--text-primary);

    &:hover {
      background: var(--hover-bg);
    }

    .fa-reply {
      color: #be1fde;
    }
    .fa-at {
      color: #0ab028;
    }
    .fa-download {
      color: #2daba5;
    }
    .fa-undo {
      color: #f5222d;
    }
    .fa-search {
      color: #1890ff;
    }
  }
}

.loading-messages {
  text-align: center;
  padding: $spacing-md;
  color: var(--text-secondary);
  font-size: 0.9em;

  i {
    margin-right: $spacing-sm;
  }
}

.location {
  font-size: 12px;
  color: var(--text-secondary);
}

.author-tag {
  background: $primary;
  color: white;
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 4px;
  margin-left: 4px;
}

.assistant-tag {
  background: #b32ce9;
  color: white;
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 4px;
  margin-left: 4px;
}

// 对于自己发送的消息，修改@样式的颜色
.message-self {
  .message-text {
    :deep(mention) {
      color: white;
      font-weight: 600;
    }
  }
}

// 添加遮罩层样式
.mobile-overlay {
  display: none;

  @media screen and (max-width: 768px) {
    display: block;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 140;
  }
}

.friend-details {
  padding: $spacing-lg;
  border-radius: 12px;
  margin: $spacing-lg auto;
  max-width: 600px;
  text-align: center;

  h3 {
    font-size: 1.5em;
    font-weight: 700;
    color: var(--text-primary);
    margin-bottom: $spacing-md;
  }

  .signature,
  .gender {
    font-size: 1em;
    color: var(--text-secondary);
    margin-bottom: $spacing-md;
  }
}

.message-file {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  background: var(--hover-bg);
  padding: $spacing-sm $spacing-md;
  border-radius: 8px;
  color: var(--text-primary);
  cursor: pointer;
  max-width: 500px;

  i {
    font-size: 1.5em;
    color: #3498db;
  }

  .file-info {
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;

    a {
      text-decoration: none;
      color: var(--text-primary);
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .file-size {
      font-size: 0.85em;
      color: var(--text-secondary);
    }
  }
}

.chat-input {
  .voice-toggle-btn {
    color: #e67e22;
  }
  .voice-record-btn {
    width: 500px !important;

    border: 1px solid var(--border-color);
    border-radius: 8px;
    background: var(--input-bg);
    color: var(--text-primary);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1em;
    transition: background 0.3s ease;

    &:hover,
    &:active,
    &.recording {
      background: #e74c3c;
      color: white;
    }
  }
}

.message-audio {
  display: flex;
  align-items: center;
  background: #e6f7ff;
  padding: 8px 12px;
  border-radius: 16px;
  cursor: pointer;
  max-width: 250px;
  position: relative;
  overflow: hidden;
  margin: 5px 0;

  .audio-bubble {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #333;

    i {
      font-size: 1.2em;
      color: #1890ff;
    }

    span {
      font-size: 0.85em;
      color: #555;
    }
  }

  &::after {
    content: "";
    position: absolute;
    left: -6px;
    top: 50%;
    transform: translateY(-50%);
    border: 6px solid transparent;
    border-right-color: #e6f7ff;
  }
}

.reply-message {
  background: var(--reply-message-bg); // 使用 CSS 变量
  padding: 2px 3px;
  border-radius: 8px;
  color: var(--text-secondary);
  cursor: pointer;

  .reply-content {
    font-size: 0.85em;
    color: var(--text-primary);
  }
}

.reply-preview {
  background: var(--hover-bg);
  padding: $spacing-sm $spacing-md;
  border-radius: 8px;
  color: var(--text-secondary);
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-sm;
  position: absolute; /* 绝对定位 */
  bottom: 100%; /* 放在输入框上方 */
  left: 50%; /* 水平居中 */
  transform: translateX(-50%); /* 水平居中调整 */
  width: calc(100% - 2 * $spacing-md); /* 与输入框宽度一致 */

  i {
    cursor: pointer;
    color: var(--text-primary);
  }
}

:deep(ol) {
  padding-left: $spacing-md !important;
}

:deep(ul) {
  padding-left: $spacing-md !important;
}

:deep(pre) {
    margin: 1em 0;
    position: relative;
    background: #282c34;
    border-radius: 6px;
    padding-top: 2.5em;
    overflow: auto;
    max-height: 500px;


    /* 调整代码内容的样式 */
    code {
      display: block;
      padding: 1em;
      padding-left: 1em;
      /* 增加左侧padding */
      margin-left: 0;
      /* 移除margin */
      overflow-x: auto;
      font-family: 'Fira Code', monospace;
      font-size: 14px;
      line-height: 1.5;
      position: relative;
    }

    /* 添加仿 macOS 风格的按钮 */
    &::before {
      content: '';
      position: absolute;
      top: 12px;
      left: 12px;
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background: #ff5f56;
      box-shadow:
        20px 0 0 #ffbd2e,
        40px 0 0 #27c93f;
    }

    /* 复制按钮容器 */
    .code-header {
      position: absolute;
      top: 8px;
      right: 12px;
      z-index: 2;
      opacity: 0;
      transition: opacity 0.2s ease;
    }

    /* 显示复制按钮 */
    &:hover .code-header {
      opacity: 1;
    }

    /* 复制按钮样式 */
    .copy-button {
      padding: 4px 8px;
      background: rgba(255, 255, 255, 0.1);
      border: none;
      border-radius: 4px;
      color: #abb2bf;
      cursor: pointer;
      font-size: 14px;
      transition: all 0.2s ease;
      display: flex;
      align-items: center;
      gap: 4px;

      i {
        font-size: 14px;
      }

      &:hover {
        background: rgba(255, 255, 255, 0.2);
        color: #fff;
      }

      &.copied {
        background: #98c379;
        color: #fff;
      }
    }
  }

.message {
    // existing styles...
    .active-message {
        background-color: rgb(222 22 22 / 55%) !important; // 你可以根据需要调整颜色
        transition: background-color 0.2s ease !important;
    }
}
</style>
