# Comparação das fontes de fórmulas

> Investigação de 01/11/2026. **Nada foi alterado no app.** Este documento só compara as fontes e recomenda.
> Todas as fórmulas aqui são **da comunidade, não oficiais**.

## As duas fontes

| | **H**: helloyanis/rucoy-calculator | **M**: Mims-Rucoy-Calculator |
|---|---|---|
| Link | <https://github.com/helloyanis/rucoy-calculator> | <https://github.com/Mimsqueeze/Mims-Rucoy-Calculator> |
| O que é | App Android (Kotlin) | Bot de Discord (Java) |
| Autor | helloyanis | **Minsi Hu ("mims", GitHub Mimsqueeze)** |
| Agradecimentos da própria fonte | "Training formulas by Mims" | Tribrid (parte das fórmulas), potatoking (desenvolvimento), Hikari (organizador da comunidade), comunidade Rucoy |
| Licença | **GPL-3.0** desde 11/02/2025. Antes disso era **CC BY-NC-SA 4.0** | **MIT**, "Copyright (c) 2022 Minsi Hu" |
| Versão lida | commit `f8677ba` (17/07/2025) | commit `c13fb8c` (17/07/2023) |
| Arquivos lidos | `formulas/Formulas.kt`, `ui/train/TrainFragment.kt`, `ui/skull/SkullFragment.kt` | `src/bot/Formulas.java`, `src/bot/Bot.java` |

**Sobre o "criador hikari.ascii":** pelo código e pelo README, quem criou e mantém o bot é o **Minsi Hu
("mims")**, que também é o dono do copyright na licença MIT. O **Hikari** aparece nos agradecimentos como
"organizador da comunidade". Se "hikari.ascii" for o perfil atual do Hikari no Discord, vale a pena
creditar os dois.

### Uma descoberta importante: H é uma adaptação de M

