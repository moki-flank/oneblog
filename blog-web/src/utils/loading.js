import Vue from 'vue'
import Loading from '@/components/common/Loading.vue'

let loadingInstance = null

export const showLoading = () => {
  if (!loadingInstance) {
    const LoadingConstructor = Vue.extend(Loading)
    loadingInstance = new LoadingConstructor()
    loadingInstance.$mount()
    document.body.appendChild(loadingInstance.$el)
  }
  loadingInstance.visible = true
}

export const hideLoading = () => {
  if (loadingInstance) {
    loadingInstance.visible = false
  }
} 