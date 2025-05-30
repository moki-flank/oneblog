<template>
  <div class="album-detail">
    <div v-if="isAuthenticated">
      <div class="album-header">
        <div class="header-content">
          <div class="back-button" @click="$router.back()">
            <i class="fas fa-arrow-left"></i>
            <span>返回</span>
          </div>
          <div class="album-info">
            <div class="album-meta">
              <span class="album-date">
                <i class="fas fa-calendar-alt"></i>
                {{ formatDate(album.createTime) }}
              </span>
              <span class="album-count">
                <i class="fas fa-image"></i>
                {{ photos.length }} 张照片
              </span>
            </div>
            <h1>{{ album.name }}</h1>
            <p class="album-description">{{ album.description }}</p>
            <div class="album-actions">
              <button class="action-btn share-btn">
                <i class="fas fa-share-alt"></i>
                分享相册
              </button>
              <button class="action-btn download-btn">
                <i class="fas fa-download"></i>
                下载全部
              </button>
            </div>
          </div>
        </div>
        <div class="header-background">
          <div class="gradient-overlay"></div>
          <div class="pattern-overlay"></div>
        </div>
      </div>

      <div class="photo-container">
        <div class="photo-grid" v-if="photos.length > 0">
          <div v-for="(photo,index) in photos" :key="photo.url" class="photo-item">
            <a href="javascript:;" class="photo-card" @click="previewImage(index)">
              <img v-lazy="photo.url" :key="photo.url" :alt="photo.description">
              <div class="photo-overlay">
                <div class="photo-info">
                  <h3 class="photo-description">{{ photo.description }}</h3>
                  <div class="photo-meta">
                    <span class="photo-date">
                      <i class="fas fa-calendar"></i> {{ photo.recordTime }}
                    </span>
                    <span class="photo-location" v-if="photo.location">
                      <i class="fas fa-map-marker-alt"></i> {{ photo.location }}
                    </span>
                  </div>
                </div>
              </div>
            </a>
          </div>
        </div>
        <div v-else class="no-photos">
          <el-empty description="暂无照片" />
        </div>
      </div>

      <!-- 添加图片预览组件 -->
      <mj-image-preview ref="imagePreview" />
    </div>
    <div v-else class="loading-container">
      <div class="loading-spinner">
        <i class="fas fa-spinner fa-spin"></i>
      </div>
    </div>

    <!-- 密码验证对话框 -->
    <album-password-dialog
      ref="passwordDialog"
      @submit="handlePasswordSubmit"
      @cancel="handlePasswordCancel"
    />
  </div>
</template>

<script>
import { getAlbumPhotosApi, verifyAlbumPasswordApi,getAlbumDetailApi } from '@/api/album'
import AlbumPasswordDialog from '@/components/AlbumPassword/index.vue'

export default {
  name: 'AlbumDetail',
  components: {
    AlbumPasswordDialog
  },
  data() {
    return {
      album: {
        id: this.$route.params.id,
        name: '',
        description: '',
        createTime: '',
        isLock: 0
      },
      photos: [],
      images: [],
      isAuthenticated: false
    }
  },
  mounted() {
    this.checkAlbumAuth()
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    /**
     * 预览图片
     */
    previewImage(index) {
      this.$refs.imagePreview.show(this.images, index)
    },
    /**
     * 检查相册权限
     */
    async checkAlbumAuth() {
      try {
        const res = await getAlbumDetailApi(this.album.id)
        this.album = res.data
        if (this.album.isLock === 1) {
          this.$refs.passwordDialog.show()
        }else{
          this.getAlbumPhotos()
        }
      } catch (error) {
        this.$message.error('获取相册信息失败')
        this.$router.push('/photos')
      }
    },
  
    /**
     * 获取相册图片
     */
    getAlbumPhotos() {
      getAlbumPhotosApi(this.album.id).then(res => {
        this.photos = res.data
        this.images = res.data.map(item => item.url)
      }).finally(() => {
        this.isAuthenticated = true
      })
    },
    /**
     * 处理密码验证
     */
    async handlePasswordSubmit(password, callback) {
      try {
        await verifyAlbumPasswordApi(this.album.id, password)
        this.getAlbumPhotos()
        // this.$refs.passwordDialog.visible = false
        callback()
      } catch (error) {
        this.$message.error(error.message || '密码错误')
        this.$refs.passwordDialog.loading = false
      }
    },
    /**
     * 处理取消密码验证
     */
    handlePasswordCancel() {
      this.$router.push('/photos')
    }
  }
}
</script>

