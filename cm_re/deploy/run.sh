#!/bin/sh
# Run the cm_re server from this committed deploy/ directory.
# Needs only a JRE 17+ (no Node, no Maven). Update with `git pull`.
#
#   PORT              listen port (default 8080)
#   ANTHROPIC_API_KEY set to enable live "Learn" explanations
#   ANTHROPIC_MODEL   optional, default claude-haiku-4-5-20251001
#   CM_KEYSTORE / CM_KEYSTORE_PASS  PKCS12 -> also serve HTTPS on PORT+1
#
# It's a PWA: off-localhost it needs real TLS. Either put nginx/Caddy in
# front, or point CM_KEYSTORE at a real-cert PKCS12.
set -e
cd "$(dirname "$0")"
exec java -jar cm_re-server.jar "${PORT:-8080}" web
