<template>
  <div class="not-found">
    <div class="not-found-content">
      <h1>404</h1>
      <p>抱歉，页面不见了 (；′⌒`)</p>
      <router-link to="/" class="back-home">
        <i class="fas fa-home"></i>
        返回首页
      </router-link>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.not-found {
  min-height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: $spacing-xl;
  background: var(--background);
}

.not-found-content {
  max-width: 600px;
  width: 100%;
  animation: fadeInUp 0.6s ease-out;

  h1 {
    font-size: 120px;
    color: $primary;
    margin-bottom: $spacing-lg;
    font-weight: 700;
    line-height: 1;
    text-shadow: 4px 4px 0px rgba($primary, 0.2);
  }

  p {
    font-size: 1.5em;
    color: var(--text-secondary);
    margin-bottom: $spacing-xl;
  }

  .back-home {
    display: inline-flex;
    align-items: center;
    gap: $spacing-sm;
    padding: $spacing-md $spacing-xl;
    background: $primary;
    color: white;
    border-radius: 25px;
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba($primary, 0.3);
    }

    i {
      font-size: 1.1em;
    }
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

@include responsive(sm) {
  .not-found-content {
    h1 {
      font-size: 80px;
    }
    
    p {
      font-size: 1.2em;
    }
  }
}
</style> 