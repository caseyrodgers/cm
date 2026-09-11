#!/bin/sh
# Stop ONLY the local cm_re dev server — never a blanket `taskkill /IM
# java.exe` (that also kills Eclipse and other JVM tools).
#
# 1. Ask it to stop via POST /api/dev/shutdown (mounted only when CM_DEV
#    is set — `make run` / `make api` / `make debug` set it).
# 2. Failing that, kill only the PID listening on PORT.
#
#   PORT   port to stop (default 5173)

PORT="${PORT:-5173}"

port_free() {
  ! curl -sf -o /dev/null --max-time 1 "http://localhost:${PORT}/api/health" 2>/dev/null
}

wait_free() {
  for _ in $(seq 1 30); do
    port_free && { echo ":${PORT} is free"; return 0; }
    sleep 0.2
  done
  echo "warning: something is still answering on :${PORT}"
  return 1
}

if port_free; then
  echo "nothing listening on :${PORT}"
  exit 0
fi

if curl -fsS -X POST "http://localhost:${PORT}/api/dev/shutdown" >/dev/null 2>&1; then
  echo "asked the server on :${PORT} to shut down"
  wait_free
  exit $?
fi

# netstat -ano lines look like:  TCP  0.0.0.0:5173  0.0.0.0:0  LISTENING  12345
pids=$(netstat -ano 2>/dev/null \
  | grep -E "[:.]${PORT}[[:space:]].*LISTENING" \
  | awk '{print $NF}' | grep -E '^[0-9]+$' | sort -u)

for pid in $pids; do
  echo "killing PID ${pid} (listening on :${PORT})"
  taskkill //F //PID "$pid" >/dev/null 2>&1 \
    || taskkill /F /PID "$pid" >/dev/null 2>&1 \
    || kill -9 "$pid" 2>/dev/null
done

wait_free
