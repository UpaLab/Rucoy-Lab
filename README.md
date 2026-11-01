# UpaLab

**Ferramentas para Rucoy Online — projeto de fã, sem vínculo com os desenvolvedores do jogo.**

O UpaLab é um hub de ferramentas feito por fã para jogos mobile, começando por Rucoy Online.
O foco é a comunidade brasileira: interface em português primeiro.

> ⚠️ **Aviso**
> - Projeto de fã, independente e sem fins oficiais. Não tem ligação, patrocínio nem aprovação dos
>   desenvolvedores ou editores do jogo. Os nomes e marcas do jogo pertencem aos seus donos.
> - As fórmulas e dados aqui são **da comunidade, não oficiais**, e podem estar errados ou desatualizados.
> - O UpaLab **não** automatiza nenhuma ação dentro do jogo. Não tem bot nem macro. São só ferramentas de
>   cálculo e consulta.

## Status

**Fase 0: estrutura e documentação.** Ainda não tem app.

O produto principal vai ser um **app Android** leve, que calcula offline. Por enquanto **nada é
público**: o APK de teste vai ser gerado automaticamente pelo GitHub, mas não tem Release pública. Veja
[`docs/app.md`](docs/app.md).

## O que tem aqui

| Caminho | Conteúdo |
|---|---|
| [`docs/formulas.md`](docs/formulas.md) | Fórmulas da comunidade (dano, precisão, ticks, defesa), com fonte e status de verificação |
| [`data/mobs.json`](data/mobs.json) | Tabela de mobs (nome, level, defesa, vida) |
| [`docs/tracker.md`](docs/tracker.md) | O que sabemos do site oficial e o plano do tracker |
| [`docs/app.md`](docs/app.md) | Estrutura proposta do app Android, chave de assinatura, verificação da Google |
| [`docs/roadmap.md`](docs/roadmap.md) | Decisões tomadas e rascunho das issues por fase |

## APK de teste (só pra quem tem acesso)

O GitHub gera o APK sozinho a cada mudança. Pra baixar pelo celular: aba **Actions** → execução mais
recente de **"APK de teste"** → **Artifacts** → `UpaLab-APK-...`. Vem um `.zip` com o APK dentro. Não
existe Release pública.

O código do app está em [`app/`](app/). As fórmulas ficam em
[`app/src/main/java/io/github/upalab/rucoylab/calculo/`](app/src/main/java/io/github/upalab/rucoylab/calculo/).
O **código é próprio do UpaLab**, escrito a partir de [`docs/formulas.md`](docs/formulas.md) e
[`docs/comparacao-formulas.md`](docs/comparacao-formulas.md). As **fórmulas e os dados** vêm da comunidade
(ver Créditos).

## Roadmap resumido

- **Fase 0:** estrutura, documentação, tabela de mobs, licenças, esqueleto do app e geração automática do
  APK de teste.
- **Fase 1 (app):** onde treinar, ticks/h, tempo até o próximo nível, custo de poções (auto × power
  training), defesa com set de armadura, balão flutuante (você digita os dados, sem leitura de tela),
  tela de créditos.
- **Fase 2:** backend (Next.js + Vercel + Postgres/Neon) pro histórico de level e o ranking semanal, bot
  de Discord, aba de criadores.
- **Fase 3:** distribuição pública (só quando liberado). Leitura de tela só se as regras do jogo
  permitirem.

Detalhes em [`docs/roadmap.md`](docs/roadmap.md).

## Créditos

O código do UpaLab é próprio. As **fórmulas** e os **dados** (tabela de mobs) foram descobertos e
reunidos pela comunidade:

- **Minsi Hu ("mims")**, autor do **[Mims' Rucoy Calculator](https://github.com/Mimsqueeze/Mims-Rucoy-Calculator)**
  (MIT): as fórmulas de treino, o cálculo de tempo até um stat e de treino offline, e a ideia de estimar
  poções de mana. O próprio Mims agradece a Tribrid e potatoking pela ajuda com as fórmulas e o bot.
- **hikari.ascii**: organizador da comunidade e mantenedor do **Mims' Rucoy Calculator - Backup**.
- **helloyanis**, autor do **[rucoy-calculator](https://github.com/helloyanis/rucoy-calculator)**
  (GPL-3.0): a tabela de mobs usada em [`data/mobs.json`](data/mobs.json), as fórmulas de defesa e os
  ajustes mais recentes (especial do mago, anel de crítico).
- **[Wiki Rucoy Online (Fandom)](https://rucoy-online.fandom.com/wiki/Monster_List)**: conferência de levels,
  vida e cores dos mobs.
- A comunidade de jogadores, que descobriu e testou essas fórmulas.

Se você é autor de algum material usado aqui e quer ajustar o crédito ou pedir remoção, abra uma issue.

## Licença

[GPL-3.0](LICENSE).
