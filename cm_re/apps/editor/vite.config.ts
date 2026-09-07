import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// The editor is online-only (no PWA / service worker — see
// NEW_DIRECTION.org "Frontend platform"). It's served by the ONE Java
// server (TutorServer) at /editor/ — hence base:"/editor/" so built
// asset URLs resolve there. Its /api/editor/* calls are same-origin,
// no proxy needed.
//
// `make editor` builds it; `CM_EDITOR=1 make api` serves everything.
//
// `make editor-dev` runs the Vite dev server below (HMR) on :5174,
// proxying /api to the Java server on :5173 — only for actively
// hacking the editor UI.
export default defineConfig({
  base: "/editor/",
  plugins: [react()],
  server: {
    port: 5174,
    proxy: {
      "/api": "http://localhost:5173",
    },
  },
});
