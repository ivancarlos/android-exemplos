#!/usr/bin/env bash

# Configurações de qualidade
image_format="png" # png, jpg, tiff, bmp
image_quality=95   # 0-100 (para JPEG/PNG)
image_name=cartesian_plane

# Centro do plano cartesi
# ====================================
# DEFINIÇÃO DOS ELEMENTOS GRÁFICOS
# ====================================

# Cabeça de seta (ponta triangular)
arrow_head="path 'M 0,0
                  l -12,-4
                  l +4,+4
                  l -4,+4
                  l +12,-4
                  z'"

# ====================================
# CONFIGURAÇÕES DO PLANO CARTESIANO
# ====================================

# | Device Name     | Width | Height | Absolute Ratio (Height / Width) | Calculated Ratio | Published Ratio |
# |-----------------|-------|--------|---------------------------------|------------------|-----------------|
# | Nexus S         | 480   | 800    | 1.66666666666667                | 3/5              | long            |
# | Nexus One       | 480   | 800    | 1.66666666666667                | 3/5              | long            |
# | Nexus 9         | 2048  | 1536   | 0.75                            | 3/4              | notlong         |
# | Nexus 4         | 768   | 1280   | 1.66666666666667                | 3/5              | notlong         |
# | Nexus 7 (2012)  | 800   | 1280   | 1.6                             | 5/8              | notlong         |
# | Nexus 7         | 1200  | 1920   | 1.6                             | 5/8              | notlong         |
# | Nexus 10        | 2560  | 1600   | 0.625                           | 5/8              | notlong         |
# | Nexus 6         | 1440  | 2560   | 1.77777777777778                | 9/16             | notlong         |
# | Nexus 5         | 1080  | 1920   | 1.77777777777778                | 9/16             | notlong         |
# | Galaxy S4       | 1080  | 1920   | 1.77777777777778                | 9/16             | notlong         |
# | Galaxy Nexus    | 720   | 1280   | 1.77777777777778                | 9/16             | long            |
# Dimensões da imagem
#
## Nexus S
#width=480
#height=800
##
## Nexus One
#width=480
#height=800
##
## Nexus 9
#width=2048
#height=1536
##
## Nexus 4
#width=768
#height=1280
##
## Nexus 7 (2012)
#width=800
#height=1280
##
## Nexus 7
#width=1200
#height=1920
##
## Nexus 10
#width=2560
#height=1600
##
## Nexus 6
#width=1440
#height=2560
##
## Nexus 5
#width=1080
#height=1920
##
## Galaxy S4
#width=1080
#height=1920
##
## Galaxy Nexus
#width=720
#height=1280
#
# Samsung Galaxy X
width=720
height=1280

# Centro do plano cartesiano
center_x=$((width / 2))
center_y=$((height / 2))

declare -A line_opt
declare -A seta
declare -A label

offset_x=20
offset_y=20
padding=5
n=1

xi_0=${offset_x}
yi_0=${center_y}
xf_0=$((width - offset_x))
yf_0=${center_y}
line_opt["horizontal"]="${xi_0},${yi_0} ${xf_0},${yf_0}"

x=${xf_0}
y=${yf_0}
seta["horizontal"]="${x},${y}"

xi_0=${center_x}
yi_0=${offset_y}
xf_0=${center_x}
yf_0=$((height - offset_y))
line_opt["vertical"]="${xi_0},${yi_0} ${xf_0},${yf_0}"

x=$((xf_0))
y=$((-yf_0))
seta["p1"]="${x},${y}"
seta["p2"]="$((y)),$((x))"

x=$((width - offset_x))
y=$((center_y + offset_y))
label["_X"]="${x},${y}"

x=$((center_x - offset_y - padding))
y=$((offset_y * 2))
label["_Y"]="${x},${y}"

x=$((center_x - offset_x))
y=$((center_y + offset_y))
label["_O"]="${x},${y}"

XL=180
YL=180
XL=$((XL))
YL=$((-YL))
echo "XL,XL=$XL,$XL"
ARGUMENT=$(echo "scale=5; 180*a(${YL}/${XL})/(4*a(1))" | bc -l)
echo ARGUMENT=$ARGUMENT
NARGUMENT=$(echo "scale=5; $ARGUMENT*-1" | bc -l)

# ====================================
# GERAÇÃO DA IMAGEM
# ====================================

convert -size ${width}x${height} xc:white \
    -draw "
    # ==================
    # EIXOS CARTESIANOS
    # ==================

    stroke gray70
    stroke-width 1

    # Eixo X (horizontal)
    line ${line_opt["horizontal"]}

    # Eixo Y (vertical)
    line ${line_opt["vertical"]}

    # ==================
    # SETAS NOS EIXOS
    # ==================

    push graphic-context
      stroke black
      fill black
      stroke-width 1

      # Seta do eixo X (direita)
      translate ${seta["horizontal"]}
      ${arrow_head}

      # Seta do eixo Y (cima)
      translate ${seta["p1"]}
      translate ${seta["p2"]}
      rotate -90
      ${arrow_head}
    pop graphic-context

    # ==================
    # LABELS DOS EIXOS
    # ==================

    stroke none
    fill black
    font-size 28
    text-antialias true

    # Label X
    text ${label["_X"]} 'X'

    # Label Y
    text ${label["_Y"]} 'Y'

    # Origem (0,0)
    text ${label["_O"]} '0'

    # ==================
    # CÍRCULO NO CENTRO
    # ==================

    stroke black
    fill none
    stroke-width 1
    circle ${center_x},${center_y} $((center_x + padding)),$((center_y + padding))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))
    circle ${center_x},${center_y} $((center_x + 10 * n++)),$((center_y + 10 * n++))

    # ==================
    # EXEMPLO DE USO
    # ==================

    push graphic-context
      stroke blue
      fill lightblue
      stroke-width 2

      # Vetor exemplo
      translate ${center_x},${center_y}
      line 0,0 100,-100
      line 0,0 ${XL},${YL}

      # Seta no final do vetor
      translate ${XL},${YL}
      rotate ${ARGUMENT}
      ${arrow_head}

      # Label do vetor
      stroke none
      fill blue
      font-size 42

      translate 0,0
      text 10,10 'Com Ângulo'

      rotate ${NARGUMENT}
      translate 0,0
      text 10,10 'Sem ângulo'

    pop graphic-context

  " \
    -quality ${image_quality} \
    ${image_name}.${image_format}

echo "Plano cartesiano gerado: ${image_name}.${image_format}"

echo eog ${image_name}.${image_format}
