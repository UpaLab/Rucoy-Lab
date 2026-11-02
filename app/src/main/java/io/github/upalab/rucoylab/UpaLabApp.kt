/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 */
package io.github.upalab.rucoylab

import android.app.Application
import android.content.Context
import android.os.Build
import java.io.File

/**
 * Registrador de erro: se o app fechar sozinho, guarda o erro NO PRÓPRIO CELULAR (nada é enviado pra
 * internet). Na próxima vez que o app abrir, ele mostra o erro pra pessoa copiar e mandar, se quiser.
 */
class UpaLabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val anterior = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { linha, erro ->
            runCatching {
                arquivo(this).writeText(
                    "UpaLab ${versao(this)} · Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) · " +
                        "${Build.MANUFACTURER} ${Build.MODEL}\n\n" + erro.stackTraceToString().take(8000),
                )
            }
            anterior?.uncaughtException(linha, erro)
        }
    }

    companion object {
        private fun arquivo(c: Context) = File(c.filesDir, "ultimo-erro.txt")

        private fun versao(c: Context) = runCatching {
            c.packageManager.getPackageInfo(c.packageName, 0).let { "${it.versionName} (${it.longVersionCode})" }
        }.getOrDefault("?")

        fun ultimoErro(c: Context): String? = arquivo(c).takeIf { it.exists() }?.readText()
        fun apagarErro(c: Context) { arquivo(c).delete() }
    }
}
