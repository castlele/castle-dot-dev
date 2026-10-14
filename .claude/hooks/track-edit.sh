#!/usr/bin/env bash
set -euo pipefail

input=$(cat)
session=$(jq -r '.session_id' <<<"$input")
file=$(jq -r '.tool_input.file_path // empty' <<<"$input")

case "$file" in
  *.ts | *.tsx | *.mjs | *.css | *.md | *.json) ;;
  *) exit 0 ;;
esac

root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
case "$file" in
  "$root"/*) file="${file#"$root"/}" ;;
  /*) exit 0 ;;
esac

echo "$file" >>"${TMPDIR:-/tmp}/claude-prettier-$session"
