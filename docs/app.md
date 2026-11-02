# App Android: estrutura proposta

> **Proposta para revisão.** Nada foi programado ainda.
> Escrito em linguagem simples. Termos técnicos vêm com explicação.

## 1. O que é o app

Um app Android **leve**, em português, que faz os cálculos de treino **sem precisar de internet**.

- **Kotlin**: a linguagem de programação oficial do Android.
- **Jetpack Compose**: o jeito moderno, recomendado pela Google, de desenhar as telas do app em Kotlin.
- **APK**: o arquivo instalador do app, o equivalente ao ".exe" do Windows.

## 2. Telas

| Tela | O que faz |
|---|---|
| **Início** | Atalhos pras calculadoras e o aviso "Projeto de fã, sem vínculo com os desenvolvedores do Rucoy Online". |
| **Onde treinar** | Você informa classe, stat, ataque da arma, level e se usa anel de crítico. O app mostra o melhor mob, a precisão e os ticks por hora, tanto no ataque normal quanto no power training. |
| **Progresso** | Tempo até o próximo stat e até o próximo level. |
| **Poções** | Custo em gold de poções de MP/HP por hora e por stat, comparando ataque normal × power training. Você informa o preço e quanto gasta. |
| **Defesa** | Você monta o set de armadura e vê quanto dano cada mob causa (defesa alta e baixa). |
| **Mobs** | Lista de mobs com level, defesa e vida (de `data/mobs.json`), com o aviso "dados da comunidade". |
| **Créditos** | Aviso de fã, créditos (helloyanis/rucoy-calculator, Mims, comunidade) e licença GPL-3.0. |
| **Configurações** | Liga/desliga o balão flutuante. |

## 3. Balão flutuante

- É uma bolinha ("UP") que fica **por cima do jogo**. Dá pra arrastar pra qualquer canto.
- **Tocou na bolinha:** abre um painel quase da largura da tela, com os menus numa faixa que **rola para o
  lado** (Onde treinar, Progresso, Poções, Defesa, Mobs).
- **Tocou num menu:** a ferramenta abre **logo abaixo dos menus**, estilo dropdown, com rolagem própria. O botão
  **"Tamanho"** troca a altura: P (~30% da tela), M (~42%, padrão) ou G (~60%); fica salvo. Tocar no mesmo menu de novo fecha. "Minimizar" volta pra bolinha; "✕" tira o balão.
- Os valores digitados são os mesmos das telas do app (ficam salvos nos dois lugares).
- Com o painel aberto, toques **fora dele** continuam indo pro jogo.
- Precisa da permissão **"aparecer sobre outros apps"**. O app só pede essa permissão quando você ativa o
  balão. Se você não ativar, ele nem pede.
- O balão **não lê a tela, não toca e não clica no jogo**. Você digita os números e ele calcula. Isso é
  pra respeitar a regra de não automatizar nada no jogo.

## 4. Permissões (só o necessário)

| Permissão | Pra quê | Quando |
|---|---|---|
| Aparecer sobre outros apps | Balão flutuante | Só se você ativar o balão |
| Notificação | O Android exige uma notificação fixa enquanto o balão está ligado | Só com o balão ligado |
| Internet | **Nenhuma na Fase 1.** Só entra na Fase 2, pra ler o histórico/ranking | Fase 2 |

## 5. Como o projeto vai ser organizado (pastas)

```
app/
  calculo/     ← as fórmulas (de docs/formulas.md). Não depende de tela nenhuma, então dá pra testar sozinho.
  dados/       ← a tabela de mobs (cópia de data/mobs.json dentro do app)
  telas/       ← cada tela da tabela acima
  balao/       ← o balão flutuante
.github/workflows/
  apk.yml      ← a "receita" que o GitHub segue pra gerar o APK sozinho
```

- **Fórmulas separadas das telas**: assim os testes automáticos conferem os cálculos toda vez que algo
  muda.
