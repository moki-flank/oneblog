<template>
  <canvas ref="fireworks" class="fireworks"></canvas>
</template>

<script>
export default {
  name: 'Fireworks',
  data() {
    return {
      canvas: null,
      ctx: null,
      particles: [],
      mouse: { x: 0, y: 0 }
    }
  },
  mounted() {
    this.initCanvas()
    window.addEventListener('click', this.createFireworks)
  },
  beforeDestroy() {
    window.removeEventListener('click', this.createFireworks)
  },
  methods: {
    initCanvas() {
      this.canvas = this.$refs.fireworks
      this.ctx = this.canvas.getContext('2d')
      this.canvas.width = window.innerWidth
      this.canvas.height = window.innerHeight
      window.addEventListener('resize', () => {
        this.canvas.width = window.innerWidth
        this.canvas.height = window.innerHeight
      })
    },
    createFireworks(e) {
      const x = e.clientX
      const y = e.clientY
      const particles = 20
      const colors = ['#ff0000', '#ffa500', '#ffff00', '#00ff00', '#00ffff', '#0000ff', '#ff00ff']

      for (let i = 0; i < particles; i++) {
        const angle = (Math.PI * 2 * i) / particles
        const velocity = 3 + Math.random() * 3
        const color = colors[Math.floor(Math.random() * colors.length)]

        this.particles.push({
          x,
          y,
          vx: Math.cos(angle) * velocity,
          vy: Math.sin(angle) * velocity,
          color,
          alpha: 1,
          life: 100,
          size: 8 + Math.random() * 8
        })
      }

      this.animate()
    },
    animate() {
      if (this.particles.length === 0) return

      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height)

      for (let i = this.particles.length - 1; i >= 0; i--) {
        const p = this.particles[i]
        p.x += p.vx
        p.y += p.vy
        p.vy += 0.1
        p.alpha -= 0.01
        p.life--

        if (p.x < 0 || p.x > this.canvas.width || p.y < 0 || p.y > this.canvas.height) {
          this.particles.splice(i, 1)
          continue
        }

        this.ctx.beginPath()
        this.ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2)
        this.ctx.fillStyle = `rgba(${this.hexToRgb(p.color)},${p.alpha})`
        this.ctx.fill()

        if (p.life <= 0 || p.alpha <= 0) {
          this.particles.splice(i, 1)
        }
      }

      if (this.particles.length > 0) {
        requestAnimationFrame(this.animate)
      }
    },
    hexToRgb(hex) {
      const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
      return result
        ? `${parseInt(result[1], 16)},${parseInt(result[2], 16)},${parseInt(result[3], 16)}`
        : '255,255,255'
    }
  }
}
</script>

<style scoped>
.fireworks {
  position: fixed;
  top: 0;
  left: 0;
  pointer-events: none;
  z-index: 9999;
}
</style> 