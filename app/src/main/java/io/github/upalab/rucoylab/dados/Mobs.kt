/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Os dados vêm de data/mobs.json, extraído de helloyanis/rucoy-calculator (GPL-3.0).
 * DADOS DA COMUNIDADE, NÃO OFICIAIS.
 */
package io.github.upalab.rucoylab.dados

import android.content.Context
import org.json.JSONObject

data class Mob(
    val nome: String,
    val level: Int,
    val defesa: Int,
    val vida: Int,
    val puladoNoTreinoAuto: Boolean,
    val puladoNoPowerTraining: Boolean,
)

object Mobs {

    /** Lê o JSON da tabela de mobs (mesmo formato de data/mobs.json). Ordem = ordem da fonte. */
    fun deJson(texto: String): List<Mob> {
        val lista = JSONObject(texto).getJSONArray("mobs")
        return List(lista.length()) { i ->
            val o = lista.getJSONObject(i)
            Mob(
                nome = o.getString("nome"),
                level = o.getInt("level"),
                defesa = o.getInt("defesa"),
                vida = o.getInt("vida"),
                puladoNoTreinoAuto = o.optBoolean("pulado_no_treino_auto"),
                puladoNoPowerTraining = o.optBoolean("pulado_no_power_training"),
            )
        }
    }

    fun carregar(context: Context): List<Mob> =
        context.assets.open("mobs.json").bufferedReader().use { deJson(it.readText()) }
}
