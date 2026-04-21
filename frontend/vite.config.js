import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/auth': 'http://localhost:8080',
      '/users': 'http://localhost:8080',
      '/cache': 'http://localhost:8080',
      '/files': 'http://localhost:8080',
      '/notifications': 'http://localhost:8080',
    }
  },
  // Ensure env vars are defined even if .env is empty
  define: {
    'import.meta.env.VITE_API_BASE_URL': JSON.stringify('')
  }
})
