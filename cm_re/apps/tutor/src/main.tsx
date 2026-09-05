import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { registerSW } from "virtual:pwa-register";
import "./styles.css";
import "./widgets";

// autoUpdate: new service worker versions activate automatically on next
// load, no user-facing "update available" prompt (fine for a first pass;
// revisit if silent updates ever surprise a student mid-solution).
registerSW({ immediate: true });

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
