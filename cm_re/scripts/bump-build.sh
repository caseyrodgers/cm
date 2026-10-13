#!/bin/sh
# Increment the BUILD_NUMBER in apps/tutor/src/buildNumber.ts
FILE="apps/tutor/src/buildNumber.ts"
N=$(sed -n 's/.*BUILD_NUMBER = \([0-9]*\).*/\1/p' "$FILE")
N=$((N + 1))
printf 'export const BUILD_NUMBER = %d;\n' "$N" > "$FILE"
echo "build #$N"
