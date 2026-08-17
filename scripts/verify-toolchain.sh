#!/usr/bin/env bash
set -euo pipefail

jbr_candidates=()
if [[ -n "${JAVA_HOME:-}" ]]; then
  jbr_candidates+=("$JAVA_HOME")
fi
jbr_candidates+=(
  "/Applications/Android Studio.app/Contents/jbr/Contents/Home"
  "/Applications/Android Studio.app/Contents/jbr"
  "$HOME/Applications/Android Studio.app/Contents/jbr/Contents/Home"
  "$HOME/Applications/Android Studio.app/Contents/jbr"
)

java_home=""
for candidate in "${jbr_candidates[@]}"; do
  if [[ -x "$candidate/bin/java" ]]; then
    java_home="$candidate"
    break
  fi
done

if [[ -z "$java_home" ]]; then
  echo "ERROR: Android Studio bundled JBR was not found. Set JAVA_HOME to its home directory (for example /Applications/Android Studio.app/Contents/jbr/Contents/Home)." >&2
  exit 1
fi

java_version_line="$("$java_home/bin/java" -version 2>&1 | head -n 1)"
java_major="$(sed -E -n 's/.*version "([0-9]+).*/\1/p' <<<"$java_version_line")"
if [[ -z "$java_major" || "$java_major" -lt 17 ]]; then
  echo "ERROR: Java 17 or newer is required; selected $java_home reports: $java_version_line" >&2
  exit 1
fi

android_sdk_root="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/Library/Android/sdk}}"
if [[ ! -d "$android_sdk_root/platforms/android-36" ]]; then
  echo "ERROR: Android SDK platform android-36 is required at $android_sdk_root/platforms/android-36." >&2
  exit 1
fi

printf 'Java home: %s\n' "$java_home"
printf 'Java runtime: %s\n' "$java_version_line"
printf 'Android SDK: %s\n' "$android_sdk_root"
printf 'Android platform: android-36\n'
