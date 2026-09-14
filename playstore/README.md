# Assets da Google Play

Tudo aqui foi gerado a partir dos arquivos em `src/`, renderizados com Chrome
headless. Para regerar depois de editar um HTML, veja [Regerar](#regerar).

## Arquivos prontos para upload

| Arquivo | Tamanho | Onde vai |
| --- | --- | --- |
| `icon-512.png` | 512×512 | Ícone do app (a Play arredonda os cantos sozinha) |
| `feature-graphic-1024x500.png` | 1024×500 | Gráfico de destaque **pt-BR** — obrigatório |
| `feature-graphic-1024x500-en.png` | 1024×500 | Gráfico de destaque **en-US** |
| `feature-graphic-1024x500-es.png` | 1024×500 | Gráfico de destaque **es-ES / es-419** |
| `screenshots/01.png` … `05.png` | 1080×1920 | Capturas para a ficha **pt-BR** |
| `screenshots-en/01.png` … `05.png` | 1080×1920 | Capturas para a ficha **en-US** |
| `screenshots-es/01.png` … `05.png` | 1080×1920 | Capturas para a ficha **es-ES / es-419** |

Política de privacidade publicada: **https://luckydraw.borzanti.com/privacidade/**
(fonte em `privacy-policy.html`, servida pelo repositório `borzanr/luckydraw-site`).

Os prints dentro das molduras são **capturas reais** do app rodando num Redmi
Note 9S. O rodapé foi recortado em cada uma para o banner de anúncio não aparecer
na loja — prática padrão e permitida.

## Identidade

- Fundo `#0B0F14`, lime `#B4FF39` (ação), ciano `#00E5FF` (destaque), roxo `#A855F7`
- O ícone é um dado: metáfora universal de sorteio e ainda legível a 48px, que é o
  tamanho em que a pessoa decide se toca ou não.
- O mesmo dado virou o ícone do app em
  `app/src/main/res/drawable/ic_launcher_foreground.xml`.

## Textos da ficha (PT-BR)

**Título** (máx. 30)

```
LuckyDraw Sorteador de Nomes
```

**Descrição curta** (máx. 80)

```
Sorteador de nomes e números na hora. Rifa, amigo secreto e sorteio rápido.
```

**Descrição longa**

```
Precisa sortear agora? O LuckyDraw resolve em um toque, sem cadastro e sem
complicação.

SORTEIO DE NÚMEROS
Escolha o intervalo (1 a 100, 1 a 5000, o que você quiser), diga quantos números
quer e pronto. Pode permitir repetidos ou exigir números diferentes.

DADO VIRTUAL E CARA OU COROA
Sorteie de 1 a 6 e você tem um dado virtual sempre no bolso, para quando a caixa do
jogo de tabuleiro perdeu o dado. De 1 a 2, vira cara ou coroa.

SORTEIO DE NOMES
Cole a lista — um nome por linha ou separados por vírgula — e deixe o app decidir.
Ideal para amigo secreto, rifa entre amigos, escolher quem começa o jogo ou quem
paga a conta.

COM SUSPENSE
Antes de revelar o ganhador, o app segura a expectativa por um segundo e meio. O
resultado aparece com confete.

HISTÓRICO
Os últimos 50 sorteios ficam salvos no aparelho. Fechou o app sem querer? Está tudo
lá.

RÁPIDO E JUSTO
O sorteio usa o gerador aleatório do próprio sistema e roda inteiro no seu celular.
Sem conta, sem login, sem enviar seus nomes para lugar nenhum.

PARA QUE AS PESSOAS USAM
• Rifas e sorteios de Instagram: sorteie o número ou o nome do ganhador na frente
  de todo mundo, com o resultado à vista
• Amigo secreto: cole a lista e deixe o app tirar
• Sala de aula: sortear quem responde, formar grupos, definir a ordem das
  apresentações
• Lives e transmissões: escolher um ganhador entre os participantes
• Reuniões: quem fala primeiro, quem apresenta, a ordem da daily
• Jogos de tabuleiro e RPG: dado virtual e quem começa a partida
• Times e duplas: dividir a galera sem discussão
• Em casa: quem lava a louça, quem escolhe o filme, quem passeia com o cachorro
• Quando bate a indecisão: deixe a sorte escolher por você
```

## Textos da ficha (EN)

**Título** (máx. 30)

```
LuckyDraw: Random Name Picker
```

**Descrição curta** (máx. 80)

```
Random name and number picker. Raffles, secret santa and giveaways in one tap.
```

**Descrição longa**

```
Need to pick a winner right now? LuckyDraw does it in one tap — no sign-up, no
fuss.

NUMBER PICKER
Set your range (1 to 100, 1 to 5000, whatever you need), choose how many numbers
you want, and draw. Allow repeats or force every number to be different.

VIRTUAL DICE AND COIN FLIP
Draw from 1 to 6 and you have a virtual dice in your pocket, for when the board
game box lost its own. From 1 to 2, it is a coin flip.

NAME PICKER
Paste your list — one name per line or separated by commas — and let the app
decide. Perfect for secret santa, raffles, picking who goes first, or who pays the
bill.

WITH SUSPENSE
Before revealing the winner, the app builds up the tension for a second and a
half. The result lands with confetti.

HISTORY
Your last 50 draws stay saved on the device. Closed the app by accident? It is all
still there.

FAST AND FAIR
Draws use the system random generator and run entirely on your phone. No account,
no login, and your names never leave the device.

WHAT PEOPLE USE IT FOR
• Raffles and Instagram giveaways: draw the winning number or name in front of
  everyone, result in plain sight
• Secret santa: paste the list and let the app draw
• Classroom: pick who answers, form groups, set the presentation order
• Live streams: choose a winner among the participants
• Meetings: who speaks first, who presents, the standup order
• Board games and RPG: virtual dice and who goes first
• Teams and pairs: split the group without arguing
• At home: who does the dishes, who picks the movie, who walks the dog
• When you just cannot decide: let luck pick for you
```

## Textos da ficha (ES)

Espanhol neutro, para Espanha e América Latina. Na descrição longa aparecem
**"amigo secreto"** e **"amigo invisible"** de propósito: é o mesmo costume com
nomes diferentes de um lado e do outro do Atlântico, e cobrir os dois ajuda quem
busca por qualquer um deles.

**Título** (máx. 30)

```
LuckyDraw Sorteador de Nombres
```

**Descrição curta** (máx. 80)

```
Sorteador de nombres y números al instante. Rifas, sorteos y amigo secreto.
```

**Descrição longa**

```
¿Necesitas sortear algo ahora mismo? LuckyDraw lo resuelve en un toque, sin
registro y sin complicaciones.

SORTEO DE NÚMEROS
Elige el rango (del 1 al 100, del 1 al 5000, el que quieras), di cuántos números
necesitas y listo. Puedes permitir repetidos o exigir que todos sean distintos.

DADO VIRTUAL Y CARA O CRUZ
Sortea del 1 al 6 y tienes un dado virtual siempre en el bolsillo, para cuando la
caja del juego de mesa perdió el suyo. Del 1 al 2, es cara o cruz.

SORTEO DE NOMBRES
Pega la lista —un nombre por línea o separados por comas— y deja que la app
decida. Ideal para el amigo secreto (o amigo invisible), rifas entre amigos,
elegir quién empieza el juego o quién paga la cuenta.

CON SUSPENSO
Antes de revelar el resultado, la app mantiene la expectativa durante un segundo y
medio. El resultado aparece con confeti.

HISTORIAL
Los últimos 50 sorteos quedan guardados en el dispositivo. ¿Cerraste la app sin
querer? Ahí sigue todo.

RÁPIDO Y JUSTO
El sorteo usa el generador aleatorio del sistema y funciona por completo en tu
teléfono. Sin cuenta, sin inicio de sesión y sin enviar tus nombres a ninguna
parte.

PARA QUÉ LO USA LA GENTE
• Rifas y sorteos de Instagram: sortea el número o el nombre del ganador delante de
  todos, con el resultado a la vista
• Amigo secreto (o amigo invisible): pega la lista y deja que la app sortee
• Aula: elegir quién responde, formar grupos, definir el orden de las exposiciones
• Directos y transmisiones: elegir un ganador entre los participantes
• Reuniones: quién habla primero, quién presenta, el orden de la daily
• Juegos de mesa y rol: dado virtual y quién empieza la partida
• Equipos y parejas: repartir al grupo sin discusiones
• En casa: quién friega los platos, quién elige la película, quién saca al perro
• Cuando no te decides: deja que la suerte elija por ti
```

## Checklist de publicação na Play Console

A Console mostra um painel com itens obrigatórios. A ordem abaixo evita retrabalho.

### 1. Criar o app
**Todos os apps → Criar app.** Nome `LuckyDraw`, idioma padrão **português (Brasil)**,
tipo **App**, **Gratuito**. O idioma padrão é o que aparece onde não houver tradução —
não dá para trocar depois.

### 2. Configuração do app (a seção que mais reprova)

| Item | Resposta |
| --- | --- |
| Política de privacidade | `https://luckydraw.borzanti.com/privacidade/` |
| Acesso ao app | todo o conteúdo é livre, sem login |
| Anúncios | **Sim, contém anúncios** |
| Classificação de conteúdo | ver abaixo |
| Público-alvo | 13+ (não marcar faixas infantis: o app tem anúncios e não é infantil) |
| App de notícias | Não |
| COVID-19 | Não |
| Segurança dos dados | ver abaixo |
| Recursos do governo | Não |

### 3. Classificação de conteúdo — cuidado com "sorteio"

⚠️ O questionário pergunta sobre **jogos de azar**. A resposta correta é **NÃO**:
o LuckyDraw não envolve apostas, dinheiro, prêmios nem simulação de cassino — é um
gerador de números aleatórios. Responder "sim" empurra a classificação para 18+ e
restringe países desnecessariamente.

Se algum revisor questionar por causa da palavra "rifa" na descrição, o argumento é
que o app **não intermedia** rifa nenhuma: ele sorteia um número, como um dado.

### 4. Segurança dos dados (Data Safety)

Precisa bater com a política de privacidade, senão a revisão reprova:

- **Coleta dados?** Sim — por meio do SDK de anúncios.
- **Tipo:** "ID do dispositivo ou outros IDs" → **ID de publicidade**.
- **Finalidade:** Publicidade ou marketing.
- **É obrigatório?** Sim (não há como recusar e seguir usando o app).
- **Compartilhado com terceiros?** Sim (Google AdMob).
- **Criptografado em trânsito?** Sim.
- **Usuário pode pedir exclusão?** Sim (redefinir o ID de publicidade nas
  configurações do Android).
- Nada de nome, e-mail, localização, fotos, contatos ou arquivos.

O histórico de sorteios **não** entra: ele nunca sai do aparelho, e o formulário
trata de dados coletados/transmitidos.

### 5. Fichas da loja (uma por idioma)

**Crescimento → Presença na Play Store → Ficha principal da loja.** Comece pelo
pt-BR e depois adicione traduções em **Gerenciar traduções → Adicionar traduções**:

| Idioma | Textos | Screenshots |
| --- | --- | --- |
| Português (Brasil) | seção PT-BR acima | `screenshots/` |
| Inglês (EUA) | seção EN acima | `screenshots-en/` |
| Espanhol (Espanha) e Espanhol (América Latina) | seção ES acima | `screenshots-es/` |

Ícone e gráfico de destaque são os mesmos em todos os idiomas.

### 6. Países

O app tem três idiomas e cai no inglês em qualquer outro lugar, então **distribuir
em todos os países** é mais simples do que marcar dezenas à mão e não traz
desvantagem — um app de sorteio não tem restrição cultural ou legal por território.

Se preferir restringir, os mercados que importam são: Brasil, Portugal, EUA, Canadá,
Reino Unido, Irlanda, Austrália, Nova Zelândia, Índia, África do Sul, Espanha,
México, Argentina, Colômbia, Chile, Peru, Equador, Bolívia, Uruguai, Paraguai,
Venezuela, Costa Rica, Panamá, Guatemala, Honduras, El Salvador, Nicarágua,
República Dominicana.

⚠️ Espanha, Irlanda e Reino Unido estão sob **GDPR** — confirme que a mensagem de
consentimento do AdMob está sendo servida antes de contar com receita de lá.

### 7. Primeira versão

Comece por **Teste interno** (libera em minutos, sem revisão longa), confirme que o
app instala e abre pela Play, e só então promova para **Produção**.

- Upload: `app/build/outputs/bundle/release/app-release.aab`
- Na primeira versão a Console oferece o **Play App Signing** — aceite.
- A cada nova versão é preciso subir o `versionCode` em `app/build.gradle.kts`.

### 8. Antes de enviar para revisão

- [ ] A conta de organização terminou a **verificação de identidade** (pode levar dias)
- [ ] Política de privacidade abre em janela anônima
- [ ] Data Safety coerente com a política
- [ ] Classificação de conteúdo respondida sem marcar jogos de azar
- [ ] IDs reais do AdMob no AAB (v1.1 já tem)

## Regerar

```bash
bash playstore/render.sh
```

Gera tudo: ícone, gráfico de destaque e os dois conjuntos de screenshots. As
headlines e o recorte de cada print ficam no objeto `SLIDES` em `src/shot.html`,
separados por idioma (`pt` e `en`).

Para atualizar os prints do app depois de mudar a UI: capture com
`adb exec-out screencap -p > arquivo.png` e substitua os quatro arquivos em
`src/shots/` (app em português) ou `src/shots-en/` (app em inglês). Se o banner de
anúncio passar a aparecer numa altura diferente, ajuste o `crop` daquele slide —
é a fração da tela que fica visível dentro da moldura.
