package io.github.upalab.rucoylab.calculo

import io.github.upalab.rucoylab.dados.Mobs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.math.pow

/**
 * Confere o código próprio do UpaLab contra valores calculados à mão a partir de
 * docs/formulas.md e docs/comparacao-formulas.md.
 * Isso garante que fazemos a MESMA conta da comunidade — não que a conta está certa no jogo.
 */
class FormulasTest {

    private val d = 1e-6
    private val mobs by lazy { Mobs.deJson(File("../data/mobs.json").readText()) }
    private fun melee(level: Double, stat: Double, arma: Double, anel: Boolean = false) =
        Personagem(Classe.MELEE, level, stat, arma, anel)

    // ---- Combate ----

    @Test fun golpeNormal() {
        val g = Combate.golpeNormal(melee(40.0, 50.0, 20.0))
        assertEquals(60.0, g.menor, d)   // 50*20/20 + 40/4
        assertEquals(110.0, g.maior, d)  // 50*20/10 + 40/4
    }

    @Test fun especialMeleeE15xONormal() {
        val p = melee(40.0, 50.0, 20.0)
        assertEquals(165.0, Combate.golpeEspecial(p).maior, d)
    }

    @Test fun especialMagoComFatorAConfirmar() {
        val g = Combate.golpeEspecial(Personagem(Classe.MAGIC, 40.0, 50.0, 20.0))
        assertEquals(1.5 * (1.08 * 50 * 20 / 20 + 9 * 40.0 / 32), g.menor, d)
    }

    @Test fun chanceDeAcertarIgualAsFontesSemAnel() {
        val g = FaixaDano(60.0, 110.0)
        // fontes: 0,99 × (110−85)/(110−60) + 0,01 × min(1, (115,5−85)/(115,5−110))
        assertEquals(0.99 * 0.5 + 0.01 * 1.0, Combate.chanceDeAcertar(g, 85.0, false), d)
        assertEquals(1.0, Combate.chanceDeAcertar(g, 10.0, false), d)
    }

    @Test fun chanceDeAcertarNuncaENegativa() {
        assertEquals(0.0, Combate.chanceDeAcertar(FaixaDano(10.0, 20.0), 500.0, false), d)
    }

    @Test fun danoMedioIgualAsFontesSemAnel() {
        val g = FaixaDano(60.0, 110.0)
        val acerto = 0.99 * 0.5 + 0.01
        // fontes: acerto × 0,99 × (25+0)/2 + 0,01 × (30,5+25)/2
        assertEquals(acerto * 0.99 * 12.5 + 0.01 * 27.75, Combate.danoMedio(g, 85.0, false), d)
    }

    @Test fun defesa() {
        val a = Combate.Armadura(defesa = 50.0, escudo = 20.0, outras = 5.0)
        assertEquals(10.0 + 50 + 20 + 5, Combate.defesaAlta(40.0, a), d)
        assertEquals(5.0 + 25 + 10 + 5, Combate.defesaBaixa(40.0, a), d)
    }

    // ---- Ticks ----

    @Test fun precisaoMinima() {
        assertEquals(0.1749, Ticks.precisaoMinima(1), 1e-4)
        assertEquals(0.0916, Ticks.precisaoMinima(2), 1e-4)
        assertEquals(0.0377, Ticks.precisaoMinima(5), 1e-4)
    }

    @Test fun ticksNormalEPower() {
        assertEquals(3600.0, Ticks.porHora(1.0), d)
        assertEquals(3600 * (1 - 0.5.pow(10)), Ticks.porHora(0.5), d)
        assertEquals(10800 * (1 - 0.8.pow(30)), Ticks.porHora(0.2, 3), d)
        assertEquals(18000.0, Ticks.teto(8), d)
        // No mínimo, rende ~85,4% do teto.
        assertEquals(0.8538, Ticks.porHora(0.1749) / 3600, 1e-4)
    }

    // ---- Progressão ----

