#!/usr/bin/env bash

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
    /^id:/ { id = $2; getline; name = $0; getline; api = $0;
             gsub(/^[ \t]*Name: /, "", name);
             gsub(/^[ \t]*API level: /, "", api);
             printf "%s \"%s (API %s)\" ", id, name, api }
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
            9) exit 0 ;;
            *) exit 0 ;;
        esac
    done
}

# Configurar target
configure_target() {
    local targets_list=$(get_targets)
    if [ -z "$targets_list" ]; then
        dialog --msgbox "ERRO: Não foi possível obter lista de targets.\nVerifique sua instalação do Android SDK." 8 60
        return
    fi

    target=$(dialog --menu "Escolha o target Android:" 20 80 10 $targets_list 2>&1 >/dev/tty)

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
            break
        fi

        if validate_project_name "$project_name"; then
            break
        else
            dialog --msgbox "ERRO: Nome inválido!\n\nO nome deve:\n- Começar com letra\n- Conter apenas letras, números e _\n- Não ter espaços" 10 50
        fi
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

        cat <<EOF >${project_path}/Makefile
LINUXBREW_HOME = /home/linuxbrew/.linuxbrew
ANT  =  /usr/bin/ant
JENV =	\$(LINUXBREW_HOME)/bin/jenv

PACKAGE = \`xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml\`
MAINACTIVITY = \`xmlstarlet select -T -t -v "//manifest/@package" -n AndroidManifest.xml\`.\`xmlstarlet select -T -t -v "//application/activity/@android:name" -n AndroidManifest.xml\`

status:
	\$(ANT) -version
	\$(JENV) version

build:
	\$(ANT) debug
install:
	\$(ANT) installd
uninstall:
	adb uninstall \$(PACKAGE)
start:
	adb shell am start -n \$(PACKAGE)/\$(MAINACTIVITY)
clean:
	ant clean

EOF
        # Perguntar se quer abrir o diretório
        dialog --yesno "Abrir o diretório do projeto no gerenciador de arquivos?" 8 50
        if [ $? -eq 0 ]; then
            if command -v nautilus &>/dev/null; then
                nautilus "$project_path" &
            elif command -v dolphin &>/dev/null; then
                dolphin "$project_path" &
            elif command -v thunar &>/dev/null; then
                thunar "$project_path" &
            fi
        fi
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

exit 0

