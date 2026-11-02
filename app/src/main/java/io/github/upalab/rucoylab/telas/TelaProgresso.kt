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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import io.github.upalab.rucoylab.calculo.Bonus
import io.github.upalab.rucoylab.calculo.Cansaco
import io.github.upalab.rucoylab.calculo.Progressao
import io.github.upalab.rucoylab.calculo.Ticks

private const val PREFS = "progresso"

enum class Ritmo(val rotulo: String) { NORMAL("Normal"), POWER("Power"), OFFLINE("Offline") }


@Composable
fun TelaProgresso(modifier: Modifier) {
    val prefs = LocalContext.current.let { c -> remember { c.getSharedPreferences(PREFS, Context.MODE_PRIVATE) } }

    var statAtual by rememberSaveable { mutableStateOf(prefs.getString("statAtual", "") ?: "") }
    var statMeta by rememberSaveable { mutableStateOf(prefs.getString("statMeta", "") ?: "") }
    var ritmo by rememberSaveable { mutableStateOf(Ritmo.entries[prefs.getInt("ritmo", 0)]) }
    var mobs by rememberSaveable { mutableStateOf(prefs.getInt("mobs", 4)) }
    var verde by rememberSaveable { mutableStateOf(prefs.getBoolean("verde", false)) }
    var bonusTotal by rememberSaveable { mutableStateOf(prefs.getString("bonusTotal_v3", null) ?: Bonus.padrao(verde).texto()) }
    var revezar by rememberSaveable { mutableStateOf(prefs.getBoolean("revezar", false)) }
    var troca by rememberSaveable { mutableStateOf(prefs.getString("troca", "15") ?: "15") }
    var meusTicks by rememberSaveable { mutableStateOf(prefs.getString("meusTicks", "") ?: "") }
    var levelAtual by rememberSaveable { mutableStateOf(prefs.getString("levelAtual", "") ?: "") }
    var levelMeta by rememberSaveable { mutableStateOf(prefs.getString("levelMeta", "") ?: "") }
    var expHora by rememberSaveable { mutableStateOf(prefs.getString("expHora", "") ?: "") }

    LaunchedEffect(statAtual, statMeta, ritmo, verde, bonusTotal, revezar, troca, meusTicks, levelAtual, levelMeta, expHora) {
        prefs.edit().putString("statAtual", statAtual).putString("statMeta", statMeta).putInt("ritmo", ritmo.ordinal)
            .putBoolean("verde", verde).putString("bonusTotal_v3", bonusTotal)
            .putBoolean("revezar", revezar).putString("troca", troca).putString("meusTicks", meusTicks)
            .putString("levelAtual", levelAtual).putString("levelMeta", levelMeta).putString("expHora", expHora).apply()
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Stat", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Stat atual", statAtual, Modifier.weight(1f)) { statAtual = it }
            CampoNumero("Stat desejado", statMeta, Modifier.weight(1f)) { statMeta = it }
        }
        Escolha("Como vai treinar", Ritmo.entries, ritmo, { it.rotulo }) { ritmo = it }
        if (ritmo != Ritmo.OFFLINE) {
            Text("Bônus de exp", style = MaterialTheme.typography.labelLarge)
            Chave("Estamina verde (acima de 24 h)", verde) { verde = it; bonusTotal = Bonus.padrao(verde).texto() }
            CampoDecimal("Multiplicador de exp (1 = sem bônus)", bonusTotal, Modifier.fillMaxWidth()) { bonusTotal = it }
            Text(
                "É um bônus só: a estamina vai até 26 h, e as últimas 2 h (acima de 24 h) são a parte verde. A mensagem " +
                    "\"120 minutes of bonus level and skill experience\" é o jogo avisando quanto resta dela. " +
                    "Padrão 1,5× (sua experiência e o teste de 02/11). ❗ A wiki diz 2×. Você pode mudar.",
                style = MaterialTheme.typography.bodySmall,
            )
            Chave("Revezar mobs (o mob cansa antes de morrer)", revezar) { revezar = it }
            if (revezar) {
                CampoNumero("Segundos pra trocar de mob", troca, Modifier.fillMaxWidth()) { troca = it }
                Text(
                    "Cada mob só dá stat por 7 min 30 s. 🔶 Suposição simples: a cada 7 min 30 s você perde esses segundos " +
                        "trocando de mob (padrão 15 s, sem fonte). Ligue se a tela Onde treinar avisar que o mob cansa.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        if (ritmo == Ritmo.NORMAL) {
            CampoNumero("Seus ticks por hora (opcional)", meusTicks, Modifier.fillMaxWidth()) { meusTicks = it }
            Text(
                "Vazio = considera o máximo possível. Pra um número mais real, use o \"Ticks por hora\" da tela Onde treinar.",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        val bonus = bonusTotal.numero()?.takeIf { it > 0 } ?: 1.0
        val fatorCansaco = if (revezar && ritmo != Ritmo.OFFLINE) Cansaco.fatorTempo(troca.toDoubleOrNull() ?: 0.0) else 1.0
        val de = statAtual.toIntOrNull(); val ate = statMeta.toIntOrNull()
        if (de == null || ate == null || de < 1) {
            Text("Preencha o stat atual e o desejado.")
        } else if (ate <= de) {
            Text("O stat desejado precisa ser maior que o atual.")
        } else {
            val ticks = Progressao.ticksEntre(de, ate)
            val porHora = when (ritmo) {
                Ritmo.OFFLINE -> Ticks.OFFLINE_POR_HORA
                else -> (meusTicks.toDoubleOrNull()?.takeIf { it > 0 } ?: Ticks.teto(1)) * bonus
            }
            Resultado {
                Linha("Ticks necessários", milhar(ticks))
                if (ritmo != Ritmo.POWER) {
                    Linha("Ritmo", "${milhar(porHora)} ticks/h")
                    Linha("Tempo", horasEMinutos(Progressao.horas(ticks, porHora) * fatorCansaco))
                    if (fatorCansaco > 1) Text("Inclui as paradas pra trocar de mob cansado.", style = MaterialTheme.typography.bodySmall)
                }
                if (de <= 54 && ate >= 55) {
                    Text(
                        "🔶 Passa do stat 54 pro 55, onde a fórmula da comunidade muda de curva. " +
                            "Aqui somamos ponto a ponto; o resultado nessa faixa é menos confiável.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            if (ritmo == Ritmo.POWER) PainelPocoes(ticks, bonus, fatorCansaco)
        }

        HorizontalDivider()
        Text("Level", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Level atual", levelAtual, Modifier.weight(1f)) { levelAtual = it }
            CampoNumero("Level desejado", levelMeta, Modifier.weight(1f)) { levelMeta = it }
        }
        CampoNumero("Sua exp por hora", expHora, Modifier.fillMaxWidth()) { expHora = it }
        val l1 = levelAtual.toIntOrNull(); val l2 = levelMeta.toIntOrNull()
        if (l1 != null && l2 != null && l1 >= 1) {
            if (l2 <= l1) Text("O level desejado precisa ser maior que o atual.")
            else Resultado {
                val exp = Progressao.expEntre(l1, l2)
                Linha("Exp necessária", milhar(exp))
                expHora.toDoubleOrNull()?.takeIf { it > 0 }?.let { Linha("Tempo", horasEMinutos(exp / it)) }
                    ?: Text("Informe sua exp por hora pra ver o tempo.", style = MaterialTheme.typography.bodySmall)
            }
        }

        Text("Fórmulas da comunidade, não oficiais (créditos na tela Créditos).", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Resultado(conteudo: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { conteudo() }
    }
}

fun horasEMinutos(horas: Double): String {
    if (horas.isInfinite() || horas.isNaN()) return "—"
    val totalMin = Math.round(horas * 60)
    val d = totalMin / (60 * 24); val h = (totalMin / 60) % 24; val m = totalMin % 60
    return buildString {
        if (d > 0) append("${d}d ")
        if (d > 0 || h > 0) append("${h}h ")
        append("${m}min")
    }
}

@Composable
fun Chave(texto: String, ligado: Boolean, mudar: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(texto, Modifier.weight(1f))
        Switch(checked = ligado, onCheckedChange = mudar)
    }
}
