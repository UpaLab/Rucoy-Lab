# Fórmulas da comunidade

> ⚠️ **Fórmulas da comunidade, não oficiais.** Os desenvolvedores do jogo nunca publicaram essas
> fórmulas. Elas foram deduzidas por jogadores e podem estar erradas ou desatualizadas.
> A tabela de mobs da fonte foi alterada pela última vez em **25/04/2025** e **precisa ser validada**
> no jogo.

## Fonte e crédito

- **Projeto:** [helloyanis/rucoy-calculator](https://github.com/helloyanis/rucoy-calculator) (GPL-3.0)
- **Arquivo:** `src/app/src/main/java/com/helloyanis/rucoycalculator/formulas/Formulas.kt`
- **Commit lido:** `f8677ba` (último commit do repo: 17/07/2025; última alteração do `Formulas.kt`: 25/04/2025)
- O README da fonte credita **Mims** pelas fórmulas de treino:
  [Mimsqueeze/Mims-Rucoy-Calculator](https://github.com/Mimsqueeze/Mims-Rucoy-Calculator).

Também foi lido o `ui/train/TrainFragment.kt` da mesma fonte, para ver **como** as fórmulas são usadas.
Os números de linha citados abaixo são do `Formulas.kt` (`F:`) ou do `TrainFragment.kt` (`T:`).

### Legenda

| Marca | Significado |
|---|---|
| ✅ **Conferido na fonte** | Está escrito assim no código da fonte (conferido linha a linha). Isso **não** quer dizer que está certo no jogo. |
| 🔶 **Interpretação** | Explicação nossa ou da comunidade. Não aparece no código. |
| ❗ **Inconsistência** | Algo estranho no código da fonte que precisa ser investigado. |

### Variáveis

| Nome | Significado |
|---|---|
| `stat` | skill de ataque usada (melee, distance ou magic) |
| `atk` | ataque da arma |
| `level` | level base do personagem (`base` no código) |
| `def_mob` | defesa do mob (`data/mobs.json`) |
| `n` | nº de mobs atacando juntos no power training (`tick` no código) |

---

## 1. Dano bruto (antes da defesa do mob)

### Ataque automático ✅ (F:6–12)

```
mín_bruto = stat * atk / 20 + level / 4
máx_bruto = stat * atk / 10 + level / 4
```

### Especial melee / distance ✅ (F:22–28)

1,5× o auto:

```
mín_bruto = 1.5 * (stat * atk / 20 + level / 4)
máx_bruto = 1.5 * (stat * atk / 10 + level / 4)
```

### Especial magic ✅ (F:30–36)

```
mín_bruto = 1.5 * (1.08 * stat * atk / 20 + 9 * level / 32)
máx_bruto = 1.5 * (1.08 * stat * atk / 10 + 9 * level / 32)
```

❗ O mesmo arquivo tem uma **cópia antiga** dessas fórmulas dentro de `Weapon.Formulas` (F:612+), que usa
**1.05** no lugar de **1.08** (F:642, F:650). O app usa a versão de cima (1.08). Precisa confirmar qual vale.

### Crítico ✅ (F:62–68)

```
máx_bruto_crit = máx_bruto * 1.05    (sem anel de crítico)
máx_bruto_crit = máx_bruto * 1.075   (com anel de crítico)
```

## 2. Dano real contra o mob ✅ (F:38–56, F:70–72)

```
dano_mín  = max(0, mín_bruto - def_mob)
dano_máx  = máx_bruto - def_mob
dano_crit = máx_bruto_crit - def_mob
```

O dano máximo **não** é limitado a 0 no código. Se ficar negativo, você não causa dano.

## 3. Precisão (chance de causar dano)

### Precisão normal ✅ (F:74–81)

```
precisão_normal = min(1, (máx_bruto - def_mob) / (máx_bruto - mín_bruto))
```

### Precisão do crítico ✅ (F:92–99)

```
precisão_crit = min(1, (máx_bruto_crit - def_mob) / (máx_bruto_crit - máx_bruto))
```

### Precisão final, com ajuste de crítico ✅ (F:101–123)

```
sem anel:  precisão = precisão_normal * 0.99 + precisão_crit * 0.01
com anel:  precisão = precisão_normal * 0.99 + precisão_crit * 0.075
```

🔶 Interpretação: cerca de 1% dos ataques são críticos sem o anel. O peso 0.99 seria o restante.

❗ Problemas encontrados:
- Com anel, os pesos somam 0.99 + 0.075 = 1.065, ou seja, mais que 100%.
- No dano médio (seção 4), o peso do crítico com anel é **0.035**, não 0.075.
- Na cópia antiga (F:695–716), os pesos estão **trocados**: 0.01 com anel e 0.035 sem.

**Não dá pra saber qual é a chance de crítico com anel. Precisa validar.**

A precisão pode dar **negativa** quando `máx_bruto < def_mob`, porque o código só limita o valor por cima.

## 4. Dano médio e tempo pra matar ✅ (F:129–145)

```
sem anel:  dano_médio = precisão * 0.99 * (dano_máx + dano_mín)/2 + 0.01  * (dano_crit + dano_máx)/2
com anel:  dano_médio = precisão * 0.99 * (dano_máx + dano_mín)/2 + 0.035 * (dano_crit + dano_máx)/2

tempo_pra_matar = vida_mob / dano_médio     (em ataques; o app mostra como segundos)
```

🔶 O app trata o resultado como segundos, então assume **1 ataque por segundo**. Isso bate com o teto de
3600 ticks/h.

## 5. Ticks de skill — treino no automático

### Ticks por hora ✅ (F:147–149, uso em T:881)

```
ticks_por_hora = 3600 * (1 - (1 - precisão)^10)
```

O teto é **3600/h**.

🔶 Interpretação da comunidade: o tick só conta se você causar dano **pelo menos 1 vez a cada 10 ataques**.
O expoente 10 da fórmula é compatível com isso, mas o código não explica a regra.

### Escolha do mob ideal ✅ (T:858–871)

O app testa os mobs do **mais forte pro mais fraco**. Ele escolhe o primeiro com **precisão ≥ 0.1749**.

🔶 Por que 0.1749? Com essa precisão, `1 - (1 - 0.1749)^10 ≈ 0.854`, ou seja, ~85% do teto de ticks.
0.1749 é exatamente `1 - 0.8251^(1/1)` (seção 6, com n = 1).

❗ O treino automático **pula os índices 26 (Dead Eyes Lv.170) e 31 (Dragon Hatchling Lv.240)** da tabela
(T:862). O código não diz o motivo.
🔶 Provavelmente são mobs de evento/especiais ou de acesso restrito.

## 6. Ticks de skill — power training (vários mobs)

### Teto ✅ (F:155–161)

```
teto = n * 3600     se n ≤ 5
teto = 18000        se n > 5
```

### Precisão mínima ✅ (F:232–234, uso em T:1034–1046)

```
precisão_mín = 1 - 0.8251^(1/n)
```

| n mobs | precisão mínima |
|---|---|
| 1 | 0.1749 |
| 2 | 0.0916 |
| 3 | 0.0621 |
| 4 | 0.0469 |
| 5 | 0.0377 |

(valores calculados por nós a partir da fórmula)

### Ticks por hora ✅ (F:125–127, F:151–153)

```
precisão_total = 1 - ((1 - precisão)^n)^10
ticks_por_hora = teto * precisão_total
```

O power training usa o dano do **especial** (seção 1) para calcular a precisão (T:1017–1028).

❗ O power training **pula muitos mobs** (T:1040): índices 13, 19, 20, 22, 24, 25, 26, 29, 31, 33, 36, 37
e todos a partir do 39. São eles: Skeleton Archer, Drow Ranger, Drow Mage, Drow Sorceress, Lizard Archer,
Lizard Shaman, Dead Eyes, Lizard High Shaman, Dragon Hatchling, Dragon, Dragon Warden, Ice Elemental,
Minotaur Lv.275, Ice Dragon, Yeti, Lava Golem, Orthrus e Demon.
🔶 Provavelmente porque mobs que atacam de longe ou chefes não dá pra juntar e bater em grupo. Precisa
confirmar.

## 7. Ticks acumulados até o stat S ✅ (F:167–176)

```
S ≤ 54:  ticks(S) = S^(S/1000 + 2.373)
S ≥ 55:  ticks(S) = S^(S/1000 + 2.171)
```

Para achar o stat a partir dos ticks (F:191–230), o código procura em qual faixa os ticks caem e interpola
linearmente entre `S-1` e `S`. A busca vai de 5 a 54 e depois de 55 a 1000. Fora disso, retorna `-1`.

❗ **Descontinuidade na virada 54 → 55** (calculado por nós):
`ticks(54) ≈ 16015`, mas `ticks(55) ≈ 7483`. O total de ticks **cai** de um stat pro outro, o que não faz
sentido para um valor acumulado. O arquivo tem fórmulas alternativas comentadas (F:172–174, F:178–190)
com deslocamentos (offsets), o que sugere que o autor já sabia disso.
**Não usar perto do stat 55 sem validar.**

## 8. Experiência por level ✅ (F:163–165)

```
exp(level) = level^(level/1000 + 3)
```

🔶 A fonte não diz se é a exp total acumulada até o level ou a exp desse level. Precisa validar.

## 9. Defesa (dano que o mob causa em você) ✅ (F:14–20, uso em T:1304–1345)

```
defesa_alta  = level/4 + def + (def * armadura_escudo / 50)  + armadura_outros
defesa_baixa = level/8 + def/2 + (def * armadura_escudo / 100) + armadura_outros
```

- `def` = sua skill de defesa
- `armadura_escudo` = valor de armadura do escudo
- `armadura_outros` = soma da armadura das outras peças

Dano recebido (F:46–60):

```
dano_mín_recebido = max(0, ataque_mín_mob - defesa)
dano_máx_recebido = ataque_máx_mob - defesa
```

O app calcula esses dois valores para a defesa alta e para a defesa baixa.

🔶 Interpretação: `defesa_alta` e `defesa_baixa` seriam o maior e o menor valor de defesa possíveis em
cada golpe. O dano recebido varia entre esses dois casos.

❗ Pontos em aberto:
- Se a classe não for melee, o app força `armadura_escudo = 10` (T:1310–1312). Ninguém sabe se o jogo faz
  isso ou se é uma simplificação do app.
- A tabela de mobs **não tem ataque mín/máx**: os campos existem, mas estão zerados (F:582). O usuário
  digita esses valores na mão (`//TODO replace with actual mob values`, T:1316–1317).
- A precisão da defesa (T:1321, T:1336) passa os argumentos numa ordem que não bate com a assinatura da
  função (F:83). O resultado nem é exibido.

## 10. One-shot (consistência) ✅ (F:236–258)

```
total = vida_mob + def_mob
se máx_bruto_crit < total  → 0 (não dá one-shot)
se máx_bruto > total       → (máx_bruto - total) / (máx_bruto - mín_bruto) * 0.99 + 0.01
senão (só no crítico)      → (máx_bruto_crit - total) / (máx_bruto_crit - máx_bruto) * 0.01
```

## 11. Tabela de armas (referência)

A fonte tem uma lista de ataques de arma (F:307–580). Ela não foi copiada para `data/` nesta fase.

❗ As entradas "Golden Weapon(56+4)" e "(58+5)" estão com os valores **54 e 3** (copiados da entrada
anterior).

---

## Pendências de validação

- [ ] Peso do crítico com anel (0.075 × 0.035) e soma > 1.
- [ ] Multiplicador do especial magic (1.08 × 1.05).
- [ ] Descontinuidade dos ticks no stat 54 → 55.
- [ ] O que `exp(level)` representa exatamente.
- [ ] Por que alguns mobs são pulados no treino auto e no power training (seções 5 e 6).
- [ ] Ataque mín/máx dos mobs (não existe na fonte).
- [ ] Tabela de mobs (defesa/vida) contra o jogo atual.
