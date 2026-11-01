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
- **Como medir:** no mob em que você treina, com o seu set de sempre e o mesmo número de mobs, anote a
  vida, treine **1 minuto sem tomar poção de vida** (ou conte quantas tomou e quanto recupera cada uma) e
  veja quanto perdeu. Mande junto: mob, quantos mobs batiam em você, sua classe, level, defense e
  armadura.

## 5b. Quantos ticks rende cada especial

A estimativa de poções depende disso mais do que de qualquer outra coisa.

- **Hipótese atual (do Mims):** cada especial rende **1 tick por mob atingido**, até 5, vezes o bônus de exp.
  Então, com 4 mobs, cada poção básica (≈ 2 especiais) rende ≈ 8 ticks.
- **Dúvidas:**
  - O archer acerta **no máximo 3 mobs** no especial (wiki). Isso limita os ticks do archer a 3 por especial?
  - Os golpes normais entre um especial e outro também dão ticks? Se sim, gasta menos poção por tick.
  - O bônus de exp multiplica os ticks do especial?
- **Como testar:** anotar o **stat (com a porcentagem)** antes e depois de **100 especiais** contados, com o
  número de mobs atingidos. Assim dá pra calcular quantos ticks cada especial rendeu.

## 6. Outros pontos em aberto (menor prioridade)

- **Virada do stat 54 → 55:** a fórmula da comunidade muda de curva aqui e dá números estranhos (o total de
  ticks "cai"). O app soma ponto a ponto, mas a estimativa nessa faixa é menos confiável. Quem tiver
  anotado quantos ticks levou de 54 pra 55 ajuda muito.
- **Vida do Assassin Ninja:** 160 (página do mob na wiki) ou 180 (lista da wiki)?
- **Vida do Djinn Lv 150:** 640 (helloyanis e Mims) ou 680 (wiki)?
- **Por que alguns mobs são pulados** no treino (ex.: Dead Eyes, Dragon Hatchling) e no power training
  (mobs que atacam de longe e chefes?).
