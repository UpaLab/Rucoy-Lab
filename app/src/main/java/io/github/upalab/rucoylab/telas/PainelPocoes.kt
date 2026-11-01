/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 */
package io.github.upalab.rucoylab.telas

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.upalab.rucoylab.calculo.AjusteMana
import io.github.upalab.rucoylab.calculo.Estilo
import io.github.upalab.rucoylab.calculo.ParametrosPower
import io.github.upalab.rucoylab.calculo.PlanoEstilo
import io.github.upalab.rucoylab.calculo.PowerTraining
import io.github.upalab.rucoylab.calculo.Pocoes
import io.github.upalab.rucoylab.calculo.TipoPocao
import io.github.upalab.rucoylab.calculo.TiposPocao
import io.github.upalab.rucoylab.dados.Mob
import io.github.upalab.rucoylab.dados.Mobs
import java.util.Locale

private const val PREFS = "pocoes"
private val pt = Locale.forLanguageTag("pt-BR")

private fun Double.texto(): String =
    if (this % 1.0 == 0.0) this.toLong().toString() else String.format(pt, "%.2f", this).trimEnd('0')

private fun String.numero(): Double? = replace(',', '.').toDoubleOrNull()

/** Campo de número que aceita vírgula (ex.: 1,25). */
@Composable
fun CampoDecimal(rotulo: String, valor: String, modifier: Modifier, mudar: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = { n -> if (n.length <= 8 && n.count { it == ',' || it == '.' } <= 1 && n.all { it.isDigit() || it == ',' || it == '.' }) mudar(n) },
        label = { Text(rotulo) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
    )
}

