Para projetos Android antigos que usam **Ant** (antes do Android Studio e Gradle), você pode gerar um **keystore** manualmente usando o comando `keytool`, que faz parte do JDK.

---

### ✅ Passo a passo: Gerar um `keystore` com `keytool`

```bash
keytool -genkeypair -v \
  -keystore meu-release-key.keystore \
  -alias minha-chave \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

---

### 🧾 O que cada opção significa:

| Opção         | Significado                                          |
| ------------- | ---------------------------------------------------- |
| `-genkeypair` | Cria um par de chaves pública/privada                |
| `-v`          | Verbose (detalhado)                                  |
| `-keystore`   | Nome do arquivo a ser criado                         |
| `-alias`      | Nome interno da chave no keystore                    |
| `-keyalg`     | Algoritmo da chave (RSA é o comum)                   |
| `-keysize`    | Tamanho da chave (2048 ou 4096 recomendados)         |
| `-validity`   | Validade do certificado em dias (10.000 = \~27 anos) |

---

### ✅ Após isso...

Você terá um arquivo `meu-release-key.keystore` que pode ser usado para assinar o APK:

---

### 🧾 Configurar o Ant (`build.properties`)

Abra o arquivo `build.properties` do seu projeto e adicione:

```properties
key.store=meu-release-key.keystore
key.alias=minha-chave
key.store.password=SUASENHA
key.alias.password=SUASENHA
```

---

### 🛠️ Compilar com assinatura:

```bash
ant release
```

Isso gerará um `.apk` assinado (ex: `bin/NomeDoApp-release.apk`).

---

Se quiser que eu gere um script para facilitar todo esse processo (gerar, assinar, configurar), posso criar. Deseja?

