# Tracker de personagens — o que sabemos

> Status: **planejamento (Fase 2).** Nada foi implementado e **não vamos mexer na Vercel/Neon agora**.
> Este documento separa o que já foi **verificado** do que ainda é **suposição**.

Site oficial do jogo: <https://www.rucoyonline.com>

## Legenda

| Marca | Significado |
|---|---|
| ✅ **Observado** | Visto no site, segundo o levantamento do dono do projeto. Ainda não foi reconferido automaticamente. |
| 🔶 **Suposição** | Hipótese que precisa ser testada. |
| ⏳ **Pendente** | Precisa ser checado **antes** de implementar. |

## 1. Perfil público do personagem

✅ Campos visíveis **sem login**:

| Campo no site | Exemplo | Observação |
|---|---|---|
| Name | — | nome do personagem |
| Level | — | level base |
| Title | — | título do personagem |
| Last online | `about 17 hours ago` | **aproximado**, texto relativo, não é data exata |
| Born | — | data de criação |

✅ As **skills** (melee, distance, magic, defense) **só aparecem quando você está logado na própria conta**.
Não dá pra ler as skills de outros jogadores pelo perfil público.

🔶 Suposições a testar:
- A URL do perfil segue um padrão previsível a partir do nome.
- O HTML é estável o bastante pra extrair os dados (sem depender de JavaScript).
- "Last online" muda de unidade conforme o tempo passa (minutes, hours, days). Por isso a precisão de
  atividade vai ser, no melhor caso, de horas.

## 2. Highscores públicos

✅ O site tem o **top 100** em cada categoria, na rota `/highscores/<categoria>`:

- `experience`
- `melee`
- `distance`
- `magic`
- `defense`

🔶 Suposições a testar:
- Os highscores mostram o valor da skill. Se mostrarem, as skills de quem está no top 100 podem ser
  lidas sem login.
- Existe paginação ou um limite de 100 fixo.
- Os nomes das categorias na URL são exatamente esses.

## 3. Plano: tracker híbrido (decidido)

### Como funciona

| Dado | Origem | Quando |
|---|---|---|
| Level, título, last online | **automático**: leitura leve do perfil público | **sob demanda** (quando alguém pesquisa o personagem, com cache) + **1 cron diário** |
| Posição nos highscores | **automático**: leitura das páginas de top 100 | junto com o cron diário |
| Skills (melee/distance/magic/defense) | **manual**: o próprio jogador informa | quando quiser |

- **Cron** é uma tarefa agendada que roda sozinha num horário fixo.
- **Cache** é guardar a resposta por um tempo, pra não buscar a mesma página de novo.
- **Personagens acompanhados**: quem alguém marcou pra seguir. Só esses entram na foto diária, pra manter a
  coleta pequena.

### Tecnologia (decidida: opção A)

- **Next.js** em TypeScript, hospedado na **Vercel**: o servidor que busca, guarda e entrega os dados.
- **Vercel Cron**: no plano grátis roda **no máximo 1× por dia**, com horário **aproximado (±59 min)**.
  Isso foi confirmado pelo dono do projeto na documentação da Vercel.
- **Postgres (Neon)**: o banco de dados onde fica o histórico.
- O **app Android** só **lê** esses dados (histórico de level, ranking semanal). Os cálculos continuam
  dentro do app, offline.
- **Login fica pra depois.** Sem login, dados manuais (skills) ficam só no celular do jogador.

### O que sai disso
- Gráfico de evolução de level por personagem.
- Ranking "quem mais subiu na semana".
- Estimativa de tempo até o próximo stat, com as skills informadas e `docs/formulas.md`.

🔶 Dados informados pelo jogador **não são verificáveis**. Na interface, eles aparecem como
"informado pelo jogador".

## 4. Regras de coleta (scraping leve)

- Somente páginas **públicas**. Nunca usar login, conta ou sessão de ninguém.
- Requisições **sob demanda** (quando alguém busca um personagem) ou **poucas vezes ao dia** pelo cron.
- **Cache obrigatório**: o mesmo perfil não é buscado de novo dentro de um intervalo mínimo (proposta:
  6–12 h).
- Intervalo entre requisições, sem paralelismo agressivo, e um User-Agent identificável com link pro
  projeto.
- Se o site responder com erro, bloqueio ou pedido de parada: **parar** e reavaliar.
- Nada disso automatiza ações **dentro do jogo**. É só leitura do site público.

## 5. ⏳ Pendente antes de implementar

- [ ] Ler o `robots.txt` de `rucoyonline.com` e registrar aqui o que ele permite.
- [ ] Ler os termos de uso / políticas do site e do jogo, e registrar aqui o que dizem sobre acesso
      automatizado.
- [ ] Confirmar os padrões de URL do perfil e dos highscores.
- [ ] Confirmar se os highscores mostram valores de skill.
- [ ] Definir o intervalo de cache e o limite de requisições por dia.
- [ ] Decidir o que fazer se os termos proibirem scraping (alternativa: tracker 100% manual).
