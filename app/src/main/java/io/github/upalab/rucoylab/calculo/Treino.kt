/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Lógica de escolha de mob ADAPTADA de helloyanis/rucoy-calculator (GPL-3.0),
 * ui/train/TrainFragment.kt, funções train() e ptrain(), commit f8677ba.
 */
package io.github.upalab.rucoylab.calculo

import io.github.upalab.rucoylab.dados.Mob

data class ResultadoTreino(
    val mob: Mob,
    val precisao: Double,
    val ticksPorHora: Double,
    val tetoTicks: Double,
)

object Treino {

    /**
     * Treino no automático: do mob mais forte pro mais fraco, o primeiro com precisão ≥ 0.1749.
     * Mobs marcados como pulados na fonte são ignorados. Se nenhum servir, usa o primeiro da lista
     * (igual à fonte, que começa com o índice 0).
     */
    fun ondeTreinarAuto(
        mobs: List<Mob>, stat: Double, atkArma: Double, level: Double, anelCritico: Boolean,
    ): ResultadoTreino? {
        if (mobs.isEmpty()) return null
        val dano = Formulas.danoAuto(stat, atkArma, level)
        val candidatos = mobs.filterNot { it.puladoNoTreinoAuto }
        val escolhido = candidatos.asReversed().firstOrNull {
            Formulas.precisao(dano, it.defesa.toDouble(), anelCritico) >= Formulas.PRECISAO_MINIMA_AUTO
        } ?: mobs.first()
        val acc = Formulas.precisao(dano, escolhido.defesa.toDouble(), anelCritico)
        return ResultadoTreino(escolhido, acc, Formulas.ticksPorHoraAuto(acc), Formulas.TICKS_POR_HORA_AUTO)
    }

    /** Power training com [nMobs] mobs, usando o dano do especial. */
    fun ondeTreinarPower(
        mobs: List<Mob>, classe: Classe, stat: Double, atkArma: Double, level: Double,
        anelCritico: Boolean, nMobs: Int,
    ): ResultadoTreino? {
        if (mobs.isEmpty() || nMobs < 1) return null
        val dano = Formulas.danoEspecial(classe, stat, atkArma, level)
        val minimo = Formulas.precisaoMinimaPower(nMobs)
        val candidatos = mobs.filterNot { it.puladoNoPowerTraining }
        val escolhido = candidatos.asReversed().firstOrNull {
            Formulas.precisao(dano, it.defesa.toDouble(), anelCritico) >= minimo
        } ?: mobs.first()
        val acc = Formulas.precisao(dano, escolhido.defesa.toDouble(), anelCritico)
        return ResultadoTreino(
            escolhido, acc, Formulas.ticksPorHoraPower(acc, nMobs), Formulas.tetoPowerTraining(nMobs),
        )
    }
}
