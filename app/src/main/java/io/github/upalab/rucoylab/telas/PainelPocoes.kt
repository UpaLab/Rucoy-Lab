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
 * Estimativa de poções do power training (mana e vida), com tudo editável.
 * [ticks] e [ticksPorHora] vêm da tela Progresso.
 */
@Composable
fun PainelPocoes(ticks: Double, ticksPorHora: Double, mobs: Int, bonus: Double) {
    val contexto = LocalContext.current
    val prefs = remember { contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    val listaMobs = remember { Mobs.carregar(contexto).sortedBy { it.level } }

    // ---- Mana ----
    var tipoMana by rememberSaveable { mutableStateOf(prefs.getInt("tipoMana", 0)) }
    val tm = TiposPocao.MANA[tipoMana]
    var custo by lembrar(prefs, "custo", AjusteMana.MANA_POR_ESPECIAL_MEDIDA.texto())
    var recupera by lembrar(prefs, "recupera_$tipoMana", tm.recuperaMedia.texto())
    var preco by lembrar(prefs, "preco_$tipoMana", tm.preco.toString())
    var cooldown by lembrar(prefs, "cooldown_$tipoMana", tm.cooldownSeg.texto())
    var regen by lembrar(prefs, "regen", "0")
    var intervalo by lembrar(prefs, "intervalo", "1,5")

    // ---- Vida ----
    var tipoVida by rememberSaveable { mutableStateOf(prefs.getInt("tipoVida", 0)) }
    val tv = TiposPocao.VIDA[tipoVida]
    var mobNome by rememberSaveable { mutableStateOf(prefs.getString("mob", null) ?: listaMobs.first().let { "${it.nome}|${it.level}" }) }
    val mob: Mob = listaMobs.firstOrNull { "${it.nome}|${it.level}" == mobNome } ?: listaMobs.first()
    var vidaPorMin by lembrar(prefs, "vidaPorMin_$mobNome", "")
    var recuperaVida by lembrar(prefs, "recuperaVida_$tipoVida", tv.recuperaMedia.texto())
    var precoVida by lembrar(prefs, "precoVida_$tipoVida", tv.preco.toString())

    LaunchedEffect(tipoMana, custo, recupera, preco, cooldown, regen, intervalo, tipoVida, mobNome, vidaPorMin, recuperaVida, precoVida) {
        prefs.edit().putInt("tipoMana", tipoMana).putString("custo", custo).putString("recupera_$tipoMana", recupera)
            .putString("preco_$tipoMana", preco).putString("cooldown_$tipoMana", cooldown).putString("regen", regen).putString("intervalo", intervalo)
            .putInt("tipoVida", tipoVida).putString("mob", mobNome).putString("vidaPorMin_$mobNome", vidaPorMin)
            .putString("recuperaVida_$tipoVida", recuperaVida).putString("precoVida_$tipoVida", precoVida).apply()
    }

    val ajuste = AjusteMana(
        manaPorEspecial = custo.numero() ?: 0.0, recupera = recupera.numero() ?: 0.0,
        preco = preco.numero() ?: 0.0, cooldownSeg = cooldown.numero() ?: 0.0, regenPorSegundo = regen.numero() ?: 0.0,
    )

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Poções de mana (estimativa)", style = MaterialTheme.typography.titleMedium)
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoDecimal("Regeneração de mana/s", regen, Modifier.weight(1f)) { regen = it }
                CampoDecimal("Intervalo entre especiais (s)", intervalo, Modifier.weight(1f)) { intervalo = it }
            }
            Text(
                "Mana por especial: 50, medido por você. Intervalo: 1,5 s, informado por você. " +
                    "Regeneração: sem fonte ainda (padrão 0). " +
                    "Anel e pingente Soul dão +1 cada (wiki), mas a wiki não diz por quanto tempo.",
                style = MaterialTheme.typography.bodySmall,
            )

            val iv = intervalo.numero()
            if (iv != null && iv > 0 && ajuste.manaPorEspecial > 0) {
                val manaPedida = ajuste.manaPorEspecial / iv
                val manaPocao = if (ajuste.cooldownSeg > 0) ajuste.recupera / ajuste.cooldownSeg else 0.0
                Text(
                    "Nesse intervalo: ${String.format(pt, "%.0f", 60 / iv)} especiais por minuto, pedindo " +
                        "${String.format(pt, "%.0f", manaPedida)} de mana/s. A poção escolhida dá até " +
                        "${String.format(pt, "%.0f", manaPocao + ajuste.regenPorSegundo)} de mana/s" +
                        (if (manaPocao + ajuste.regenPorSegundo >= manaPedida) " ✅ aguenta." else " ⚠️ não aguenta."),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    "🔶 O intervalo ainda NÃO entra na conta de poções: estamos testando se os golpes normais entre " +
                        "um especial e outro também dão ticks (ver docs/modelo-golpes-liberados.md).",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            val p = Pocoes.planoMana(ticks, ticksPorHora, mobs, bonus, ajuste)
            HorizontalDivider()
            Linha("Especiais", milhar(p.especiais))
            Linha("Mana total", milhar(p.manaTotal))
            if (p.manaDaRegeneracao > 0) Linha("Volta sozinha (regeneração)", milhar(p.manaDaRegeneracao))
            Linha("Poções", milhar(p.pocoes.toDouble()))
            Linha("Gold", milhar(p.gold.toDouble()))
            Linha("Poções por minuto", "${String.format(pt, "%.1f", p.pocoesPorMinuto)} (máx. ${String.format(pt, "%.0f", p.maxPocoesPorMinuto)})")
            if (p.limitadoPeloCooldown) {
                Text(
                    "⚠️ O cooldown não deixa tomar poção rápido o bastante pra esse ritmo. " +
                        "Na prática leva ${horasEMinutos(p.horasReais)}, em vez de ${horasEMinutos(p.horas)}.",
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                "Conta: cada especial gasta a mana acima e rende 1 tick por mob atingido (até 5) × o bônus de exp.",
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
            val perde = vidaPorMin.numero()
            if (perde == null) {
                Text(
                    "Ainda não temos o dano dos mobs. Meça no jogo: anote sua vida, treine 1 minuto nesse mob SEM tomar " +
                        "poção de vida (ou conte as que tomou) e veja quanto perdeu. O valor fica salvo pra esse mob.",
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                val v = Pocoes.planoVida(perde, p.horasReais, recuperaVida.numero() ?: 0.0, precoVida.numero() ?: 0.0, tv.cooldownSeg)
                Linha("Poções de vida", milhar(v.pocoes.toDouble()))
                Linha("Gold", milhar(v.gold.toDouble()))
                if (v.limitadoPeloCooldown) Text("⚠️ Você perderia vida mais rápido do que o cooldown deixa curar.", fontWeight = FontWeight.Bold)
                Text(
                    "Estimativa: muda com sua defesa, armadura e quantos mobs batem em você. " +
                        "🔶 Ainda não sabemos se a poção de vida e a de mana dividem o mesmo cooldown.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            HorizontalDivider()
            Linha("Total de gold (mana + vida)", milhar((p.gold + (perde?.let {
                Pocoes.planoVida(it, p.horasReais, recuperaVida.numero() ?: 0.0, precoVida.numero() ?: 0.0, tv.cooldownSeg).gold
            } ?: 0L)).toDouble()))
        }
    }
}

private fun fonteDe(t: TipoPocao) =
    "Padrão: recupera ${t.recuperaMin}–${t.recuperaMax} (média ${t.recuperaMedia.texto()}), ${t.preco} gold, " +
        "cooldown ${t.cooldownSeg.texto()} s" + (if (t.levelMinimo > 0) ", level ${t.levelMinimo}+" else "") + ". Fonte: ${t.fonte}."