- **Código próprio**: o cálculo foi escrito do zero pelo UpaLab, seguindo `docs/formulas.md` e
  `docs/comparacao-formulas.md`. Das fontes da comunidade vêm só as **fórmulas e os dados** (tabela de
  mobs), com crédito no README e na tela de Créditos.
- **Poucas dependências**: dependência é código pronto de terceiros que o app inclui. Quanto menos, mais
  leve e mais seguro. A ideia é usar só as bibliotecas oficiais do Android/Compose.

## 6. Como o APK é gerado (sem computador)

1. Uma mudança é enviada pro GitHub.
2. O **GitHub Actions** (um "robô" do GitHub que roda tarefas automáticas) compila o app, roda os testes e
   assina o APK.
3. O APK fica como **artefato**: um arquivo anexado àquela execução, que só quem tem acesso ao repositório
   consegue baixar. O GitHub apaga o artefato sozinho depois de alguns dias (dá pra configurar até 90).
4. Você baixa pelo navegador do celular: repositório → aba **Actions** → execução mais recente → **Artifacts**.
   Vem um `.zip` com o APK dentro.

**Não vamos criar Release pública** (Release é a página de download aberta a qualquer pessoa).

⚠️ O repositório **UpaLab/Rucoy-Lab é público**. Isso quer dizer que o **código** fica visível pra
qualquer um, mas os **artefatos** só podem ser baixados por quem está logado no GitHub. Se quiser que nem o
código fique visível até a autorização, dá pra deixar o repositório privado. Me avisa.

## 7. Identificador do app: `io.github.upalab.rucoylab`

**O que é:** é o "CPF" do app. O Android usa esse nome, e não o nome que aparece na tela, pra saber que
app é esse.

**Por que não pode mudar depois:**
- Se mudar, o Android acha que é **outro app**. Quem já instalou não recebe a atualização: fica com dois
  apps, e os dados salvos (como o seu set de armadura) **não passam** pro novo.
- Na verificação da Google (seção 9), o app é **registrado por esse nome**. Mudar o nome é registrar tudo
  de novo.
- Por isso ele já fica definido desde o primeiro APK de teste.

O nome que aparece embaixo do ícone (ex.: "UpaLab") **pode** mudar quando quiser. Só o identificador é
fixo.

## 8. Chave de assinatura

### O que é
Todo APK é **assinado** com uma chave, como se fosse uma assinatura com firma reconhecida. O Android só
aceita uma **atualização** se ela vier assinada **pela mesma chave** do app instalado. A Google também
usa essa chave pra confirmar que você é o dono do app na hora de registrar.

**Se perder a chave:** não dá mais pra atualizar o app pra quem já instalou. E não dá pra provar que o app
é seu na verificação da Google. **Não tem como recuperar.**

### Proposta (aguardando seu ok)

1. Eu gero a chave aqui na sessão, com senhas fortes e aleatórias.
2. Te mando **2 coisas**: o arquivo da chave (`upalab-release.jks`) e um texto com as senhas e o alias
   (alias é o "nome" da chave dentro do arquivo).
3. **Você guarda a cópia de segurança fora do GitHub, em 2 lugares diferentes.** Por exemplo:
   - o arquivo e as senhas no seu **Google Drive**, numa pasta privada;
   - o arquivo num **pendrive ou e-mail pra você mesmo**, e as senhas num **gerenciador de senhas** (ex.:
     o do Google) ou anotadas em papel guardado.
   - O ideal é **não** deixar arquivo e senha juntos no mesmo lugar.
4. Você cadastra **4 segredos** no GitHub. Dá pra fazer pelo navegador do celular: repositório →
   **Settings → Secrets and variables → Actions → New repository secret**. Eu te passo o nome e o valor de
   cada um:
   - `SIGNING_KEY_BASE64`: a chave em formato de texto
   - `SIGNING_KEY_ALIAS`
   - `SIGNING_KEY_PASSWORD`
   - `SIGNING_STORE_PASSWORD`
   - **Segredo do GitHub** é um cofre: depois de salvo, ninguém consegue ver o valor de novo, nem você.
     Só o robô do GitHub Actions usa. Por isso a cópia de segurança fora do GitHub é obrigatória.
