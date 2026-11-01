# Modelo proposto: golpes normais "liberados" pelo especial

> **Atualização (01/11/2026): os testes A e B confirmaram a hipótese e o modelo está no app** (tela
> Progresso → Power: Rápido × Econômico). Resultados em `pendencias-teste.md`, seção 5b.
>
> Valores calibrados: 2,5 mobs atingidos, 1,45 s entre especiais, 1 tick por mob no especial, 9 golpes entre
> especiais, 0,5 tick por golpe (🔶 supondo que o bônus dobra tudo), ~1 s por golpe.

## A hipótese, em linguagem simples

Hipótese de um guia antigo da comunidade, **não confirmada**:

> Cada golpe dá 1 tick, **mesmo sem causar dano**, desde que pelo menos **1 golpe a cada 10** cause dano.

Com arma de treino nos ninjas, o golpe normal **não causa dano** (observado pelo dono do projeto). Só o
especial causa. Se a hipótese estiver certa, o especial "libera" os golpes normais que vêm depois dele:
eles contam tick sem gastar mana.

## Hoje (conta atual)

```
cada especial = 4 ticks (1 por mob atingido)
cada tick custa 50 ÷ 4 = 12,5 de mana
```

Os golpes normais **não contam**. Por isso a estimativa sai alta.

## Como ficaria (modelo proposto)

Pense no treino como **ciclos que se repetem**: 1 especial e, em seguida, alguns golpes normais até o
próximo especial.

```
ciclo                  = 1 especial + G golpes normais
ticks por ciclo        = (mobs × T_esp + G × T_normal) × bônus
G (golpes no ciclo)    = (intervalo entre especiais − D) × golpes por segundo
mana por ciclo         = 50
ticks por poção        = ticks por ciclo × (recupera da poção ÷ 50)
ticks por hora         = ticks por ciclo × (3600 ÷ intervalo)
```

A regra do "1 a cada 10" entra como limite: só valem os golpes normais **até 9 depois de cada especial**.
Se o intervalo for maior que isso, os golpes a mais não contam:

```
G que contam = mínimo(G, 9)
```

### Exemplo com os seus números (melee, 4 mobs, 1 especial a cada ~1,5 s)

| Suposição | Ticks por especial | Poções básicas para ~72 → 90 (≈ 11.600 ticks) |
|---|---|---|
| Hoje (golpes normais não contam) | 4 | ~1.450 |
| Hipótese, 1,5 s entre especiais | 4 + 0,5 = 4,5 | ~1.290 |
| Hipótese, 1 especial + 9 golpes | 4 + 9 = 13 | ~445 |
| **Sua lembrança (~400 poções)** | **~14** | ~400 |

**O que isso mostra:** a sua lembrança (~400 poções) só bate se cada especial rendesse **~14 ticks**.
Isso acontece no ritmo "1 especial + ~9 golpes", que é exatamente o **teste B**. Com 1 especial a cada 1,5 s,
a hipótese ajuda pouco. Então, ou o seu ritmo real tinha mais golpes normais entre os especiais, ou existe
outro fator: o bônus de exp, a regeneração, ou a própria curva de ticks da comunidade, que pode não estar
certa nos stats 70–90.

## Números que vão precisar ser calibrados

| Símbolo | O que é | Valor provisório | Como os testes ajudam |
|---|---|---|---|
| `T_esp` | ticks por mob atingido pelo especial | 1 | Teste A |
| `T_normal` | ticks por golpe normal **sem dano** | 0 hoje; 1 na hipótese | Teste B (comparando com o A) |
| `golpes por segundo` | velocidade do golpe normal | 1 (o teto de 3.600/h sugere isso) | Teste B (tempo ÷ golpes) |
| `D` | tempo que o especial "ocupa" (substitui 1 golpe?) | 1 s | Testes A e B |
| `intervalo` | segundos entre um especial e outro | **1,5 s** (informado por você) | Teste A: tempo ÷ 100 |
| limite de golpes liberados | até quantos golpes depois do especial contam | 9 (regra do "1 a cada 10") | Teste B; um teste C com mais golpes confirmaria |
| `bônus` | multiplicador do bônus de exp nos ticks | 1 sem bônus | Fazer os testes **sem** bônus |
| curva de ticks por stat | fórmula da comunidade (55+: `S^(S/1000+2,171)`) | a atual | Os testes medem ticks reais num stat conhecido |

## Como transformar os seus testes em números

A porcentagem do stat no jogo vira ticks assim:

```
ticks ganhos = (% depois − % antes) × ticks do ponto atual
```

O "ticks do ponto atual" vem da fórmula da comunidade. Se o stat subir de nível no meio do teste, a conta é
por partes.

- **Teste A** (100 especiais no ritmo normal):
  - `ticks por especial (A) = ticks ganhos ÷ 100`
  - `intervalo real = tempo ÷ 100`
  - Se der **≈ 4**: golpes normais no meio não contam (ou quase não houve golpes no meio).
- **Teste B** (1 especial + ~9 golpes, ~3 min):
  - `golpes normais ≈ (tempo em segundos − especiais × D) × golpes por segundo`
  - `T_normal ≈ (ticks ganhos − especiais × 4) ÷ golpes normais`
  - Se der **≈ 1**: a hipótese está certa. Se der **≈ 0**: os golpes sem dano não contam.
- **Poções** (nos dois): `mana usada = poções × ~100`. Comparando com `especiais × 50`, dá pra ver se a
  regeneração natural ajudou.

### Teste C (opcional, de controle)

Só golpes normais nos ninjas, **sem nenhum especial**, por 1 minuto. Pela hipótese, **não deveria subir
nada**, porque nenhum golpe causa dano. Se subir, a regra do "1 a cada 10" não é bem assim.

## O que já está no app

- O campo **"Intervalo entre especiais (s)"**, com padrão de 1,5 s (informado por você), no painel de poções.
  Por enquanto ele só **mostra** quantos especiais por minuto e quanta mana por segundo esse ritmo pede,
  comparando com o que a poção aguenta. **Não entra na conta de poções.**
