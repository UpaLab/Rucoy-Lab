# Validação da tabela de mobs

> Pesquisa de 01/11/2026. **Nenhum dado foi alterado ainda.** Este arquivo só registra o que cada fonte diz.

## Fontes consultadas

| Fonte | Tipo | O que tem | Data |
|---|---|---|---|
| **A**: `data/mobs.json` (helloyanis/rucoy-calculator, GPL-3.0) | Comunidade | nome, level, **defesa**, vida | abr/2025 |
| **B**: [Mimsqueeze/Mims-Rucoy-Calculator](https://github.com/Mimsqueeze/Mims-Rucoy-Calculator) (**MIT**) | Comunidade (bot de Discord) | nome, **defesa**, vida | jul/2023 |
| **C**: [Wiki Rucoy Online (Fandom), "Monster List"](https://rucoy-online.fandom.com/wiki/Monster_List) | Comunidade. A página diz "Updated by: GM Atena, 05/06/2023" | nome, level, vida, exp, gold. **Sem defesa.** | jun/2023 |
| **D**: Site oficial [rucoyonline.com](https://www.rucoyonline.com) | Oficial | **Nenhum dado de mob.** Só notícias, highscores, personagens e guildas | — |

A wiki (C) foi lida pela API pública da MediaWiki, porque a página normal bloqueia leitores automáticos.

## Assassinos: o que as fontes dizem

| Level | Nome (A e B) | Nome na wiki (C) | Cor na imagem da wiki | Vida A / B / C |
|---|---|---|---|---|
| 45 | Assassin | Assassin ("Elde Assassin" no título) | **vermelho** | 120 / 120 / 120 |
| 50 | Assassin | **Yellow Assassin** | **amarelo** | 140 / 140 / 140 |
| 55 | Assassin Ninja | Assassin Ninja | **cinza** (olhos azuis) | 160 / 160 / **180** |

Imagens da wiki lado a lado: [`assassinos-wiki.png`](assassinos-wiki.png). Os arquivos `Assassin (Updated).png`,
`Yellow Assassin (Updated).png` e `Assasin Ninja.png` foram enviados à wiki em 05/06/2024.

✅ **Conferido no jogo pelo dono do projeto (01/11/2026):** 45 = vermelho, 50 = amarelo, 55 = cinza
(Ninja), igual à wiki. A dúvida inicial era só confusão de memória.

✅ **Validação prática:** no jogo, o Assassin Ninja com adaga de treino em power training leva cerca de
**1 minuto** pra morrer, o que bate com o tempo médio calculado pelo app.

A vida do Assassin Ninja está diferente na própria wiki: a página do mob diz 160, mas a Monster List diz 180.

## Outras diferenças encontradas

| Mob | Campo | A (nosso) | B (Mims) | C (wiki) |
|---|---|---|---|---|
| Minotaur Lv 250 | defesa | 601 | **591** | (sem defesa) |
| Minotaur Lv 275 | defesa | 691 | **681** | (sem defesa) |
| Ice Dragon Lv 320 | vida | 45000 | **50000** | 45000 |
| Yeti Lv 350 | vida | 55000 | **60000** | 55000 |
| Orthrus | level | 400 | **425** (comentado) | 400 |
| Djinn Lv 150 | vida | 640 | 640 | **680** |
| Crab Lv 1 | — | (não existe) | (não existe) | existe: vida 20 |

Nos outros 38 mobs, level e vida batem entre A e C.

❗ Na fonte B, os nomes dos emojis dizem `120_drow_ranger` e `125_drow_assassin`, mas os nomes dos mobs dizem
"Drow Ranger Lv.125" e "Drow Assassin Lv.120". A wiki (C) confirma **Drow Assassin = 120 e Drow Ranger = 125**,
então o erro parece ser só no nome do emoji.

## O que fazer

- [x] Dono confere no jogo a cor dos 3 assassinos (45, 50, 55).
- [ ] Conferir no jogo a vida do Assassin Ninja (160 ou 180) e do Djinn (640 ou 680).
- [ ] Defesa dos mobs: **nenhuma fonte oficial**. A e B concordam, exceto nos Minotaurs 250/275.
- [x] Campo `apelido` + `apelido_origem` no `data/mobs.json`. Assassinos = conferido no jogo; Rat Lv 3
      (cinza) e Vampire Lv 110 (vermelho) = só pela wiki.
