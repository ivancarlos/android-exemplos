#!/usr/bin/env bash

SCRIPT_KEY_STORE_FILE=meu-release-key.keystore
SCRIPT_KEY_ALIAS=minha-chave
SCRIPT_KEY_STORE_PASSWORD=senha123
SCRIPT_KEY_ALIAS_PASSWORD=senha123
SCRIPT_JAVA=1.7

# Verificar se ANDROID_HOME está definido
[ "$ANDROID_HOME" ] || {
    dialog --msgbox "ERRO: Defina a variável \$ANDROID_HOME\n\nExemplo:\nexport ANDROID_HOME=/home/ivan/Android/api/16/android-sdk-linux" 10 70
    exit 2
}

# Configurações padrão
DEFAULT_TARGET="2"
DEFAULT_NAME="MyApp"
DEFAULT_ACTIVITY="MainActivity"
DEFAULT_PACKAGE="br.eng.ivanlopes.myapp"
DEFAULT_PATH="$PWD"
# Verificar dependências
check_dependencies() {
    local missing_deps=()

    if ! command -v dialog &>/dev/null; then
        missing_deps+=("dialog")
    fi

    if ! command -v android &>/dev/null; then
        dialog --msgbox "ERRO: Comando 'android' não encontrado!\n\nVerifique se \$ANDROID_HOME/tools está no \$PATH\n\nAtual ANDROID_HOME: $ANDROID_HOME" 12 70
        exit 1
    fi

    if [ ${#missing_deps[@]} -ne 0 ]; then
        echo "Dependências não encontradas: ${missing_deps[*]}"
        echo "Instale com: sudo apt install ${missing_deps[*]}"
        exit 1
    fi
}

# Listar targets disponíveis
get_targets() {
    android list targets 2>/dev/null | grep -E "^id:|Name:|API level:" |
        awk '
/^id:/ {
    if (id != "") {
        printf "%s \"%s%s\" \n", id, name, (api != "" ? " (API " api ")" : "")
    }
    id = $2
    name = ""
    api = ""
}
/^ *Name:/ {
    sub(/^ *Name: /, "", $0)
    name = $0
}
/^ *API level:/ {
    sub(/^ *API level: /, "", $0)
    api = $0
}
END {
    if (id != "") {
        printf "%s \"%s%s\" \n", id, name, (api != "" ? " (API " api ")" : "")
    }
}
'
}

# Validar nome do projeto
validate_project_name() {
    local name="$1"
    if [[ ! "$name" =~ ^[A-Za-z][A-Za-z0-9_]*$ ]]; then
        return 1
    fi
    return 0
}

# Validar package name
validate_package_name() {
    local package="$1"
    if [[ ! "$package" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)*$ ]]; then
        return 1
    fi
    return 0
}

# Validar activity name
validate_activity_name() {
    local activity="$1"
    if [[ ! "$activity" =~ ^[A-Z][A-Za-z0-9_]*$ ]]; then
        return 1
    fi
    return 0
}

# Menu principal
main_menu() {
    while true; do
        choice=$(dialog --clear --backtitle "Criador de Projetos Android v1.0" \
            --title "Menu Principal" \
            --menu "Escolha uma opção:" 16 70 9 \
            1 "Configurar target Android ($target)" \
            2 "Configurar nome do projeto ($project_name)" \
            3 "Configurar diretório base ($base_path)" \
            4 "Configurar activity principal ($activity_name)" \
            5 "Configurar package name ($package_name)" \
            6 "Listar targets disponíveis" \
            7 "Visualizar configurações" \
            8 "Criar projeto" \
            9 "Sair" \
            2>&1 >/dev/tty)

        case $choice in
            1) configure_target ;;
            2) configure_project_name ;;
            3) configure_base_path ;;
            4) configure_activity ;;
            5) configure_package ;;
            6) list_targets ;;
            7) show_config ;;
            8) create_project ;;
            9) break ;;
            *) break ;;
        esac
    done

    return 0 # Continua execução após menu
}