    @Test fun ticksDentroDeUmaFaixaIgualAsFontes() {
        val baixa = { s: Double -> s.pow(s / 1000 + 2.373) }
        val alta = { s: Double -> s.pow(s / 1000 + 2.171) }
        assertEquals(baixa(50.0) - baixa(30.0), Progressao.ticksEntre(30, 50), 1e-6)
        assertEquals(alta(90.0) - alta(60.0), Progressao.ticksEntre(60, 90), 1e-6)
    }

    @Test fun ticksNuncaFicamNegativosNaVirada54Para55() {
        val t = Progressao.ticksEntre(50, 60)
        assertTrue("50→60 deu $t", t > 0)   // as fontes dão −3.810 aqui
    }

    @Test fun statDepoisVoltaNoMesmoPonto() {
        val ticks = Progressao.ticksEntre(40, 70)
        assertEquals(70.0, Progressao.statDepois(40.0, ticks), 1e-6)
    }

    @Test fun offlineUsa600PorHoraEmQualquerFaixa() {
        assertEquals(Progressao.ticksEntre(20, 30) / 600, Progressao.horasOffline(20, 30), d)
        // A fonte helloyanis errava abaixo do 55; aqui usa a curva certa.
        val baixa = { s: Double -> s.pow(s / 1000 + 2.373) }
        assertEquals((baixa(30.0) - baixa(20.0)) / 600, Progressao.horasOffline(20, 30), 1e-6)
    }

    @Test fun expEntreLevels() {
        val e = { l: Double -> l.pow(l / 1000 + 3) }
        assertEquals(e(101.0) - e(100.0), Progressao.expEntre(100, 101), 1e-3)
    }

    // ---- Poções ----

    @Test fun pocoesComValoresDaWiki() {
        assertEquals(100.0, TiposPocao.MANA[0].recuperaMedia, d)       // wiki: 70–130
        assertEquals(250.0, TiposPocao.MANA[1].recuperaMedia, d) // wiki: 200–300
        assertEquals(1.25, TiposPocao.MANA[1].cooldownSeg, d)    // notas oficiais 1.22
    }

    private val basica get() = AjusteMana.de(TiposPocao.MANA[0])
    private fun ticksDoPonto(s: Int) = Progressao.ticksDoPonto(s)

    /** Teste A do dono: 100 especiais em ~145 s, 50 poções, de 98 (93%) a 99 (44%), bônus medido 1,5×. */
    @Test fun reproduzTesteA() {
        val ganhou = 0.07 * ticksDoPonto(98) + 0.44 * ticksDoPonto(99)   // ≈ 479 ticks
        val p = PowerTraining.plano(Estilo.RAPIDO, ganhou, ParametrosPower(bonus = Bonus.NOS_TESTES), basica)!!
        assertEquals(100.0, p.especiais, 6.0)          // ~100 especiais
        assertEquals(145.0, p.horas * 3600, 10.0)      // ~145 s
        assertEquals(50.0, p.pocoes.toDouble(), 5.0)   // ~50 poções
    }

    /** Teste B do dono: ~91 s, ~9,4 especiais, 3 poções + 190 de mana da barra, de 99 (44%) a 99 (58%), bônus medido 1,5×. */
    @Test fun reproduzTesteB() {
        val ganhou = 0.14 * ticksDoPonto(99)                              // ≈ 132 ticks
        val p = PowerTraining.plano(Estilo.ECONOMICO, ganhou, ParametrosPower(bonus = Bonus.NOS_TESTES), basica)!!
        assertEquals(9.4, p.especiais, 1.0)
        assertEquals(91.0, p.horas * 3600, 10.0)
        assertEquals(470.0, p.manaTotal, 50.0)
    }

    /**
     * Teste de 02/11 (quadro a quadro): 99 (59%) → 100 (1%) em 116 s, ~87 especiais, ~276 acertos,
     * 415 de mana da barra + 37 poções básicas ≈ 4.350 de mana. Estamina verde (1,5×).
     */
    @Test fun reproduzTesteQuadroAQuadro() {
        val ganhou = 0.41 * ticksDoPonto(99) + 0.01 * ticksDoPonto(100)   // ≈ 396 ticks
        assertEquals(1.43, ganhou / 276, 0.02)                             // ~1,4 tick por acerto
        val p = PowerTraining.plano(Estilo.RAPIDO, ganhou, ParametrosPower(bonus = Bonus.NOS_TESTES), basica)!!
        assertEquals(87.0, p.especiais, 5.0)
        assertEquals(116.0, p.horas * 3600, 8.0)
        assertEquals(4350.0, p.manaTotal, 250.0)
    }

