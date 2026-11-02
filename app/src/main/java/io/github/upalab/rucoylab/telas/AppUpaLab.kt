/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 */
package io.github.upalab.rucoylab.telas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.github.upalab.rucoylab.R
import io.github.upalab.rucoylab.UpaLabApp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

/** Telas do app (docs/app.md, seção 2). */
enum class Tela(val titulo: String, val descricao: String) {
    INICIO("UpaLab", ""),
    ONDE_TREINAR("Onde treinar", "Melhor mob, precisão e ticks por hora"),
    PROGRESSO("Progresso", "Tempo até o próximo stat e level"),
    POCOES("Poções", "Custo em gold: ataque normal × power training"),
    DEFESA("Defesa", "Dano recebido com o seu set de armadura"),
    MOBS("Mobs", "Level, defesa e vida dos mobs"),
    CREDITOS("Créditos", "Aviso de fã, créditos e licença"),
    CONFIGURACOES("Configurações", "Balão flutuante e outras opções"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUpaLab() {
    val cores = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = cores) {
        var tela by rememberSaveable { mutableStateOf(Tela.INICIO) }
        AvisoDeErro()
        BackHandler(enabled = tela != Tela.INICIO) { tela = Tela.INICIO }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(tela.titulo) },
                    navigationIcon = {
                        if (tela != Tela.INICIO) {
                            IconButton(onClick = { tela = Tela.INICIO }) {
                                Icon(painterResource(R.drawable.ic_voltar), contentDescription = "Voltar")
                            }
                        }
                    },
                )
            },
        ) { espaco ->
            val m = Modifier.padding(espaco)
            when (tela) {
                Tela.INICIO -> TelaInicio(m) { tela = it }
                Tela.ONDE_TREINAR -> TelaOndeTreinar(m)
                Tela.PROGRESSO -> TelaProgresso(m)
                Tela.POCOES -> TelaPocoes(m)
                Tela.CONFIGURACOES -> TelaConfiguracoes(m)
                Tela.DEFESA -> TelaDefesa(m)
                Tela.MOBS -> TelaMobs(m)
                Tela.CREDITOS -> TelaCreditos(m)
                else -> TelaEmBreve(tela, m)
            }
        }
    }
}

/** Se o app fechou sozinho da última vez, mostra o erro pra copiar e mandar. */
@Composable
private fun AvisoDeErro() {
    val contexto = LocalContext.current
    var erro by remember { mutableStateOf(UpaLabApp.ultimoErro(contexto)) }
    val texto = erro ?: return
    val area = LocalClipboardManager.current
    AlertDialog(
        onDismissRequest = {},
        title = { Text("O app fechou sozinho") },
        text = {
            Column {
                Text("Copie o erro abaixo e mande pro desenvolvedor do UpaLab. Ele fica só no seu celular.")
                Text(
                    texto, style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp).heightIn(max = 240.dp).verticalScroll(rememberScrollState()),
                )
            }
        },
        confirmButton = { TextButton(onClick = { area.setText(AnnotatedString(texto)) }) { Text("Copiar") } },
        dismissButton = { TextButton(onClick = { UpaLabApp.apagarErro(contexto); erro = null }) { Text("Fechar") } },
    )
}
