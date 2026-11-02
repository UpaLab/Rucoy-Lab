# Pendências de teste no jogo

> Lista para pedir ajuda à comunidade. Cada item diz **o que não sabemos**, **o que as fontes dizem** e
> **como dá para testar**. Fórmulas da comunidade, não oficiais.
>
> Ao mandar um resultado, inclua: classe, level, stat, arma (ataque e +buff), mob, e se usou anel ou bônus
> de exp. Um print ajuda muito.

## Já confirmado no jogo ✅

| O quê | Resultado | Quem / quando |
|---|---|---|
| Cores dos assassinos | Lv 45 vermelho, Lv 50 amarelo, Lv 55 cinza (Ninja) | dono do projeto, 01/11/2026 |
| Tempo pra matar o Assassin Ninja (adaga de treino, power training) | ~1 minuto, igual ao calculado | dono do projeto, 01/11/2026 |
| Mana gasta pelo especial | **50** | medido pelo dono, 01/11/2026 |
| Quanto a poção recupera | **varia** dentro de uma faixa. Básica 70–130 e Greater 200–300 (wiki); as medições do dono (+117, +82 na básica; 200 na Greater) caem dentro dessas faixas | wiki + dono, 01/11/2026 |
| Cooldown da Greater e da Super | **1,25 s** e **1,65 s** (mudou na versão 1.22; antes era 1,5 s e 2,0 s) | notas oficiais em rucoyonline.com/news |
| Preço das poções | 50 / 150 / 350 / 650 gold (básica / Greater / Super / Ultimate) | wiki (Supplies Merchant) |
| Tomar a poção básica no ritmo do cooldown mantém os especiais | sim | observação do dono; bate com a conta (100 de mana por segundo > 50 por segundo do especial) |
| **Golpes normais sem dano contam tick** quando o especial dá dano (a regra do "1 a cada 10") | sim: no teste B sobraram ~87 ticks que os especiais não explicam | testes A e B do dono, 01/11/2026 |
| Ticks do especial | **~1,4 por mob atingido com estamina verde + bônus online** (~276 acertos → ~396 ticks). Sem bônus, 🔶 ~0,95 | teste quadro a quadro, 02/11/2026 (seção 5c) |
| Intervalo entre especiais (só especial) | **~1,33 s** com as pausas (116 s ÷ ~87); teste A deu ~1,45 s | teste de 02/11 e teste A |
| Mobs atingidos por especial | **~3,2 em média** puxando 4 (4 → 2–3 → 1 conforme morrem) | teste de 02/11 (contado em vídeo) |
| Mana por poção básica no teste | **~93** (dentro da faixa 70–130 da wiki) | teste A |
| **Mob cansa** ("this monster is exhausted"): cada mob só dá stat por ~7 min 30 s. Vale **por indivíduo**: outros do mesmo tipo continuam dando stat | sim, de forma aproximada (Assassins duravam mais de 5 min sem morrer com melee ~70–80) | wiki + observação do dono |
| Estamina verde (24 h ou mais) | **+50%** de exp de level e skill (**1,5×**, e não 2× como dizia a wiki) | notas oficiais, update 1.21 |
| Bônus de treino online ("120 minutes of bonus…") | **2×** de exp de skill; 2 h a cada 24 h, com estamina própria | notas oficiais, update 1.25 |
| Total com os dois bônus ligados, no power training | **~1,4–1,5×** (se 1 acerto = 1 tick). O 2× do online **não apareceu** | teste de 02/11 (seção 5c) 🔶 |
| Fórmula de exp de level (`level^(level/1000+3)`) | bate: 872.188 de exp fica dentro do level 84 (859.958 a 895.896) | teste de 02/11 |

---

## 1. Especial do mago 🔶 (a confirmar)

- **Dúvida:** o especial do mago usa `1,08 × stat` ou `1,05 × stat`?
- **Fontes:** helloyanis usa **1,08** (mudou de propósito em jan/2024); o Mims usa **1,05**. O app usa
  **1,08**, marcado como "a confirmar".
- **Fórmula completa:** dano máximo do especial = `1,5 × (FATOR × stat × arma ÷ 10 + 9 × level ÷ 32)`.
- **Como testar:** com um mago, solte vários especiais num mob fraco (que você acerta sempre) e anote o
  **maior dano** que aparecer. Mande o maior valor junto com level, stat de magic e ataque da arma.
  - Exemplo: level 100, magic 80, arma 30 → com 1,08: máx. ≈ **431**; com 1,05: máx. ≈ **420** (antes da
    defesa do mob).