    @Test fun vidaNosNinjas() {
        assertEquals(905.0, VidaMedida.ASSASSIN_NINJA_02_11, 1.0)          // 7 × 250 em 116 s
        assertEquals(VidaMedida.ASSASSIN_NINJA_02_11, VidaMedida.padrao("Assassin Ninja", 55)!!, d)
        assertEquals(null, VidaMedida.padrao("Assassin", 50))
        // Vida que caiu também conta: 1 poção de 100 + 200 de vida perdida em 60 s = 300/min.
        assertEquals(300.0, VidaMedida.porMinuto(60.0, 1, 100.0, 1000.0, 800.0), d)
    }

    @Test fun defesaDoSetDoTeste() {
        val a = Combate.Armadura(defesa = 92.0, escudo = 28.0, outras = 77.0)
        assertEquals(241.52, Combate.defesaAlta(84.0, a), 0.01)
        assertEquals(159.26, Combate.defesaBaixa(84.0, a), 0.01)
    }

    @Test fun economicoGastaMenosPocaoERapidoEMaisRapido() {
        val t = Progressao.ticksEntre(70, 90)
        val r = PowerTraining.plano(Estilo.RAPIDO, t, ParametrosPower(), basica)!!
        val e = PowerTraining.plano(Estilo.ECONOMICO, t, ParametrosPower(), basica)!!
        assertTrue(e.pocoes < r.pocoes)
        assertTrue(r.horas < e.horas)
    }

    @Test fun soNoveGolpesContam() {
        val nove = PowerTraining.ciclo(Estilo.ECONOMICO, ParametrosPower(golpesEntreEspeciais = 9))
        val quinze = PowerTraining.ciclo(Estilo.ECONOMICO, ParametrosPower(golpesEntreEspeciais = 15))
        assertEquals(nove.ticks, quinze.ticks, d)
        assertTrue(quinze.segundos > nove.segundos)
    }

    @Test fun regeneracaoDescontaMana() {
        val p0 = PowerTraining.plano(Estilo.RAPIDO, 10000.0, ParametrosPower(), basica)!!
        val p1 = PowerTraining.plano(Estilo.RAPIDO, 10000.0, ParametrosPower(), basica.copy(regenPorSegundo = 5.0))!!
        assertTrue(p1.pocoes < p0.pocoes)
    }

    @Test fun cooldownLimitaORitmo() {
        // Básica: até 100 de mana/s. Especial custando 300 a cada 1,45 s pede ~207/s → limitado.
        val p = PowerTraining.plano(Estilo.RAPIDO, 1000.0, ParametrosPower(), basica.copy(manaPorEspecial = 300.0))!!
        assertTrue(p.limitadoPeloCooldown)
        assertEquals(3.0, p.ciclo.segundos, 1e-6)  // 300 ÷ 100
    }

    @Test fun vidaPorMinuto() {
        val v = Pocoes.planoVida(300.0, 0.5, 100.0, 50.0, 1.0) // 30 min × 300 = 9.000 de vida
        assertEquals(90L, v.pocoes)
        assertEquals(4500L, v.gold)
    }

    // ---- Treino ----

    @Test fun tabelaDeMobs() {
        assertEquals(44, mobs.size)
        assertEquals(2, mobs.count { it.puladoNoTreinoAuto })
        assertEquals(17, mobs.count { it.puladoNoPowerTraining })
    }

    /** Valores esperados calculados replicando a regra das fontes (ver histórico de testes). */
    @Test fun mesmoMobQueAsFontes() {
        fun mob(stat: Double, arma: Double, level: Double) =
            Treino.sugerir(mobs, PedidoTreino(melee(level, stat, arma), Modo.NORMAL))!!.melhor.mob.let { it.nome to it.level }
        assertEquals("Skeleton Archer" to 80, mob(50.0, 20.0, 40.0))
        assertEquals("Rat" to 1, mob(10.0, 5.0, 5.0))
        assertEquals("Lizard Captain" to 180, mob(120.0, 35.0, 150.0))
        assertEquals("Orthrus" to 400, mob(300.0, 50.0, 300.0))
        assertEquals("Yeti" to 350, mob(200.0, 44.0, 260.0))
    }

