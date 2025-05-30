<template>
  <div class="about-page">
    <el-card>
      <div class="about-content" v-html="$store.state.webSiteInfo.aboutMe" ref="content">
      </div>
      <mj-image-preview ref="imagePreview" />
    </el-card>
  </div>
</template>

<script>

export default {
  name: 'About',

  mounted() {
    this.initImagePreview();
  },
  methods: {
    initImagePreview() {
      this.$nextTick(() => {
        setTimeout(() => {  
        const content = this.$refs.content;
        if (content) {
          const images = content.getElementsByTagName('img');
          Array.from(images).forEach(img => {
            img.style.cursor = 'zoom-in';
            img.addEventListener('click', () => {
              this.$refs.imagePreview.show(img.src);
            });
          });
        }
      }, 500); 
      });
    }
  }
}
</script>

<style lang="scss" scoped>

.about-page {
  max-width: 1200px;
  margin: 0 auto;
  margin-top: $spacing-lg;
  margin-bottom: $spacing-md;

  .about-content {
    line-height: 1.8;
    color: var(--text-primary);
    padding: $spacing-lg;

  }


}
@include responsive(sm) {
  :deep(img) {
      width: 100% !important;
  }
}
</style>
