/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab
 *
 * Este arquivo é ADAPTADO de helloyanis/rucoy-calculator
 * (https://github.com/helloyanis/rucoy-calculator, arquivo
 * src/app/src/main/java/com/helloyanis/rucoycalculator/formulas/Formulas.kt, commit f8677ba),
 * licenciado sob GPL-3.0. As fórmulas de treino são creditadas pela fonte a Mims
 * (https://github.com/Mimsqueeze/Mims-Rucoy-Calculator).
 *
 * Mudanças em relação à fonte: tradução de nomes, funções puras (sem depender da tabela global
 * de mobs) e comentários. O comportamento numérico foi mantido igual ao da fonte, INCLUSIVE as
 * inconsistências documentadas em docs/formulas.md (marcadas com "❗").
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. See the LICENSE file.
 *
 * FÓRMULAS DA COMUNIDADE, NÃO OFICIAIS.
 */
package io.github.upalab.rucoylab.calculo

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** Faixa de dano bruto (antes da defesa do alvo). */
data class Dano(val minimo: Double, val maximo: Double)

enum class Classe { MELEE, DISTANCE, MAGIC }

object Formulas {

    /** Precisão mínima usada pela fonte para o treino no automático (docs/formulas.md, seção 5). */
    const val PRECISAO_MINIMA_AUTO = 0.1749

    /** Teto de ticks por hora no automático. */
    const val TICKS_POR_HORA_AUTO = 3600.0

    // ---- 1. Dano bruto ----------------------------------------------------------------------

    fun danoAuto(stat: Double, atkArma: Double, level: Double) = Dano(
        minimo = stat * atkArma / 20 + level / 4,
        maximo = stat * atkArma / 10 + level / 4,
    )

    fun danoEspecial(classe: Classe, stat: Double, atkArma: Double, level: Double): Dano =
        when (classe) {
            // ❗ A fonte tem uma cópia antiga com 1.05; o app original usa 1.08.
            Classe.MAGIC -> Dano(
                minimo = 1.5 * (1.08 * stat * atkArma / 20 + 9 * level / 32),
                maximo = 1.5 * (1.08 * stat * atkArma / 10 + 9 * level / 32),
            )
            else -> Dano(
                minimo = 1.5 * (stat * atkArma / 20 + level / 4),
                maximo = 1.5 * (stat * atkArma / 10 + level / 4),
            )
        }

    fun maximoCritico(maximoBruto: Double, anelCritico: Boolean) =
        maximoBruto * if (anelCritico) 1.075 else 1.05

    // ---- 3. Precisão -----------------------------------------------------------------------

    fun precisaoNormal(dano: Dano, defesaMob: Double) =
        min(1.0, (dano.maximo - defesaMob) / (dano.maximo - dano.minimo))

    fun precisaoCritico(maxCritico: Double, maximoBruto: Double, defesaMob: Double) =
        min(1.0, (maxCritico - defesaMob) / (maxCritico - maximoBruto))

    /**
     * Precisão final com ajuste de crítico.
     * ❗ Com anel, os pesos somam 1.065 — igual à fonte; ver docs/formulas.md, seção 3.
     */
    fun precisao(dano: Dano, defesaMob: Double, anelCritico: Boolean): Double {
        val maxCrit = maximoCritico(dano.maximo, anelCritico)
        val pesoCrit = if (anelCritico) 0.075 else 0.01
        return precisaoNormal(dano, defesaMob) * 0.99 +
            precisaoCritico(maxCrit, dano.maximo, defesaMob) * pesoCrit
    }

    // ---- 4. Dano médio ---------------------------------------------------------------------

    /** ❗ O peso do crítico com anel aqui é 0.035 (diferente da precisão), igual à fonte. */
    fun danoMedio(dano: Dano, defesaMob: Double, anelCritico: Boolean): Double {
        val acc = precisao(dano, defesaMob, anelCritico)
        val dMin = max(0.0, dano.minimo - defesaMob)
        val dMax = dano.maximo - defesaMob
        val dCrit = maximoCritico(dano.maximo, anelCritico) - defesaMob
        val pesoCrit = if (anelCritico) 0.035 else 0.01
        return acc * (0.99 * (dMax + dMin) / 2) + pesoCrit * (dCrit + dMax) / 2
    }

    /** Em ataques; a fonte trata como segundos (1 ataque por segundo). */
    fun tempoParaMatar(vidaMob: Double, danoMedio: Double) = vidaMob / danoMedio

    // ---- 5 e 6. Ticks ----------------------------------------------------------------------

    fun ticksPorHoraAuto(precisao: Double) =
        TICKS_POR_HORA_AUTO * (1.0 - (1.0 - precisao).pow(10.0))

    /** Teto do power training: n * 3600 até 5 mobs, depois 18000. */
    fun tetoPowerTraining(nMobs: Int): Double = if (nMobs <= 5) nMobs * 3600.0 else 18000.0

    fun precisaoMinimaPower(nMobs: Int): Double = 1.0 - 0.8251.pow(1.0 / nMobs)

    fun ticksPorHoraPower(precisao: Double, nMobs: Int): Double {
        val total = 1.0 - ((1.0 - precisao).pow(nMobs.toDouble())).pow(10.0)
        return tetoPowerTraining(nMobs) * total
    }

    // ---- 7. Ticks acumulados até o stat ----------------------------------------------------

    /** ❗ Descontinuidade entre 54 e 55 (ver docs/formulas.md, seção 7). */
    fun ticksAteStat(stat: Double): Double =
        if (stat <= 54) stat.pow(stat / 1000 + 2.373) else stat.pow(stat / 1000 + 2.171)

    /**
     * Stat (com fração) a partir dos ticks acumulados, como na fonte: busca a faixa e interpola.
     * Retorna -1 se estiver fora das faixas (5..54 e 55..1000).
     */
    fun statPelosTicks(ticks: Double): Double {
        val faixas = if (ticks <= ticksAteStat(54.0)) 5..54 else 55..1000
        for (s in faixas) {
            val atual = ticksAteStat(s.toDouble())
            if (ticks <= atual) {
                val anterior = ticksAteStat((s - 1).toDouble())
                return s - 1 + (ticks - anterior) / (atual - anterior)
            }
        }
        return -1.0
    }

    // ---- 8. Experiência --------------------------------------------------------------------

    /** 🔶 Não está claro se é a exp acumulada até o level ou do level. */
    fun exp(level: Double) = level.pow(level / 1000 + 3)

    // ---- 9. Defesa -------------------------------------------------------------------------

    fun defesaAlta(level: Double, def: Double, armaduraEscudo: Double, armaduraOutros: Double) =
        level / 4 + def + def * armaduraEscudo / 50 + armaduraOutros

    fun defesaBaixa(level: Double, def: Double, armaduraEscudo: Double, armaduraOutros: Double) =
        level / 8 + def / 2 + def * armaduraEscudo / 100 + armaduraOutros

    /** Dano recebido (mín, máx) para um valor de defesa. */
    fun danoRecebido(ataqueMinMob: Double, ataqueMaxMob: Double, defesa: Double) =
        Dano(max(0.0, ataqueMinMob - defesa), ataqueMaxMob - defesa)
}
