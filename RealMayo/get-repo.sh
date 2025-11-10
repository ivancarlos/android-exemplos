#!/usr/bin/env bash

# Script para clonar todos os repositórios que começam com "wx" do usuário lszl84
USUARIO="RealMayo"
PREFIXO="AndEngine"

echo "🔍 Buscando repositórios de $USUARIO que começam com '$PREFIXO'..."

# Usa a API do GitHub para listar os repositórios
# Pega até 100 repos por página (ajuste se necessário)
REPOS=$(curl -s "https://api.github.com/users/$USUARIO/repos?per_page=100" |
    grep -o '"name": "[^"]*"' |
    grep -o '"[^"]*"$' |
    tr -d '"' |
    grep "^$PREFIXO")

if [ -z "$REPOS" ]; then
    echo "❌ Nenhum repositório encontrado com o prefixo '$PREFIXO'"
    exit 1
fi

echo "📦 Repositórios encontrados:"
echo "$REPOS"
echo ""

# Cria uma pasta para os repositórios
PASTA_DESTINO="${USUARIO,,}_${PREFIXO,,}_repos"
mkdir -p "$PASTA_DESTINO"
cd "$PASTA_DESTINO"

# Clona cada repositório
COUNT=0
for REPO in $REPOS; do
    echo "⬇️  Clonando $REPO..."
    git clone "https://github.com/$USUARIO/$REPO.git"

    if [ $? -eq 0 ]; then
        COUNT=$((COUNT + 1))
        echo "✅ $REPO clonado com sucesso!"
    else
        echo "❌ Erro ao clonar $REPO"
    fi
    echo ""
done

echo "🎉 Concluído! $COUNT repositórios clonados em $(pwd)"
