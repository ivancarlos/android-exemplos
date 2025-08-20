KEYSTORE_FILE = .keystore/meu-release-key.keystore
ALIAS         = minha-chave
KEYSTORE_PASS = senha123
KEY_PASS      = senha123
VALIDITY_DAYS = 10000

# Identidade (dname): CN, OU, O, L, ST, C
DNAME         = CN=Usuario, OU=Dev, O=Empresa, L=Cidade, ST=Estado, C=BR

KEYTOOL_OPT   = -genkeypair \
				-deststoretype pkcs12 \
				-keystore "$(KEYSTORE_FILE)" \
				-alias "$(ALIAS)" \
				-storepass "$(KEYSTORE_PASS)" \
				-keypass "$(KEY_PASS)" \
				-keyalg RSA \
				-keysize 2048 \
				-validity "$(VALIDITY_DAYS)" \
				-dname "$(DNAME)" \
				-v

build:
	ant debug
release:
	ant release

install: installd
installr:
	ant installr
installd:
	ant installd
uninstall:
	adb uninstall `xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml`
start:
	adb shell am start -n `xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml`/`xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml`.`xmlstarlet select -T -t -v "//application/activity/@android:name" -n AndroidManifest.xml`
clean:
	rm -rf .keystore
	ant clean
key:
	mkdir .keystore
	keytool  $(KEYTOOL_OPT)

