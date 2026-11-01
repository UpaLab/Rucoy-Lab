/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Fórmulas da comunidade, não oficiais (docs/formulas.md, seções 5 e 6).
 */
package io.github.upalab.rucoylab.calculo

import kotlin.math.pow

/**
 * Ticks de treino. O ataque normal é tratado como "power training com 1 mob": as duas situações
 * usam a mesma conta, só muda quantos mobs você bate ao mesmo tempo.
 */
object Ticks {

    const val POR_MOB_POR_HORA = 3600.0
    const val MAX_MOBS_QUE_CONTAM = 5
    const val OFFLINE_POR_HORA = 600.0

    /** Golpes olhados de cada vez: o tick conta se ao menos 1 deles causar dano (🔶 interpretação). */
    private const val JANELA_DE_GOLPES = 10

    fun teto(mobs: Int): Double = POR_MOB_POR_HORA * mobs.coerceIn(1, MAX_MOBS_QUE_CONTAM)

    /** Chance de ao menos um golpe causar dano na janela, somando todos os mobs. */
    private fun chanceDeContar(acerto: Double, mobs: Int): Double =
        1 - (1 - acerto.coerceIn(0.0, 1.0)).pow(JANELA_DE_GOLPES * mobs.coerceAtLeast(1))

    fun porHora(acerto: Double, mobs: Int = 1): Double = teto(mobs) * chanceDeContar(acerto, mobs)

    /**
     * Menor chance de acertar que a comunidade considera boa para treinar.
     * 1 mob → 0,1749; 5 mobs → 0,0377. Fórmula: 1 − 0,8251^(1/mobs).
     */
    fun precisaoMinima(mobs: Int): Double = 1 - 0.8251.pow(1.0 / mobs.coerceAtLeast(1))
}
