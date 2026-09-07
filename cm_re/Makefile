.PHONY: build run debug dist serve serve-legacy kill-python api editor editor-dev

# JDWP address for `make debug`. suspend=y makes the JVM wait for a
# debugger before running main() (good for breakpoints in startup);
# set JDWP_SUSPEND=n to start immediately and attach whenever.
JDWP_PORT    ?= 5005
JDWP_SUSPEND ?= y

LEGACY_SOLUTIONS_DIR := c:/dev/projects/catchupmath/temp/help_2022_06/solutions

# Real Vite + vite-plugin-pwa build: app shell, manifest with real
# icons, generated service worker (dist/sw.js) via Workbox generateSW.
build:
	cd apps/tutor && npm install && npm run build

# Builds the tutor, then serves it + the REST API from the
# dependency-free Java dev server (apps/backend, TutorServer) instead of
# python -m http.server. One origin on :5173 so the app's relative
# fetches and the service worker work with no CORS. Plain HTTP is fine
# on localhost; set CM_KEYSTORE / CM_KEYSTORE_PASS (PKCS12) to also get
# HTTPS on :5174.
#
# Each recipe line is its own shell, so the `cd` on the first line does
# not carry to the second — the java paths are relative to cm_re/.
# mvn exec:java runs TutorServer with the full Maven classpath (it uses
# Gson), so no classpath assembly here. TutorServer finds apps/tutor/dist
# by walking up from the working directory.
run: build
	cd apps/backend && mvn -q compile exec:java -Dexec.mainClass=com.catchupmath.cmre.server.TutorServer

# Same as `run` but with a JDWP debug socket on JDWP_PORT (default 5005).
# With JDWP_SUSPEND=y the JVM waits for a debugger to attach before
# running. Attach from Eclipse: Debug Configurations -> "TutorServer
# (attach 5005)". See DEBUG.org.  (MAVEN_OPTS puts the agent on the JVM
# that mvn+exec:java share.)
debug: build
	cd apps/backend && MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=$(JDWP_SUSPEND),address=localhost:$(JDWP_PORT)" mvn -q compile exec:java -Dexec.mainClass=com.catchupmath.cmre.server.TutorServer

# Assemble the committed, runnable distribution into cm_re/deploy/
# (deploy/cm_re-server.jar + deploy/web/ + deploy/web/editor/). Run
# this on a machine with Node, commit deploy/, push. The server then
# just `git pull`s.
dist: build editor
	cd apps/backend && mvn -q -DskipTests package
	@echo "cm_re/deploy/ updated -> git add deploy && commit && push; then 'git pull' + 'make serve' on the server"

# Run from a committed deploy/ on a box with only a JRE (no Node, no
# Maven). Override the port:  make serve PORT=9000
# Serves tutor + editor (one instance). On an internet-facing box set
# CM_NO_EDITOR=1 to drop /editor/ + /api/editor/* until editor auth
# exists (the editor API has unauthenticated write endpoints).
PORT ?= 8080
serve:
	java -jar deploy/cm_re-server.jar $(PORT) deploy/web

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

# --- solution editor (apps/editor) — online-only authoring UI ---
# ONE server, two APIs, two UIs (all in one Java instance):
#   /          tutor            /api/          tutor API
#   /editor/   solution editor  /api/editor/   editor CRUD API
#
#   make editor    # build apps/editor/dist
#   make api       # the one server (prints the /editor/ URL)
#
# The editor mounts automatically when apps/editor/dist and a modules/
# content root are present. It has write endpoints and no auth yet, so
# set CM_NO_EDITOR=1 on any internet-facing instance to suppress it.
#
# `api` is `run` without the tutor rebuild: it ensures apps/tutor/dist
# exists once (TutorServer needs a web root), then starts the server via
# mvn exec:java (full classpath for Gson).
editor:
	cd apps/editor && npm install && npm run build

api:
	cd apps/tutor && [ -d dist ] || (npm install && npm run build)
	cd apps/backend && mvn -q compile exec:java -Dexec.mainClass=com.catchupmath.cmre.server.TutorServer

# Only for actively hacking the editor UI: Vite dev server (HMR) on
# :5174, proxying /api to a separately-running `make api` on :5173.
# Not needed just to view the editor.
editor-dev:
	cd apps/editor && npm install && npm run dev
