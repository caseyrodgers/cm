#!/bin/sh
# Run the cm_re server from this committed deploy/ directory.
# Needs only a JRE 17+ (no Node, no Maven). Update with `git pull`.
#
#   PORT              listen port (default 5173, same as `make run`)
#   ANTHROPIC_API_KEY set to enable live "Learn" explanations
#   ANTHROPIC_MODEL   optional, default claude-haiku-4-5-20251001
#   CM_KEYSTORE / CM_KEYSTORE_PASS  PKCS12 -> also serve HTTPS on PORT+1
#   CM_NO_EDITOR=1    drop /editor/ + /api/editor/* — SET THIS on any
#                     internet-facing box: the editor API has
#                     unauthenticated write endpoints (no auth yet).
#   CM_DEV           dev only — mounts POST /api/dev/shutdown (stops the
#                     JVM). Do NOT set this on production; leave unset.
#
# It's a PWA: off-localhost it needs real TLS. Either put nginx/Caddy in
# front, or point CM_KEYSTORE at a real-cert PKCS12.
set -e
cd "$(dirname "$0")"
exec java -jar cm_re-server.jar "${PORT:-5173}" web
