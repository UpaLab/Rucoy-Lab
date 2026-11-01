/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Fórmulas da comunidade, não oficiais (docs/formulas.md, seções 7 e 8;
 * docs/comparacao-formulas.md, itens 15 a 17).
 */
package io.github.upalab.rucoylab.calculo

import kotlin.math.floor
import kotlin.math.pow

object Progressao {

    /** Stat a partir do qual a comunidade troca de curva. */
    private const val INICIO_CURVA_ALTA = 55

    private fun curvaBaixa(s: Double) = s.pow(s / 1000 + 2.373)
    private fun curvaAlta(s: Double) = s.pow(s / 1000 + 2.171)

    /**
     * Ticks para sair do stat [s] e chegar em [s] + 1.
     *
     * 🔶 Decisão do UpaLab: as fontes subtraem o total de ticks de uma curva pela outra, e isso dá
     * resultado NEGATIVO quando passa do 54 para o 55 (ex.: 50 → 60 dá −3.810). Aqui somamos o custo
     * de cada ponto, sempre dentro de uma curva só: até 54 usa a baixa; do 54 em diante, a alta.
     * Dentro de uma mesma faixa o resultado é igual ao das fontes.
     */
    fun ticksDoPonto(s: Int): Double {
        val de = s.toDouble(); val ate = de + 1
        return if (ate < INICIO_CURVA_ALTA) curvaBaixa(ate) - curvaBaixa(de)
        else curvaAlta(ate) - curvaAlta(de)
    }

    /** Ticks para ir do stat [de] até [ate] (os dois inteiros). */
    fun ticksEntre(de: Int, ate: Int): Double =
        if (ate <= de) 0.0 else (de until ate).sumOf { ticksDoPonto(it) }

    /** Stat (com fração) depois de treinar [ticks] a partir de [stat], ponto por ponto. */
    fun statDepois(stat: Double, ticks: Double, limite: Int = 2000): Double {
        var atual = floor(stat).toInt()
        var sobra = ticks + (stat - atual) * ticksDoPonto(atual)
        while (atual < limite) {
            val custo = ticksDoPonto(atual)
            if (sobra < custo) return atual + sobra / custo
            sobra -= custo
            atual++
        }
        return limite.toDouble()
    }

    /** Horas de treino, dado o ritmo de ticks por hora. */
    fun horas(ticks: Double, ticksPorHora: Double) =
        if (ticksPorHora > 0) ticks / ticksPorHora else Double.POSITIVE_INFINITY

    // ---- Treino offline (boneco): 600 ticks por hora ----------------------------------------

    fun horasOffline(de: Int, ate: Int) = horas(ticksEntre(de, ate), Ticks.OFFLINE_POR_HORA)
    fun statDepoisOffline(stat: Double, horas: Double) = statDepois(stat, horas * Ticks.OFFLINE_POR_HORA)

    // ---- Level (experiência) ------------------------------------------------------------

    /** Experiência TOTAL acumulada até chegar no [level]. */
    fun expTotal(level: Double) = level.pow(level / 1000 + 3)

    fun expEntre(de: Int, ate: Int) =
        if (ate <= de) 0.0 else expTotal(ate.toDouble()) - expTotal(de.toDouble())
}
