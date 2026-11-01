/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Lógica de escolha de mob ADAPTADA de helloyanis/rucoy-calculator (GPL-3.0),
 * ui/train/TrainFragment.kt, funções train() e ptrain(), commit f8677ba.
 *
 * Diferenças em relação à fonte:
 * - "Mobs equivalentes": a fonte mostra 2 mobs só em alguns índices fixos (pares com a mesma
 *   defesa). Aqui listamos qualquer mob com a mesma defesa do escolhido.
 * - "Stats que faltam": a fonte soma 1 a mais (incrementa depois do último teste). Aqui é o
 *   número exato de pontos até atingir a precisão mínima no próximo mob.
 */
package io.github.upalab.rucoylab.calculo

import io.github.upalab.rucoylab.dados.Mob

enum class Modo { AUTO, POWER }

data class Entrada(
    val classe: Classe,
    val level: Double,
    val stat: Double,
    val atkArma: Double,
    val anelCritico: Boolean,
    val modo: Modo,
    val nMobs: Int = 1,
)

data class ProximoMob(
    val mob: Mob,
    /** Pontos de stat que faltam para treinar nele; null se passar de 1000 pontos. */
    val statsFaltando: Int?,
    /** Dano máximo que você já tira dele agora (≤ 0 = ainda não machuca). */
    val danoMaximoAgora: Double,
)

data class ResultadoTreino(
    val mob: Mob,
    val equivalentes: List<Mob>,
    val precisao: Double,
    val precisaoMinima: Double,
    val ticksPorHora: Double,
    val tetoTicks: Double,
    val dano: Dano,
    val danoMedio: Double,
    /** Segundos (a fonte assume 1 ataque por segundo). */
    val tempoParaMatar: Double,
    val proximo: ProximoMob?,
    /** Nenhum mob atingiu a precisão mínima; o resultado é só o mob mais fraco. */
    val nenhumIdeal: Boolean,
)

object Treino {

    private const val LIMITE_STATS = 1000

    private fun dano(e: Entrada, stat: Double): Dano =
        if (e.modo == Modo.AUTO) Formulas.danoAuto(stat, e.atkArma, e.level)
        else Formulas.danoEspecial(e.classe, stat, e.atkArma, e.level)

    private fun pulado(e: Entrada, m: Mob) =
        if (e.modo == Modo.AUTO) m.puladoNoTreinoAuto else m.puladoNoPowerTraining

    fun calcular(mobs: List<Mob>, e: Entrada): ResultadoTreino? {
        if (mobs.isEmpty() || e.stat <= 0 || e.atkArma <= 0 || e.level <= 0) return null
        val n = if (e.modo == Modo.AUTO) 1 else e.nMobs.coerceAtLeast(1)
        val minimo = if (e.modo == Modo.AUTO) Formulas.PRECISAO_MINIMA_AUTO else Formulas.precisaoMinimaPower(n)
        val d = dano(e, e.stat)

        // Mesma ordem da fonte: do fim da lista (mais forte) pro começo.
        val candidatos = mobs.filterNot { pulado(e, it) }
        val ideal = candidatos.asReversed().firstOrNull {
            Formulas.precisao(d, it.defesa.toDouble(), e.anelCritico) >= minimo
        }
        val mob = ideal ?: mobs.first()
        val acc = Formulas.precisao(d, mob.defesa.toDouble(), e.anelCritico)
        val ticks = if (e.modo == Modo.AUTO) Formulas.ticksPorHoraAuto(acc) else Formulas.ticksPorHoraPower(acc, n)
        val teto = if (e.modo == Modo.AUTO) Formulas.TICKS_POR_HORA_AUTO else Formulas.tetoPowerTraining(n)
        val medio = Formulas.danoMedio(d, mob.defesa.toDouble(), e.anelCritico)

        val equivalentes = candidatos.filter { it != mob && it.defesa == mob.defesa }

        val idx = candidatos.indexOf(mob)
        val proxMob = if (idx >= 0) candidatos.drop(idx + 1).firstOrNull { it.defesa > mob.defesa } else null
        val proximo = proxMob?.let { p ->
            val faltam = (0..LIMITE_STATS).firstOrNull { extra ->
                Formulas.precisao(dano(e, e.stat + extra), p.defesa.toDouble(), e.anelCritico) >= minimo
            }
            ProximoMob(p, faltam, d.maximo - p.defesa)
        }

        return ResultadoTreino(
            mob = mob,
            equivalentes = equivalentes,
            precisao = acc,
            precisaoMinima = minimo,
            ticksPorHora = ticks,
            tetoTicks = teto,
            dano = d,
            danoMedio = medio,
            tempoParaMatar = if (medio > 0) Formulas.tempoParaMatar(mob.vida.toDouble(), medio) else Double.POSITIVE_INFINITY,
            proximo = proximo,
            nenhumIdeal = ideal == null,
        )
    }
}
