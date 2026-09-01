.PHONY: build run serve-legacy

LEGACY_SOLUTIONS_DIR := c:/dev/projects/catchupmath/temp/help_2022_06/solutions

# Real Vite + vite-plugin-pwa build: app shell, manifest with real
# icons, generated service worker (dist/sw.js) via Workbox generateSW.
build:
	cd apps/tutor && npm install && npm run build

# Serves the built PWA. Plain HTTP is fine here specifically because
# it's localhost — browsers exempt localhost from the "PWA needs
# HTTPS" requirement. Anywhere else this would need real TLS.
run: build
	cd apps/tutor/dist && python -m http.server 5173

# Dev-only: serves the real legacy solution export tree as-is (images,
# alternate tutor_steps*.html, JSON sidecars) so SolutionNav's "View
# source directory" link can point at real, clickable files instead of
# a hand-maintained listing. --directory avoids needing to cd into a
# path outside this repo. Not part of `run` — only needed when you're
# actually cross-checking a converted solution against its original.
serve-legacy:
	python -m http.server 8090 --directory "$(LEGACY_SOLUTIONS_DIR)"

kill-python:
	taskkill /F /IM python.exe