## 2. Anel de crítico 🧪 (experimental)

- **Dúvida:** com o anel de crítico, qual é a **chance** de crítico e quanto o crítico **aumenta** o dano?
- **Fontes:** só o helloyanis tem, e os números não combinam entre si (7,5% num lugar, 3,5% em outro).
  Sem anel, as duas fontes dizem: **1%** de chance e crítico de até **1,05×** o dano máximo.
- **O app usa (experimental):** 3,5% de chance e até 1,075× com anel.
- **Como testar:** num mob fraco, contar quantos golpes críticos aparecem em, por exemplo, **300 golpes**:
  primeiro sem o anel, depois com o anel. Anotar também o **maior dano crítico**.

## 3. Defesa dos Minotaurs 250 e 275

- **Dúvida:** qual é a defesa deles?
- **Fontes:** helloyanis: **601** (Lv 250) e **691** (Lv 275). Mims: **591** e **681**. A wiki não informa
  defesa. O app usa os valores do helloyanis.
- **Como testar:** a defesa aparece no jogo pelo **menor dano possível**. Com um golpe de faixa conhecida,
  ver a partir de qual stat começa a sair dano. Mais simples: anotar o **maior dano** que você tira num
  Minotaur e o maior que tira num mob de defesa conhecida, com o mesmo personagem; a diferença entre os
  dois é a diferença de defesa.

## 4. Poções: o que ainda falta

Valores que o app usa hoje (todos editáveis na tela):

| Poção | Recupera | Preço | Cooldown | Fonte |
|---|---|---|---|---|
| Mana / Health Potion | 70–130 (média 100) | 50 | **1 s** | wiki; cooldown: wiki francesa |
| Greater | 200–300 (média 250) | 150 | 1,25 s | wiki; cooldown: notas oficiais |
| Super | 400–600 (média 500) | 350 | 1,65 s | wiki; cooldown: notas oficiais |
| Ultimate | 600–900 (média 750) | 650 | 2 s | wiki francesa; preço: wiki |

Em aberto:

- **Cooldown da poção básica:** a wiki francesa diz **1 s**; o dono lembra de **~1,5 s** (que era o cooldown
  antigo da Greater). **Como testar:** tomar 10 poções básicas seguidas o mais rápido possível e cronometrar.
- **Poção de vida e de mana dividem o mesmo cooldown?** Se dividirem, no power training você toma menos
  poções de mana quando precisa curar. **Como testar:** tomar uma de vida e, logo em seguida, uma de mana.
- **Regeneração natural de mana:** a wiki diz que o Soul Ring e o Soul Pendant dão **+1** cada e que "os
  dois juntos fazem recuperar o dobro", o que sugere uma base de **2**, mas **não diz por quanto tempo**.
  O app usa **0** até termos o valor. **Como testar:** com a mana abaixo do máximo, sem atacar e sem poção,
  anotar a mana a cada 10 segundos durante 1 minuto. Repetir com o anel, se tiver.
- **Ultimate:** quanto recupera (600–900 só aparece na wiki francesa) e o cooldown de 2 s.

## 5. Poção de vida por mob (dano dos mobs)

- **Dúvida:** quanto cada mob tira de vida. **Nenhuma fonte tem o ataque dos mobs** (wiki, helloyanis e Mims).
- **O app pede:** "vida que você perde por minuto nesse mob", e salva o valor pra cada mob.
- **Primeira medição (02/11/2026):** Assassin Ninjas, **~905 de vida por minuto** (7 Greater em 1 min 56 s;
  level 84, HP 1.360, Defense 92, defesa alta ~241). Já é o padrão do app nesse mob. Detalhes na seção 5c.
- **Como medir:** no mob em que você treina, com o seu set de sempre e o mesmo número de mobs, anote a
  vida, treine **1 minuto sem tomar poção de vida** (ou conte quantas tomou e quanto recupera cada uma) e
  veja quanto perdeu. Mande junto: mob, quantos mobs batiam em você, sua classe, level, defense e
  armadura.

## 5b. Power training: resultados dos testes A e B ✅ (com ressalvas)

Condições: melee, stat 98/99, arma de treino, ninjas, **bônus de exp ativo**. Tempo medido pela estamina e
avanço do stat estimado pelo tamanho da barra, então os números são **aproximados**.

