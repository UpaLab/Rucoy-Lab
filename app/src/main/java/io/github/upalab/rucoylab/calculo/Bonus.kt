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
    /** Estamina com 24 h ou mais: +50% de exp de level e skill (oficial, update 1.21). */
    const val ESTAMINA_VERDE = 0.5

    /** Bônus de treino online: 2× de exp de skill, 2 h a cada 24 h (oficial, update 1.25). */
    const val TREINO_ONLINE = 1.0

    /**
     * 🔶 Multiplicador total sugerido. O oficial NÃO diz se os dois juntos somam (2,5×) ou multiplicam (3×).
     * Usamos a SOMA porque o Mims' Rucoy Calculator oferece 1,5×, 2× e 2,5×.
     */
    fun sugerido(estaminaVerde: Boolean, treinoOnline: Boolean): Double =
        1.0 + (if (estaminaVerde) ESTAMINA_VERDE else 0.0) + (if (treinoOnline) TREINO_ONLINE else 0.0)

    /** Bônus total suposto nos testes A e B (estamina 26 h verde + "120 minutes of bonus"). 🔶 */
    const val DOS_TESTES_A_B = 2.5
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