5. Eu apago a chave desta sessão.

**Ponto de atenção:** nesse caminho, a chave passa por esta sessão na nuvem antes de chegar em você. O
risco é baixo porque ela é apagada no fim, mas existe. A alternativa mais fechada é o próprio GitHub
Actions gerar a chave. Só que aí você teria que configurar um acesso extra (um "token"), o que é mais
complicado sem computador. Recomendo o caminho acima.

**Eu não consigo cadastrar os segredos sozinho**: as ferramentas desta sessão não têm essa permissão. Por
isso o passo 4 é seu.

## 9. Regra nova do Android no Brasil (verificação de desenvolvedor)

Pesquisa de 01/11/2026 em fontes oficiais da Google (links no fim).

### O que mudou
- Desde **30/09/2026**, no Brasil (e também na Indonésia, em Singapura e na Tailândia), celulares Android
  certificados só instalam normalmente apps cujo **identificador foi registrado** por um desenvolvedor
  verificado. Isso vale também pra APK baixado fora da Play Store.
- Sem registro, ainda dá pra instalar, mas só pelo **"fluxo avançado"**, um caminho nas configurações do
  Android com avisos de segurança e espera, ou pelo **ADB** (instalação via computador).

### Opção 1: conta de **distribuição limitada** (pra testes) ⭐ recomendada agora
- **Grátis**, **sem documento com foto**.
- Precisa de: conta Google **com verificação em 2 etapas** ligada, um **perfil de pagamentos da Google**
  (só pra informar nome e endereço, sem cobrança) e um e-mail de contato.
- Permite **registrar o app** e compartilhar com um **número limitado de celulares**, que precisam ser
  autorizados um por um (por QR code ou link, com confirmação no celular). O guia atual fala em **até 50
  celulares**. Um post anterior do blog falava em 20, então confira o número na hora.
- Serve bem pra você e um grupo de testadores enquanto a autorização do desenvolvedor do jogo não sai.

### Opção 2: conta de **distribuição completa** (pra lançar pra todo mundo)
- Instalações ilimitadas.
- Como **pessoa física**: documento oficial com foto, comprovante de endereço, e-mail e telefone
  verificados.
- Como **organização**: além disso, número **D-U-N-S** (um cadastro gratuito de empresas da Dun &
  Bradstreet), site verificado no Google Search Console e documentos da empresa.
- Taxa: fontes falam em **US$ 25 uma vez**, igual à da Play Store. **Não confirmei** na página oficial.

### Nos dois casos
- O registro é feito no **Android Developer Console**: <https://android.google.com/developerconsole/developers>
- Pra registrar o identificador, você **prova que tem a chave de assinatura** (seção 8). Por isso a chave
  precisa existir e estar guardada **antes** do registro.
- Se o identificador já estiver em uso por outra pessoa, pode ser necessária uma análise extra.

### O que você vai fazer (quando formos testar com outras pessoas)
1. Ligar a verificação em 2 etapas na sua conta Google.
2. Criar a conta de distribuição limitada no Android Developer Console.
3. Registrar `io.github.upalab.rucoylab` usando a chave de assinatura.
4. Autorizar os celulares dos testadores.
5. Quando o desenvolvedor do jogo liberar: passar pra distribuição completa.

Pra instalar o APK de teste **só no seu celular** agora, dá pra usar o fluxo avançado, sem registro. Segundo
uma fonte não oficial, ele tem uma espera de cerca de **24 h** na primeira vez.

### Fontes
- [Blog Android Developers: Android developer verification (jun/2026)](https://android-developers.googleblog.com/2026/06/android-developer-verification.html)
- [Guia de distribuição limitada](https://developer.android.com/developer-verification/guides/limited-distribution)
- [Guia de distribuição completa](https://developer.android.com/developer-verification/guides/full-distribution)
- [Visão geral da verificação](https://developer.android.com/developer-verification)
- [FAQ da verificação](https://developer.android.com/developer-verification/guides/faq)
