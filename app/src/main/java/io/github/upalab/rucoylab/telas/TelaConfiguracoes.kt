/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 */
package io.github.upalab.rucoylab.telas

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.github.upalab.rucoylab.balao.ServicoBalao

@Composable
fun TelaConfiguracoes(modifier: Modifier) {
    val contexto = LocalContext.current
    var permitido by remember { mutableStateOf(ServicoBalao.podeMostrar(contexto)) }

    // Quando a pessoa volta das configurações do Android, confere de novo a permissão.
    val dono = LocalLifecycleOwner.current
    DisposableEffect(dono) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) permitido = ServicoBalao.podeMostrar(contexto) }
        dono.lifecycle.addObserver(obs)
        onDispose { dono.lifecycle.removeObserver(obs) }
    }

    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Balão flutuante", style = MaterialTheme.typography.titleLarge)
        Text(
            "Um botão redondo que fica por cima do jogo. Toque nele para abrir os menus; toque num menu para a " +
                "calculadora abrir logo abaixo, sem sair do jogo. Arraste pela barra de cima para mover.",
        )
        Text(
            "O balão só mostra as contas. Ele não toca no jogo, não lê a tela e não faz nada sozinho.",
            style = MaterialTheme.typography.bodySmall,
        )
        if (!permitido) {
            Text(
                "Para funcionar, o Android pede a permissão \"Sobrepor a outros apps\" (ou \"Mostrar sobre outros apps\"). " +
                    "Toque no botão, ative a chave do UpaLab e volte para cá.",
            )
            Button(
                onClick = {
                    contexto.startActivity(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${contexto.packageName}"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Dar permissão") }
        } else {
            Button(onClick = { ServicoBalao.abrir(contexto) }, modifier = Modifier.fillMaxWidth()) { Text("Mostrar o balão") }
            OutlinedButton(onClick = { ServicoBalao.fechar(contexto) }, modifier = Modifier.fillMaxWidth()) { Text("Tirar o balão") }
            Text(
                "Enquanto o balão estiver ativo, aparece um aviso na barra de notificações (o Android exige). " +
                    "Dá pra fechar por lá também.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