| | Teste A (só especial) | Teste B (1 especial + ~9 golpes) |
|---|---|---|
| Tempo | ~145 s | ~91 s |
| Especiais | 100 | ~9,4 (calculado pela mana: 3 poções × ~93 + 190 da barra = ~470 ÷ 50) |
| Poções básicas | 50 | 3 |
| Stat | 98 (93%) → 99 (44%) | 99 (44%) → 99 (58%) |
| Ticks ganhos (pela curva da comunidade: ~924 no ponto 98, ~942 no 99) | **~479** | **~132** |
| Ticks por especial (com bônus) | **~4,8** | — |
| Ticks por hora | ~11.900 | ~5.200 |
| Ticks por poção | ~10 | ~26 |

**Conclusões:**
- ✅ Os golpes normais **contam** tick sem dar dano, desde que o especial dê dano. No teste B, os ~9 especiais
  explicam ~45 ticks; os outros **~87** vieram dos ~82 golpes normais.
- ✅ O especial rende **~1 tick por mob atingido** (4,8 ÷ 2 de bônus ÷ ~2,4 mobs).
- ✅ **Rápido** (só especial) sobe o stat ~2,3× mais rápido. **Econômico** (especial + golpes) gasta ~2,6×
  menos poção por tick.

**Bônus durante os testes:** a estamina estava em **26:00:00 verde** e também apareceu **"You have 120 minutes
of bonus level and skill experience"**. São **dois bônus diferentes** (notas oficiais):

| Bônus | Quanto | Fonte |
|---|---|---|
| Estamina verde (24 h ou mais) | +50% (1,5×) | update 1.21 |
| Treino online (2 h por dia) | 2× de skill | update 1.25 |
| **Os dois juntos** | 🔶 **2,5× (se somam) ou 3× (se multiplicam)**: o oficial não diz | o Mims oferece 1,5×/2×/2,5×, o que sugere soma |

> ⚠️ **Substituído pela seção 5c (02/11/2026).** O teste quadro a quadro mostrou que o bônus total era
> ~1,5×, não 2,5×. O texto abaixo fica como histórico.

**Recalibração (01/11/2026):** antes a gente supunha 2× nos testes. Agora o app supõe **2,5×** (soma), e os
valores **sem bônus** ficaram:
- especial: **~0,77 tick por mob atingido** (4,8 ÷ 2,5 ÷ 2,5 mobs). Se os mobs atingidos foram mais perto de 2,
  dá ~0,96;
- golpe normal: **~0,42 tick** (1,06 ÷ 2,5), se o bônus vale nos golpes.

O app continua reproduzindo os testes A e B com 2,5×. O multiplicador total é editável na tela.

**Em aberto:**
- 🔶 **Os dois bônus somam ou multiplicam?** E o **bônus de treino online vale nos golpes normais** sem dano?
- **Teste que separa os dois (combinado):** repetir o **teste B** (1 especial + ~9 golpes, melee, arma de
  treino, ninjas) **depois que os 120 minutos acabarem**, ainda com a **estamina verde**. Assim só fica o
  bônus de 1,5×. Anotar o mesmo de antes: stat com porcentagem antes e depois, especiais, poções e tempo.
  Pra diminuir o erro da barra, vale fazer **mais tempo (5 min ou mais)**.
  - Se os dois **somavam** (2,5×): o mesmo ritmo deve render **~60%** dos ticks do teste B original.
  - Se **multiplicavam** (3×): deve render **~50%**.
  - Se o online **não valia nos golpes**: a parte dos golpes quase não muda, e só a dos especiais cai.
  - Se der pra repetir também com a **estamina branca** (menos de 24 h) e sem o online, sai o valor **sem bônus**.
- **Mob cansado: matar e renascer zera?** A wiki diz que **não** (o mob renasce ainda cansado e leva 1 hora pra
  voltar), mas isso não foi conferido. **Como testar:** num mob que já cansou, matar, esperar renascer e ver
  se a mensagem volta logo no primeiro golpe.
- **Quanto tempo se perde trocando de mob:** o app usa **15 s** a cada 7 min 30 s (suposição, editável).
- Repetir os testes com outros stats, armas e mobs, pra ver se os números se mantêm.

## 5c. Teste quadro a quadro (02/11/2026) ✅ — o mais preciso até agora

Contado quadro a quadro em vídeo pelo dono. Melee, Assassin Ninjas, power training só com especial
(golpes normais só num trecho em que a mana zerou). **Estamina verde e "120 minutes of bonus" ligados.**

### O personagem

