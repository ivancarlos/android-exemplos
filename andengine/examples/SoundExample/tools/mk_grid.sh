#!/usr/bin/env bash
set -euo pipefail

# ---- Parâmetros (mude se quiser) ----
W=${W:-512}             # largura da imagem
H=${H:-512}             # altura da imagem
CELL=${CELL:-32}        # tamanho da célula do grid
LINE=${LINE:-2}         # espessura da linha do grid
MAJOR_EVERY=${MAJOR_EVERY:-4}  # a cada N células, desenha linha "forte"
BG=${BG:-"#ffffff"}     # cor de fundo
GRID=${GRID:-"#9e9e9e"} # cor das linhas normais
GRID_MAJOR=${GRID_MAJOR:-"#616161"} # cor das linhas fortes

OUT_DIR="assets/gfx"
OUT_FILE="${OUT_DIR}/tank.png"

# ---- Detecta ImageMagick ----
if command -v magick >/dev/null 2>&1; then
  IMCMD=(magick)
elif command -v convert >/dev/null 2>&1; then
  IMCMD=(convert)
else
  echo "Erro: ImageMagick não encontrado (nem 'magick' nem 'convert' no PATH)." >&2
  exit 1
fi

mkdir -p "${OUT_DIR}"

# ---- Gera comandos de desenho (MVG) em arquivo temporário ----
MVG_FILE="$(mktemp)"
{
  echo "push graphic-context"
  echo "stroke ${GRID}"
  echo "stroke-width ${LINE}"
  # Linhas verticais
  i=0
  for ((x=0; x<=W; x+=CELL)); do
    if (( i % MAJOR_EVERY == 0 )); then
      echo "stroke ${GRID_MAJOR}"
      echo "stroke-width $((LINE*2))"
      echo "line ${x},0 ${x},${H}"
      echo "stroke ${GRID}"
      echo "stroke-width ${LINE}"
    else
      echo "line ${x},0 ${x},${H}"
    fi
    ((i++))
  done
  # Linhas horizontais
  j=0
  for ((y=0; y<=H; y+=CELL)); do
    if (( j % MAJOR_EVERY == 0 )); then
      echo "stroke ${GRID_MAJOR}"
      echo "stroke-width $((LINE*2))"
      echo "line 0,${y} ${W},${y}"
      echo "stroke ${GRID}"
      echo "stroke-width ${LINE}"
    else
      echo "line 0,${y} ${W},${y}"
    fi
    ((j++))
  done
  echo "pop graphic-context"
} > "${MVG_FILE}"

# ---- Cria a imagem com fundo + grid ----
"${IMCMD[@]}" -size "${W}x${H}" xc:"${BG}" -draw @"${MVG_FILE}" \
  -define png:color-type=6 -define png:bit-depth=8 \
  "${OUT_FILE}"

rm -f "${MVG_FILE}"
echo "Gerado: ${OUT_FILE} (${W}x${H}), célula=${CELL}px, linha=${LINE}px (major a cada ${MAJOR_EVERY})."

