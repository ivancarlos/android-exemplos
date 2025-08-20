# MyApp2

**MyApp2** é um aplicativo Android simples desenvolvido em
Java utilizando o sistema de build `Ant`. Ele demonstra a
estrutura básica de um app Android, com interface gráfica,
recursos e uma `MainActivity`.

## 📁 Estrutura do Projeto

```{bash}
.
├── AndroidManifest.xml
├── build.xml
├── Makefile
├── res/ # Recursos do app (layouts, strings, ícones)
├── src/ # Código-fonte Java
└── ...
```


## 🚀 Funcionalidades

- Interface básica com layout XML
- Atividade principal (`MainActivity.java`)
- Suporte a múltiplas densidades de tela (`drawable-ldpi`, `mdpi`, `hdpi`, `xhdpi`)
- Estrutura compatível com `ant` (build clássico)

## 🛠️ Tecnologias

- **Java**
- **Android SDK**
- **Ant** (sistema de build antigo para Android)
- Layouts XML

## ⚙️ Como compilar e rodar

### Pré-requisitos

- Android SDK instalado
- `ant` instalado
- Variáveis de ambiente `ANDROID_HOME` configuradas

### Passos

# Para compilar o projeto

```{bash}
ant debug
```

# Para gerar o APK
```{bash}
ant release
```
O APK será gerado na pasta bin/.

⚠️ Este projeto utiliza um sistema legado de build. Para
projetos mais modernos, recomenda-se o uso do Android Studio
com Gradle.

📦 Instalação do APK
Após gerar o APK (bin/MyApp2-release.apk), instale em um
dispositivo ou emulador Android com:

```{bash}
adb install -r bin/MyApp2-release.apk
```
🤝 Contribuindo
Contribuições são bem-vindas! Para contribuir:

Fork este repositório

Crie uma branch (git checkout -b nova-feature)

Commit suas alterações (git commit -m 'Adiciona nova feature')

Faça push para o seu fork (git push origin nova-feature)

Abra um Pull Request

📄 Licença
Este projeto está sob a licença MIT.
