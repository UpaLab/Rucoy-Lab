/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Modelo de power training calibrado com os testes A e B do dono do projeto
 * (01/11/2026; melee, stat 98–99, arma de treino, ninjas, bônus de exp ativo). Ver
 * docs/modelo-golpes-liberados.md e docs/pendencias-teste.md.
 */
package io.github.upalab.rucoylab.calculo

import kotlin.math.ceil
import kotlin.math.min

/**
 * Rápido: só especial, um atrás do outro.
 * Econômico: 1 especial e depois golpes normais, que contam tick porque o especial deu dano.
 */
enum class Estilo(val rotulo: String) { RAPIDO("Rápido"), ECONOMICO("Econômico") }

data class ParametrosPower(
    /** Média de mobs atingidos por especial. Teste A: 2 a 3 (mobs morrem e o grupo diminui). */
    val mobsAtingidos: Double = MOBS_MEDIDO,
    /** Segundos entre um especial e outro no estilo Rápido. Teste A: 145 s ÷ 100 especiais. */
    val intervaloEspecial: Double = INTERVALO_MEDIDO,
    /** Golpes normais depois de cada especial no estilo Econômico. Teste B: ~9. */
    val golpesEntreEspeciais: Int = GOLPES_MEDIDO,
    /** Ticks por mob atingido pelo especial, SEM bônus. Teste A: 4,8 por especial ÷ 2,5 de bônus ÷ 2,5 mobs. */
    val ticksPorMob: Double = TICKS_POR_MOB_MEDIDO,
    /** Ticks por golpe normal sem dano, SEM bônus. 🔶 Teste B: ~1,06 com bônus ÷ 2,5 (se o bônus vale nos golpes). */
    val ticksPorGolpe: Double = TICKS_POR_GOLPE_MEDIDO,
    /** Segundos por golpe normal. Teste B: 91 s ÷ (~9,4 × 10) ≈ 0,97. */
    val segundosPorGolpe: Double = 1.0,
    /** Multiplicador total do bônus de exp (ver [Bonus]). */
    val bonus: Double = 1.0,
) {
    companion object {
        const val MOBS_MEDIDO = 2.5
        const val INTERVALO_MEDIDO = 1.45
        const val GOLPES_MEDIDO = 9
        /** Calibrados supondo bônus total de [Bonus.DOS_TESTES_A_B] (2,5×) nos testes. 🔶 */
        const val TICKS_POR_MOB_MEDIDO = 0.77
        const val TICKS_POR_GOLPE_MEDIDO = 0.42

        /** Pela regra do "1 a cada 10", só contam até 9 golpes depois de cada especial. */
        const val MAX_GOLPES_LIBERADOS = 9
    }
}

/** Um ciclo = 1 especial (+ golpes normais, no Econômico). */
data class Ciclo(val segundos: Double, val ticks: Double)

data class PlanoEstilo(
    val estilo: Estilo,
    val ciclo: Ciclo,
    val ticksPorHora: Double,
    val horas: Double,
    val especiais: Double,
    val manaTotal: Double,
    val manaDaRegeneracao: Double,
    val pocoes: Long,
    val gold: Long,
    /** A poção (pelo cooldown) não repõe mana rápido o bastante: o ritmo foi reduzido. */
    val limitadoPeloCooldown: Boolean,
)

object PowerTraining {

    fun ciclo(estilo: Estilo, p: ParametrosPower): Ciclo {
        val doEspecial = p.mobsAtingidos.coerceIn(0.0, Ticks.MAX_MOBS_QUE_CONTAM.toDouble()) * p.ticksPorMob
        return when (estilo) {
            Estilo.RAPIDO -> Ciclo(p.intervaloEspecial, doEspecial * p.bonus)
            Estilo.ECONOMICO -> {
                val golpes = p.golpesEntreEspeciais.coerceAtLeast(0)
                val contam = min(golpes, ParametrosPower.MAX_GOLPES_LIBERADOS)
                // O especial ocupa o lugar de 1 golpe; depois vêm os golpes normais.
                Ciclo(p.segundosPorGolpe * (golpes + 1), (doEspecial + contam * p.ticksPorGolpe) * p.bonus)
            }
        }
    }

    fun plano(estilo: Estilo, ticks: Double, p: ParametrosPower, mana: AjusteMana): PlanoEstilo? {
        var c = ciclo(estilo, p)
        if (c.ticks <= 0 || c.segundos <= 0 || ticks <= 0) return null

        // Mana que entra por segundo (poção no ritmo do cooldown + regeneração) × mana que o ciclo pede.
        val entra = (if (mana.cooldownSeg > 0) mana.recupera / mana.cooldownSeg else Double.POSITIVE_INFINITY) +
            mana.regenPorSegundo.coerceAtLeast(0.0)
        val pede = mana.manaPorEspecial / c.segundos
        val limitado = pede > entra
        if (limitado) c = c.copy(segundos = mana.manaPorEspecial / entra)

        val ciclos = ticks / c.ticks
        val segundos = ciclos * c.segundos
        val manaTotal = ciclos * mana.manaPorEspecial
        val regen = min(manaTotal, mana.regenPorSegundo.coerceAtLeast(0.0) * segundos)
        val pocoes = if (mana.recupera > 0) ceil((manaTotal - regen) / mana.recupera).toLong() else 0L

        return PlanoEstilo(
            estilo = estilo, ciclo = c, ticksPorHora = c.ticks / c.segundos * 3600, horas = segundos / 3600,
            especiais = ciclos, manaTotal = manaTotal, manaDaRegeneracao = regen,
            pocoes = pocoes, gold = (pocoes * mana.preco).toLong(), limitadoPeloCooldown = limitado,
        )
    }
}
