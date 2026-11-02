# Guia dos cientistas do UpaLab 🔬

> Para quem quer ajudar a descobrir como o jogo funciona por dentro. A ideia é **jogar normalmente e gravar
> a tela**. Depois a gente transforma o vídeo em números e coloca no app, com o seu nome nos créditos (se
> você quiser).
>
> Projeto de fã, sem vínculo com os desenvolvedores do Rucoy Online.

## Regras rápidas

- **Só jogar e gravar.** Nada de bot, macro, auto-clicker ou app que jogue por você. Isso quebra as regras
  do jogo e estraga o teste.
- **Grave a tela inteira, sem cortes.** Do começo ao fim do teste, num vídeo só. Se cortar, a gente perde o
  tempo exato.
- **Não precisa ser perfeito.** Um teste com algum erro, mas bem anotado, já ajuda muito.

## Antes de começar

1. Ative o modo **Não Perturbe** antes de gravar, para nenhuma notificação (WhatsApp, e-mail, nome da
   conta) aparecer no vídeo.
2. Ligue a gravação de tela do celular.
3. **Abra a tela de status** (onde aparecem level, exp, stats e as barras de porcentagem) e deixe aparecer
   uns 2 segundos. A gente mede a barra pelos pixels, então não precisa anotar a porcentagem.
4. **Mostre a estamina** (o relógio com a cor verde ou branca) e, se aparecer, a mensagem
   "You have X minutes of bonus level and skill experience".
5. **Mostre o inventário:** a quantidade de cada poção e o equipamento que você está vestindo.
6. Anote ou fale no áudio: **classe, level, arma (e o +buff, se tiver), anel e colar.**

## Durante o teste

- Treine **pelo menos 2 minutos**. 5 minutos ou mais é melhor, porque o erro da barra pesa menos.
- **Mantenha o mesmo ritmo** do começo ao fim (só especial, ou especial + golpes, conforme o teste).
- Se algo mudar no meio (um mob morreu, a mana acabou, apareceu a mensagem vermelha de que o mob está cansado,
  outro jogador chegou), **não pare a gravação**: só continue. A gente marca no vídeo.

## No fim

Repita o começo, na mesma ordem: **tela de status, estamina e inventário.** É a diferença entre o começo e
o fim que vira o resultado.

## Como mandar

No canal de testes do Discord do UpaLab:

> ℹ️ O canal de testes do Discord do UpaLab **ainda será criado**. Até lá, guarde o vídeo e a ficha.

- o vídeo (ou o link dele);
- esta ficha preenchida (copie e cole):

```
Teste: (A, B, C… da lista abaixo)
Classe / level:
Stat treinado (antes):
Defense:
Arma (ataque e +buff):
Anel / colar:
Mob e quantos você puxou:
Estamina: verde ou branca? (se mudou no meio, avise)
Poções usadas (tipo):
Observações:
Pode colocar seu nome nos créditos? sim / não — como quer aparecer:
```

## Os testes que mais ajudam agora

Em ordem de prioridade. Cada um diz **o que queremos descobrir**.

### A. Power training com a estamina BRANCA (sem bônus) ⭐ (o mais importante)

- **Descobre:** quanto vale um golpe **sem nenhum bônus**. Assim a gente descobre também quanto vale a
  estamina verde: parece ser **1,5×** (a wiki diz 2×).
- **Sobre o bônus:** existe **um bônus só**, o da estamina verde. A estamina vai até 26 h; abaixo de 24 h ela
  fica **branca** (sem bônus). A parte verde são as últimas 2 h, e o jogo mostra quanto resta dela com a
  mensagem **"You have X minutes of bonus level and skill experience"**.
- **Quem consegue:** quem joga muitas horas e deixa a estamina cair para **menos de 24 h**. Quem joga pouco
  não consegue, porque a estamina volta a encher enquanto você está off.
- **Como:** só especial, nos mesmos mobs de sempre, 3 a 5 minutos. Mostre a estamina branca no começo e no
  fim.

### B. Especial + golpes normais

- **Descobre:** se o golpe normal (que não dá dano) vale o mesmo que um acerto do especial.
- **Como:** 1 especial e depois uns 9 golpes normais, repetindo, por 3 a 5 minutos. Melhor com arma de
  treino, em mobs em que o golpe normal não dá dano.

### C. Regeneração natural de vida e de mana

- **Descobre:** quanto a vida e a mana voltam sozinhas, e de quanto em quanto tempo.
- **Como:** com a vida e a mana abaixo do máximo, **fique parado**, longe dos mobs e sem tomar poção, por
  1 minuto, com a tela de status aberta. Se tiver, repita com o **colar de vida** e com o **anel de mana**.

### D. Vida perdida por mob

- **Descobre:** quanto cada mob tira de vida, para o app calcular as poções de vida.
- **Como:** treine 2 minutos no seu mob de sempre. No começo e no fim, mostre a **vida** na tela. Conte as
  poções de vida (ou mostre o inventário). Na ficha, diga **quantos mobs batiam em você**.

### E. Cooldown das poções

- **Descobre:** de quanto em quanto tempo dá pra tomar cada poção, e se a de vida e a de mana dividem o
  mesmo tempo.
- **Como:** faça com a vida e a mana baixas, para não desperdiçar poção. Tome 10 poções seguidas o mais
  rápido que der. Depois, alterne: uma de vida, uma de mana.

### F. Mob cansado

- **Descobre:** se matar o mob e esperar ele renascer faz ele voltar a dar stat.
- **Como:** num mob que já mostrou a mensagem vermelha de que ele está cansado, mate, espere renascer e bata de novo. Veja
  se a mensagem aparece logo no primeiro golpe.

## O que a gente faz com o seu vídeo

1. Conta quadro a quadro os especiais, os golpes, os mobs atingidos e as poções.
2. Mede as barras pelos pixels.
3. Faz a conta com as fórmulas do app e compara com os outros testes.
4. Se fechar, vira o valor padrão do app, com a fonte anotada (e o seu nome, se você quiser).

Os resultados ficam em `docs/pendencias-teste.md`.
