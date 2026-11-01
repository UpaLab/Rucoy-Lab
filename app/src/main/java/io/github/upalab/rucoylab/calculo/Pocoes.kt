/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. A IDEIA da estimativa (mana por especial, poções pela mana) vem do
 * comando /stat do Mims' Rucoy Calculator (créditos no README). Os números são HIPÓTESES da
 * comunidade, a confirmar (docs/pendencias-teste.md).
 */
package io.github.upalab.rucoylab.calculo

import kotlin.math.ceil

enum class Pocao(val nome: String, val manaHipotese: Int, val precoHipotese: Int) {
    NORMAL("Mana Potion", 100, 50),
    GREATER("Greater Mana Potion", 250, 150),
    SUPER("Super Mana Potion", 500, 350),
    ULTIMATE("Ultimate Mana Potion", 750, 650),
}

data class GastoPocao(val pocao: Pocao, val quantidade: Long, val gold: Long)

object Pocoes {

    /** 🔶 Hipótese: cada especial gasta 50 de mana. */
    const val MANA_POR_ESPECIAL_HIPOTESE = 50.0

    /**
     * Quantos especiais você solta para juntar [ticks]: cada especial acerta [mobs] mobs, e cada
     * acerto vale 1 tick vezes o [bonusExp] (1,0 sem bônus; 1,5; 2,0; 2,5).
     */
    fun especiais(ticks: Double, mobs: Int, bonusExp: Double): Double =
        ticks / (mobs.coerceAtLeast(1) * bonusExp.coerceAtLeast(1.0))

    fun mana(ticks: Double, mobs: Int, bonusExp: Double) =
        especiais(ticks, mobs, bonusExp) * MANA_POR_ESPECIAL_HIPOTESE

    fun gasto(ticks: Double, mobs: Int, bonusExp: Double): List<GastoPocao> {
        val m = mana(ticks, mobs, bonusExp)
        return Pocao.entries.map { p ->
            val qtd = ceil(m / p.manaHipotese).toLong()
            GastoPocao(p, qtd, qtd * p.precoHipotese)
        }
    }
}
