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

1. Ligue a gravação de tela do celular.
2. **Abra a tela de status** (onde aparecem level, exp, stats e as barras de porcentagem) e deixe aparecer
   uns 2 segundos. A gente mede a barra pelos pixels, então não precisa anotar a porcentagem.
3. **Mostre a estamina** (o relógio com a cor verde ou branca) e, se aparecer, a mensagem
   "You have X minutes of bonus level and skill experience".
4. **Mostre o inventário:** a quantidade de cada poção e o equipamento que você está vestindo.
5. Anote ou fale no áudio: **classe, level, arma (e o +buff, se tiver), anel e colar.**

## Durante o teste

- Treine **pelo menos 2 minutos**. 5 minutos ou mais é melhor, porque o erro da barra pesa menos.
- **Mantenha o mesmo ritmo** do começo ao fim (só especial, ou especial + golpes, conforme o teste).
- Se algo mudar no meio (um mob morreu, a mana acabou, apareceu a mensagem "this monster is exhausted",
  outro jogador chegou), **não pare a gravação**: só continue. A gente marca no vídeo.

## No fim

Repita o começo, na mesma ordem: **tela de status, estamina e inventário.** É a diferença entre o começo e
o fim que vira o resultado.

## Como mandar

No canal de testes do Discord do UpaLab:

- o vídeo (ou o link dele);
- esta ficha preenchida (copie e cole):

```
Teste: (A, B, C, D… da lista abaixo)
Classe / level:
Stat treinado (antes):
Defense:
Arma (ataque e +buff):
Anel / colar:
Mob e quantos você puxou:
Estamina: verde ou branca?
Bônus online ("120 minutes…") estava ativo? sim / não / acabou no meio
Poções usadas (tipo):
Observações:
Pode colocar seu nome nos créditos? sim / não — como quer aparecer:
```

## Os testes que mais ajudam agora

Em ordem de prioridade. Cada um diz **o que queremos descobrir**.

### A. Power training SEM o bônus online ⭐ (o mais importante)

- **Descobre:** se o bônus de treino online (2×) vale no power training.
- **Quem consegue:** quem joga mais de 2 horas seguidas no dia. Faça o teste **depois que a mensagem dos
  120 minutos acabar**, ainda com a **estamina verde**.
- **Como:** só especial, nos mesmos mobs de sempre, 3 a 5 minutos.

### B. Power training com a estamina BRANCA

- **Descobre:** quanto vale um golpe **sem nenhum bônus**.
- **Quem consegue:** quem joga muito e deixa a estamina cair para menos de 24 horas (ela fica branca).
- **Como:** igual ao A. Se o bônus online ainda estiver ativo, avise na ficha.

### C. Especial + golpes normais

- **Descobre:** se o golpe normal (que não dá dano) vale o mesmo que um acerto do especial.
- **Como:** 1 especial e depois uns 9 golpes normais, repetindo, por 3 a 5 minutos. Melhor com arma de
  treino, em mobs em que o golpe normal não dá dano.

### D. Regeneração natural de vida e de mana

- **Descobre:** quanto a vida e a mana voltam sozinhas, e de quanto em quanto tempo.
- **Como:** com a vida e a mana abaixo do máximo, **fique parado**, longe dos mobs e sem tomar poção, por
  1 minuto, com a tela de status aberta. Se tiver, repita com o **colar de vida** e com o **anel de mana**.

### E. Vida perdida por mob

- **Descobre:** quanto cada mob tira de vida, para o app calcular as poções de vida.
- **Como:** treine 2 minutos no seu mob de sempre. No começo e no fim, mostre a **vida** na tela. Conte as
  poções de vida (ou mostre o inventário). Na ficha, diga **quantos mobs batiam em você**.

### F. Cooldown das poções

- **Descobre:** de quanto em quanto tempo dá pra tomar cada poção, e se a de vida e a de mana dividem o
  mesmo tempo.
- **Como:** tome 10 poções seguidas o mais rápido que der. Depois, alterne: uma de vida, uma de mana.

### G. Mob cansado

- **Descobre:** se matar o mob e esperar ele renascer faz ele voltar a dar stat.
- **Como:** num mob que já mostrou "this monster is exhausted", mate, espere renascer e bata de novo. Veja
  se a mensagem aparece logo no primeiro golpe.

## O que a gente faz com o seu vídeo

1. Conta quadro a quadro os especiais, os golpes, os mobs atingidos e as poções.
2. Mede as barras pelos pixels.
3. Faz a conta com as fórmulas do app e compara com os outros testes.
4. Se fechar, vira o valor padrão do app, com a fonte anotada (e o seu nome, se você quiser).

Os resultados ficam em `docs/pendencias-teste.md`.
