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

        # Verifica se o branch existe localmente
        if git show-ref --verify --quiet "refs/heads/$BRANCH_NAME"; then
            echo "   ✓ Branch existe localmente, fazendo checkout..."
            git checkout "$BRANCH_NAME"
        # Verifica se o branch existe remotamente
        elif git ls-remote --heads origin "$BRANCH_NAME" | grep -q "$BRANCH_NAME"; then
            echo "   ✓ Branch existe remotamente, criando localmente..."
            git checkout -b "$BRANCH_NAME" "origin/$BRANCH_NAME"
        else
            echo "   ✗ Branch $BRANCH_NAME não encontrado neste repositório"
        fi

        # Volta para o diretório pai
        cd ..
    else
        echo "⊘ Pulando $dir_name (não é um repositório Git)"
    fi
done

echo ""
echo "================================================"
echo "✓ Processo concluído!"