/** Um campo lembrado no celular; o padrão vem com fonte. */
@Composable
private fun lembrar(prefs: SharedPreferences, chave: String, padrao: String) =
    rememberSaveable(chave) { mutableStateOf(prefs.getString(chave, null) ?: padrao) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> Lista(rotulo: String, opcoes: List<T>, atual: T, nome: (T) -> String, escolher: (T) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = aberto, onExpandedChange = { aberto = it }) {
        OutlinedTextField(
            value = nome(atual), onValueChange = {}, readOnly = true, label = { Text(rotulo) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(aberto) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEach { o -> DropdownMenuItem(text = { Text(nome(o)) }, onClick = { escolher(o); aberto = false }) }
        }
    }
}

/**
 * Power training: compara os estilos Rápido (só especial) e Econômico (especial + golpes normais),
 * com mana, poções e gold. Tudo editável. [ticks] vem da tela Progresso.
 */
@Composable
fun PainelPocoes(ticks: Double, bonus: Double) {
    val contexto = LocalContext.current
    val prefs = remember { contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    val listaMobs = remember { Mobs.carregar(contexto).sortedBy { it.level } }

    // ---- Ritmo (calibrado nos testes A e B) ----
    var mobsAtingidos by lembrar(prefs, "mobsAtingidos", ParametrosPower.MOBS_MEDIDO.texto())
    var intervalo by lembrar(prefs, "intervaloMedido", ParametrosPower.INTERVALO_MEDIDO.texto())
    var golpes by lembrar(prefs, "golpes", ParametrosPower.GOLPES_MEDIDO.toString())
    var ticksGolpe by lembrar(prefs, "ticksGolpe", ParametrosPower.TICKS_POR_GOLPE_MEDIDO.texto())

    // ---- Mana ----
    var tipoMana by rememberSaveable { mutableStateOf(prefs.getInt("tipoMana", 0)) }
    val tm = TiposPocao.MANA[tipoMana]
    var custo by lembrar(prefs, "custo", AjusteMana.MANA_POR_ESPECIAL_MEDIDA.texto())
    var recupera by lembrar(prefs, "recupera_$tipoMana", tm.recuperaMedia.texto())
    var preco by lembrar(prefs, "preco_$tipoMana", tm.preco.toString())
    var cooldown by lembrar(prefs, "cooldown_$tipoMana", tm.cooldownSeg.texto())
    var regen by lembrar(prefs, "regen", "0")

    // ---- Vida ----
    var tipoVida by rememberSaveable { mutableStateOf(prefs.getInt("tipoVida", 0)) }
    val tv = TiposPocao.VIDA[tipoVida]
    var mobNome by rememberSaveable { mutableStateOf(prefs.getString("mob", null) ?: listaMobs.first().let { "${it.nome}|${it.level}" }) }
    val mob: Mob = listaMobs.firstOrNull { "${it.nome}|${it.level}" == mobNome } ?: listaMobs.first()
    var vidaPorMin by lembrar(prefs, "vidaPorMin_$mobNome", "")
    var recuperaVida by lembrar(prefs, "recuperaVida_$tipoVida", tv.recuperaMedia.texto())
    var precoVida by lembrar(prefs, "precoVida_$tipoVida", tv.preco.toString())

    LaunchedEffect(mobsAtingidos, intervalo, golpes, ticksGolpe, tipoMana, custo, recupera, preco, cooldown, regen, tipoVida, mobNome, vidaPorMin, recuperaVida, precoVida) {
        prefs.edit().putString("mobsAtingidos", mobsAtingidos).putString("intervaloMedido", intervalo)
            .putString("golpes", golpes).putString("ticksGolpe", ticksGolpe)
            .putInt("tipoMana", tipoMana).putString("custo", custo).putString("recupera_$tipoMana", recupera)
            .putString("preco_$tipoMana", preco).putString("cooldown_$tipoMana", cooldown).putString("regen", regen)
            .putInt("tipoVida", tipoVida).putString("mob", mobNome).putString("vidaPorMin_$mobNome", vidaPorMin)
            .putString("recuperaVida_$tipoVida", recuperaVida).putString("precoVida_$tipoVida", precoVida).apply()
    }

    val params = ParametrosPower(
        mobsAtingidos = mobsAtingidos.numero() ?: 0.0,
        intervaloEspecial = intervalo.numero() ?: 0.0,
        golpesEntreEspeciais = golpes.numero()?.toInt() ?: 0,
        ticksPorGolpe = ticksGolpe.numero() ?: 0.0,
        bonus = bonus,
    )
    val ajuste = AjusteMana(
        manaPorEspecial = custo.numero() ?: 0.0, recupera = recupera.numero() ?: 0.0,
        preco = preco.numero() ?: 0.0, cooldownSeg = cooldown.numero() ?: 0.0, regenPorSegundo = regen.numero() ?: 0.0,
    )
    val rapido = PowerTraining.plano(Estilo.RAPIDO, ticks, params, ajuste)
    val economico = PowerTraining.plano(Estilo.ECONOMICO, ticks, params, ajuste)
    val perde = vidaPorMin.numero()
    fun vida(pl: PlanoEstilo?) = if (pl == null || perde == null) null
        else Pocoes.planoVida(perde, pl.horas, recuperaVida.numero() ?: 0.0, precoVida.numero() ?: 0.0, tv.cooldownSeg)

    // ---- Comparação ----
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Rápido × Econômico", style = MaterialTheme.typography.titleMedium)
            Text(
                "Rápido: só especial. Econômico: 1 especial + $golpes golpes normais (contam tick porque o especial dá dano).",
                style = MaterialTheme.typography.bodySmall,
            )
            if (rapido == null || economico == null) {
                Text("Preencha mobs atingidos, intervalo e mana por especial.")
            } else {
                val vr = vida(rapido); val ve = vida(economico)
                Comparacao("", "Rápido", "Econômico", negrito = true)
                Comparacao("Tempo", horasEMinutos(rapido.horas), horasEMinutos(economico.horas))
                Comparacao("Ticks/h", milhar(rapido.ticksPorHora), milhar(economico.ticksPorHora))
                Comparacao("Especiais", milhar(rapido.especiais), milhar(economico.especiais))
                Comparacao("Poções de mana", milhar(rapido.pocoes.toDouble()), milhar(economico.pocoes.toDouble()))
                Comparacao("Gold (mana)", milhar(rapido.gold.toDouble()), milhar(economico.gold.toDouble()))
                if (vr != null && ve != null) {
                    Comparacao("Poções de vida", milhar(vr.pocoes.toDouble()), milhar(ve.pocoes.toDouble()))
                    Comparacao("Gold total", milhar((rapido.gold + vr.gold).toDouble()), milhar((economico.gold + ve.gold).toDouble()), negrito = true)
                }
                if (rapido.limitadoPeloCooldown || economico.limitadoPeloCooldown) {
                    Text("⚠️ O cooldown da poção limita o ritmo (o tempo acima já considera isso).", fontWeight = FontWeight.Bold)
                }
                Text(
                    "🔶 Calibrado com os seus testes A e B (stat 98–99, arma de treino, ninjas, com bônus). " +
                        "Em outros stats, armas e mobs pode mudar.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    // ---- Ajustes ----
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ritmo do power training", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoDecimal("Mobs atingidos (média)", mobsAtingidos, Modifier.weight(1f)) { mobsAtingidos = it }
                CampoDecimal("Intervalo entre especiais (s)", intervalo, Modifier.weight(1f)) { intervalo = it }
            }
            Text(
                "⚠️ Mobs morrem e o grupo diminui: use a MÉDIA de mobs que cada especial acerta, não quantos você puxou. " +
                    "Padrões medidos no teste A: 2,5 mobs e 1,45 s.",
                style = MaterialTheme.typography.bodySmall,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoNumero("Golpes entre especiais", golpes, Modifier.weight(1f)) { golpes = it }
                CampoDecimal("Ticks por golpe normal", ticksGolpe, Modifier.weight(1f)) { ticksGolpe = it }
            }
            Text(
                "Econômico (teste B): ~9 golpes; só contam até 9 depois de cada especial (regra do 1 a cada 10). " +
                    "🔶 Ticks por golpe: 0,5 sem bônus, supondo que o bônus dobra tudo. Se o bônus não valer nos golpes, seria 1.",
                style = MaterialTheme.typography.bodySmall,
            )

            HorizontalDivider()
            Text("Poções de mana", style = MaterialTheme.typography.titleMedium)
            Lista("Poção", TiposPocao.MANA.indices.toList(), tipoMana, { TiposPocao.MANA[it].nome }) { tipoMana = it }
            Text(fonteDe(tm), style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoDecimal("Recupera (média)", recupera, Modifier.weight(1f)) { recupera = it }
                CampoDecimal("Preço (gold)", preco, Modifier.weight(1f)) { preco = it }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoDecimal("Cooldown (s)", cooldown, Modifier.weight(1f)) { cooldown = it }
                CampoDecimal("Mana por especial", custo, Modifier.weight(1f)) { custo = it }
            }
            CampoDecimal("Regeneração de mana/s", regen, Modifier.fillMaxWidth()) { regen = it }
            Text(
                "Mana por especial: 50, medido por você. Regeneração: sem fonte ainda (padrão 0). " +
                    "No teste A a poção básica rendeu ~93 de mana, dentro da faixa da wiki.",
                style = MaterialTheme.typography.bodySmall,
            )

            HorizontalDivider()
            Text("Poções de vida (estimativa)", style = MaterialTheme.typography.titleMedium)
            Lista("Mob", listaMobs, mob, { "${it.nomeExibido} (Lv ${it.level})" }) { mobNome = "${it.nome}|${it.level}" }
            Lista("Poção", TiposPocao.VIDA.indices.toList(), tipoVida, { TiposPocao.VIDA[it].nome }) { tipoVida = it }
            Text(fonteDe(tv), style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoDecimal("Recupera (média)", recuperaVida, Modifier.weight(1f)) { recuperaVida = it }
                CampoDecimal("Preço (gold)", precoVida, Modifier.weight(1f)) { precoVida = it }
            }
            CampoDecimal("Vida que você perde por minuto nesse mob", vidaPorMin, Modifier.fillMaxWidth()) { vidaPorMin = it }
            Text(
                if (perde == null) "Ainda não temos o dano dos mobs. Meça no jogo: anote sua vida, treine 1 minuto nesse mob sem tomar " +
                    "poção de vida (ou conte as que tomou) e veja quanto perdeu. O valor fica salvo pra esse mob."
                else "Estimativa: muda com sua defesa, armadura e quantos mobs batem em você. " +
                    "🔶 Ainda não sabemos se a poção de vida e a de mana dividem o mesmo cooldown.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun Comparacao(rotulo: String, a: String, b: String, negrito: Boolean = false) {
    val peso = if (negrito) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth()) {
        Text(rotulo, Modifier.weight(1.3f), fontWeight = peso)
        Text(a, Modifier.weight(1f), fontWeight = peso)
        Text(b, Modifier.weight(1f), fontWeight = peso)
    }
}

private fun fonteDe(t: TipoPocao) =
    "Padrão: recupera ${t.recuperaMin}–${t.recuperaMax} (média ${t.recuperaMedia.texto()}), ${t.preco} gold, " +
        "cooldown ${t.cooldownSeg.texto()} s" + (if (t.levelMinimo > 0) ", level ${t.levelMinimo}+" else "") + ". Fonte: ${t.fonte}."
