#!/usr/bin/env bash

# ====================================
# CONFIGURAÇÕES DA IMAGEM TANK
# ====================================

# Especificações exatas do formato desejado
width=73
height=132
output_file="../assets/gfx/tank.png"

# Criar diretório se não existir
mkdir -p "$(dirname "$output_file")"

# ====================================
# GERAÇÃO DA IMAGEM TANK
# ====================================

convert -size ${width}x${height} xc:transparent \
    -depth 8 \
    -type TrueColorAlpha \
    -define png:color-type=6 \
    -draw "
    # ==================
    # CORPO DO TANK
    # ==================

    stroke black
    stroke-width 2
    fill darkgreen

    # Base do tank (corpo principal)
    rectangle 15,80 58,120

    # Torre do tank
    stroke black
    fill darkgreen
    rectangle 20,60 53,85

    # Canhão
    stroke black
    stroke-width 3
    fill dimgray
    line 36,60 36,10

    # ==================
    # ESTEIRAS (TRACKS)
    # ==================

    stroke black
    stroke-width 1
    fill dimgray

    # Esteira esquerda
    rectangle 8,85 18,125

    # Esteira direita
    rectangle 55,85 65,125

    # Detalhes das esteiras (rodas)
    stroke black
    fill gray
    circle 13,95 16,95
    circle 13,105 16,105
    circle 13,115 16,115

    circle 60,95 63,95
    circle 60,105 63,105
    circle 60,115 63,115

    # ==================
    # DETALHES DO TANK
    # ==================

    # Visor da torre
    stroke black
    fill black
    rectangle 32,65 41,70

    # Antena
    stroke red
    stroke-width 1
    line 25,60 25,45
    circle 25,45 27,45

  " \
    "$output_file"

# ====================================
# VERIFICAÇÃO DO RESULTADO
# ====================================

if [ -f "$output_file" ]; then
    echo "✅ Imagem gerada com sucesso: $output_file"
    echo ""
    echo "📋 Informações da imagem:"
    file "$output_file"
    echo ""
    echo "📐 Dimensões verificadas:"
    identify "$output_file"
else
    echo "❌ Erro ao gerar a imagem"
    exit 1
fi
