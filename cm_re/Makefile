.PHONY: build run debug serve-legacy kill-python

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
