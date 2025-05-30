<template>
  <div class="profile-container">
    <!-- 左侧固定导航 -->
    <div class="profile-sidebar" role="complementary">
      <!-- 用户信息卡片 -->
      <el-card class="user-card">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="triggerUpload" role="button" tabindex="0" @keydown.enter="triggerUpload"
            aria-label="更换头像">
            <el-avatar :size="100" :src="userInfo.avatar" alt="用户头像"></el-avatar>
            <div class="upload-overlay" inert>
              <i class="el-icon-camera"></i>
            </div>
          </div>
          <input type="file" ref="fileInput" class="hidden-input" accept="image/*" @change="handleAvatarChange"
            aria-label="上传头像">
        </div>
        <h3 class="username">{{ userInfo.nickname }}</h3>
        <p class="bio">{{ userInfo.bio || '这个人很懒，还没有写简介...' }}</p>
        
        <!-- 添加签到按钮 -->
        <div class="sign-in-section">
          <el-button 
            type="primary" 
            :disabled="signInStatus"
            @click="handleSignIn"
            size="small"
            :loading="signInLoading"
          >
            <i class="el-icon-check"></i>
            {{ signInStatus ? '今日已签到' : '立即签到' }}
          </el-button>
          <div class="sign-in-stats">
            <div class="stat-item">
              <span class="label">连续签到</span>
              <span class="value">{{ signInStats.continuousDays }}天</span>
            </div>
            <div class="stat-item">
              <span class="label">累计签到</span>
              <span class="value">{{ signInStats.totalDays }}天</span>
            </div>
          </div>
        </div>

        <div class="user-stats" role="list">
          <div class="stat-item" role="listitem">
            <span class="number">{{ statistics.posts }}</span>
            <span class="label">文章</span>
          </div>
          <div class="stat-item" role="listitem">
            <span class="number">{{ statistics.followers }}</span>
            <span class="label">关注者</span>
          </div>
          <div class="stat-item" role="listitem">
            <span class="number">{{ statistics.likes }}</span>
            <span class="label">获赞</span>
          </div>
        </div>
      </el-card>

      <!-- 导航菜单 -->
      <el-menu class="nav-menu" :default-active="currentTab" @select="currentTab = $event" role="navigation">
        <el-menu-item v-for="tab in tabs" :key="tab.key" :index="tab.key" :aria-label="tab.label">
          <i :class="tab.icon"></i>
          <span>{{ tab.label }}</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧内容区 -->
    <main class="content-area" role="main">
      <!-- 个人资料 -->
      <div v-if="currentTab === 'profile'" class="content-section">
        <h2 class="section-title">个人资料</h2>
        <el-form ref="profileForm" :model="profileForm" :rules="profileRules" label-width="80px" class="profile-form"
          @submit.prevent="submitProfile">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="profileForm.nickname" placeholder="请输入昵称" aria-label="昵称输入框"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="profileForm.email" placeholder="请输入邮箱" aria-label="邮箱输入框"></el-input>
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input type="textarea" v-model="profileForm.bio" :rows="4" placeholder="介绍一下自己吧..."></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="profileForm.sex">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="0">女</el-radio>
              <el-radio :label="2">保密</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitProfile" :loading="loading" icon="el-icon-edit"
              size="small">保存修改</el-button>
            <el-button size="small" @click="resetProfile" icon="el-icon-refresh">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 账号绑定 -->
      <div v-if="currentTab === 'binding'" cla1110ss="content-section">
        <h2 class="section-title">账号绑定</h2>
        <div class="binding-tips">
          <el-alert title="账号绑定提示" type="info" description="绑定第三方账号后，您可以直接使用第三方账号登录本站，还可以同步您的个人信息。" show-icon
            :closable="false">
          </el-alert>
        </div>
        <div class="binding-list">
          <el-card v-for="account in boundAccounts" :key="account.type" class="binding-item">
            <div class="binding-content">
              <div class="account-info">
                <div class="account-icon">
                  <i :class="account.icon" :style="{ color: account.color }"></i>
                </div>
                <div class="account-details">
                  <span class="account-name">{{ account.name }}</span>
                  <span class="account-desc">{{ account.isBound ? account.username : '未绑定账号' }}</span>
                </div>
              </div>
              <div class="binding-status">
                <el-tag :type="account.isBound ? 'success' : 'info'" size="small" effect="dark" class="status-tag">
                  {{ account.isBound ? '已绑定' : '未绑定' }}
                </el-tag>
                <el-button :type="account.isBound ? 'danger' : 'primary'" size="small"
                  :icon="account.isBound ? 'el-icon-close' : 'el-icon-link'"
                  @click="account.isBound ? unbindAccount(account.type) : bindAccount(account.type)">
                  {{ account.isBound ? '解除绑定' : '立即绑定' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </div>

      </div>

      <!-- 我的文章 -->
      <div v-if="currentTab === 'posts'" class="content-section">
        <h2 class="section-title">我的文章</h2>
        <div class="action-bar">
          <div>
            <el-input v-model="params.title" size="mini" placeholder="输入文字标题搜索文章..." prefix-icon="el-icon-search"
              style="width: 300px;margin-right: 10px"></el-input>
            <el-button type="primary" size="mini" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          </div>

          <el-button type="primary" icon="el-icon-edit" size="mini" @click="$router.push('/editor')">写文章</el-button>
        </div>

        <div v-loading="loading" v-if="posts.length">
          <el-card v-for="post in posts" :key="post.id" class="post-item">
            <div class="post-content">
              <h3 class="post-title" @click="viewPost(post.id)">{{ post.title }}</h3>
              <p class="post-excerpt">{{ post.summary }}</p>
              <div class="post-meta">
                <el-tag size="small"><i class="el-icon-date"></i>{{ post.createTime }}</el-tag>
                <el-tag size="small" type="info"><i class="el-icon-view"></i>{{ post.quantity }} 阅读</el-tag>
                <el-tag size="small" type="success"><i class="el-icon-chat-line-square"></i>{{ post.commentNum || 0 }}
                  评论</el-tag>
                <el-tag size="small" type="warning"><i class="el-icon-star-off"></i>{{ post.likeNum || 0 }} 点赞</el-tag>
              </div>
            </div>
            <div class="post-actions">
              <el-button type="text" icon="el-icon-view" @click="viewPost(post.id)">查看</el-button>
              <el-button type="text" icon="el-icon-edit" @click="editPost(post.id)">编辑</el-button>
              <el-button type="text" icon="el-icon-delete" class="delete" @click="deletePost(post)">删除</el-button>
            </div>
          </el-card>

          <div class="pagination-box">
            <el-pagination background @current-change="handlePostChange" :current-page="params.pageNum"
              :page-size="params.pageSize" :total="total" layout="prev, pager, next" class="pagination"></el-pagination>
          </div>
        </div>
        <el-empty v-else description="暂无文章，快去发布你的文章吧~~"></el-empty>
      </div>

      <!-- 我的评论 -->
      <div v-if="currentTab === 'comments'" class="content-section">
        <h2 class="section-title">我的评论</h2>
        <div v-if="myComments.length" v-loading="loading">
          <el-card v-for="comment in myComments" :key="comment.id" class="comment-item">
            <div class="comment-actions">
              <p class="comment-text" v-html="parseContent(comment.content)"></p>
              <el-button type="text" icon="el-icon-delete" class="delete"
                @click="deleteComment(comment.id)">删除</el-button>
            </div>
            <div class="comment-meta">
              <el-link type="primary" @click="viewPost(comment.articleId)">文章：{{ comment.articleTitle }}</el-link>
              <el-tag size="small">
                <i class="el-icon-time"></i>
                {{ comment.createTime }}
              </el-tag>
              <el-tag size="small" type="success"><i class="el-icon-star-off"></i>{{ comment.likeCount ?
                comment.likeCount : 0 }} 赞</el-tag>
            </div>
          </el-card>
          <div class="pagination-box">
            <el-pagination background v-if="myComments.length" @current-change="handlePageChange"
              :current-page="params.pageNum" :page-size="params.pageSize" layout="prev, pager, next" :total="total">
            </el-pagination>
          </div>
        </div>
        <el-empty v-else description="暂无评论数据"></el-empty>
      </div>

      <!-- 我的回复 -->
      <div v-if="currentTab === 'replies'" class="content-section">
        <h2 class="section-title">我的回复</h2>
        <div v-if="myReplies.length" v-loading="loading">
          <el-card v-for="reply in myReplies" :key="reply.id" class="reply-item">
            <div class="reply-content">
              <div class="comment-actions">
                <p class="reply-text">
                  <el-tag size="small" type="info">回复 @{{ reply.replyNickname }}</el-tag>
                <p v-html="parseContent(reply.content)"></p>
                </p>
                <el-button type="text" icon="el-icon-delete" class="delete"
                  @click="deleteReply(reply.id)">删除</el-button>
              </div>
              <div class="reply-meta">
                <el-link type="primary" @click="viewPost(reply.articleId)">文章：{{ reply.articleTitle }}</el-link>
                <el-tag size="small">
                  <i class="el-icon-time"></i>
                  {{ reply.createTime }}
                </el-tag>
              </div>
            </div>
          </el-card>

          <div class="pagination-box">
            <el-pagination background v-if="myReplies.length" @current-change="handleReplyPageChange"
              :current-page="params.pageNum" :page-size="params.pageSize" layout="prev, pager, next" :total="total">
            </el-pagination>
          </div>
        </div>

        <el-empty v-else description="暂无回复评论数据"></el-empty>

      </div>

      <!-- 我的点赞 -->
      <div v-if="currentTab === 'likes'" class="content-section">
        <h2 class="section-title">我的点赞</h2>
        <div v-if="myLikes.length" v-loading="loading">
          <el-card v-for="like in myLikes" :key="like.id" class="like-item">
            <div class="like-content">
              <div class="comment-actions">
                <el-link class="article-title" @click="viewPost(like.id)">{{ like.title }}</el-link>
                <el-button type="text" icon="el-icon-star-off" class="delete"
                  @click="cancelLike(like.id)">取消点赞</el-button>
              </div>
              <div class="like-meta">
                <!-- <el-tag size="small"><i class="el-icon-user"></i>{{ like.author }}</el-tag> -->
                <el-tag size="small">
                  <i class="el-icon-time"></i>
                  {{ like.createTime }}
                </el-tag>
              </div>
            </div>
          </el-card>
          <div class="pagination-box">
            <el-pagination background v-if="myLikes.length" @current-change="handleLikePageChange"
              :current-page="params.pageNum" :page-size="params.pageSize" layout="prev, pager, next" :total="total">
            </el-pagination>
          </div>
        </div>

        <el-empty v-else description="暂无点赞数据"></el-empty>
      </div>

      <!-- 修改密码 -->
      <div v-if="currentTab === 'security'" class="content-section">
        <h2 class="section-title">修改密码</h2>

        <div class="binding-tips">
          <el-alert title="修改密码提示" type="info" description="只有邮箱登录的才可修改密码，其他第三方登录不存在修改密码功能。" show-icon
            :closable="false">
          </el-alert>
        </div>
        <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="100px"
          class="security-form">
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input type="password" v-model="passwordForm.oldPassword" show-password placeholder="请输入当前密码"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input type="password" v-model="passwordForm.newPassword" show-password placeholder="请输入新密码"></el-input>
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input type="password" v-model="passwordForm.confirmPassword" show-password
              placeholder="请再次输入新密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button size="small" type="primary" @click="submitPasswordChange" icon="el-icon-edit"
              :loading="loading">确认修改</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 反馈 -->
      <div v-if="currentTab === 'feedback'" class="content-section">
        <h2 class="section-title">意见反馈</h2>
        <el-tabs>
          <el-tab-pane label="提交反馈">
            <el-form ref="feedbackForm" :model="feedbackForm" :rules="feedbackRules" label-width="100px"
              class="feedback-form">
              <el-form-item label="反馈类型" prop="type">
                <el-select v-model="feedbackForm.type" placeholder="请选择反馈类型">
                  <el-option v-for="item in feedbackTypes" :label="item.label" :value="item.value"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="反馈内容" prop="content">
                <el-input type="textarea" v-model="feedbackForm.content" :rows="5"
                  placeholder="请详细描述您的问题或建议..."></el-input>
              </el-form-item>
              <el-form-item label="联系邮箱" prop="email">
                <el-input v-model="feedbackForm.email" placeholder="请留下您的联系邮箱，方便我们回复您"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitFeedback" icon="el-icon-check"
                  :loading="loading">提交反馈</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="我的反馈">
            <div class="feedback-list">
              <div v-loading="loading" v-if="myFeedbacks.length">
                <el-card v-for="feedback in myFeedbacks" :key="feedback.id" class="feedback-item">
                  <div class="feedback-header">
                    <div class="feedback-info">
                      <el-tag v-if="item.value === feedback.type" v-for="item in feedbackTypes" :type="item.style">
                        {{ item.label }}
                      </el-tag>
                      <span class="feedback-time">
                        <i class="far fa-clock"></i>
                        {{ feedback.createTime }}
                      </span>
                    </div>
                    <el-tag v-if="item.value === String(feedback.status)" v-for="item in feedbackStatus"
                      :type="item.style">
                      {{ item.label }}
                    </el-tag>
                  </div>
                  <div class="feedback-content">
                    <p>{{ feedback.content }}</p>
                  </div>
                  <div class="feedback-reply" v-if="feedback.replyContent">
                    <div class="reply-title">
                      <i class="el-icon-chat-line-round"></i> 管理员回复：
                    </div>
                    <p class="reply-content">{{ feedback.replyContent }}</p>
                  </div>
                </el-card>
                <div class="pagination-box" v-if="myFeedbacks.length">
                  <el-pagination background @current-change="handleFeedbackPageChange" :current-page="params.pageNum"
                    :page-size="params.pageSize" layout="prev, pager, next" :total="total">
                  </el-pagination>
                </div>
              </div>
              <el-empty v-else description="暂无反馈记录"></el-empty>


            </div>
          </el-tab-pane>
        </el-tabs>
      </div>


    </main>
  </div>
</template>

<script>
import {
  getUserInfoApi, updateProfileApi, updatePasswordApi,
  getMyCommentApi, delMyCommentApi, getMyLikeApi, getMyReplyApi, getMyFeedbackApi, addFeedbackApi,
  signInApi, getSignInStatusApi, getSignInStatsApi
} from '@/api/user'
import { uploadFileApi } from '@/api/file'
import { getMyArticleApi, likeArticleApi, delArticleApi } from '@/api/article'
import { getDictDataApi } from '@/api/dict'

import { marked } from "marked";
export default {
  name: 'Profile',
  data() {
    // 密码确认验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      userInfo: {},
      editForm: {
        username: '',
        email: '',
        bio: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      statistics: {
        posts: 0,
        likes: 0,
        followers: 0
      },
      currentTab: 'profile',
      tabs: [
        { key: 'profile', label: '个人资料', icon: 'fas fa-user' },
        { key: 'binding', label: '账号绑定', icon: 'fas fa-link' },
        { key: 'posts', label: '我的文章', icon: 'fas fa-file-alt' },
        { key: 'comments', label: '我的评论', icon: 'fas fa-comments' },
        { key: 'replies', label: '我的回复', icon: 'fas fa-reply' },
        { key: 'likes', label: '我的点赞', icon: 'fas fa-heart' },
        { key: 'security', label: '修改密码', icon: 'fas fa-lock' },
        { key: 'feedback', label: '反馈', icon: 'fas fa-comment-dots' }
      ],
      boundAccounts: [
        {
          type: 'wechat',
          name: '微信公众号',
          icon: 'fab fa-weixin',
          isBound: true,
          username: 'wx_user123',
          color: '#10b981'
        },
        {
          type: 'qq',
          name: 'QQ',
          icon: 'fab fa-qq',
          isBound: false,
          color: '#60a5fa'
        },
        {
          type: 'gitee',
          name: '码云',
          icon: 'fab fa-git-alt',
          isBound: true,
          username: 'github_user',
          color: '#FF0000'
        }
      ],

      posts: [],
      myComments: [],
      myReplies: [],
      myLikes: [],
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      // 个人资料表单
      profileForm: {
        nickname: '',
        email: '',
        sex: 2,
      },
      profileRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      params: {
        pageNum: 1,
        pageSize: 10,
        title: '',
      },
      total: 0,
      loading: false,
      // 添加反馈列表相关数据
      feedbackTypes: [],
      feedbackStatus: [],
      feedbackForm: {
        type: '',
        content: '',
        contact: ''
      },
      myFeedbacks: [],
      feedbackRules: {
        type: [
          { required: true, message: '请选择反馈类型', trigger: 'blur' },
        ],
        content: [
          { required: true, message: '请输入反馈内容', trigger: 'blur' },
        ],
        email: [
          { required: false, message: '请输入联系邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
        ],
      },
      signInStatus: false,
      signInStats: {
        continuousDays: 0,
        totalDays: 0
      },
      signInLoading: false,
    }
  },

  watch: {
    '$store.state.userInfo': {
      handler(newVal, oldVal) {
        if (!newVal) {
          this.$router.push('/')
        }
      },
      deep: true
    },
    currentTab(newVal, oldVal) {
      switch (newVal) {
        case 'posts':
          this.params.pageNum = 1
          this.getMyArticle()
          break
        case 'comments':
          this.params.pageNum = 1
          this.getMyComment()
          break
        case 'replies':
          this.params.pageNum = 1
          this.getMyReplies()
          break
        case 'likes':
          this.params.pageNum = 1
          this.getMyLikes()
          break
        case 'feedback':
          this.params.pageNum = 1
          this.getMyFeedbacks()
          break
        default:
          break
      }
    },
  },
  created() {
    getUserInfoApi().then(res => {
      this.userInfo = res.data.sysUser
      Object.assign(this.profileForm, res.data.sysUser)
    })

    this.getFeedbackDict()
    // 获取签到状态和统计
    this.getSignInStatus()
    this.getSignInStats()
  },
  methods: {
    /**
     * 获取反馈类型字典
     */
    getFeedbackDict() {
      getDictDataApi(['feedback_type', 'feedback_status']).then(res => {
        this.feedbackTypes = res.data.feedback_type.list
        this.feedbackStatus = res.data.feedback_status.list
      })
    },
    /**
     * 获取我的评论
     */
    getMyComment() {
      this.loading = true
      getMyCommentApi(this.params).then(res => {
        this.myComments = res.data.records
        this.total = res.data.total
      }).finally(() => {
        this.loading = false
      })
    },
    /**
     * 解析评论内容
     */
    parseContent(content) {
      return marked(content || "");
    },
    /**
     * 分页
     * @param val
     */
    handlePageChange(val) {
      this.params.pageNum = val
      this.getMyComment()
    },

    triggerUpload() {
      this.$refs.fileInput.click()
    },
    /**
     * 修改头像
     * @param e
     */
    async handleAvatarChange(e) {
      const file = e.target.files[0]
      if (!file) return

      // 这里可以添加文件类型和大小检查
      if (!file.type.includes('image/')) {
        alert('请上传图片文件')
        return
      }

      // 创建 FormData 对象
      const formData = new FormData()
      formData.append('file', file)

      // 调用上传接口
      const response = await uploadFileApi(formData,'avatar')

      // 发送图片消息
      if (response.data) {
        updateProfileApi({ id: this.profileForm.id, avatar: response.data }).then(res => {
          this.$message.success('头像更新成功')
          this.userInfo.avatar = response.data
          this.$store.state.userInfo.avatar = response.data
        }).catch(err => {
          this.$message.error(err.message || '头像更新失败')
        })
      } else {
        alert('上传失败')
      }
    },

    /**
     * 获取我的文章
     */
    getMyArticle() {
      this.loading = true
      getMyArticleApi(this.params).then(res => {
        this.posts = res.data.records
        this.total = res.data.total
      }).finally(() => {
        this.loading = false
      })
    },

    /**
     * 跳转文章详情
     * @param id
     */
    viewPost(id) {
      this.$router.push(`/post/${id}`)
    },
    /**
     * 编辑文章
     * @param id
     */
    editPost(id) {
      this.$router.push(`/editor?id=${id}`)
    },
    /**
     * 删除文章
     * @param id
     */
    deletePost(row) {
      this.$confirm(`确定要删除标题为 '${row.title}' 的文章吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delArticleApi(row.id).then(res => {
          this.$message.success('删除成功')
          this.getMyArticle()
        }).catch(err => {
          this.$message.error(err.message || '删除失败')
        })
      }).catch(() => { })
    },
    /**
     * 搜索文章
     * @param id
     */
    handleSearch() {
      this.params.pageNum = 1
      this.getMyArticle()
    },
    /**
     * 分页
     * @param val
     */
    handlePostChange(page) {
      this.params.pageNum = page
      this.getMyArticle()
    },
    /**
     * 绑定账号
     * @param type
     */
    bindAccount(type) {
      // 模拟绑定过程
      const account = this.boundAccounts.find(acc => acc.type === type)
      if (account) {
        this.$confirm('确定要绑定该账号吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          account.isBound = true
          this.$message.success('绑定成功')
        }).catch(() => { })
      }
    },
    unbindAccount(type) {
      const account = this.boundAccounts.find(acc => acc.type === type)
      if (account) {
        this.$confirm('确定要解除绑定吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          account.isBound = false
          this.$message.success('已解除绑定')
        }).catch(() => { })
      }
    },
    /**
     * 删除评论
     */
    deleteComment(id) {
      this.$confirm('此操作会把该评论下的子评论也一并删除，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delMyCommentApi(id).then(res => {
          this.$message.success('删除成功')
          this.getMyComment()
        })
      }).catch(() => { })
    },
    /**
     * 删除回复
     */
    deleteReply(id) {
      this.$confirm('确定要删除这条回复吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delMyCommentApi(id).then(res => {
          this.$message.success('删除成功')
          this.getMyReplies()
        })
      }).catch(() => { })
    },
    /**
     * 取消点赞
     * @param id
     */
    cancelLike(id) {
      this.$confirm('确定要取消点赞吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        likeArticleApi(id).then(res => {
          this.$message.success('已取消点赞')
          this.getMyLikes()
        })
      }).catch(() => { })
    },

    /**
     * 添加反馈
     */
    submitFeedback() {
      this.$refs.feedbackForm.validate(valid => {
        if (valid) {
          this.loading = true
          addFeedbackApi(this.feedbackForm).then(res => {
            this.$message.success('感谢您的反馈！')
            this.feedbackForm = { ...{} }
          }).finally(() => {
            this.loading = false
          })
        }
      })

    },
    // 提交密码修改
    submitPasswordChange() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          this.loading = true
          updatePasswordApi(this.passwordForm).then(res => {
            this.$message.success('密码修改成功！')
            this.$refs.passwordForm.resetFields()
          }).catch(err => {
            this.$message.error(err.message)
          }).finally(() => {
            this.loading = false

          })
        }
      })
    },
    // 提交个人资料
    submitProfile() {
      this.loading = true
      updateProfileApi(this.profileForm).then(res => {
        this.userInfo.nickname = this.profileForm.nickname
        this.$store.state.userInfo.nickname = this.profileForm.nickname
        this.$message.success('个人资料更新成功！')
      }).catch(err => {
        this.$message.error(err.message)
      }).finally(() => {
        this.loading = false
      })
    },
    // 重置个人资料
    resetProfile() {
      this.profileForm = { ...{} }
    },
    /**
     * 获取我的回复
     */
    getMyReplies() {
      this.loading = true
      getMyReplyApi(this.params).then(res => {
        this.myReplies = res.data.records
        this.total = res.data.total
      }).finally(() => {
        this.loading = false
      })
    },
    /**
     * 回复分页
     */
    handleReplyPageChange(val) {
      this.params.pageNum = val
      this.getMyReplies()
    },
    /**
     * 获取我的点赞
     */
    getMyLikes() {
      this.loading = true
      getMyLikeApi(this.params).then(res => {
        this.myLikes = res.data.records
        this.total = res.data.total
      }).finally(() => {
        this.loading = false
      })
    },
    /**
     * 点赞分页
     */
    handleLikePageChange(val) {
      this.params.pageNum = val
      this.getMyLikes()
    },
    /**
     * 获取我的反馈列表
     */
    getMyFeedbacks() {
      this.loading = true
      let params = {
        ...this.params,
        source: 'PC'
      }
      getMyFeedbackApi(params).then(res => {
        this.myFeedbacks = res.data.records
        this.total = res.data.total
      }).finally(() => {
        this.loading = false
      })
    },

    /**
     * 反馈列表分页
     */
    handleFeedbackPageChange(val) {
      this.params.pageNum = val
      this.getMyFeedbacks()
    },

    handleCoverUpload(e) {
      const file = e.target.files[0]
      if (!file) return

      const formData = new FormData()
      formData.append('file', file)

      uploadFileApi(formData).then(res => {
        if (res.code === 200) {
          this.articleForm.cover = res.data.url  // 使用返回的 url
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
     * 获取签到状态
     */
    getSignInStatus() {
      getSignInStatusApi().then(res => {
        this.signInStatus = res.data
      })
    },

    /**
     * 获取签到统计
     */
    getSignInStats() {
      getSignInStatsApi().then(res => {
        this.signInStats = res.data
      })
    },

    /**
     * 签到
     */
    handleSignIn() {
      if (this.signInStatus.hasSignedIn) return
      
      this.signInLoading = true
      signInApi().then(res => {
        this.$message.success('签到成功！')
        this.getSignInStatus()
        this.getSignInStats()
      }).catch(err => {
        this.$message.error(err.message || '签到失败')
      }).finally(() => {
        this.signInLoading = false
      })
    },
  }
}
</script>

<style scoped lang="scss">
:deep(input[aria-hidden=true]) {
  display: none !important;
}

.delete {
  color: red;
}

.profile-container {
  display: flex;
  gap: 20px;
  padding: 20px;
  min-height: 100vh;
}

.profile-sidebar {
  position: sticky;
  top: 80px;
  height: fit-content;
  width: 300px;
  flex-shrink: 0;


  @include responsive(sm) {
    position: unset;

    .el-dialog {
      width: 95% !important;
    }
  }
}



.user-card {
  text-align: center;
  background: var(--card-bg);
  border: var(--border-color);

  .avatar-section {
    margin-bottom: 16px;
  }

  .avatar-wrapper {
    width: 100px;
    height: 100px;
    margin: 0 auto;
    position: relative;
    cursor: pointer;
    border-radius: 50%;
    overflow: hidden;

    .upload-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;

      i {
        font-size: 24px;
        color: white;
      }
    }

    &:hover .upload-overlay {
      opacity: 1;
    }
  }

  .username {
    font-size: 18px;
    font-weight: 600;
    margin: 0 0 8px;
    color: var(--text-primary);
  }

  .bio {
    color: var(--text-secondary);
    font-size: 14px;
    margin: 0 0 16px;
    line-height: 1.5;
  }

  .user-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    padding-top: 16px;
    border-top: 1px solid var(--border-color);

    .stat-item {
      .number {
        display: block;
        font-size: 18px;
        font-weight: 600;
        color: var(--primary-color);
      }

      .label {
        font-size: 12px;
        color: var(--text-secondary);
      }
    }
  }
}
.el-menu-item{
  color: var(--text-secondary) !important;
}
.nav-menu {
  border-radius: 8px;
  background: var(--card-bg);
  border-right: none;
  margin-top: $spacing-md;


  .is-active {
    background: var(--hover-bg);
    color: $primary;
  }

  :deep(.el-menu-item) {
    height: 48px;
    line-height: 48px;

    &:hover {
      background: var(--hover-bg);
      color: $primary;
    }

    i {
      margin-right: 12px;
    }
  }
}

.content-area {
  flex: 1;
  min-width: 0;
  background: var(--card-bg);
  border-radius: 12px;
  padding: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-primary);
}

.profile-form,
.security-form,
.feedback-form {
  max-width: 600px;
}

.post-item {
  margin-bottom: 16px;

  .post-content {
    margin-bottom: 16px;
  }

  .post-title {
    font-size: 18px;
    color: var(--text-secondary);
    margin: 0 0 12px;
    cursor: pointer;

    &:hover {
      color: $primary;
    }
  }

  .post-excerpt {
    color: var(--text-secondary);
    margin: 0 0 12px;
    line-height: 1.5;
  }

  .post-meta {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;

    .el-tag {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .post-actions {
    display: flex;
    gap: 16px;
    justify-content: flex-end;
    padding-top: 16px;
    border-top: 1px solid var(--border-color);
  }
}

.comment-item,
.reply-item,
.like-item {
  margin-bottom: 16px;

  .comment-actions,
  .reply-actions,
  .like-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .article-title {
      font-size: 18px;
      font-weight: 700;
    }
  }

  .comment-text,
  .reply-text {
    color: var(--text-secondary);
    margin: 0 0 12px;
    line-height: 1.5;

    :deep(img) {
      max-width: 200px !important;
      max-height: 200px !important;
    }
  }

  .comment-meta,
  .reply-meta,
  .like-meta {
    display: flex;
    gap: 12px;
    align-items: center;
  }
}



.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.binding-item {
  margin-bottom: 16px;

  .account-info {
    display: flex;
    align-items: center;
    gap: 16px;

    i {
      font-size: 24px;
      color: var(--primary-color);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
  }

  .content-area {
    padding: 16px;
  }

  .action-bar {
    .el-input {
      width: 100% !important;
    }
  }
}

.hidden-input {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.binding-item,
.comment-item,
.reply-item,
.like-item,
.post-item {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
}


.binding-tips {
  margin-bottom: 24px;

  :deep(.el-alert) {
    border-radius: 8px;

    .el-alert__title {
      font-size: 15px;
      font-weight: 500;
    }

    .el-alert__description {
      margin: 8px 0 0;
      font-size: 13px;
      color: var(--text-secondary);
    }
  }
}

.binding-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;

  .binding-item {
    .binding-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 8px;
    }

    .account-info {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .account-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--hover-bg);
      transition: all 0.3s ease;

      i {
        font-size: 24px;
        color: var(--primary-color);
      }
    }

    .account-details {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .account-name {
      font-size: 16px;
      font-weight: 500;
      color: var(--text-primary);
    }

    .account-desc {
      font-size: 13px;
      color: var(--text-secondary);
    }

    .binding-status {
      display: flex;
      align-items: center;
      gap: 16px;

      .status-tag {
        min-width: 64px;
        text-align: center;
      }
    }

    &:hover {
      .account-icon {
        transform: scale(1.05);
        background: var(--primary-color);

        i {
          color: $primary;
        }
      }
    }
  }
}

.feedback-list {
  .feedback-item {
    margin-bottom: 16px;

    .feedback-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .feedback-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .feedback-time {
          display: flex;
          align-items: center;
          gap: 5px;
          color: var(--text-secondary);
          font-size: 14px;

        }
      }
    }

    .feedback-content {
      color: var(--text-primary);
      line-height: 1.6;
      margin-bottom: 16px;
    }

    .feedback-reply {
      background: var(--hover-bg);
      padding: 12px;
      border-radius: 8px;

      .reply-title {
        color: var(--text-secondary);
        font-weight: 500;
        margin-bottom: 8px;

        i {
          margin-right: 4px;
        }
      }

      .reply-content {
        color: var(--text-secondary);
        margin: 0;
        line-height: 1.6;
      }
    }
  }
}

.posts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .write-btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    border-radius: 4px;
    background: $primary;
    color: white;
    text-decoration: none;
    transition: all 0.3s ease;

    &:hover {
      background: darken($primary, 10%);
    }

    i {
      font-size: 14px;
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: var(--text-secondary);

  i {
    font-size: 48px;
    margin-bottom: 16px;
  }

  p {
    margin-bottom: 24px;
  }

  .btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 8px 24px;
    border-radius: 4px;
    background: var(--hover-bg);
    color: var(--text-primary);
    text-decoration: none;
    transition: all 0.3s ease;

    &:hover {
      background: var(--hover-bg-dark);
    }

    &.primary {
      background: $primary;
      color: white;

      &:hover {
        background: darken($primary, 10%);
      }
    }
  }
}

.sign-in-section {
  padding: 16px 0;
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
  margin: 16px 0;
  
  .sign-in-stats {
    display: flex;
    justify-content: center;
    gap: 24px;
    margin-top: 16px;
    
    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
      
      .label {
        font-size: 12px;
        color: var(--text-secondary);
      }
      
      .value {
        font-size: 16px;
        font-weight: 600;
        color: var(--primary-color);
      }
    }
  }
}
</style>