| | Valor |
|---|---|
| Level / HP / MP | 84 / 1.360 / 940 |
| Stats | Defense 92, Melee 99 → 100 durante o teste |
| Arma | adaga de treino (ataque 5) |
| Defesa dos itens | capacete 12, armadura 18, calça 15, bota 9, escudo 28, cinturão 9, anel verde 2, luvas 6 + 6 = **105** (77 sem o escudo) |
| Colar de vida | sem defesa; a regeneração natural de vida passa de **2 para 3 por vez** (🔶 intervalo a medir) |

### O que foi lido e contado

| | Valor |
|---|---|
| Tempo | **116 s** (estamina 25:59:21 → 25:57:25) |
| Melee | 99 (~59%) → 100 (~1%), barra medida por pixel (±2%) |
| Defense | 92 (~60%) → 92 (~75%) |
| Exp | 872.188 → 873.868 (**+1.680**, ~52.000 por hora) |
| Especiais | **86–87**, ~1,33 s cada (contando as pausas) |
| Mobs por especial | 4 (~57 especiais) → 2–3 (~12) → 1 (~18): **~276 acertos**, média ~3,2 |
| Golpes normais | ~15 (estimado), num trecho sem especial |
| Mana | 940 → 525 (415 da barra) + **37 poções básicas** (~106 cada) ≈ 4.350 ≈ 87 × 50 ✅ fecha |
| Vida | **7 Greater Health Potions** (200–300, média 250) |
| Mob cansado | a mensagem **não apareceu** |

### A conta, conferida com as fórmulas do app

```
ticks do ponto 99 (99 → 100) = 942     ticks do ponto 100 = 961     (curva da comunidade, 55+)
melee ganho = 41% × 942 + 1% × 961 ≈ 396 ticks     (faixa pelo erro da barra: ~367 a ~434)
ticks por acerto = 396 ÷ 276 ≈ 1,43               (faixa: ~1,33 a ~1,57)
```

✅ **A sua conta está certa:** ~395 ticks e ~1,4 tick por acerto. Os ~15 golpes normais mudam pouco: eram
mais de 9 seguidos sem especial, então no máximo 9 contariam, e isso tiraria no máximo ~10 ticks
(ficaria ~1,37 por acerto).

**O que isso diz sobre o bônus:** os dados medem só "ticks por acerto **com** bônus" (~1,4). Pra separar o
bônus, precisa supor quanto vale um acerto sem bônus:
- Se 1 acerto = **1 tick** (o mais simples): o total foi **~1,4–1,5×**. Isso bate com **só a estamina verde
  (1,5×)**. O 2× do treino online **não apareceu** no power training (ou não vale nele, ou não soma).
- Pra o total ser 2,5×, cada acerto valeria só ~0,57 tick sem bônus, o que não tem motivo pra ser assim.

🔶 Então o app agora supõe **1,5× com os dois ligados** e **~0,95 tick por acerto sem bônus**. Ainda é
suposição: a curva de ticks da comunidade também pode estar um pouco errada no stat 99–100.

### Juntando com os testes A e B

Refazendo A e B com o ~1,43 tick por acerto do teste novo:

| | Teste A | Teste B |
|---|---|---|
| Ticks medidos | ~479 | ~132 |
| Mobs por especial que explicam | **~3,35** (479 ÷ 1,43 ÷ 100). A anotação era "2 a 3", mas era estimativa, não contagem | — |
| Ticks por golpe normal (com bônus) | — | **~1,1** (132 − 9,4 especiais × ~3 mobs × 1,43, ÷ ~82 golpes). Com 2,5 mobs, ~1,2 |

Ou seja, os três testes ficam coerentes com: **~1,4 tick por acerto do especial e ~1,1 por golpe normal
(os dois com bônus)**. O golpe normal parece valer quase o mesmo que um acerto do especial; a barra do
teste B tem erro de ±2%, então pode ser igual.

**Intervalo entre especiais:** 116 s ÷ 87 = 1,33 s contando o trecho dos ~15 golpes normais. Se esse trecho
durou ~15 s, o intervalo real entre especiais seguidos foi **~1,17 s**. O app usa 1,33 s (o ritmo real, com
pausas); quem quiser o mais rápido possível pode trocar.

### O que mudou no app

| Valor padrão | Antes | Agora | Fonte |
|---|---|---|---|
| Bônus total com estamina verde + online | 2,5× | **1,5×** | teste 02/11 |
| Ticks por mob no especial (sem bônus) | 0,77 | **0,95** | 1,43 ÷ 1,5 |
| Ticks por golpe normal (sem bônus) | 0,42 | **0,75** | teste B ÷ 1,5 |
| Mobs atingidos por especial | 2,5 | **3,2** | 276 ÷ 87 |
| Intervalo entre especiais | 1,45 s | **1,33 s** | 116 ÷ 87 |
| Golpes entre especiais | 9 | 9 | teste B (igual) |
| Vida perdida por minuto (Assassin Ninja) | vazio | **~905** | 7 × 250 em 116 s |

