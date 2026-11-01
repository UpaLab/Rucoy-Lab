/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 */
package io.github.upalab.rucoylab.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.upalab.rucoylab.dados.Mobs

const val AVISO_FA = "Projeto de fã, sem vínculo com os desenvolvedores do Rucoy Online."

@Composable
private fun Aviso(texto: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(texto, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TelaInicio(modifier: Modifier, abrir: (Tela) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Ferramentas para Rucoy Online", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Aviso(AVISO_FA)
        }
        items(Tela.entries.filter { it != Tela.INICIO }) { t ->
            Card(Modifier.fillMaxWidth().clickable { abrir(t) }) {
                Column(Modifier.padding(16.dp)) {
                    Text(t.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(t.descricao, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun TelaEmBreve(tela: Tela, modifier: Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(tela.descricao, style = MaterialTheme.typography.titleMedium)
        Text("Em construção — chega na Fase 1.")
    }
}

@Composable
fun TelaMobs(modifier: Modifier) {
    val contexto = LocalContext.current
    val mobs = remember { Mobs.carregar(contexto).sortedBy { it.level } }
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Aviso("Dados da comunidade, não oficiais (fonte: helloyanis/rucoy-calculator, abr/2025; cores conferidas no jogo). Precisam de validação.")
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth()) {
                Text("Mob", Modifier.weight(2f), fontWeight = FontWeight.Bold)
                Text("Lv", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Def", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("Vida", Modifier.weight(1.2f), fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
        }
        items(mobs) { m ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(m.nomeExibido, Modifier.weight(2f))
                Text("${m.level}", Modifier.weight(1f))
                Text("${m.defesa}", Modifier.weight(1f))
                Text("${m.vida}", Modifier.weight(1.2f))
            }
        }
    }
}

@Composable
fun TelaCreditos(modifier: Modifier) {
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Aviso(AVISO_FA)
        Text(
            "Rucoy Online e seus nomes e marcas pertencem aos seus donos. O UpaLab não automatiza " +
                "nenhuma ação dentro do jogo: você digita os dados e o app só calcula.",
        )
        Text("Créditos", style = MaterialTheme.typography.titleMedium)
        Text("O código do UpaLab é próprio. As fórmulas e os dados vêm da comunidade:")
        Text(
            "• Minsi Hu (\"mims\") — autor do Mims' Rucoy Calculator, base das fórmulas de treino, " +
                "do cálculo de stat/offline e da ideia de estimar poções.\n" +
                "  github.com/Mimsqueeze/Mims-Rucoy-Calculator",
        )
        Text("• hikari.ascii — organizador da comunidade e mantenedor do Mims' Rucoy Calculator - Backup.")
        Text(
            "• helloyanis — rucoy-calculator: tabela de mobs, fórmulas de defesa e ajustes recentes.\n" +
                "  github.com/helloyanis/rucoy-calculator",
        )
        Text("• A comunidade de jogadores, que descobriu e testou essas fórmulas.")
        Text("Fórmulas da comunidade, não oficiais. Podem estar erradas ou desatualizadas.")
        Text("Licença", style = MaterialTheme.typography.titleMedium)
        Text(
            "Este app é software livre sob a GNU GPL versão 3. Código-fonte: " +
                "github.com/UpaLab/Rucoy-Lab",
        )
    }
}
