package io.github.upalab.rucoylab.calculo

import io.github.upalab.rucoylab.dados.Mobs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Confere as fórmulas contra valores calculados à mão a partir de docs/formulas.md.
 * Isso garante que o app faz o MESMO cálculo da fonte — não que a fonte está certa no jogo.
 */
class FormulasTest {

    private val delta = 1e-6

    @Test fun danoAuto() {
        val d = Formulas.danoAuto(stat = 50.0, atkArma = 20.0, level = 40.0)
        assertEquals(60.0, d.minimo, delta)   // 50*20/20 + 40/4
        assertEquals(110.0, d.maximo, delta)  // 50*20/10 + 40/4
    }

    @Test fun especialMeleeE15xAuto() {
        val auto = Formulas.danoAuto(50.0, 20.0, 40.0)
        val esp = Formulas.danoEspecial(Classe.MELEE, 50.0, 20.0, 40.0)
        assertEquals(auto.maximo * 1.5, esp.maximo, delta)
    }

    @Test fun especialMagic() {
        val esp = Formulas.danoEspecial(Classe.MAGIC, 50.0, 20.0, 40.0)
        assertEquals(1.5 * (1.08 * 50 * 20 / 20 + 9 * 40.0 / 32), esp.minimo, delta)
    }

    @Test fun precisaoLimitadaA1() {
        val d = Dano(60.0, 110.0)
        assertEquals(1.0, Formulas.precisaoNormal(d, defesaMob = 10.0), delta)
        assertEquals(0.5, Formulas.precisaoNormal(d, defesaMob = 85.0), delta)
    }

    @Test fun precisaoMinimaPower() {
        assertEquals(0.1749, Formulas.precisaoMinimaPower(1), 1e-4)
        assertEquals(0.0916, Formulas.precisaoMinimaPower(2), 1e-4)
        assertEquals(0.0377, Formulas.precisaoMinimaPower(5), 1e-4)
    }

    @Test fun tetos() {
        assertEquals(3600.0, Formulas.ticksPorHoraAuto(1.0), delta)
        assertEquals(18000.0, Formulas.tetoPowerTraining(5), delta)
        assertEquals(18000.0, Formulas.tetoPowerTraining(8), delta)
    }

    @Test fun ticksNoLimiarDao85PorCento() {
        assertEquals(0.8538, Formulas.ticksPorHoraAuto(0.1749) / 3600, 1e-4)
    }

    @Test fun ticksAteStatEDescontinuidadeConhecida() {
        assertEquals(16015.0, Formulas.ticksAteStat(54.0), 1.0)
        assertEquals(7483.0, Formulas.ticksAteStat(55.0), 1.0)  // ❗ cai — documentado
    }

    @Test fun statPelosTicksVoltaNoStat() {
        assertEquals(30.0, Formulas.statPelosTicks(Formulas.ticksAteStat(30.0)), 1e-6)
    }

    @Test fun defesa() {
        assertEquals(10.0 + 50 + 50 * 20.0 / 50 + 5, Formulas.defesaAlta(40.0, 50.0, 20.0, 5.0), delta)
        assertEquals(5.0 + 25 + 50 * 20.0 / 100 + 5, Formulas.defesaBaixa(40.0, 50.0, 20.0, 5.0), delta)
    }

    @Test fun tabelaDeMobsCarrega() {
        val mobs = Mobs.deJson(File("../data/mobs.json").readText())
        assertEquals(44, mobs.size)
        assertEquals("Rat", mobs.first().nome)
        assertTrue(mobs.count { it.puladoNoTreinoAuto } == 2)
    }

    private val mobs by lazy { Mobs.deJson(File("../data/mobs.json").readText()) }

    @Test fun ondeTreinarAutoEscolheMobComPrecisaoMinima() {
        val r = Treino.calcular(mobs, Entrada(Classe.MELEE, 40.0, 50.0, 20.0, false, Modo.AUTO))!!
        assertTrue(r.precisao >= Formulas.PRECISAO_MINIMA_AUTO)
        assertTrue(!r.mob.puladoNoTreinoAuto)
        assertTrue(!r.nenhumIdeal)
        // O próximo mob, mais forte, ainda não pode ter a precisão mínima.
        val p = r.proximo!!
        assertTrue(p.mob.defesa > r.mob.defesa)
        assertTrue(p.statsFaltando!! > 0)
        // Com os stats que faltam, a precisão no próximo mob chega no mínimo (e 1 a menos não chega).
        val dMais = Formulas.danoAuto(50.0 + p.statsFaltando!!, 20.0, 40.0)
        val dMenos = Formulas.danoAuto(50.0 + p.statsFaltando!! - 1, 20.0, 40.0)
        assertTrue(Formulas.precisao(dMais, p.mob.defesa.toDouble(), false) >= 0.1749)
        assertTrue(Formulas.precisao(dMenos, p.mob.defesa.toDouble(), false) < 0.1749)
    }

    @Test fun mobsComMesmaDefesaAparecemComoEquivalentes() {
        // Scorpion e Cobra têm defesa 18 na fonte.
        val r = Treino.calcular(mobs, Entrada(Classe.MELEE, 10.0, 12.0, 4.0, false, Modo.AUTO))!!
        if (r.mob.defesa == 18) assertTrue(r.equivalentes.isNotEmpty())
        r.equivalentes.forEach { assertEquals(r.mob.defesa, it.defesa) }
    }

    @Test fun powerTrainingUsaEspecialELimiteDosMobs() {
        val r = Treino.calcular(mobs, Entrada(Classe.DISTANCE, 100.0, 80.0, 30.0, false, Modo.POWER, 3))!!
        assertEquals(10800.0, r.tetoTicks, 1e-9)
        assertTrue(r.precisao >= Formulas.precisaoMinimaPower(3))
        assertTrue(!r.mob.puladoNoPowerTraining)
    }

    @Test fun entradaInvalidaNaoCalcula() {
        assertEquals(null, Treino.calcular(mobs, Entrada(Classe.MELEE, 0.0, 50.0, 20.0, false, Modo.AUTO)))
    }

    /** Valores esperados calculados replicando a lógica da fonte (TrainFragment.train()). */
    @Test fun mesmoMobQueOAppOriginal() {
        fun mob(stat: Double, atk: Double, lvl: Double) =
            Treino.calcular(mobs, Entrada(Classe.MELEE, lvl, stat, atk, false, Modo.AUTO))!!.mob.let { it.nome to it.level }
        assertEquals("Skeleton Archer" to 80, mob(50.0, 20.0, 40.0))
        assertEquals("Rat" to 1, mob(10.0, 5.0, 5.0))
        assertEquals("Lizard Captain" to 180, mob(120.0, 35.0, 150.0))
        assertEquals("Orthrus" to 400, mob(300.0, 50.0, 300.0))
        assertEquals("Yeti" to 350, mob(200.0, 44.0, 260.0))
    }

    @Test fun assassinosTemCorConferida() {
        val a = mobs.filter { it.nome.startsWith("Assassin") }.associate { it.level to it.apelido }
        assertEquals(mapOf(45 to "vermelho", 50 to "amarelo", 55 to "cinza"), a)
        assertEquals("Assassin (amarelo)", mobs.first { it.level == 50 }.nomeExibido)
    }
}
