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

## 4. Mana gasta pelo especial

- **Dúvida:** quanto de mana custa um especial? É igual pra todas as classes?
- **Hipótese do Mims (usada no app):** **50 de mana** por especial.
- **Como testar:** anote a mana antes e depois de **um** especial. Repita com outra classe, se puder.

## 5. Quanto cada poção de mana recupera (e quanto custa)

- **Dúvida:** quanta mana cada poção recupera, e o preço na loja.
- **Hipótese do Mims (usada no app):**

| Poção | Recupera | Preço |
|---|---|---|
| Mana Potion | 100 | 50 gold |
| Greater Mana Potion | 250 | 150 gold |
| Super Mana Potion | 500 | 350 gold |
| Ultimate Mana Potion | 750 | 650 gold |

- **Como testar:** com a mana baixa, tome **uma** poção de cada tipo e veja quanto subiu. O preço aparece
  na loja do NPC.

## 6. Outros pontos em aberto (menor prioridade)

- **Virada do stat 54 → 55:** a fórmula da comunidade muda de curva aqui e dá números estranhos (o total de
  ticks "cai"). O app soma ponto a ponto, mas a estimativa nessa faixa é menos confiável. Quem tiver
  anotado quantos ticks levou de 54 pra 55 ajuda muito.
- **Vida do Assassin Ninja:** 160 (página do mob na wiki) ou 180 (lista da wiki)?
- **Vida do Djinn Lv 150:** 640 (helloyanis e Mims) ou 680 (wiki)?
- **Por que alguns mobs são pulados** no treino (ex.: Dead Eyes, Dragon Hatchling) e no power training
  (mobs que atacam de longe e chefes?).