    @Test fun proximoMobEPontosQueFaltam() {
        val p = melee(40.0, 50.0, 20.0)
        val s = Treino.sugerir(mobs, PedidoTreino(p, Modo.NORMAL))!!
        val alvo = s.proximo!!
        val faltam = s.faltamPontos!!
        assertTrue(alvo.defesa > s.melhor.mob.defesa)
        fun acerto(extra: Int) = Combate.chanceDeAcertar(Combate.golpeNormal(p.copy(stat = p.stat + extra)), alvo.defesa.toDouble(), false)
        assertTrue(acerto(faltam) >= 0.1749)
        assertTrue(acerto(faltam - 1) < 0.1749)
    }

    @Test fun powerTraining() {
        val s = Treino.sugerir(mobs, PedidoTreino(Personagem(Classe.DISTANCE, 100.0, 80.0, 30.0), Modo.POWER, 3))!!
        assertEquals(10800.0, s.teto, d)
        assertTrue(s.melhor.acerto >= Ticks.precisaoMinima(3))
        assertTrue(!s.melhor.mob.puladoNoPowerTraining)
    }

    @Test fun buffsSomamNoStat() {
        val sem = Treino.sugerir(mobs, PedidoTreino(melee(40.0, 50.0, 20.0), Modo.NORMAL))!!
        val com = Treino.sugerir(mobs, PedidoTreino(melee(40.0, 50.0 + sem.faltamPontos!!, 20.0), Modo.NORMAL))!!
        assertTrue(com.melhor.mob.defesa >= sem.proximo!!.defesa)
    }

    @Test fun entradaInvalida() {
        assertEquals(null, Treino.sugerir(mobs, PedidoTreino(melee(0.0, 50.0, 20.0), Modo.NORMAL)))
    }

    @Test fun assassinosTemCorConferida() {
        val a = mobs.filter { it.nome.startsWith("Assassin") }.associate { it.level to it.apelido }
        assertEquals(mapOf(45 to "vermelho", 50 to "amarelo", 55 to "cinza"), a)
    }

    /** 600 fichas aleatórias: a escolha do mob tem que ser igual à regra das fontes (sem anel). */
    @Test fun seiscentasFichasIguaisAsFontes() {
        val linhas = javaClass.getResource("/casos-fontes.csv")!!.readText().lines().filter { it.isNotBlank() && !it.startsWith("#") }
        assertEquals(600, linhas.size)
        val erros = linhas.mapNotNull { l ->
            val c = l.split(",")
            val p = Personagem(Classe.valueOf(c[0]), c[1].toDouble(), c[2].toDouble(), c[3].toDouble())
            val modo = if (c[4] == "N") Modo.NORMAL else Modo.POWER
            val s = Treino.sugerir(mobs, PedidoTreino(p, modo, c[5].toInt()))!!
            val obtido = if (s.semOpcaoBoa) "" else "${s.melhor.mob.nome}|${s.melhor.mob.level}"
            if (obtido != c[6]) "$l → $obtido" else null
        }
        assertTrue("Diferenças:\n" + erros.take(10).joinToString("\n"), erros.isEmpty())
    }

    @Test fun bonusOficiais() {
        assertEquals(1.0, Bonus.padrao(false), d)   // estamina branca: sem bônus
        assertEquals(1.5, Bonus.padrao(true), d)    // estamina verde: 1,5× (❗ a wiki diz 2×)
    }

    @Test fun cansacoDoMob() {
        assertTrue(Cansaco.cansaAntesDeMorrer(451.0))
        assertTrue(!Cansaco.cansaAntesDeMorrer(300.0))
        assertEquals(1.0 + 15.0 / 450, Cansaco.fatorTempo(15.0), d)
    }

}
