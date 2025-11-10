#!/usr/bin/env bash

# Script para trocar para o branch GLES2-AnchorCenter em todos os repositórios

BRANCH_NAME="GLES2-AnchorCenter"

echo "Trocando para o branch $BRANCH_NAME em todos os repositórios..."
echo "================================================"

# Percorre todas as pastas no diretório atual
for dir in */ ; do
    # Remove a barra do final
    dir_name="${dir%/}"

    # Verifica se é um repositório git
    if [ -d "$dir_name/.git" ]; then
        echo ""
        echo "📁 Processando: $dir_name"
        cd "$dir_name"

        # Volta para o diretório pai
		git dry-run
		#git undo
        cd ..
    else
        echo "⊘ Pulando $dir_name (não é um repositório Git)"
    fi
done

echo ""
echo "================================================"
echo "✓ Processo concluído!"
