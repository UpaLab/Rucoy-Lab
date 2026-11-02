/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Custo em gold de subir um stat: treino normal × power training.
 * Valores padrão com fonte em docs/pendencias-teste.md.
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.upalab.rucoylab.calculo.Bonus
import io.github.upalab.rucoylab.calculo.Pocoes
import io.github.upalab.rucoylab.calculo.Progressao
import io.github.upalab.rucoylab.calculo.Ticks
import io.github.upalab.rucoylab.calculo.TiposPocao

private const val PREFS = "tela_pocoes"

@Composable
fun TelaPocoes(modifier: Modifier) {
    val prefs = LocalContext.current.let { c -> remember { c.getSharedPreferences(PREFS, Context.MODE_PRIVATE) } }
    fun lido(chave: String, padrao: String = "") = prefs.getString(chave, null) ?: padrao

    var statAtual by rememberSaveable { mutableStateOf(lido("statAtual")) }
    var statMeta by rememberSaveable { mutableStateOf(lido("statMeta")) }
    var verde by rememberSaveable { mutableStateOf(prefs.getBoolean("verde", false)) }
    var bonusTotal by rememberSaveable { mutableStateOf(lido("bonus", Bonus.padrao(verde).texto())) }
    var ticksNormal by rememberSaveable { mutableStateOf(lido("ticksNormal")) }
    var vidaNormal by rememberSaveable { mutableStateOf(lido("vidaNormal")) }
    var tipoVida by rememberSaveable { mutableStateOf(prefs.getInt("tipoVida", 0)) }

    LaunchedEffect(statAtual, statMeta, verde, bonusTotal, ticksNormal, vidaNormal, tipoVida) {
        prefs.edit().putString("statAtual", statAtual).putString("statMeta", statMeta).putBoolean("verde", verde)
            .putString("bonus", bonusTotal).putString("ticksNormal", ticksNormal).putString("vidaNormal", vidaNormal)
            .putInt("tipoVida", tipoVida).apply()
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Quanto custa subir o stat", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Stat atual", statAtual, Modifier.weight(1f)) { statAtual = it }
            CampoNumero("Stat desejado", statMeta, Modifier.weight(1f)) { statMeta = it }
        }
        Chave("Estamina verde (acima de 24 h)", verde) { verde = it; bonusTotal = Bonus.padrao(verde).texto() }
        CampoDecimal("Multiplicador de exp (1 = sem bônus)", bonusTotal, Modifier.fillMaxWidth()) { bonusTotal = it }
        Text(
            "Um bônus só: a estamina verde (padrão 1,5×; ❗ a wiki diz 2×).",
            style = MaterialTheme.typography.bodySmall,
        )

        val bonus = bonusTotal.numero()?.takeIf { it > 0 } ?: 1.0
        val de = statAtual.toIntOrNull(); val ate = statMeta.toIntOrNull()
        if (de == null || ate == null || de < 1) {
            Text("Preencha o stat atual e o desejado.")
            return@Column
        }
        if (ate <= de) {
            Text("O stat desejado precisa ser maior que o atual.")
            return@Column
        }
        val ticks = Progressao.ticksEntre(de, ate)

        // ---- Treino normal ----
        HorizontalDivider()
        Text("Treino normal (sem especial)", style = MaterialTheme.typography.titleLarge)
        Text(
            "Não gasta mana: o custo é só a poção de vida. Ticks por hora: use o da tela Onde treinar " +
                "(vazio = o máximo de 1 mob, ${milhar(Ticks.teto(1))}/h).",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Ticks por hora", ticksNormal, Modifier.weight(1f)) { ticksNormal = it }
            CampoDecimal("Vida perdida/min", vidaNormal, Modifier.weight(1f)) { vidaNormal = it }
        }
        Lista("Poção de vida", TiposPocao.VIDA.indices.toList(), tipoVida, { TiposPocao.VIDA[it].nome }) { tipoVida = it }
        val tv = TiposPocao.VIDA[tipoVida]
        val porHora = (ticksNormal.toDoubleOrNull()?.takeIf { it > 0 } ?: Ticks.teto(1)) * bonus
        val (horas, vida) = Pocoes.treinoNormal(ticks, porHora, vidaNormal.numero() ?: 0.0, tv.recuperaMedia, tv.preco.toDouble(), tv.cooldownSeg)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Linha("Ticks necessários", milhar(ticks))
                Linha("Tempo", horasEMinutos(horas))
                Linha("Poções de vida", milhar(vida.pocoes.toDouble()))
                Linha("Gold", milhar(vida.gold.toDouble()))
                if (vidaNormal.numero() == null) {
                    Text(
                        "Sem a vida perdida por minuto, o custo fica 0. Meça na tela Defesa.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // ---- Power training ----
        HorizontalDivider()
        Text("Power training", style = MaterialTheme.typography.titleLarge)
        PainelPocoes(ticks, bonus)

        Text(
            "Compare o gold do treino normal com o \"Gold total\" do power training. O normal é mais barato, mas bem mais lento. " +
                "Fórmulas da comunidade, não oficiais (créditos na tela Créditos).",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