Os campos lembrados no celular foram renomeados, então quem já usava o app passa a ver os padrões novos.

**Rápido × Econômico** (melee 72 → 90, ~11.560 ticks, poção básica):

| | Rápido antes | Rápido agora | Econômico antes | Econômico agora |
|---|---|---|---|---|
| **Com os dois bônus** (2,5× antes, 1,5× agora) | 1.201 poções, ~1,0 h | 1.267 poções, ~0,9 h | 405 poções, ~2,3 h | **394 poções, ~2,2 h** |
| **Sem bônus** | 3.002 poções, ~2,4 h | 1.901 poções, ~1,4 h | 1.013 poções, ~5,6 h | 590 poções, ~3,3 h |

- **Com os bônus ligados quase nada muda**: era o caso que os testes mediram, então antes e agora batem
  com eles. A sua lembrança de ~400 poções continua batendo com o Econômico (394).
- **Sem bônus o app ficou bem mais otimista** (~40% menos poção e tempo), porque antes ele "tirava" um
  bônus de 2,5× que, pelos dados, era só 1,5×.
- O **Rápido** continua ~2,3× mais rápido; o **Econômico** agora gasta ~3,2× menos poção (antes ~3×),
  porque o golpe normal ficou valendo mais.

### Vida perdida e defesa

```
vida devolvida pelas poções = 7 × 250 = 1.750 em 116 s ≈ 905 por minuto   (faixa: 724 a 1.086, pela faixa 200–300)
defesa alta  = 84/4 + 92 + 92 × 28/50 + 77  ≈ 241,5
defesa baixa = 84/8 + 92/2 + 92 × 28/100 + 77 ≈ 159,3
```

- Isso é com **4 ninjas** na maior parte do tempo, depois 2–3 e 1. Por ninja, dá uns **~225+ de vida por
  minuto**, mas não dá pra separar direito.
- 🔶 Falta: a vida no começo e no fim (se caiu, o dano foi maior) e a regeneração natural.
- A tela **Defesa** (nova) calcula a defesa alta/baixa do set e tem um medidor: tempo, poções, vida no
  começo e no fim → vida perdida por minuto.
- **Colar de vida:** regeneração de 2 → 3 por vez. 🔶 **Falta o intervalo** (de quanto em quanto tempo).

### Ganho de defesa (registro)

```
Defense 92: 60% → 75% = 15% × 818 ticks ≈ 123 ticks em 116 s ≈ 3.800 por hora   (faixa: ~90 a ~155)
```

Com o bônus de 1,5×, seriam ~82 ticks "base". Ainda não sabemos quanto vale cada golpe recebido, então
fica só registrado. 🔶 Hipótese pra testar depois: 1 tick por golpe recebido que a defesa bloqueia.

### Em aberto depois deste teste

- **O online vale no power training?** Agora o teste que separa é ainda mais útil: repetir o mesmo
  ritmo **depois que os 120 minutos acabarem**, ainda com a estamina verde.
  - Se render **o mesmo** (~1,4 tick por acerto): o online não vale no power (é o que o app supõe agora).
  - Se render **~60%**: os bônus somam (2,5×) e um acerto sem bônus vale ~0,57.
  - Se render **~50%**: multiplicam (3×).
- **Estamina branca, sem online:** dá o valor sem bônus direto (o app supõe ~0,95 por acerto).
- **Intervalo da regeneração de vida** (com e sem o colar).
- **Golpe normal vale 1 acerto?** Um teste B contado em vídeo, como este, resolveria.

## 6. Outros pontos em aberto (menor prioridade)

- **Virada do stat 54 → 55:** a fórmula da comunidade muda de curva aqui e dá números estranhos (o total de
  ticks "cai"). O app soma ponto a ponto, mas a estimativa nessa faixa é menos confiável. Quem tiver
  anotado quantos ticks levou de 54 pra 55 ajuda muito.
- **Vida do Assassin Ninja:** 160 (página do mob na wiki) ou 180 (lista da wiki)?
- **Vida do Djinn Lv 150:** 640 (helloyanis e Mims) ou 680 (wiki)?
- **Por que alguns mobs são pulados** no treino (ex.: Dead Eyes, Dragon Hatchling) e no power training
  (mobs que atacam de longe e chefes?).
