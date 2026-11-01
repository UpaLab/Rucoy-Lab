/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 */
package io.github.upalab.rucoylab.telas

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.upalab.rucoylab.calculo.Classe
import io.github.upalab.rucoylab.calculo.Entrada
import io.github.upalab.rucoylab.calculo.Modo
import io.github.upalab.rucoylab.calculo.ResultadoTreino
import io.github.upalab.rucoylab.calculo.Treino
import io.github.upalab.rucoylab.dados.Mobs
import java.util.Locale
import kotlin.math.roundToInt

private const val PREFS = "onde_treinar"

private val Classe.rotulo get() = when (this) {
    Classe.MELEE -> "Melee"
    Classe.DISTANCE -> "Distance"
    Classe.MAGIC -> "Magic"
}

@Composable
fun TelaOndeTreinar(modifier: Modifier) {
    val contexto = LocalContext.current
    val prefs = remember { contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    val mobs = remember { Mobs.carregar(contexto) }

    var classe by rememberSaveable { mutableStateOf(Classe.entries[prefs.getInt("classe", 0)]) }
    var level by rememberSaveable { mutableStateOf(prefs.getString("level", "") ?: "") }
    var stat by rememberSaveable { mutableStateOf(prefs.getString("stat", "") ?: "") }
    var arma by rememberSaveable { mutableStateOf(prefs.getString("arma", "") ?: "") }
    var anel by rememberSaveable { mutableStateOf(prefs.getBoolean("anel", false)) }
    var modo by rememberSaveable { mutableStateOf(Modo.entries[prefs.getInt("modo", 0)]) }
    var nMobs by rememberSaveable { mutableStateOf(prefs.getInt("nMobs", 3)) }

    LaunchedEffect(classe, level, stat, arma, anel, modo, nMobs) {
        prefs.edit()
            .putInt("classe", classe.ordinal).putString("level", level).putString("stat", stat)
            .putString("arma", arma).putBoolean("anel", anel).putInt("modo", modo.ordinal)
            .putInt("nMobs", nMobs).apply()
    }

    val resultado = remember(classe, level, stat, arma, anel, modo, nMobs) {
        val l = level.toDoubleOrNull(); val s = stat.toDoubleOrNull(); val a = arma.toDoubleOrNull()
        if (l == null || s == null || a == null) null
        else Treino.calcular(mobs, Entrada(classe, l, s, a, anel, modo, nMobs))
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Escolha("Classe", Classe.entries, classe, { it.rotulo }) { classe = it }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Level", level, Modifier.weight(1f)) { level = it }
            CampoNumero("Stat (${classe.rotulo})", stat, Modifier.weight(1f)) { stat = it }
        }
        CampoNumero("Ataque da arma", arma, Modifier.fillMaxWidth()) { arma = it }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Anel de crítico", Modifier.weight(1f))
            Switch(checked = anel, onCheckedChange = { anel = it })
        }
        Escolha("Tipo de treino", Modo.entries, modo, { if (it == Modo.AUTO) "Ataque normal" else "Power training" }) { modo = it }
        if (modo == Modo.POWER) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Mobs juntos:", Modifier.weight(1f))
                FilledTonalButton(onClick = { if (nMobs > 1) nMobs-- }) { Text("−") }
                Text("$nMobs", style = MaterialTheme.typography.titleMedium)
                FilledTonalButton(onClick = { if (nMobs < 8) nMobs++ }) { Text("+") }
            }
            if (nMobs > 5) Text("Acima de 5 mobs o limite de ticks não aumenta (18.000/h).", style = MaterialTheme.typography.bodySmall)
        }

        if (resultado == null) {
            Text("Preencha level, stat e ataque da arma.", style = MaterialTheme.typography.bodyMedium)
        } else {
            CartaoResultado(resultado, modo)
        }

        Text(
            "Fórmulas da comunidade, não oficiais (fonte: helloyanis/rucoy-calculator). " +
                "Podem estar erradas ou desatualizadas.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun CartaoResultado(r: ResultadoTreino, modo: Modo) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (r.nenhumIdeal) {
                Text("Nenhum mob da lista te dá a precisão mínima. Mostrando o mais fraco.", fontWeight = FontWeight.Bold)
            }
            Text("Treine em:", style = MaterialTheme.typography.labelLarge)
            Text("${r.mob.nome} (Lv ${r.mob.level})", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (r.equivalentes.isNotEmpty()) {
                Text("Vale igual: " + r.equivalentes.joinToString { "${it.nome} (Lv ${it.level})" })
            }
            Linha("Precisão", porcento(r.precisao) + "  (mínimo ${porcento(r.precisaoMinima)})")
            Linha("Ticks por hora", "${milhar(r.ticksPorHora)} / ${milhar(r.tetoTicks)}")
            Linha(if (modo == Modo.AUTO) "Dano (normal)" else "Dano (especial)", "${milhar(r.dano.minimo)} – ${milhar(r.dano.maximo)}")
            Linha("Tempo médio pra matar", tempo(r.tempoParaMatar))
            r.proximo?.let { p ->
                Text("")
                Text("Próximo mob: ${p.mob.nome} (Lv ${p.mob.level})", fontWeight = FontWeight.Bold)
                Text(
                    when (p.statsFaltando) {
                        null -> "Precisa de mais de 1000 pontos de stat."
                        0 -> "Você já pode treinar nele."
                        1 -> "Falta 1 ponto de stat."
                        else -> "Faltam ${p.statsFaltando} pontos de stat."
                    },
                )
                if (p.danoMaximoAgora >= 1) Text("Hoje você já tira até ${milhar(p.danoMaximoAgora)} de dano nele.")
            }
        }
    }
}

@Composable
private fun Linha(rotulo: String, valor: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(rotulo, Modifier.weight(1f))
        Text(valor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CampoNumero(rotulo: String, valor: String, modifier: Modifier, mudar: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = { novo -> if (novo.length <= 7 && novo.all { it.isDigit() }) mudar(novo) },
        label = { Text(rotulo) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}

@Composable
fun <T> Escolha(titulo: String, opcoes: List<T>, atual: T, rotulo: (T) -> String, escolher: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            opcoes.forEachIndexed { i, o ->
                SegmentedButton(
                    selected = o == atual,
                    onClick = { escolher(o) },
                    shape = SegmentedButtonDefaults.itemShape(i, opcoes.size),
                ) { Text(rotulo(o)) }
            }
        }
    }
}

private val pt = Locale.forLanguageTag("pt-BR")
fun porcento(v: Double) = String.format(pt, "%.1f%%", v * 100)
fun milhar(v: Double) = String.format(pt, "%,d", v.roundToInt())
fun tempo(segundos: Double): String {
    if (segundos.isInfinite() || segundos.isNaN()) return "—"
    val s = segundos.roundToInt()
    return if (s >= 60) "${s / 60} min ${s % 60} s" else "$s s"
}
