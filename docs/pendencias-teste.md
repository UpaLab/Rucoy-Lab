# Pendências de teste no jogo

> Lista para pedir ajuda à comunidade. Cada item diz **o que não sabemos**, **o que as fontes dizem** e
> **como dá para testar**. Fórmulas da comunidade, não oficiais.
>
> Ao mandar um resultado, inclua: classe, level, stat, arma (ataque e +buff), mob, e se usou anel ou bônus
> de exp. Um print ajuda muito.
>
> **Vai gravar um teste?** Veja o passo a passo em [`guia-cientistas.md`](guia-cientistas.md).

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
| Ticks do especial | **~1,4 por mob atingido com a estamina verde** (~276 acertos → ~396 ticks). Sem bônus, 🔶 ~0,95 | teste quadro a quadro, 02/11/2026 (seção 5c) |
| Intervalo entre especiais (só especial) | **~1,33 s** com as pausas (116 s ÷ ~87); teste A deu ~1,45 s | teste de 02/11 e teste A |
| Mobs atingidos por especial | **~3,2 em média** puxando 4 (4 → 2–3 → 1 conforme morrem) | teste de 02/11 (contado em vídeo) |
| Mana por poção básica no teste | **~93** (dentro da faixa 70–130 da wiki) | teste A |
| **Mob cansa** (mensagem vermelha de mob cansado; 🔶 texto exato a confirmar): cada mob só dá stat por ~7 min 30 s. Vale **por indivíduo**: outros do mesmo tipo continuam dando stat | sim, de forma aproximada (Assassins duravam mais de 5 min sem morrer com melee ~70–80) | wiki + observação do dono |
| **Bônus de exp: existe UM só**, o da estamina verde. A estamina vai até 26 h e fica branca abaixo de 24 h; a parte verde são exatamente as últimas 2 h. A mensagem "You have 120 minutes of bonus level and skill experience" é o jogo avisando quanto resta da parte verde | sim | dono do projeto, 02/11/2026 |
| Quanto vale a estamina verde | **1,5×**. ❗ **Conflito de fontes:** a wiki diz 2×. O teste de 02/11 (~1,4 tick por acerto) bate com 1,5× | experiência do dono + teste de 02/11 (seção 5c) × wiki |
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
- ✅ O especial rende **~1 tick por mob atingido** sem bônus (ver a recalibração na seção 5c).
- ✅ **Rápido** (só especial) sobe o stat ~2,3× mais rápido. **Econômico** (especial + golpes) gasta ~2,6×
  menos poção por tick.

**Bônus durante os testes:** a estamina estava em **26:00:00 verde** e apareceu **"You have 120 minutes of
bonus level and skill experience"**. É **um bônus só**: a mensagem diz quanto resta da parte verde da
estamina (as 2 h acima de 24 h). Vale **1,5×** pela experiência do dono (❗ a wiki diz 2×).

> ⚠️ **Histórico corrigido (02/11/2026).** Antes, este documento tratava a mensagem dos 120 minutos como um
> segundo bônus ("treino online", leitura nossa das notas do update 1.25) e calibrava os testes com 2× e
> depois 2,5×. **Isso estava errado:** é um bônus só, de 1,5×. Os valores certos estão na seção 5c.

**Em aberto:**
- **Mob cansado: matar e renascer zera?** A wiki diz que **não** (o mob renasce ainda cansado e leva 1 hora pra
  voltar), mas isso não foi conferido. **Como testar:** num mob que já cansou, matar, esperar renascer e ver
  se a mensagem volta logo no primeiro golpe.
- **Quanto tempo se perde trocando de mob:** o app usa **15 s** a cada 7 min 30 s (suposição, editável).
- Repetir os testes com outros stats, armas e mobs, pra ver se os números se mantêm.

## 5c. Teste quadro a quadro (02/11/2026) ✅ — o mais preciso até agora

Contado quadro a quadro em vídeo pelo dono. Melee, Assassin Ninjas, power training só com especial
(golpes normais só num trecho em que a mana zerou). **Estamina verde** (a mensagem "120 minutes of bonus" é ela).

### O personagem

| | Valor |
|---|---|
| Level / HP / MP | 84 / 1.360 / 940 |
| Stats | Defense 92, Melee 99 → 100 durante o teste |
| Arma | adaga de treino (ataque 5) |
| Defesa dos itens | capacete 12, armadura 18, calça 15, bota 9, escudo 28, anel 9, um item verde de 2 e dois itens de 6 (🔶 tipos a confirmar) = **105** (77 sem o escudo) |
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
- Se 1 acerto = **1 tick** (o mais simples): o bônus foi **~1,4–1,5×**. Isso bate com a **estamina verde
  de 1,5×**, o único bônus de exp que existe.
- Pra bater com os 2× da wiki, cada acerto valeria só ~0,72 tick sem bônus, o que não tem motivo pra ser assim.

🔶 Então o app supõe **1,5×** e **~0,95 tick por acerto sem bônus**. Ainda é
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
| Bônus nos testes (estamina verde, um bônus só) | 2× e depois 2,5× (supunha dois bônus) | **1,5×** | experiência do dono + teste 02/11 |
| Ticks por mob no especial (sem bônus) | 0,77 | **0,95** | 1,43 ÷ 1,5 |
| Ticks por golpe normal (sem bônus) | 0,42 | **0,75** | teste B ÷ 1,5 |
| Mobs atingidos por especial | 2,5 | **3,2** | 276 ÷ 87 |
| Intervalo entre especiais | 1,45 s | **1,33 s** | 116 ÷ 87 |
| Golpes entre especiais | 9 | 9 | teste B (igual) |
| Vida perdida por minuto (Assassin Ninja) | vazio | **~905** | 7 × 250 em 116 s |

Os campos lembrados no celular foram renomeados, então quem já usava o app passa a ver os padrões novos.
Na tela Progresso ficou **uma chave só**, "Estamina verde", com o multiplicador editável (padrão 1,5×).

**Rápido × Econômico** (melee 72 → 90, ~11.560 ticks, poção básica):

| | Rápido antes | Rápido agora | Econômico antes | Econômico agora |
|---|---|---|---|---|
| **Com a estamina verde** (2,5× antes, 1,5× agora) | 1.201 poções, ~1,0 h | 1.267 poções, ~0,9 h | 405 poções, ~2,3 h | **394 poções, ~2,2 h** |
| **Sem bônus** | 3.002 poções, ~2,4 h | 1.901 poções, ~1,4 h | 1.013 poções, ~5,6 h | 590 poções, ~3,3 h |

- **Com a estamina verde quase nada muda**: era o caso que os testes mediram, então antes e agora batem
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

- **Power training com a estamina BRANCA (sem bônus)** ⭐: o mesmo ritmo deste teste com a estamina abaixo
  de 24 h. Dá o valor sem bônus direto e resolve o conflito com a wiki:
  - se render **~2/3** deste teste (~0,95 tick por acerto): a estamina verde vale **1,5×** (o app supõe isso);
  - se render **~1/2** (~0,72 tick por acerto): vale **2×**, como diz a wiki.
  - Quem joga pouco não consegue (a estamina enche de novo quando fica off): é pra quem joga muitas horas.
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
