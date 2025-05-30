export const animateOnScroll = {
  mounted(el) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          el.classList.add('animate-in')
          observer.unobserve(el)
        }
      })
    }, {
      threshold: 0.1
    })

    observer.observe(el)
  }
} 