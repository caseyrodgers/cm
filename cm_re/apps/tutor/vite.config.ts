import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { VitePWA } from "vite-plugin-pwa";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
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
