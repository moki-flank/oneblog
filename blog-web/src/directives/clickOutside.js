export default {
  bind(el, binding) {
    el.clickOutsideEvent = function(event) {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value(event)
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unbind(el) {
    document.removeEventListener('click', el.clickOutsideEvent)
  }
} 