# Configurar target
configure_target() {
    # Usa um array para preservar os pares id/descrição
    IFS=$'\n' read -r -d '' -a targets_array < <(get_targets && printf '\0')

    if [ ${#targets_array[@]} -eq 0 ]; then
        dialog --msgbox "ERRO: Não foi possível obter lista de targets.\nVerifique sua instalação do Android SDK." 8 60
        return
    fi

    # Monta argumentos do menu como pares: id "description"
    local menu_items=()
    for item in "${targets_array[@]}"; do
        id="${item%% *}"
        desc="${item#* }"
        menu_items+=("$id" "$desc")
    done

    target=$(dialog --menu "Escolha o target Android:" 20 80 10 "${menu_items[@]}" 2>&1 >/dev/tty)

    if [ -z "$target" ]; then
        target="$DEFAULT_TARGET"
    fi
}

# Configurar nome do projeto
configure_project_name() {
    while true; do
        project_name=$(dialog --inputbox "Digite o nome do projeto:" 8 50 "$project_name" 2>&1 >/dev/tty)

        if [ -z "$project_name" ]; then
            project_name="$DEFAULT_NAME"
        fi

        if ! validate_project_name "$project_name"; then
            dialog --msgbox "ERRO: Nome inválido!\n\nO nome deve:\n- Começar com letra\n- Conter apenas letras, números e _\n- Não ter espaços" 10 50
            continue
        fi

        # Verifica se diretório com esse nome já existe
        if [ -d "$PWD/$project_name" ]; then
            dialog --msgbox "ERRO: Já existe um diretório com o nome '$project_name'.\nEscolha outro nome." 8 60
            continue
        fi

        break
    done
}

# Configurar diretório base
configure_base_path() {
    base_path=$(dialog --dselect "$base_path/" 20 60 2>&1 >/dev/tty)

    if [ ! -d "$base_path" ]; then
        dialog --yesno "Diretório não existe. Criar $base_path?" 8 50
        if [ $? -eq 0 ]; then
            mkdir -p "$base_path" || {
                dialog --msgbox "ERRO: Não foi possível criar o diretório" 8 50
                base_path="$DEFAULT_PATH"
            }
        else
            base_path="$DEFAULT_PATH"
        fi
    fi
}

# Configurar activity
configure_activity() {
    while true; do
        activity_name=$(dialog --inputbox "Digite o nome da Activity principal:" 8 50 "$activity_name" 2>&1 >/dev/tty)

        if [ -z "$activity_name" ]; then
            activity_name="$DEFAULT_ACTIVITY"
            break
        fi

        if validate_activity_name "$activity_name"; then
            break
        else
            dialog --msgbox "ERRO: Nome de Activity inválido!\n\nO nome deve:\n- Começar com letra maiúscula\n- Conter apenas letras, números e _\n- Seguir convenção PascalCase" 10 55
        fi
    done
}

# Configurar package
configure_package() {
    while true; do
        package_name=$(dialog --inputbox "Digite o package name:" 8 50 "$package_name" 2>&1 >/dev/tty)

        if [ -z "$package_name" ]; then
            package_name="$DEFAULT_PACKAGE"
            break
        fi

        if validate_package_name "$package_name"; then
            break
        else
            dialog --msgbox "ERRO: Package name inválido!\n\nO formato deve ser:\ncom.empresa.app\n\n- Apenas letras minúsculas e números\n- Separado por pontos\n- Cada parte deve começar com letra" 12 55
        fi
    done
}

# Listar targets
list_targets() {
    android list targets >/tmp/android_targets.txt 2>&1
    dialog --textbox /tmp/android_targets.txt 20 80
    rm -f /tmp/android_targets.txt
}

# Mostrar configurações
show_config() {
    local project_path="$base_path/$project_name"

    dialog --msgbox "CONFIGURAÇÕES ATUAIS

Target: $target
Nome do Projeto: $project_name
Diretório Base: $base_path
Caminho Completo: $project_path
Activity Principal: $activity_name
Package Name: $package_name

ANDROID_HOME: $ANDROID_HOME

Comando que será executado:
android create project \\
    --target $target \\
    --name $project_name \\
    --path $project_path \\
    --activity $activity_name \\
    --package $package_name" 18 70
}

# Criar projeto
create_project() {
    local project_path="$base_path/$project_name"

    # Verificar se o diretório já existe
    if [ -d "$project_path" ]; then
        dialog --yesno "O diretório $project_path já existe!\n\nContinuar mesmo assim?" 8 60
        if [ $? -ne 0 ]; then
            return
        fi
    fi

    # Confirmar criação
    dialog --yesno "Criar projeto Android?\n\nTarget: $target\nNome: $project_name\nPath: $project_path\nActivity: $activity_name\nPackage: $package_name" 12 60
    if [ $? -ne 0 ]; then
        return
    fi

    # Criar projeto
    (
        echo "Criando projeto Android..."
        android create project \
            --target "$target" \
            --name "$project_name" \
            --path "$project_path" \
            --activity "$activity_name" \
            --package "$package_name" 2>&1
        echo "DONE"
    ) | dialog --programbox "Criando projeto Android..." 15 70

    if [ $? -eq 0 ]; then
        dialog --msgbox "Projeto criado com sucesso!\n\nLocalização: $project_path\n\nPara compilar:\ncd $project_path\nant debug" 10 60

        cat <<EOF >${project_path}/build.xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="project_name" default="help">

    <!-- The local.properties file is created and updated by the 'android' tool.
         It contains the path to the SDK. It should *NOT* be checked into
         Version Control Systems. -->
    <property file="local.properties" />

    <!-- The ant.properties file can be created by you. It is only edited by the
         'android' tool to add properties to it.
         This is the place to change some Ant specific build properties.
         Here are some properties you may want to change/update:

         source.dir
             The name of the source directory. Default is 'src'.
         out.dir
             The name of the output directory. Default is 'bin'.

         For other overridable properties, look at the beginning of the rules
         files in the SDK, at tools/ant/build.xml

         Properties related to the SDK location or the project target should
         be updated using the 'android' tool with the 'update' action.

         This file is an integral part of the build system for your
         application and should be checked into Version Control Systems.

         -->
    <property file="ant.properties" />

    <!-- if sdk.dir was not set from one of the property file, then
         get it from the ANDROID_HOME env var.
         This must be done before we load project.properties since
         the proguard config can use sdk.dir -->
    <property environment="env" />
    <condition property="sdk.dir" value="\${env.ANDROID_HOME}">
        <isset property="env.ANDROID_HOME" />
    </condition>

    <!-- The project.properties file is created and updated by the 'android'
         tool, as well as ADT.

         This contains project specific properties such as project target, and library
         dependencies. Lower level build properties are stored in ant.properties
         (or in .classpath for Eclipse projects).

         This file is an integral part of the build system for your
         application and should be checked into Version Control Systems. -->
    <loadproperties srcFile="project.properties" />

    <!-- quick check on sdk.dir -->
    <fail
            message="sdk.dir is missing. Make sure to generate local.properties using 'android update project' or to inject it through the ANDROID_HOME environment variable."
            unless="sdk.dir"
    />

    <!--
        Import per project custom build rules if present at the root of the project.
        This is the place to put custom intermediary targets such as:
            -pre-build
            -pre-compile
            -post-compile (This is typically used for code obfuscation.
                           Compiled code location: \${out.classes.absolute.dir}
                           If this is not done in place, override \${out.dex.input.absolute.dir})
            -post-package
            -post-build
            -pre-clean
    -->
    <import file="custom_rules.xml" optional="true" />

    <!-- Import the actual build file.

         To customize existing targets, there are two options:
         - Customize only one target:
             - copy/paste the target into this file, *before* the
               <import> task.
             - customize it to your needs.
         - Customize the whole content of build.xml
             - copy/paste the content of the rules files (minus the top node)
               into this file, replacing the <import> task.
             - customize to your needs.

         ***********************
         ****** IMPORTANT ******
         ***********************
         In all cases you must update the value of version-tag below to read 'custom' instead of an integer,
         in order to avoid having your file be overridden by tools such as "android update project"
    -->
    <!-- version-tag: 1 -->
    <import file="\${sdk.dir}/tools/ant/build.xml" />

</project>
EOF
        cat <<EOF >${project_path}/ant.properties
# This file is used to override default values used by the Ant build system.
#
# This file must be checked into Version Control Systems, as it is
# integral to the build system of your project.

# This file is only used by the Ant script.

# You can use this to override default values such as
#  'source.dir' for the location of your java source folder and
#  'out.dir' for the location of your output folder.

# You can also use it define how the release builds are signed by declaring
# the following properties:
#  'key.store' for the location of your keystore and
#  'key.alias' for the name of the key to use.
# The password will be asked during the build when you use the 'release' target.
key.store= .keystore/$SCRIPT_KEY_STORE_FILE
key.alias= $SCRIPT_KEY_ALIAS
key.store.password= $SCRIPT_KEY_STORE_PASSWORD
key.alias.password= $SCRIPT_KEY_ALIAS_PASSWORD

java.source=$SCRIPT_JAVA
java.target=$SCRIPT_JAVA
EOF
        cat <<EOF >${project_path}/key.sh
#!/usr/bin/env bash

# Defina as variáveis de ambiente (ou passe diretamente)
KEYSTORE_FILE="$SCRIPT_KEY_STORE_FILE"
ALIAS="$SCRIPT_KEY_ALIAS"
KEYSTORE_PASS="\${KEYSTORE_PASS:-$SCRIPT_KEY_STORE_PASSWORD}"
KEY_PASS="\${KEY_PASS:-$SCRIPT_KEY_ALIAS_PASSWORD}"
VALIDITY_DAYS=10000

# Identidade (dname): CN, OU, O, L, ST, C
DNAME="CN=Usuario, OU=Dev, O=Empresa, L=Cidade, ST=Estado, C=BR"

# Gera a chave sem prompts interativos
keytool -genkeypair \
  -deststoretype pkcs12 \
  -keystore "\$KEYSTORE_FILE" \
  -alias "\$ALIAS" \
  -storepass "\$KEYSTORE_PASS" \
  -keypass "\$KEY_PASS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity "\$VALIDITY_DAYS" \
  -dname "\$DNAME" \
  -v

exit 0
EOF
        cat <<EOF >${project_path}/Makefile
ANT = /usr/bin/ant

KEYSTORE_FILE = .keystore/$SCRIPT_KEY_STORE_FILE
ALIAS         = $SCRIPT_KEY_ALIAS
KEYSTORE_PASS = $SCRIPT_KEY_STORE_PASSWORD
KEY_PASS      = $SCRIPT_KEY_ALIAS_PASSWORD
VALIDITY_DAYS = 10000

# Identidade (dname): CN, OU, O, L, ST, C
DNAME         = CN=Usuario, OU=Dev, O=Empresa, L=Cidade, ST=Estado, C=BR

KEYTOOL_OPT   = -genkeypair \
				-deststoretype pkcs12 \
				-keystore "\$(KEYSTORE_FILE)" \
				-alias "\$(ALIAS)" \
				-storepass "\$(KEYSTORE_PASS)" \
				-keypass "\$(KEY_PASS)" \
				-keyalg RSA \
				-keysize 2048 \
				-validity "\$(VALIDITY_DAYS)" \
				-dname "\$(DNAME)" \
				-v

build:
	\$(ANT) debug
release:
	\$(ANT) release

deploy: key release installr start

install: installd
installr:
	\$(ANT) installr
installd:
	\$(ANT) installd
uninstall:
	adb uninstall \`xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml\`
start:
	adb shell am start -n \`xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml\`/\`xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml\`.\`xmlstarlet select -T -t -v "//application/activity/@android:name" -n AndroidManifest.xml\`
clean:
	rm -rf .keystore
	\$(ANT) clean
key:
	mkdir .keystore
	keytool  \$(KEYTOOL_OPT)

EOF
    else
        dialog --msgbox "ERRO: Falha ao criar projeto!\n\nVerifique:\n- Se o target existe\n- Se há permissões no diretório\n- Se o Android SDK está configurado" 10 60
    fi
}

# Inicialização
check_dependencies

# Configurações padrão
target="$DEFAULT_TARGET"
project_name="$DEFAULT_NAME"
base_path="$DEFAULT_PATH"
activity_name="$DEFAULT_ACTIVITY"
package_name="$DEFAULT_PACKAGE"

# Mostrar informações iniciais
dialog --msgbox "Criador de Projetos Android

ANDROID_HOME: $ANDROID_HOME
PATH configurado: $(echo $PATH | grep -o "$ANDROID_HOME[^:]*" | head -1)

Pressione ENTER para continuar..." 10 70

# Iniciar interface
main_menu

echo "📁 Projeto disponível em: $project_name"
echo "💡 Comando: 'cd $project_name'"

exit 0
