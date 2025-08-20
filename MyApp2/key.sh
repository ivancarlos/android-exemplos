#!/usr/bin/env bash

# Defina as variáveis de ambiente (ou passe diretamente)
KEYSTORE_FILE="meu-release-key.keystore"
ALIAS="minha-chave"
KEYSTORE_PASS="${KEYSTORE_PASS:-senha123}"
KEY_PASS="${KEY_PASS:-senha123}"
VALIDITY_DAYS=10000

# Identidade (dname): CN, OU, O, L, ST, C
DNAME="CN=Usuario, OU=Dev, O=Empresa, L=Cidade, ST=Estado, C=BR"

# Gera a chave sem prompts interativos
keytool -genkeypair \
  -deststoretype pkcs12 \
  -keystore "$KEYSTORE_FILE" \
  -alias "$ALIAS" \
  -storepass "$KEYSTORE_PASS" \
  -keypass "$KEY_PASS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity "$VALIDITY_DAYS" \
  -dname "$DNAME" \
  -v

exit 0

