/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Valores padrão com fonte (ver docs/pendencias-teste.md):
 * - quanto recupera e preço: wiki Rucoy Online (Potions List, Supplies Merchant);
 *   Ultimate: wiki francesa (Potion de Mana / Potion de vie, mar/2025)
 * - cooldown: wiki francesa; Greater e Super confirmados nas notas oficiais (update 1.22)
 * - mana do especial: medida pelo dono do projeto (01/11/2026)
 * A ideia de estimar poções pela mana do especial vem do /stat do Mims' Rucoy Calculator.
 */
package io.github.upalab.rucoylab.calculo

import kotlin.math.ceil
import kotlin.math.min

/** Um tipo de poção, com os valores padrão e de onde eles vieram. */
data class TipoPocao(
    val nome: String,
    val recuperaMin: Int,
    val recuperaMax: Int,
    val preco: Int,
    val cooldownSeg: Double,
    val levelMinimo: Int,
    val fonte: String,
) {
    /** A poção recupera um valor sorteado entre o mínimo e o máximo; em média, o meio. */
    val recuperaMedia get() = (recuperaMin + recuperaMax) / 2.0
}

object TiposPocao {
    private const val WIKI = "wiki Rucoy Online (Potions List / Supplies Merchant)"
    private const val WIKI_FR = "wiki francesa (Potion de Mana / Potion de vie)"
    private const val OFICIAL = "notas oficiais 1.22"

    val MANA = listOf(
        TipoPocao("Mana Potion", 70, 130, 50, 1.0, 0, "$WIKI; cooldown: $WIKI_FR"),
        TipoPocao("Greater Mana Potion", 200, 300, 150, 1.25, 75, "$WIKI; cooldown: $OFICIAL"),
        TipoPocao("Super Mana Potion", 400, 600, 350, 1.65, 150, "$WIKI; cooldown: $OFICIAL"),
        TipoPocao("Ultimate Mana Potion", 600, 900, 650, 2.0, 250, "recupera e cooldown: $WIKI_FR; preço: $WIKI"),
    )
    val VIDA = listOf(
        TipoPocao("Health Potion", 70, 130, 50, 1.0, 0, "$WIKI; cooldown: $WIKI_FR"),
        TipoPocao("Greater Health Potion", 200, 300, 150, 1.25, 75, "$WIKI; cooldown: $OFICIAL"),
        TipoPocao("Super Health Potion", 400, 600, 350, 1.65, 150, "$WIKI; cooldown: $OFICIAL"),
        TipoPocao("Ultimate Health Potion", 600, 900, 650, 2.0, 250, "recupera e cooldown: $WIKI_FR; preço: $WIKI"),
    )
}

/** O que o jogador pode ajustar na tela. */
data class AjusteMana(
    /** Medido pelo dono do projeto: 50. */
    val manaPorEspecial: Double = MANA_POR_ESPECIAL_MEDIDA,
    val recupera: Double,
    val preco: Double,
    val cooldownSeg: Double,
    /** Mana que volta sozinha por segundo. Sem fonte: padrão 0. */
    val regenPorSegundo: Double = 0.0,
) {
    companion object {
        const val MANA_POR_ESPECIAL_MEDIDA = 50.0
        fun de(t: TipoPocao) = AjusteMana(recupera = t.recuperaMedia, preco = t.preco.toDouble(), cooldownSeg = t.cooldownSeg)
    }
}

data class PlanoMana(
    val especiais: Double,
    val manaTotal: Double,
    val manaDaRegeneracao: Double,
    val pocoes: Long,
    val gold: Long,
    /** Horas no ritmo pedido. */
    val horas: Double,
    val pocoesPorMinuto: Double,
    val maxPocoesPorMinuto: Double,
    /** O cooldown não deixa tomar poção rápido o bastante para esse ritmo. */
    val limitadoPeloCooldown: Boolean,
    /** Se limitado: horas de verdade, no ritmo que a mana aguenta. */
    val horasReais: Double,
)

data class PlanoVida(val pocoes: Long, val gold: Long, val pocoesPorMinuto: Double, val limitadoPeloCooldown: Boolean)

object Pocoes {

    /** Ticks que cada especial rende: 1 por mob atingido (até o limite de 5 que contam), vezes o bônus de exp. */
    fun ticksPorEspecial(mobs: Int, bonusExp: Double): Double =
        mobs.coerceIn(1, Ticks.MAX_MOBS_QUE_CONTAM) * bonusExp.coerceAtLeast(1.0)

    fun planoMana(ticks: Double, ticksPorHora: Double, mobs: Int, bonusExp: Double, a: AjusteMana): PlanoMana {
        val porEspecial = ticksPorEspecial(mobs, bonusExp)
        val especiais = ticks / porEspecial
        val horas = Progressao.horas(ticks, ticksPorHora)
        val segundos = horas * 3600

        val manaTotal = especiais * a.manaPorEspecial
        val regen = min(manaTotal, a.regenPorSegundo.coerceAtLeast(0.0) * segundos)
        val pocoes = if (a.recupera > 0) ceil((manaTotal - regen) / a.recupera).toLong() else 0L

        val porMinuto = if (horas > 0) pocoes / (horas * 60) else 0.0
        val maxPorMinuto = if (a.cooldownSeg > 0) 60 / a.cooldownSeg else Double.POSITIVE_INFINITY
        val limitado = porMinuto > maxPorMinuto

        // Se o cooldown limita: a mana que entra por segundo define quantos especiais dá pra soltar.
        val horasReais = if (!limitado) horas else {
            val manaPorSegundo = a.recupera / a.cooldownSeg + a.regenPorSegundo
            val especiaisPorSegundo = manaPorSegundo / a.manaPorEspecial
            especiais / especiaisPorSegundo / 3600
        }

        return PlanoMana(
            especiais = especiais, manaTotal = manaTotal, manaDaRegeneracao = regen,
            pocoes = pocoes, gold = (pocoes * a.preco).toLong(), horas = horas,
            pocoesPorMinuto = porMinuto, maxPocoesPorMinuto = maxPorMinuto,
            limitadoPeloCooldown = limitado, horasReais = horasReais,
        )
    }

    /**
     * ESTIMATIVA de poções de vida: depende de quanto você apanha, que muda com o mob, a sua defesa e
     * quantos mobs batem em você. Por isso usa a vida perdida por minuto que o jogador MEDE no jogo.
     */
    fun planoVida(vidaPerdidaPorMinuto: Double, horas: Double, recupera: Double, preco: Double, cooldownSeg: Double): PlanoVida {
        val minutos = horas * 60
        val pocoes = if (recupera > 0) ceil(vidaPerdidaPorMinuto.coerceAtLeast(0.0) * minutos / recupera).toLong() else 0L
        val porMinuto = if (minutos > 0) pocoes / minutos else 0.0
        val max = if (cooldownSeg > 0) 60 / cooldownSeg else Double.POSITIVE_INFINITY
        return PlanoVida(pocoes, (pocoes * preco).toLong(), porMinuto, porMinuto > max)
    }
}
