<template>
  <footer class="site-footer">
    <div class="footer-content">
      <div class="footer-info">
        <div class="running-time">
          <span class="icon">⏱</span>
          本站居然运行了 <span class="time-value">{{ days }}</span> 天
          <span class="time-value">{{ hours }}</span> 时
          <span class="time-value">{{ minutes }}</span> 分
          <span class="time-value">{{ seconds }}</span> 秒
        </div>
        <div class="copyright">
          Copyright©{{ startYear }}-{{ currentYear }} {{ $store.state.webSiteInfo.name }}
          <span class="divider">·</span>
          <a href="https://beian.miit.gov.cn/" 
             target="_blank" 
             rel="noopener"
             class="record">
             {{$store.state.webSiteInfo.recordNum}}
          </a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'TheFooter',
  data() {
    return {
      startYear: 2021,
      days: 0,
      hours: 0,
      minutes: 0,
      seconds: 0,
      timer: null,
      startDate: new Date('2021-08-31')
    }
  },
  computed: {
    currentYear() {
      return new Date().getFullYear()
    }
  },
  methods: {
    calculateRunningTime() {
      const now = new Date()
      const diff = now - this.startDate
      
      this.days = Math.floor(diff / (1000 * 60 * 60 * 24))
      this.hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      this.minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      this.seconds = Math.floor((diff % (1000 * 60)) / 1000)
    }
  },
  mounted() {
    this.calculateRunningTime()
    this.timer = setInterval(this.calculateRunningTime, 1000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  }
}
</script>

<style lang="scss" scoped>
.site-footer {
  background: var(--card-bg);
  padding: $spacing-lg 0;
  margin-top: auto;
  border-top: 1px solid rgba(125, 125, 125, 0.1);
}

.footer-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 $spacing-xl;
}

.footer-info {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.running-time {
  font-size: 0.875rem;
  color: var(--text-secondary);
  
  .time-value {
    color: #ff6b81;
    font-family: 'Fira Code', monospace;
  }
  
  .icon {
    color: #4CAF50;
    margin-right: 4px;
  }
}

.copyright {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  flex-wrap: wrap;
  font-size: 0.875rem;
  color: var(--text-secondary);



  .record {
    color: inherit;
    text-decoration: none;
    transition: color 0.2s ease;
    
    &:hover {
      color: $primary;
    }
  }
}

@include responsive(sm) {
  .site-footer {
    padding: $spacing-md 0;
  }

  .footer-content {
    padding: 0 $spacing-md;
  }

  .copyright {
    font-size: 0.8125rem;
    gap: $spacing-xs;
  }
  
  .running-time {
    font-size: 0.8125rem;
  }
}
</style> 
