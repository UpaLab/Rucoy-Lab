/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. O que aparece dentro do balão: o botão redondo e, aberto, a faixa de
 * menus (rola para o lado) com a ferramenta escolhida abrindo logo abaixo, estilo dropdown.
 */
package io.github.upalab.rucoylab.balao

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.upalab.rucoylab.telas.TelaDefesa
import io.github.upalab.rucoylab.telas.TelaMobs
import io.github.upalab.rucoylab.telas.TelaOndeTreinar
import io.github.upalab.rucoylab.telas.TelaPocoes
import io.github.upalab.rucoylab.telas.TelaProgresso

/** Ferramentas que aparecem na faixa de menus do balão. */
enum class Ferramenta(val rotulo: String) {
    ONDE_TREINAR("Onde treinar"), PROGRESSO("Progresso"), POCOES("Poções"), DEFESA("Defesa"), MOBS("Mobs"),
}

@Composable
fun ConteudoBalao(mover: (Float, Float) -> Unit, abrirOuFechar: (Boolean) -> Unit, encerrar: () -> Unit) {
    val cores = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = cores) {
        var aberto by rememberSaveable { mutableStateOf(false) }
        var escolhida by rememberSaveable { mutableStateOf<Ferramenta?>(null) }
        LaunchedEffect(aberto) { abrirOuFechar(aberto) }

        val arrastar = Modifier.pointerInput(Unit) {
            detectDragGestures { mudanca, d -> mudanca.consume(); mover(d.x, d.y) }
        }

        if (!aberto) {
            Box(
                Modifier.padding(4.dp).size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)
                    .then(arrastar)
                    .pointerInput(Unit) { detectTapGestures { aberto = true } },
                contentAlignment = Alignment.Center,
            ) {
                Text("UP", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            return@MaterialTheme
        }

        // Altura do "dropdown": ~55% da tela, com rolagem própria.
        val altura = with(LocalDensity.current) { (LocalContext.current.resources.displayMetrics.heightPixels * 0.55f).toDp() }
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(Modifier.fillMaxWidth()) {
                // Barra de cima: arraste por ela pra mover o painel.
                Row(
                    Modifier.fillMaxWidth().then(arrastar).padding(start = 12.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("UpaLab", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    TextButton(onClick = { aberto = false }) { Text("Minimizar") }
                    TextButton(onClick = encerrar) { Text("✕") }
                }
                // Faixa de menus: rola para o lado.
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Ferramenta.entries.forEach { f ->
                        FilterChip(
                            selected = escolhida == f,
                            onClick = { escolhida = if (escolhida == f) null else f },
                            label = { Text(f.rotulo) },
                        )
                    }
                }
                // A ferramenta abre logo abaixo dos menus (tocar de novo no menu fecha).
                escolhida?.let { f ->
                    HorizontalDivider()
                    Box(Modifier.fillMaxWidth().height(altura)) {
                        val m = Modifier
                        when (f) {
                            Ferramenta.ONDE_TREINAR -> TelaOndeTreinar(m)
                            Ferramenta.PROGRESSO -> TelaProgresso(m)
                            Ferramenta.POCOES -> TelaPocoes(m)
                            Ferramenta.DEFESA -> TelaDefesa(m)
                            Ferramenta.MOBS -> TelaMobs(m)
                        }
                    }
                }
            }
        }
    }
}
