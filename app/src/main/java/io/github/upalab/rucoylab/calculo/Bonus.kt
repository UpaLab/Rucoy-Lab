/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Fontes: notas oficiais em rucoyonline.com/news (updates 1.21 e 1.25) e
 * wiki Rucoy Online ("Stamina and exhaustion"). Ver docs/pendencias-teste.md.
 */
package io.github.upalab.rucoylab.calculo

/** Bônus de exp de skill. */
object Bonus {
    /**
     * Estamina verde (24 h ou mais): +50% → 1,5×. Fontes: notas oficiais (update 1.21), experiência do
     * dono do projeto e o teste de 02/11/2026. ❗ Conflito: a wiki diz 2×.
     */
    const val ESTAMINA_VERDE = 0.5

    /**
     * Bônus dos "120 minutes of bonus level and skill experience": 🔶 A CONFIRMAR.
     * As notas oficiais (update 1.25) falam em 2× de skill, mas no jogo o dono não percebe efeito.
     */
    const val BONUS_120_MIN_OFICIAL = 1.0

    /**
     * 🔶 Quanto o bônus dos 120 min acrescentou NO POWER TRAINING nos testes do dono: nada visível.
     * Teste de 02/11/2026 (quadro a quadro): com os dois bônus ligados, ~1,4 tick por acerto. Se o acerto
     * vale ~1 tick, o total foi ~1,5× (só a estamina verde). Ver docs/pendencias-teste.md, seção 5c.
     */
    const val BONUS_120_MIN_NO_POWER_MEDIDO = 0.0

    /**
     * 🔶 Multiplicador total sugerido, pelo que os testes mostraram (não pelo texto oficial):
     * estamina verde = 1,5×; o bônus dos 120 min (a confirmar) não mudou o resultado do power training.
     */
    fun sugerido(estaminaVerde: Boolean, treinoOnline: Boolean): Double =
        1.0 + (if (estaminaVerde) ESTAMINA_VERDE else 0.0) + (if (treinoOnline) BONUS_120_MIN_NO_POWER_MEDIDO else 0.0)

    /** Multiplicador total que os testes A, B e o de 02/11 sustentam (os dois bônus ligados). 🔶 */
    const val NOS_TESTES = 1.5
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
