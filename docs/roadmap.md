# Roadmap — rascunho de issues

> **Rascunho para revisão.** Nenhuma issue foi criada no GitHub ainda.
>
> Regras que valem para todas as fases:
> - nada que automatize ações dentro do jogo (bot/macro);
> - não usar o nome do jogo como marca;
> - interface em português primeiro;
> - **nada público por enquanto**: sem Releases públicas até o dono do projeto liberar (estamos aguardando
>   autorização do desenvolvedor do jogo).

## Decisões tomadas

| Assunto | Decisão | Data |
|---|---|---|
| Produto principal | **App Android nativo (APK)**, em Kotlin + Jetpack Compose | 2026-11 |
| Identificador do app | `io.github.upalab.rucoylab` (**não pode mudar depois**; ver `docs/app.md`) | 2026-11 |
| Cálculos | Rodam **dentro do app**, funcionando sem internet, com as fórmulas de `docs/formulas.md` | 2026-11 |
| Base de código | **Código próprio**, sem copiar nem adaptar código de ninguém. Fórmulas e dados vêm da comunidade, com crédito (helloyanis, Minsi Hu, hikari.ascii) | 2026-11 |
| Geração do APK | Automática pelo **GitHub Actions**; o APK fica só como **arquivo de teste (artefato)**, sem Release pública | 2026-11 |
| Assinatura do APK | Chave guardada como **segredo do GitHub**, nunca no código, com cópia de segurança fora do GitHub | 2026-11 |
| Backend (Fase 2) | **Next.js + Vercel Cron + Postgres (Neon), em TypeScript**. Só pro histórico de level e o ranking semanal. **Não mexer agora.** | 2026-11 |
| Login | Fica pra depois | 2026-11 |
| Bot de Discord | **Não criar.** Convidar o Mims' Rucoy Calculator no servidor do UpaLab, com crédito (ver `docs/ecossistema.md`) | 2026-11 |
| Leitura de tela (OCR) | Fora até confirmar que as regras do jogo permitem | 2026-11 |

---

## Fase 0 — Estrutura e documentação

### #1 Estrutura inicial do repositório
- README, aviso de projeto de fã, créditos, roadmap. ✅ rascunho feito

### #2 Documentar fórmulas da comunidade
- `docs/formulas.md` com fonte, crédito e marcação de verificado × suposição. ✅ rascunho feito

### #3 Tabela de mobs em JSON
- `data/mobs.json` extraído da fonte, com crédito. ✅ rascunho feito
- Próximo passo: validar defesa e vida dos mobs no jogo atual.

### #4 Levantar licenças das APIs/ferramentas da comunidade
- Pra cada ferramenta: link, licença e o que ela permite (copiar código? só dados? precisa de crédito?
  é compatível com GPL-3.0?).
- Já conhecidas:
  - `helloyanis/rucoy-calculator`: GPL-3.0 (conferido).
  - `Mimsqueeze/Mims-Rucoy-Calculator`: **MIT** (conferido em 01/11/2026). Permite copiar e adaptar,
    mantendo o aviso de copyright. É compatível com GPL-3.0.
- **Pronto quando:** existir `docs/licencas.md`.
- Nota do dono: existem projetos e até APIs da comunidade no GitHub que podem ser usados. Como o app é
  offline agora, o uso de APIs fica para as fases com internet (Fase 2 em diante), sempre respeitando a
  licença de cada uma.

### #5 Checar robots.txt e termos do site oficial
- Pré-requisito do tracker (Fase 2).

### #6a Comparar helloyanis × Mims ✅
- Ver `docs/comparacao-formulas.md`.

### #6 Validar as inconsistências das fórmulas
- Ver `docs/formulas.md` → "Pendências de validação".

### #7 Esqueleto do app Android + geração automática do APK
- Projeto Kotlin + Compose com o identificador `io.github.upalab.rucoylab`.
- GitHub Actions gera o APK a cada mudança e guarda como **artefato** (arquivo de teste, privado ao repo,
  apagado automaticamente depois de alguns dias).
