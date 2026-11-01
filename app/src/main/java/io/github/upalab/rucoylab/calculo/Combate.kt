/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. As FÓRMULAS seguem docs/formulas.md e docs/comparacao-formulas.md
 * (descobertas pela comunidade; créditos no README). Fórmulas da comunidade, não oficiais.
 */
package io.github.upalab.rucoylab.calculo

enum class Classe { MELEE, DISTANCE, MAGIC }

/** Intervalo de dano de um golpe, antes ou depois da defesa do alvo. */
data class FaixaDano(val menor: Double, val maior: Double) {
    val media get() = (menor + maior) / 2
    fun contra(defesa: Double) = FaixaDano((menor - defesa).coerceAtLeast(0.0), maior - defesa)
}

/** Tudo o que define o golpe do jogador. */
data class Personagem(
    val classe: Classe,
    val level: Double,
    /** Stat de ataque já somado com os buffs. */
    val stat: Double,
    val ataqueArma: Double,
    val anelCritico: Boolean = false,
)

object Combate {

    /** Parte do dano que vem só do level (comum a todas as classes). */
    private fun bonusLevel(level: Double) = level / 4

    /** Golpe normal: o stat vezes a arma define a faixa; o level soma um valor fixo. */
    fun golpeNormal(p: Personagem): FaixaDano {
        val base = p.stat * p.ataqueArma
        return FaixaDano(base / 20 + bonusLevel(p.level), base / 10 + bonusLevel(p.level))
    }

    /** Fator do especial do mago sobre o stat. 🔶 A CONFIRMAR (fontes divergem: 1,08 × 1,05). */
    const val FATOR_MAGO_A_CONFIRMAR = 1.08

    /** Especial: 1,5× o golpe normal; o mago tem conta própria (ver docs/formulas.md, seção 1). */
    fun golpeEspecial(p: Personagem): FaixaDano {
        if (p.classe != Classe.MAGIC) {
            val n = golpeNormal(p)
            return FaixaDano(n.menor * 1.5, n.maior * 1.5)
        }
        val base = FATOR_MAGO_A_CONFIRMAR * p.stat * p.ataqueArma
        val extra = 9 * p.level / 32
        return FaixaDano(1.5 * (base / 20 + extra), 1.5 * (base / 10 + extra))
    }

    /** Crítico. Sem anel, os números batem nas duas fontes. Com anel = EXPERIMENTAL. */
    object Critico {
        const val CHANCE_SEM_ANEL = 0.01
        const val MULTIPLICADOR_SEM_ANEL = 1.05

        /** 🧪 EXPERIMENTAL: valor sem confirmação (ver docs/pendencias-teste.md). */
        const val CHANCE_COM_ANEL_EXPERIMENTAL = 0.035
        const val MULTIPLICADOR_COM_ANEL_EXPERIMENTAL = 1.075

        fun chance(anel: Boolean) = if (anel) CHANCE_COM_ANEL_EXPERIMENTAL else CHANCE_SEM_ANEL
        fun multiplicador(anel: Boolean) = if (anel) MULTIPLICADOR_COM_ANEL_EXPERIMENTAL else MULTIPLICADOR_SEM_ANEL
    }

    /** Parte (de 0 a 1) da faixa [de, ate] que fica acima da defesa. */
    private fun parteAcima(de: Double, ate: Double, defesa: Double): Double =
        if (ate <= de) (if (ate > defesa) 1.0 else 0.0)
        else ((ate - defesa) / (ate - de)).coerceIn(0.0, 1.0)

    /**
     * Chance de um golpe causar dano. Mistura o golpe comum com o crítico, cada um com o seu peso:
     * (1 − c) × comum + c × crítico. Com c = 1% dá exatamente a conta das fontes.
     * Diferença proposital: o resultado nunca fica negativo (as fontes deixavam).
     */
    fun chanceDeAcertar(golpe: FaixaDano, defesa: Double, anel: Boolean): Double {
        val c = Critico.chance(anel)
        val maiorCritico = golpe.maior * Critico.multiplicador(anel)
        val comum = parteAcima(golpe.menor, golpe.maior, defesa)
        val critico = parteAcima(golpe.maior, maiorCritico, defesa)
        return (1 - c) * comum + c * critico
    }

    /**
     * Dano médio por golpe, como em docs/formulas.md (seção 4), com o mesmo peso de crítico usado
     * na chance de acertar.
     */
    fun danoMedio(golpe: FaixaDano, defesa: Double, anel: Boolean): Double {
        val c = Critico.chance(anel)
        val acerto = chanceDeAcertar(golpe, defesa, anel)
        val comum = golpe.contra(defesa)
        val maiorCritico = golpe.maior * Critico.multiplicador(anel) - defesa
        val mediaCritico = (maiorCritico + comum.maior) / 2
        return (acerto * (1 - c) * comum.media + c * mediaCritico).coerceAtLeast(0.0)
    }

    /** Segundos para matar (1 golpe por segundo). Infinito se o dano médio for zero. */
    fun segundosParaMatar(vida: Double, danoMedio: Double) =
        if (danoMedio > 0) vida / danoMedio else Double.POSITIVE_INFINITY

    // ---- Defesa: dano que você recebe (docs/formulas.md, seção 9) ---------------------------

    data class Armadura(val defesa: Double, val escudo: Double, val outras: Double)

    fun defesaAlta(level: Double, a: Armadura) =
        bonusLevel(level) + a.defesa + a.defesa * a.escudo / 50 + a.outras

    fun defesaBaixa(level: Double, a: Armadura) =
        bonusLevel(level) / 2 + a.defesa / 2 + a.defesa * a.escudo / 100 + a.outras
}
