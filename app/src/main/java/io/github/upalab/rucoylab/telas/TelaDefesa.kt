/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Fórmulas de defesa da comunidade (docs/formulas.md); medições do dono do
 * projeto (docs/pendencias-teste.md, seção 5c).
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
import io.github.upalab.rucoylab.calculo.Combate
import io.github.upalab.rucoylab.calculo.RegenVida
import io.github.upalab.rucoylab.calculo.TiposPocao
import io.github.upalab.rucoylab.calculo.VidaMedida

private const val PREFS = "defesa"

@Composable
fun TelaDefesa(modifier: Modifier) {
    val contexto = LocalContext.current
    val prefs = remember { contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    fun lido(chave: String) = prefs.getString(chave, "") ?: ""

    var level by rememberSaveable { mutableStateOf(lido("level")) }
    var stat by rememberSaveable { mutableStateOf(lido("stat")) }
    var escudo by rememberSaveable { mutableStateOf(lido("escudo")) }
    var outros by rememberSaveable { mutableStateOf(lido("outros")) }

    var segundos by rememberSaveable { mutableStateOf(lido("segundos")) }
    var pocoes by rememberSaveable { mutableStateOf(lido("pocoes")) }
    var recupera by rememberSaveable { mutableStateOf(prefs.getString("recupera", null) ?: TiposPocao.VIDA[1].recuperaMedia.texto()) }
    var vidaInicio by rememberSaveable { mutableStateOf(lido("vidaInicio")) }
    var vidaFim by rememberSaveable { mutableStateOf(lido("vidaFim")) }

    LaunchedEffect(level, stat, escudo, outros, segundos, pocoes, recupera, vidaInicio, vidaFim) {
        prefs.edit().putString("level", level).putString("stat", stat).putString("escudo", escudo)
            .putString("outros", outros).putString("segundos", segundos).putString("pocoes", pocoes)
            .putString("recupera", recupera).putString("vidaInicio", vidaInicio).putString("vidaFim", vidaFim).apply()
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Sua defesa", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Level", level, Modifier.weight(1f)) { level = it }
            CampoNumero("Stat Defense", stat, Modifier.weight(1f)) { stat = it }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Defesa do escudo", escudo, Modifier.weight(1f)) { escudo = it }
            CampoNumero("Soma dos outros itens", outros, Modifier.weight(1f)) { outros = it }
        }
        Text(
            "Outros itens: capacete, armadura, calça, bota, cinturão, anéis, luvas… (tudo menos o escudo).",
            style = MaterialTheme.typography.bodySmall,
        )

        val lv = level.toDoubleOrNull(); val st = stat.toDoubleOrNull()
        if (lv != null && st != null) {
            val a = Combate.Armadura(defesa = st, escudo = escudo.toDoubleOrNull() ?: 0.0, outras = outros.toDoubleOrNull() ?: 0.0)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Linha("Defesa (alta)", milhar(Combate.defesaAlta(lv, a)))
                    Linha("Defesa (baixa)", milhar(Combate.defesaBaixa(lv, a)))
                    Text(
                        "🔶 Fórmula da comunidade: o dano que você toma é o ataque do mob menos um valor sorteado " +
                            "entre a defesa baixa e a alta. O ataque dos mobs ainda não está no app, então a vida perdida " +
                            "vem de medição (abaixo).",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        HorizontalDivider()
        Text("Medir a vida que você perde", style = MaterialTheme.typography.titleLarge)
        Text(
            "Treine um tempo num mob e anote: quanto tempo, quantas poções de vida tomou e (se der) a vida no começo e no fim.",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Segundos", segundos, Modifier.weight(1f)) { segundos = it }
            CampoNumero("Poções de vida", pocoes, Modifier.weight(1f)) { pocoes = it }
        }
        CampoDecimal("Quanto a poção recupera (média)", recupera, Modifier.fillMaxWidth()) { recupera = it }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CampoNumero("Vida no começo", vidaInicio, Modifier.weight(1f)) { vidaInicio = it }
            CampoNumero("Vida no fim", vidaFim, Modifier.weight(1f)) { vidaFim = it }
        }
        val seg = segundos.toDoubleOrNull()
        if (seg != null && seg > 0) {
            val porMin = VidaMedida.porMinuto(
                seg, pocoes.toIntOrNull() ?: 0, recupera.numero() ?: 0.0,
                vidaInicio.toDoubleOrNull() ?: 0.0, vidaFim.toDoubleOrNull() ?: 0.0,
            )
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Linha("Vida perdida por minuto", milhar(porMin))
                    Text(
                        "Use esse número no campo \"Vida que você perde por minuto\" (Progresso → Power). " +
                            "Não conta a regeneração natural, então o real é um pouco maior.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        HorizontalDivider()
        Text("Medição de referência", style = MaterialTheme.typography.titleMedium)
        Text(
            "Assassin Ninjas (02/11, contado em vídeo): level 84, HP 1.360, Defense 92, escudo 28, outros itens 77 " +
                "(defesa alta ~241, baixa ~159). 7 Greater Health Potions em 1 min 56 s → ~${milhar(VidaMedida.ASSASSIN_NINJA_02_11)} " +
                "de vida por minuto, com 4 ninjas na maior parte do tempo. " +
                "Colar de vida: a regeneração passa de ${RegenVida.POR_VEZ_SEM_COLAR} para ${RegenVida.POR_VEZ_COM_COLAR} por vez " +
                "(🔶 intervalo ainda não medido).",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
