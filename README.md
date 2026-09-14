# LuckyDraw

Sorteador de números e nomes para Android. Sem cadastro, sem backend, sem banco de
dados: tudo roda no aparelho. Monetização por AdMob (banner fixo + interstitial
após o suspense).

A especificação de produto original está em [LuckyDraw.txt](LuckyDraw.txt).

## Rodar

Abra o projeto no Android Studio e rode, ou pela linha de comando:

```bash
./gradlew.bat installDebug
```

Testes das regras de sorteio e da serialização do histórico:

```bash
./gradlew.bat testDebugUnitTest
```

O JDK usado é o embutido no Android Studio, apontado em `gradle.properties`
(`org.gradle.java.home`) — o `java` do PATH desta máquina é um Java 8 antigo e não
serve para o build.

## Estrutura

| Pacote | O que faz |
| --- | --- |
| `draw/` | Regras de sorteio, sem dependência de Android (testáveis em JVM) |
| `data/` | Histórico e contador de sorteios no DataStore |
| `ads/` | IDs, banner adaptativo e gerenciador do interstitial |
| `ui/` | Compose: abas, suspense, resultado, histórico |

O fluxo de um sorteio é uma máquina de estados em `DrawViewModel`:
`Idle → Suspense (1,5 s) → AwaitingAd → Revealed`. O resultado é sorteado no
momento do clique e guardado; **nem a animação nem o anúncio influenciam o
sorteio**, e se o anúncio falhar o resultado aparece do mesmo jeito.

O interstitial é limitado a **1 a cada 3 sorteios** e nunca aparece no primeiro
sorteio da sessão.

## Assinatura e AAB

A chave de upload **não** fica no repositório. O build lê as credenciais de
`keystore.properties` (ignorado pelo git); sem esse arquivo, o release compila
normalmente, só sai sem assinatura.

Para configurar numa máquina nova:

```bash
# 1. Criar a chave de upload (pede a senha; guarde-a e faça backup do .jks)
mkdir -p ~/keystores && "/d/Program Files/Android/Android Studio/jbr/bin/keytool" \
  -genkeypair -v -keystore ~/keystores/luckydraw-upload.jks \
  -alias luckydraw -keyalg RSA -keysize 2048 -validity 10000
```

```bash
# 2. Preencher as credenciais
cp keystore.properties.example keystore.properties
```

```bash
# 3. Gerar o AAB assinado -> app/build/outputs/bundle/release/app-release.aab
./gradlew.bat bundleRelease
```

Com **Play App Signing** (obrigatório para apps novos), essa chave é apenas a
*upload key*: o Google reassina o app com a chave dele. Se a upload key for
perdida, dá para pedir reset ao suporte — não é o fim do app.

A cada nova versão publicada é preciso subir o `versionCode` em
`app/build.gradle.kts`; a Play recusa um AAB com `versionCode` já usado.

## Antes de publicar em produção

1. **IDs reais do AdMob** — hoje o app usa os IDs de teste do Google. Trocar em
   `ads/AdIds.kt` (`PROD_BANNER`, `PROD_INTERSTITIAL`) e o `APPLICATION_ID` no
   `AndroidManifest.xml`. Builds debug continuam nos IDs de teste de propósito:
   clicar no próprio anúncio real é motivo de banimento da conta.
2. **Consentimento (UMP)** — obrigatório para servir anúncios na UE.
3. **Ficha da Play** — assets e textos prontos em [playstore/](playstore); falta a
   política de privacidade (exigida porque o AdMob coleta dados) e o formulário de
   Data Safety.

## Verificado no aparelho

Testado num Redmi Note 9S (Android 11): sorteio de números e de nomes, suspense,
banner e interstitial de teste, confetes, histórico persistindo entre reinícios,
teclado sem cobrir o botão e o banner acima da barra de navegação.
