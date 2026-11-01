# Ecossistema: outras ferramentas da comunidade

> Registro de contexto (01/11/2026). Serve pra lembrar o que já existe na comunidade, pra não refazer o
> que outros já fazem bem e pra deixar claro como o UpaLab se relaciona com cada ferramenta.
>
> Legenda: ✅ **conferido** (visto no código ou no site) · 📝 **informado pelo dono do projeto**, ainda não
> conferido por nós.

## Mims' Rucoy Calculator

| | |
|---|---|
| O que é | Bot de Discord com calculadoras para Rucoy Online |
| Autor | **Minsi Hu ("mims")** ✅ (README e licença do repositório) |
| Versão Backup | mantida por **hikari.ascii**, organizador da comunidade e co-dono do Discord da comunidade 📝 |
| Verificação | bot **verificado pelo Discord** 📝 |
| Código público | <https://github.com/Mimsqueeze/Mims-Rucoy-Calculator>, licença **MIT** ✅ (último commit: 17/07/2023) |
| Cálculos | /train, /ptrain, /stat, /offline, /grind, /exp, /skull, /potioncost, /dmg, /weapon, /oneshot ✅. Ver a comparação em [`comparacao-formulas.md`](comparacao-formulas.md) |
| **Guild Monitor** | consulta o **site oficial** do Rucoy **a cada 30 minutos** para avisar entrada e saída de membros da guilda, troca de nome e quem está online 📝 |

Observação: o **Guild Monitor não aparece** no código público do GitHub, que é de 2023 e só tem as
calculadoras. Provavelmente existe só na versão em produção ou na versão Backup. Não conseguimos conferir
como ele funciona por dentro.

### Como o UpaLab se relaciona com o Mims

- **Decisão (01/11/2026): o UpaLab não vai criar bot de Discord.** No servidor do UpaLab vamos **convidar
  o Mims' Rucoy Calculator**, com crédito a Minsi Hu e hikari.ascii.
- O app UpaLab e o Mims usam as **mesmas fórmulas da comunidade**. O código do UpaLab é próprio (ver
  `comparacao-formulas.md`, seção 6).
- O "Bot de Discord de cálculo" saiu da Fase 2 do [`roadmap.md`](roadmap.md).

### Sobre consultar o site oficial

- O Guild Monitor consultar o site a cada 30 minutos é um **precedente da comunidade**: mostra que esse
  tipo de uso existe e é conhecido. **Não é uma autorização oficial** dos desenvolvedores do jogo.
- **O tracker do UpaLab continua com consulta leve:** **sob demanda** (quando alguém pesquisa um
  personagem, com cache) **+ 1 vez por dia** (cron) para os personagens acompanhados. Ver
  [`tracker.md`](tracker.md).
- Antes de implementar o tracker, continua pendente a leitura dos **termos de uso** do site e do jogo. O
  `robots.txt` já foi conferido e não tem regras ativas.

## rucoy-calculator (helloyanis)

| | |
|---|---|
| O que é | App Android de calculadoras ✅ |
| Autor | helloyanis ✅ |
| Código | <https://github.com/helloyanis/rucoy-calculator>, **GPL-3.0** desde 11/02/2025 (antes: CC BY-NC-SA 4.0) ✅ |
| Distribuição | F-Droid e GitHub Releases; saiu da Google Play ✅ (README do projeto) |
| Relação com o UpaLab | fonte da tabela de mobs e de parte das fórmulas, com crédito. O código do UpaLab é próprio |

## Wiki Rucoy Online (Fandom)

| | |
|---|---|
| Endereço | <https://rucoy-online.fandom.com> ✅ |
| O que tem | lista de monstros (level, vida, exp, gold), imagens, drops. **Não tem defesa** dos mobs ✅ |
| Relação com o UpaLab | usada para conferir dados (ver [`mobs-validacao.md`](mobs-validacao.md)). Lida pela API pública, com poucas requisições |

## Site oficial (rucoyonline.com)

| | |
|---|---|
| O que tem | notícias, highscores (top 100), perfis de personagens, guildas ✅ |
| O que não tem | dados de mobs ou fórmulas ✅ |
| Relação com o UpaLab | fonte futura do tracker (Fase 2), com consulta leve. Ver [`tracker.md`](tracker.md) |

---

## Princípios

1. **Não duplicar** o que a comunidade já faz bem: se já existe uma boa ferramenta, indicamos e damos
   crédito.
2. **Precedente não é permissão:** o que outras ferramentas fazem não autoriza o UpaLab a fazer igual.
   Seguimos as nossas regras (consulta leve, termos de uso lidos antes).
3. **Crédito sempre** a quem descobriu, organizou ou mantém as informações.