<style lang="scss" scoped>
.album-header {
  position: relative;
  min-height: 400px;
  padding: 60px 20px;
  margin-bottom: 40px;
  overflow: hidden;
  display: flex;
  align-items: center;
  
  .header-background {
    position: absolute;
    inset: 0;
    z-index: 0;
    
    .gradient-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(
        135deg,
        rgba(30, 41, 59, 0.95) 0%,
        rgba(30, 41, 59, 0.8) 100%
      );
    }
    
    .pattern-overlay {
      position: absolute;
      inset: 0;
      opacity: 0.4;
      background-image: 
        linear-gradient(45deg, rgba(255,255,255,0.1) 25%, transparent 25%),
        linear-gradient(-45deg, rgba(255,255,255,0.1) 25%, transparent 25%),
        linear-gradient(45deg, transparent 75%, rgba(255,255,255,0.1) 75%),
        linear-gradient(-45deg, transparent 75%, rgba(255,255,255,0.1) 75%);
      background-size: 20px 20px;
      background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
      animation: patternMove 20s linear infinite;
    }
  }
  
  .header-content {
    max-width: 1200px;
    width: 100%;
    margin: 0 auto;
    position: relative;
    z-index: 1;
  }

  .back-button {
    position: absolute;
    left: 0;
    top: 0;
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 12px 24px;
    border-radius: 30px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    border: 1px solid rgba(255, 255, 255, 0.15);
    font-weight: 500;
    letter-spacing: 0.5px;

    &:hover {
      background: rgba(255, 255, 255, 0.15);
      transform: translateX(-5px);
      border-color: rgba(255, 255, 255, 0.25);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
    }

    i {
      font-size: 1.1em;
      transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    }

    &:hover i {
      transform: translateX(-4px);
    }
  }

  .album-info {
    text-align: center;
    padding: 40px 60px;
    animation: fadeInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1);
    
    .album-meta {
      display: flex;
      justify-content: center;
      gap: 20px;
      margin-bottom: 20px;
      
      span {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 0.95em;
        color: rgba(255, 255, 255, 0.8);
        padding: 6px 16px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 20px;
        backdrop-filter: blur(8px);
        
        i {
          color: #64B5F6;
          font-size: 1.1em;
        }
      }
    }

    h1 {
      font-size: 2.5em;
      margin: 0;
      font-weight: 800;
      letter-spacing: -1px;
      background: linear-gradient(120deg, 
        #ffffff 0%,
        #e0e0e0 50%,
        #ffffff 100%
      );
      background-size: 200% auto;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      animation: shine 3s linear infinite;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .album-description {
      margin: 20px auto;
      max-width: 700px;
      font-size: 1.2em;
      line-height: 1.6;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 400;
    }

    .album-actions {
      display: flex;
      justify-content: center;
      gap: 15px;
      margin-top: 30px;

      .action-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 24px;
        border-radius: 30px;
        border: none;
        font-size: 1em;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        background: transparent;
        color: white;
        border: 1px solid rgba(255, 255, 255, 0.2);

        i {
          font-size: 1.1em;
        }

        &:hover {
          background: rgba(255, 255, 255, 0.1);
          transform: translateY(-2px);
          border-color: rgba(255, 255, 255, 0.3);
          box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        &.share-btn {
          background: #64B5F6;
          border: none;

          &:hover {
            background: #42A5F5;
          }
        }
      }
    }
  }
}

.photo-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 40px;
  animation: fadeIn 0.8s ease-out;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
}

.photo-item {
  aspect-ratio: 1;
  animation: fadeInUp 0.6s ease-out backwards;

  @for $i from 1 through 20 {
    &:nth-child(#{$i}) {
      animation-delay: #{$i * 0.1}s;
    }
  }
}

.photo-card {
  height: 100%;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: block;
  
  &:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);

    img {
      transform: scale(1.1);
    }
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .photo-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to top, 
      rgba(0, 0, 0, 0.9) 0%,
      rgba(0, 0, 0, 0.5) 50%,
      transparent 100%);
    opacity: 0;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: 25px;
    color: white;

    .photo-info {
      transform: translateY(20px);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

      .photo-description {
        margin: 0 0 12px;
        font-size: 1.1em;
        font-weight: 500;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
      }

      .photo-meta {
        display: flex;
        gap: 15px;
        font-size: 0.9em;
        opacity: 0.9;

        span {
          display: flex;
          align-items: center;
          gap: 6px;

          i {
            color: #64B5F6;
          }
        }
      }
    }
  }

  &:hover .photo-overlay {
    opacity: 1;

    .photo-info {
      transform: translateY(0);
    }
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

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes patternMove {
  from {
    background-position: 0 0, 0 10px, 10px -10px, -10px 0px;
  }
  to {
    background-position: 40px 0, 40px 10px, 50px -10px, 30px 0px;
  }
}

@keyframes shine {
  to {
    background-position: 200% center;
  }
}

@media (max-width: 768px) {
  .album-header {
    min-height: 350px;
    padding: 40px 20px;

    .header-content {
      .back-button {
        padding: 8px 16px;
        font-size: 0.9em;
      }
    }

    .album-info {
      padding: 30px 20px;
      
      .album-meta {
        flex-wrap: wrap;
        gap: 10px;
        
        span {
          font-size: 0.9em;
          padding: 4px 12px;
        }
      }

      h1 {
        font-size: 2.5em;
      }

      .album-description {
        font-size: 1.1em;
        margin: 15px auto;
      }

      .album-actions {
        flex-direction: column;
        gap: 10px;
        padding: 0 20px;

        .action-btn {
          width: 100%;
          justify-content: center;
          padding: 10px 20px;
        }
      }
    }
  }

  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }

  .photo-card {
    .photo-overlay {
      opacity: 1;
      background: linear-gradient(to top, 
        rgba(0, 0, 0, 0.9) 0%,
        rgba(0, 0, 0, 0.6) 50%,
        rgba(0, 0, 0, 0.3) 100%);

      .photo-info {
        transform: translateY(0);
      }
    }
  }
}

.loading-container {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;

  .loading-spinner {
    font-size: 40px;
    color: var(--primary-color);
  }
}
</style> 