O código de treino do H é praticamente uma **tradução do M**: mesmos nomes de funções
(`auto_min_raw_damage_Calc`, `threshold_Calc`…), mesmos comentários ("In certain cases you can effective
train on two mobs") e os mesmos índices de mobs pulados. Em 2023, o primeiro commit do H tinha até os
mesmos números de mobs do M. O próprio H credita: "Training formulas by Mims".

**Consequência para nós:** o nosso `Formulas.kt` foi adaptado do H, então ele também carrega código que
nasceu no M (MIT). Por isso, **o aviso MIT do Mims precisa estar no nosso projeto**, além do crédito ao H.
Ver a seção "Licenças".

---

## 1. Fórmula por fórmula

Legenda: ✅ **igual** nas duas · ⚠️ **diferente** · ➕ **só existe em uma**.

| # | Cálculo | H (helloyanis) | M (Mims) | Situação | Recomendação |
|---|---|---|---|---|---|
| 1 | **Dano mín./máx. (ataque normal)** | `stat×arma/20 + level/4` e `stat×arma/10 + level/4` | idem | ✅ | Usar (as duas concordam) |
| 2 | **Especial melee/distance** | 1,5 × normal | idem | ✅ | Usar |
| 3 | **Especial magic** | `1,5 × (1,08×stat×arma/20 + 9×level/32)` | mesma conta com **1,05** | ⚠️ | **1,08 (H)**, ainda pendente de validação. Ver nota A |
| 4 | **Golpe crítico (dano)** | ×1,05; com anel de crítico ×1,075 | ×1,05 (sem anel) | ➕ anel só no H | Usar 1,05. O anel fica como **experimental** |
| 5 | **Precisão** (chance de causar dano) | `normal×0,99 + crítico×0,01`. Com anel: `crítico×0,075` | `normal×0,99 + crítico×0,01` | ✅ sem anel · ⚠️ com anel | Sem anel: as duas iguais. Com anel: ver nota B |
| 6 | **Dano médio** | igual ao M sem anel; com anel usa peso 0,035 | `precisão×0,99×média + 0,01×média do crítico` | ✅ sem anel · ⚠️ com anel | Igual à 5 |
| 7 | **Ticks/h, ataque normal** | `3600 × (1 − (1−precisão)^10)` | idem | ✅ | Usar |
| 8 | **Precisão mínima** | `1 − 0,8251^(1/nº de mobs)` (0,1749 com 1 mob) | idem | ✅ | Usar |
| 9 | **Ticks/h, power training** | teto `nº×3600` (máx. 18.000) × `1 − ((1−precisão)^nº)^10` | idem | ✅ | Usar |
| 10 | **Mobs pulados** (treino normal / power) | normal: 26, 31 · power: 13, 19, 20, 22, 24, 25, 26, 29, 31, 33, 36, 37 e ≥39 | **mesmos índices** | ✅ | Usar. O motivo continua sem explicação nas duas |
| 11 | **Ticks acumulados até o stat S** | `S^(S/1000+2,373)` até 54; `S^(S/1000+2,171)` de 55 em diante | idem (com as mesmas alternativas comentadas) | ✅ | Usar, **com aviso**: as duas têm a mesma "queda" entre 54 e 55 (ver `formulas.md`, seção 7) |
| 12 | **Exp por level** | `level^(level/1000+3)` | idem | ✅ | Usar. O M deixa claro que é a **exp total** acumulada até o level (o `/exp` subtrai um level do outro) |
| 13 | **Defesa (dano recebido)** | `level/4 + def + def×escudo/50 + outros` (alta) e `level/8 + def/2 + def×escudo/100 + outros` (baixa) | não tem | ➕ só H | Usar a do H (é a única), marcada como não validada |
| 14 | **One-shot (consistência)** | mesma fórmula | idem | ✅ | Usar |
| 15 | **/grind** (tempo até um level) | só mostra a exp até o **próximo** level | `(exp(alvo) − exp(atual)) ÷ exp por hora`. Padrão: 2.000.000 exp/h | ➕ só M | **Usar a do M** (MIT), pedindo a exp/h ao jogador em vez do padrão fixo |
| 16 | **/stat** (tempo até um stat) | não tem como tela própria | `(ticks(alvo) − ticks(atual)) ÷ ticks por hora` + estimativa de poções | ➕ só M | **Usar a do M** na tela Progresso. Ver nota C |
| 17 | **/offline** (boneco de treino) | 600 ticks/h. ❗ **Erro:** com meta de stat, usa a fórmula do 55+ **para qualquer stat**, inclusive abaixo de 55 | 600 ticks/h, escolhendo a fórmula certa pela faixa do stat | ⚠️ | **Usar a do M** (sem o erro) |
| 18 | **Skull** (gold por caveira) | branca 150, amarela 150, laranja 600, vermelha 1950, preta 6000 (× level). Também mostra o "gold perdido ao morrer" | amarela 150, laranja 600, vermelha 1950, preta 6000 (× level) | ✅ (o H tem mais) | Usar a do H (mais completa) |
| 19 | **Preço de poções** | não tem | normal 50, greater 150, super 350, ultimate 650 gold | ➕ só M | Usar como valor inicial, **editável** pelo jogador (preço pode mudar) |
| 20 | **HP/MP por level** | HP `100 + 15×level`, MP `100 + 10×level` | não tem | ➕ só H | Usar, marcada como não validada |
| 21 | **Bônus (buffs) no stat** | só no one-shot | em `/train` e `/ptrain`: `stat + buffs` | ➕ M mais completo | **Acrescentar o campo "buffs"** na Onde treinar (seguindo o M) |
| 22 | **Lista de armas** | 34 armas (de Treino 4 até Golden 58+5) | **a mesma lista** | ✅ | Usar. ❗ As duas têm o mesmo erro: "Golden 56+4" e "58+5" estão com os valores 54 e +3 |

### Notas

**A. Mago 1,08 × 1,05.** O M usa 1,05. O H mudou de propósito para 1,08 em 08/01/2024 ("Big thing… overhaul
of the app"), mas deixou uma cópia antiga com 1,05 no mesmo arquivo. Como a mudança foi intencional e é
mais recente, **recomendo 1,08**. Mas é o ponto que mais precisa de teste no jogo com um mago.

**B. Anel de crítico.** Só o H tem. Ele usa números que não combinam entre si: 7,5% na precisão, 3,5% no
dano médio, e os pesos passam de 100%. **Recomendo:** a precisão sem anel fica igual nas duas fontes. O anel
aparece no app como "experimental", com uma conta coerente: `normal × (1 − p) + crítico × p`, em que `p` é
a chance de crítico. Isso dá exatamente o mesmo resultado das fontes sem anel (`p = 1%`). O valor de `p`
com anel **precisa ser descoberto** com a comunidade ou com testes.

**C. Estimativa de poções do M (/stat).** O M supõe power training com 4 mobs: cada especial gasta 50 de
mana e dá 4 ticks. A conta é `mana = 50 × ticks ÷ 4`, e depois divide pela mana que cada poção recupera:
100 (normal), 250 (greater), 500 (super) e 750 (ultimate). Com bônus de exp (1,5×, 2×, 2,5×), cada
especial rende mais ticks. 🔶 Esses números (custo do especial e mana de cada poção) são suposições do M e
precisam de validação. É uma ótima base para a tela **Poções**.

---

## 2. Lista de mobs

| | H | M |
|---|---|---|
| Quantidade | **44** (vai até o Demon Lv 450) | **41** (vai até o Yeti; Lava Golem, Orthrus e Demon estão comentados) |
| Última mudança nos mobs | 12/02/2025 (acrescentou Lava Golem, Orthrus e Demon) | 31/12/2022 ou antes |
| Último commit do projeto | 17/07/2025 | 17/07/2023 |

### Diferenças (nos outros 38 mobs, nome, defesa e vida são iguais)

| Mob | H | M | Wiki (Monster List, atualizada por um GM em 05/06/2023) | Quem parece certo |
|---|---|---|---|---|
| Minotaur Lv 250 (defesa) | **601** | 591 | (a wiki não tem defesa) | H (ver abaixo) |
| Minotaur Lv 275 (defesa) | **691** | 681 | (sem defesa) | H (ver abaixo) |
| Ice Dragon Lv 320 (vida) | **45.000** | 50.000 | **45.000** | **H** (a wiki confirma) |
| Yeti Lv 350 (vida) | **55.000** | 60.000 | **55.000** | **H** (a wiki confirma) |
| Orthrus (level) | **400** | 425 (comentado) | **400** | **H** (a wiki confirma) |
| Lava Golem, Orthrus, Demon | tem | não tem (comentados) | tem | **H** |

**Como eu sei que o H atualizou de propósito:** no primeiro commit do H (10/09/2023), os valores eram
**iguais aos do M** (591, 681, 50.000, 60.000). Em 08/01/2024, o H trocou para 601, 691, 45.000 e 55.000.
Nas duas vidas que a wiki tem, a wiki concorda com o H. Nas defesas dos Minotaurs, não existe uma
terceira fonte, mas, como o H mudou os quatro números juntos e acertou os dois que dá para conferir, é
razoável confiar nele.

**Recomendação:** manter a lista do **H** (é a que já está em `data/mobs.json`). As diferenças ficam
registradas em `docs/mobs-validacao.md`, e as defesas dos Minotaurs 250 e 275 entram para a lista de
conferência no jogo.

---

## 3. Resumo da recomendação

| Assunto | Usar | Por quê, em linguagem simples |
|---|---|---|
| Dano, precisão, ticks, power training, ticks por stat, exp, one-shot | **Qualquer uma** (são iguais) | As duas fontes fazem a mesma conta |
| Especial do mago | **H (1,08)** | Mudança mais nova e de propósito. Precisa de teste no jogo |
| Anel de crítico | **Conta nova, coerente** (experimental) | As duas fontes não têm um valor confiável |
| Defesa, HP/MP, skull | **H** | Só o H tem, ou o H é mais completo |
| /grind, /stat, /offline, preço de poções | **M** | Só o M tem, e o /offline do H tem um erro |
| Campo "buffs" | **M** | O M já soma o bônus no stat; o H esqueceu na tela de treino |
| Lista de mobs | **H** | Mais nova, mais completa, e a wiki confirma onde dá para conferir |
| Lista de armas | Qualquer uma (são iguais) | Corrigir as duas Golden com valor repetido |

---

## 4. Licenças e créditos

### helloyanis/rucoy-calculator: GPL-3.0

- Só usamos a versão **depois de 11/02/2025**, quando a licença virou GPL-3.0. As versões antigas eram
  **CC BY-NC-SA 4.0**, que **proíbe uso comercial**. Não vamos copiar nada dessas versões.
- O UpaLab também é GPL-3.0, então podemos adaptar o código, desde que:
  1. o nosso projeto continue GPL-3.0, com o código aberto (já está);
  2. cada arquivo adaptado diga de onde veio e que foi modificado (já fazemos no topo de cada arquivo);
  3. o crédito apareça no README e na tela de Créditos (já está).

### Mims-Rucoy-Calculator: MIT

A MIT é uma licença bem permissiva: deixa copiar, modificar e até usar dentro de um projeto GPL. A única
obrigação de verdade é esta, em linguagem simples:

> **Sempre que usarmos o código (ou uma parte importante dele), temos que incluir junto o aviso de
> copyright "Copyright (c) 2022 Minsi Hu" e o texto da licença MIT.**

Na prática, quando reaproveitarmos algo do Mims (como /grind, /stat, /offline e poções), vamos:

1. **Criar um arquivo `LICENSES/MIT-Mims.txt`** com o texto completo da licença MIT do Mims, sem mudar
   nada (inclusive a linha do copyright).
2. **No topo de cada arquivo adaptado**, escrever algo como:
   ```
   Partes adaptadas de Mims-Rucoy-Calculator (https://github.com/Mimsqueeze/Mims-Rucoy-Calculator)
   Copyright (c) 2022 Minsi Hu — licença MIT (ver LICENSES/MIT-Mims.txt).
   Modificado pelo UpaLab; o conjunto é distribuído sob GPL-3.0.
   ```
3. **Na tela de Créditos e no README:** citar Minsi Hu (mims) como autor, a licença MIT e os
   agradecimentos que o próprio Mims faz (Tribrid, potatoking, Hikari).
4. **Não copiar** os arquivos que não são código de cálculo, como o `Token.env` e os logs, nem os emojis
   do Discord.

**Pendência (não feita, porque o app não deve ser alterado agora):** como o nosso `Formulas.kt` atual já
carrega código que nasceu no Mims (por meio do H), o passo 1 e o aviso do passo 2 **devem ser acrescentados
mesmo antes de reaproveitarmos algo novo**. Vou fazer isso junto com a próxima mudança no app, se você
aprovar.

### O que a MIT **não** exige
- Não exige que o nosso código seja MIT: podemos continuar GPL-3.0.
- Não exige pedir permissão.
- Não dá garantia nenhuma: o código vem "como está".

---

## 5. Pendências que saíram desta comparação

- [ ] Testar no jogo o especial do **mago** (1,08 × 1,05).
- [ ] Descobrir a chance de crítico **com anel** (`p`).
- [ ] Conferir no jogo a defesa dos **Minotaurs 250 e 275** (601/691 × 591/681).
- [ ] Validar a estimativa de **poções** do M (custo do especial e mana de cada poção).
- [ ] Acrescentar `LICENSES/MIT-Mims.txt` e o aviso MIT nos arquivos adaptados (próxima mudança no app).
- [ ] Corrigir as armas "Golden 56+4" e "58+5" quando montarmos a lista de armas.

---

## 6. Reescrita do código (decisão do dono, 01/11/2026)

O dono do projeto decidiu **não copiar nem adaptar código de ninguém**. O cálculo foi reescrito do zero
(`app/src/main/java/io/github/upalab/rucoylab/calculo/`), usando como guia só este documento e o
`docs/formulas.md`. Das fontes vêm apenas as **fórmulas** e os **dados** (tabela de mobs), com crédito.

Para ser transparente: quem reescreveu (o assistente) já tinha lido o código das duas fontes durante a
comparação. A reescrita tem organização, nomes e estrutura próprios, mas não é um "clean room" formal
(quando uma pessoa reescreve sem nunca ter visto o original).

### O que mudou de propósito em relação às fontes

| Ponto | Fontes | Reescrita | Efeito |
|---|---|---|---|
| Chance de acertar | podia dar **negativa** | limitada entre 0 e 100% | Nenhum na escolha do mob (conferido em 600 fichas) |
| Ataque normal × power | duas contas separadas | **uma conta só** ("power com 1 mob" = normal) | Mesmo resultado |
| Ticks entre stats | subtrai os totais das curvas: **50 → 60 dá −3.810** | soma **ponto a ponto**; do 54 em diante usa a curva alta | Igual às fontes dentro de cada faixa; nunca negativo |
| Offline | helloyanis errava abaixo do 55 | curva certa em todas as faixas (como o Mims) | Corrige o erro |
| Anel de crítico | pesos que não combinam (7,5% × 3,5%) | **um** peso só, 3,5%, marcado como **experimental** | Muda só quem liga o anel |
| Mago | 1,08 (H) × 1,05 (M) | 1,08, marcado como **a confirmar** | — |
| Stats que faltam pro próximo mob | helloyanis/Mims somam 1 a mais | número exato | — |
| Buffs | só o Mims tinha em /train | campo "Buffs" na Onde treinar | — |

**Conferência:** 600 fichas aleatórias (as 3 classes, ataque normal e power training de 1 a 5 mobs, sem
anel) escolhem **exatamente o mesmo mob** que a regra das fontes (teste `seiscentasFichasIguaisAsFontes`).

### O aviso de licença do código ainda é necessário?

**Resposta curta: o aviso de licença do código (o texto MIT do Mims ou o aviso GPL do helloyanis) deixa de
ser obrigatório. Fica o crédito pelas fórmulas e pelos dados, que vamos manter de qualquer jeito.**

Por quê, em linguagem simples:

- Licenças como MIT e GPL protegem o **código**, ou seja, o jeito como o programa foi escrito. Elas não
  protegem **ideias, fórmulas matemáticas nem fatos**, como "o dano máximo é stat × arma ÷ 10 + level ÷ 4"
  ou "o Yeti tem 55.000 de vida".
- Como não sobrou nenhum trecho de código copiado ou adaptado, não há código das fontes para levar o
  aviso junto.
- O **crédito** continua (README e tela de Créditos), porque é o certo: foram eles que descobriram e
  organizaram tudo isso. Também deixa claro de onde vêm os números, caso alguém precise conferir.

Cuidados que continuam valendo:

- A **tabela de mobs** (`data/mobs.json`) foi extraída do helloyanis. Números de mob são fatos, mas uma
  **coleção organizada** de dados pode ter alguma proteção em alguns países (por exemplo, o "direito de
  base de dados" na União Europeia). Como o UpaLab também é GPL-3.0, igual ao helloyanis, **não existe
  conflito** mesmo nesse caso. Deixamos registrado no `data/mobs.json` de onde veio cada dado.
- Se no futuro **copiarmos código** de qualquer um dos dois (por exemplo, para o bot de Discord), o aviso
  da licença volta a ser obrigatório naquele arquivo.
- Isso não é uma consultoria jurídica. Se o app virar algo comercial, vale confirmar com um especialista.
