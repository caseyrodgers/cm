#!/bin/sh
# Increment BUILD_NUMBER in tutor and editor
bump() {
  local FILE="$1" LABEL="$2"
  N=$(sed -n 's/.*BUILD_NUMBER = \([0-9]*\).*/\1/p' "$FILE")
  N=$((N + 1))
  printf 'export const BUILD_NUMBER = %d;\n' "$N" > "$FILE"
  echo "$LABEL build #$N"
}

bump "apps/tutor/src/buildNumber.ts" "tutor"
bump "apps/editor/src/buildNumber.ts" "editor"
