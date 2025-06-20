#!/usr/bin/env bash
set -euo pipefail

# This script searches for imports from the rubenlagus TelegramBots library
# within the repository and stores the result in CSV format.
# Output: report/rubenlagus-usage.csv with columns file;line;import

project_root="$(cd "$(dirname "$0")/.." && pwd)"
output_file="${project_root}/report/rubenlagus-usage.csv"

cd "$project_root"
mkdir -p "$(dirname "$output_file")"

# Use git grep to search tracked Java files for the specific import pattern.
git grep -nE "^import org\.telegram\.telegrambots\..*" -- '*.java' |
  awk -F: '{print $1 ";" $2 ";" $3}' > "$output_file"

printf "Report generated at %s\n" "$output_file"
