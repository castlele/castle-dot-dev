#!/usr/bin/env bash
set -uo pipefail

input=$(cat)
session=$(jq -r '.session_id' <<<"$input")
stop_hook_active=$(jq -r '.stop_hook_active // false' <<<"$input")
list="${TMPDIR:-/tmp}/claude-prettier-$session"

[ -s "$list" ] || exit 0

root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
cd "$root" || exit 0

files=()
while read -r f; do
  [ -f "$f" ] && files+=("$f")
done < <(sort -u "$list")

if [ ${#files[@]} -eq 0 ]; then
  rm -f "$list"
  exit 0
fi

if output=$(npx prettier --write --ignore-unknown "${files[@]}" 2>&1); then
  rm -f "$list"
  exit 0
fi

[ "$stop_hook_active" = "true" ] && exit 0

{
  echo "prettier could not format the edited files; fix the errors below:"
  echo "$output" | tail -30
} >&2
exit 2
