/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Medições do dono do projeto (teste de 02/11/2026, contado quadro a quadro
 * em vídeo). Ver docs/pendencias-teste.md, seção 5c.
 */
package io.github.upalab.rucoylab.calculo

/** Vida perdida por minuto, medida no jogo. */
object VidaMedida {

    /**
     * Vida perdida por minuto num treino de [segundos]: o que as poções devolveram, mais o que a vida caiu
     * (início − fim), mais a regeneração natural no período.
     */
    fun porMinuto(
        segundos: Double, pocoes: Int, recuperaMedia: Double,
        vidaInicio: Double = 0.0, vidaFim: Double = 0.0, regenPorMinuto: Double = 0.0,
    ): Double {
        if (segundos <= 0) return 0.0
        val minutos = segundos / 60
        return (pocoes * recuperaMedia + (vidaInicio - vidaFim)) / minutos + regenPorMinuto.coerceAtLeast(0.0)
    }

    /**
     * Assassin Ninja, 02/11/2026: 7 Greater Health Potions (média 250) em 116 s → ~905 de vida por minuto.
     * Level 84, Defense 92, escudo 28, outros itens 77 (defesa alta ~241). 4 ninjas na maior parte do
     * tempo, depois 2–3 e 1. 🔶 Sem a vida do início e do fim nem a regeneração: pode ser um pouco mais.
     */
    val ASSASSIN_NINJA_02_11 = porMinuto(116.0, 7, 250.0)

    /** Valor padrão por mob, só onde há medição. */
    fun padrao(nome: String, level: Int): Double? =
        if (nome == "Assassin Ninja" && level == 55) ASSASSIN_NINJA_02_11 else null
}

/** Regeneração natural de vida (dono do projeto, 02/11/2026). 🔶 O intervalo ainda não foi medido. */
object RegenVida {
    const val POR_VEZ_SEM_COLAR = 2
    const val POR_VEZ_COM_COLAR = 3
}