- **Nenhuma Release pública.**
- **Pronto quando:** o dono conseguir baixar o APK pelo celular e instalar.

### #8 Chave de assinatura
- Gerar a chave, guardar como segredo do GitHub e fazer a cópia de segurança fora do GitHub.
- Ver o passo a passo em `docs/app.md` → "Chave de assinatura".

### #9 Verificação de desenvolvedor Android (Google)
- Pesquisa feita, ver `docs/app.md` → "Regra nova do Android no Brasil".
- Ação do dono: criar a conta de **distribuição limitada** (grátis) pros testes; a conta completa fica
  pra quando formos distribuir.

---

## Fase 1 — App: calculadora

### #10 Calculadora de treino: onde treinar ✅ primeira versão
- Entrada: classe, stat, ataque da arma, level, anel de crítico.
- Saída: melhor mob, precisão, ticks/h (treino auto e power training).

### #11 Ticks/h e tempo até o próximo stat ✅ primeira versão (tela Progresso)
- Avisar sobre a descontinuidade no stat 54 → 55 até ela ser validada.

### #12 Tempo até o próximo level ✅ primeira versão (tela Progresso)
- Usa `exp(level)`, depois de validar o que ela representa.

### #13 Diferencial: custo em gold de poções (MP/HP), auto × power training
- Entrada: preço das poções e consumo (informado pelo jogador).
- Saída: gold/h e gold por stat, comparando os dois tipos de treino.
- 🔶 Não temos fórmula de consumo de MP/HP. Começa com o consumo informado pelo jogador.

### #14 Diferencial: defesa com set de armadura
- Defesa alta e baixa e dano recebido por mob, com as peças do set.
- Bloqueio: a tabela de mobs não tem ataque mín/máx. Por enquanto, entrada manual.

### #15 Balão flutuante (calculadora rápida por cima do jogo)
- Usa a permissão "aparecer sobre outros apps". O app pede essa permissão só quando o jogador ativar o
  balão.
- O jogador **digita** os dados. Não tem leitura de tela, não toca e não clica no jogo.

### #16 Tela de Créditos e aviso
- "Projeto de fã, sem vínculo com os desenvolvedores do Rucoy Online."
- Crédito a helloyanis/rucoy-calculator, Mims e comunidade; licença GPL-3.0.

### #17 Testes das fórmulas
- Testes automáticos comparando com valores da calculadora original. Rodam junto com a geração do APK.

---

## Fase 2 — Backend, tracker e comunidade (não mexer agora)

### #18 Backend: Next.js + Vercel Cron + Postgres (Neon)
- TypeScript. Serve dados para o app: histórico de level e ranking semanal.
- Plano grátis da Vercel: o cron roda **no máximo 1× por dia**, com horário aproximado (±59 min).
  (Confirmado pelo dono na documentação da Vercel.)

### #19 Tracker lite
- **Busca sob demanda** quando alguém pesquisa um personagem, com cache.
- **1 cron diário** que salva uma "foto" (level, título, last online) dos personagens acompanhados.
- Skills informadas manualmente pelo jogador.
- Depende de #5.

### #20 Ranking semanal ("quem mais subiu na semana")
- Usa as fotos diárias.

### ~~#21 Bot de Discord de cálculo~~ ❌ removido
- Decisão do dono (01/11/2026): o UpaLab **não** vai criar bot de Discord. No servidor do UpaLab vamos
  **convidar o Mims' Rucoy Calculator**, com crédito. Ver `docs/ecossistema.md`.

### #22 Aba de criadores de conteúdo
- Lista curada de canais e guias BR, com autorização dos criadores.

### #23 Login
- Fica pra depois.

---

## Fase 3 — Distribuição e extras

### #24 Distribuição pública
- **Só quando o dono liberar** (depois da autorização do desenvolvedor do jogo).
- Exige conta de desenvolvedor com **distribuição completa** (ver `docs/app.md`).

### #25 Leitura de tela (OCR): só pesquisa
- **Bloqueado** até confirmar que as regras do jogo permitem. Mesmo se permitir: só leitura, nunca
  automação.
