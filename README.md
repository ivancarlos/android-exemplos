# Desenvolvimento para Android 4.1 (Jelly Bean)

Este projeto tem como objetivo explorar o desenvolvimento de aplicativos Android para a versão **Android 4.1 (Jelly Bean)**, utilizando ferramentas e práticas compatíveis com essa plataforma.

## 📱 Sobre o Android 4.1

O Android 4.1, chamado de **Jelly Bean**, foi lançado em **julho de 2012** e trouxe diversas melhorias em relação à versão anterior (Ice Cream Sandwich), com foco principal em:

- Aumento da fluidez da interface por meio do **Project Butter**.
- Novas notificações expandidas.
- Melhor desempenho em dispositivos com hardware limitado.
- Início do suporte a acessibilidade aprimorada.

Esta versão corresponde à **API Level 16** no sistema de desenvolvimento Android.

## 📌 Características Técnicas

- **Nome da versão:** Jelly Bean
- **Versão:** 4.1.x
- **API Level:** 16
- **Lançamento:** Julho de 2012
- **Requisitos mínimos para apps:** `minSdkVersion = 16`

## ⚠️ Considerações para Desenvolvimento

Desenvolver para Android 4.1 exige atenção a certas limitações técnicas, especialmente se comparado às versões mais recentes:

- Muitas bibliotecas modernas (como Jetpack e Material Design 3) **não são compatíveis** com API 16.
- O modelo de permissões ainda é o antigo (declaração apenas no `AndroidManifest.xml`).
- Recursos como `RecyclerView`, `ConstraintLayout` e `ViewModel` não estão disponíveis por padrão.
- É necessário usar **versões antigas do Android Studio** ou o **Eclipse com ADT plugin**.
- Testes devem ser feitos em dispositivos reais ou emuladores com imagem do sistema Android 4.1.

## 🛠️ Ferramentas sugeridas

Para criar e testar aplicativos nessa versão do Android, recomenda-se:

1. **Android Studio antigo** com suporte ao SDK 16
   (ex: Android Studio 1.5 ~ 2.0)

2. **Eclipse com ADT plugin**
   Embora descontinuado, ainda pode ser utilizado em ambientes legados.

3. **SDK Tools e Build Tools compatíveis**
   Certifique-se de ter instalado as versões específicas via Android SDK Manager:
   - Android SDK Platform 16
   - Build Tools 19.x ou anterior

4. **Emulador x86 com imagem Android 4.1 (API 16)**
   Ou um **dispositivo físico antigo** com suporte à versão.

## 🧪 Primeiros passos

1. Configure seu ambiente com as ferramentas acima.
2. Crie um novo projeto Android com `minSdkVersion = 16`.
3. Desenvolva interfaces simples com `Activity`, `Button`, `TextView`, etc.
4. Compile e teste no emulador ou dispositivo real com Android 4.1.

## 💡 Exemplos de aplicativos compatíveis

- Hello World com interação via botão
- Calculadora básica
- Leitor de arquivos locais
- Aplicativos offline com interface simples

---

Este projeto celebra o desenvolvimento "retrô" no Android, resgatando práticas e ferramentas utilizadas na era do Jelly Bean.

Desenvolvido por Ivan Lopes.
Desenvolvido por Ivan Lopes.

