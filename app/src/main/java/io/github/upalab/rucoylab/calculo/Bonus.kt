/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Fontes: experiência do dono do projeto e teste de 02/11/2026; wiki Rucoy
 * Online ("Stamina and exhaustion"). Ver docs/pendencias-teste.md.
 */
package io.github.upalab.rucoylab.calculo

/**
 * Bônus de exp: existe UM só, o da estamina verde.
 *
 * A estamina vai até 26 h. Abaixo de 24 h ela fica branca (sem bônus). A parte verde são exatamente as
 * últimas 2 h, e a mensagem "You have 120 minutes of bonus level and skill experience" é o jogo avisando
 * quanto resta dela.
 */
object Bonus {
    /**
     * Multiplicador da estamina verde: 1,5×. Fontes: experiência do dono do projeto e o teste de 02/11/2026
     * (~1,4 tick por acerto). ❗ Conflito: a wiki diz 2×.
     */
    const val ESTAMINA_VERDE = 1.5

    /** Multiplicador padrão: 1,5× com a estamina verde, 1× com a branca. Editável na tela. */
    fun padrao(estaminaVerde: Boolean): Double = if (estaminaVerde) ESTAMINA_VERDE else 1.0

    /** Os testes A, B e o de 02/11 foram todos feitos com a estamina verde. */
    const val NOS_TESTES = ESTAMINA_VERDE
}

/** O mob "cansa": cada mob só dá stat por 7 min 30 s (wiki; observado pelo dono, de forma aproximada). */
object Cansaco {
    const val SEGUNDOS = 450.0

    fun cansaAntesDeMorrer(segundosParaMatar: Double) = segundosParaMatar > SEGUNDOS

    /**
     * 🔶 Suposição simples: a cada 7 min 30 s você para [segundosParaTrocar] pra trocar de mob.
     * O tempo total fica multiplicado por (1 + troca ÷ 450).
     */
    fun fatorTempo(segundosParaTrocar: Double) = 1 + segundosParaTrocar.coerceAtLeast(0.0) / SEGUNDOS
}
