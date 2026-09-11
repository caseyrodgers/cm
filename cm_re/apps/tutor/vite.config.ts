import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { VitePWA } from "vite-plugin-pwa";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  // `npm run dev` (or `make tutor-dev`) — instant HMR for CSS/TSX, full
  // reload for anything HMR can't apply (e.g. index.html), no
  // `make build` step. On its own port so it can run alongside the
  // Java dev server (:5173) — /api/* is proxied there so Learn, the AI
  // chapter-name call, etc. still work. `public/**` (module JSON, the
  // subject index) is served as-is, no build needed for those either.
  // The PWA service worker is NOT registered in this mode (vite-plugin-
  // pwa only activates it in a real build), so there's no stale-cache
  // gotcha here — a plain refresh always shows the latest.
  server: {
    port: 5175,
    proxy: { "/api": "http://localhost:5173" },
  },
  plugins: [
    react(),
    tailwindcss(),
    VitePWA({
      registerType: "autoUpdate",
      includeAssets: ["icon-192.png", "icon-512.png"],
      manifest: {
        name: "Catchup Math Tutor",
        short_name: "CM Tutor",
        start_url: "/",
        display: "standalone",
        background_color: "#ffffff",
        theme_color: "#1A99D6",
        icons: [
          { src: "icon-192.png", sizes: "192x192", type: "image/png" },
          { src: "icon-512.png", sizes: "512x512", type: "image/png" },
        ],
      },
      // Precache the app shell so it loads with no network at all.
      // Actual per-subject content modules are a separate cache
      // (offline/moduleManager.ts + db.ts), not part of this precache.
      workbox: {
        globPatterns: ["**/*.{js,css,html,png,svg}"],
      },
    }),
  ],
});
