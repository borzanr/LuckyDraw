#!/usr/bin/env bash
# Regera os assets da Play Store a partir dos HTMLs em src/.
# Uso: bash playstore/render.sh
set -euo pipefail

CHROME="${CHROME:-/c/Program Files (x86)/Google/Chrome/Application/chrome.exe}"
[ -f "$CHROME" ] || CHROME="/c/Program Files/Google/Chrome/Application/chrome.exe"
[ -f "$CHROME" ] || { echo "Chrome não encontrado; defina CHROME=caminho"; exit 1; }

HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# O Chrome no Windows precisa de file:// com caminho absoluto no formato dele.
WIN_DIR="$(cd "$HERE" && pwd -W 2>/dev/null || echo "$HERE")"
URL="file:///${WIN_DIR}/src"
OUT="$(echo "$WIN_DIR" | tr '/' '\\')"

shot() { # shot <url> <arquivo-saida> <largura> <altura>
  "$CHROME" --headless=new --disable-gpu --hide-scrollbars \
    --force-device-scale-factor=1 --virtual-time-budget=3000 \
    --screenshot="$2" --window-size="$3,$4" "$1" > /dev/null 2>&1
  echo "gerado: $2"
}

shot "$URL/icon.html" "${OUT}\\icon-512.png" 512 512

# O ícone é global na Play; o gráfico de destaque é por idioma da ficha.
shot "$URL/feature.html"         "${OUT}\\feature-graphic-1024x500.png"    1024 500
shot "$URL/feature.html?lang=en" "${OUT}\\feature-graphic-1024x500-en.png" 1024 500
shot "$URL/feature.html?lang=es" "${OUT}\\feature-graphic-1024x500-es.png" 1024 500

# Um conjunto por idioma: src/shots (pt-BR), src/shots-en, src/shots-es
for n in 1 2 3 4 5; do
  shot "$URL/shot.html?n=$n"         "${OUT}\\screenshots\\0$n.png"    1080 1920
  shot "$URL/shot.html?n=$n&lang=en" "${OUT}\\screenshots-en\\0$n.png" 1080 1920
  # O conjunto em espanhol só sai depois de capturar os prints em src/shots-es.
  if [ -f "$HERE/src/shots-es/numbers.png" ]; then
    shot "$URL/shot.html?n=$n&lang=es" "${OUT}\\screenshots-es\\0$n.png" 1080 1920
  fi
done
