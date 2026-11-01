/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Regra de escolha descrita em docs/formulas.md (seções 5 e 6):
 * "o mob mais forte em que a chance de acertar ainda passa do mínimo".
 */
package io.github.upalab.rucoylab.calculo

import io.github.upalab.rucoylab.dados.Mob

enum class Modo { NORMAL, POWER }

data class PedidoTreino(val personagem: Personagem, val modo: Modo, val mobs: Int = 1) {
    val mobsJuntos get() = if (modo == Modo.NORMAL) 1 else mobs.coerceAtLeast(1)
}

/** Como o seu golpe se sai contra um mob específico. */
data class Desempenho(
    val mob: Mob,
    val golpe: FaixaDano,
    val acerto: Double,
    val ticksPorHora: Double,
    val danoMedio: Double,
    val segundosParaMatar: Double,
)

data class Sugestao(
    val melhor: Desempenho,
    /** Mobs com a mesma defesa do melhor: rendem o mesmo. */
    val mesmaDefesa: List<Mob>,
    val minimo: Double,
    val teto: Double,
    val proximo: Mob?,
    /** Pontos de stat que faltam para o [proximo]; null = mais de 1000. */
    val faltamPontos: Int?,
    /** Nenhum mob passou do mínimo: [melhor] é só o mais fraco da lista. */
    val semOpcaoBoa: Boolean,
)

object Treino {

    private fun golpe(p: Personagem, modo: Modo) =
        if (modo == Modo.NORMAL) Combate.golpeNormal(p) else Combate.golpeEspecial(p)

    private fun serve(mob: Mob, modo: Modo) =
        if (modo == Modo.NORMAL) !mob.puladoNoTreinoAuto else !mob.puladoNoPowerTraining

    fun desempenho(pedido: PedidoTreino, mob: Mob): Desempenho {
        val p = pedido.personagem
        val g = golpe(p, pedido.modo)
        val def = mob.defesa.toDouble()
        val acerto = Combate.chanceDeAcertar(g, def, p.anelCritico)
        val medio = Combate.danoMedio(g, def, p.anelCritico)
        return Desempenho(
            mob = mob,
            golpe = g,
            acerto = acerto,
            ticksPorHora = Ticks.porHora(acerto, pedido.mobsJuntos),
            danoMedio = medio,
            segundosParaMatar = Combate.segundosParaMatar(mob.vida.toDouble(), medio),
        )
    }

    fun sugerir(lista: List<Mob>, pedido: PedidoTreino): Sugestao? {
        val p = pedido.personagem
        if (lista.isEmpty() || p.level <= 0 || p.stat <= 0 || p.ataqueArma <= 0) return null

        val minimo = Ticks.precisaoMinima(pedido.mobsJuntos)
        val opcoes = lista.filter { serve(it, pedido.modo) }.sortedBy { it.defesa }
        val passa = { m: Mob -> desempenho(pedido, m).acerto >= minimo }

        val escolhido = opcoes.lastOrNull(passa)
        val melhor = desempenho(pedido, escolhido ?: lista.minBy { it.defesa })
        val proximo = opcoes.firstOrNull { it.defesa > melhor.mob.defesa }

        val faltam = proximo?.let { alvo ->
            (0..1000).firstOrNull { extra ->
                val mais = pedido.copy(personagem = p.copy(stat = p.stat + extra))
                desempenho(mais, alvo).acerto >= minimo
            }
        }

        return Sugestao(
            melhor = melhor,
            mesmaDefesa = opcoes.filter { it != melhor.mob && it.defesa == melhor.mob.defesa },
            minimo = minimo,
            teto = Ticks.teto(pedido.mobsJuntos),
            proximo = proximo,
            faltamPontos = faltam,
            semOpcaoBoa = escolhido == null,
        )
    }
}
