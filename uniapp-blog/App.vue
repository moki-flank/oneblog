<script>
export default {
  onLaunch: function() {
    console.log('App Launch')
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* 注意：这里需要引入 theme.scss */
@import "uview-ui/theme.scss";
/* 引入uView基础样式 */
@import "uview-ui/index.scss";
</style> 