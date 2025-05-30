import Vue from 'vue'
import Loading from '../components/common/Loading.vue'

const LoadingConstructor = Vue.extend(Loading)

export default {
  bind(el, binding) {
    // 设置父元素定位，以便loading可以相对定位
    const position = getComputedStyle(el).position
    if (position === 'static' || position === '') {
      el.style.position = 'relative'
    }
    
    // 确保父元素有明确的高度
    if (el.style.minHeight === '') {
      el.style.minHeight = '100px'
    }
    
    const loading = new LoadingConstructor({
      propsData: {
        local: true // 默认使用局部加载模式
      }
    })
    loading.$mount()
    el.instance = loading
    el.appendChild(loading.$el)
    
    if (binding.value) {
      el.instance.visible = true
    }
  },
  
  update(el, binding) {
    if (binding.value !== binding.oldValue) {
      el.instance.visible = binding.value
    }
  },
  
  unbind(el) {
    if (el.instance) {
      el.removeChild(el.instance.$el)
      el.instance.$destroy()
      el.instance = null
    }
  }
} 