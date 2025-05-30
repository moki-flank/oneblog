import { defineConfig } from 'vite'
import vue2 from '@vitejs/plugin-vue2'
import path from 'path'
import { loadEnv } from 'vite'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'

export default defineConfig(({ command, mode }) => {

    const env = loadEnv(mode, process.cwd());
    return {
        server: {
            // 允许IP访问
            host: "0.0.0.0",
            // 应用端口 (默认:3000)
            port: Number(env.VITE_APP_PORT),
            // 运行是否自动打开浏览器
            open: false,
            proxy: {
              /** 代理前缀为 /dev-api 的请求  */
              [env.VITE_APP_BASE_API]: {
                changeOrigin: true,
                // 接口地址
                target: env.VITE_APP_API_URL,
                rewrite: (path) =>
                  path.replace(new RegExp("^" + env.VITE_APP_BASE_API), ""),
              },
            },
        },
        plugins: [
          vue2(),
          createSvgIconsPlugin({
            // 指定需要缓存的图标文件夹
            iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
            // 指定symbolId格式
            symbolId: 'icon-[dir]-[name]',
          }),
        ],
        resolve: {
          alias: {
            '@': path.resolve(__dirname, './src')
          }
        },
        css: {
            preprocessorOptions: {
              scss: {
                api: 'modern-compiler',
                additionalData: `
                  @import "@/styles/variables.scss";
                  @import "@/styles/mixins.scss";
                  @import "@/styles/global.scss";
                  @import "@/styles/elmentui.scss";
                `
              }
            }
        }
    }
  
})
