/*
 * UpaLab — GPL-3.0. Abre cada tela (e o balão) num Android simulado pra pegar travamentos.
 */
package io.github.upalab.rucoylab.telas

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import io.github.upalab.rucoylab.balao.ConteudoBalao
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TelasAbremTest {

    @get:Rule val regra = createComposeRule()

    private val contexto get() = ApplicationProvider.getApplicationContext<Context>()

    /** Deixa o Progresso no Power e com stat preenchido, pra abrir o painel de poções também. */
    @Before fun preencher() {
        contexto.getSharedPreferences("progresso", Context.MODE_PRIVATE).edit()
            .putString("statAtual", "72").putString("statMeta", "90").putInt("ritmo", 1).putBoolean("verde", true).commit()
        contexto.getSharedPreferences("tela_pocoes", Context.MODE_PRIVATE).edit()
            .putString("statAtual", "72").putString("statMeta", "90").commit()
    }

    private fun abre(conteudo: @androidx.compose.runtime.Composable () -> Unit) {
        regra.setContent { Box(Modifier.fillMaxWidth().height(600.dp)) { conteudo() } }
        regra.waitForIdle()
    }

    @Test fun ondeTreinar() = abre { TelaOndeTreinar(Modifier) }
    @Test fun progressoPower() = abre { TelaProgresso(Modifier) }
    @Test fun pocoes() = abre { TelaPocoes(Modifier) }
    @Test fun defesa() = abre { TelaDefesa(Modifier) }
    @Test fun mobs() = abre { TelaMobs(Modifier) }
    @Test fun configuracoes() = abre { TelaConfiguracoes(Modifier) }
    @Test fun appInteiro() = abre { AppUpaLab() }

    @Test fun balaoAbreCadaFerramenta() {
        regra.setContent { ConteudoBalao(mover = { _, _ -> }, abrirOuFechar = {}, encerrar = {}) }
        regra.onNodeWithText("UP").performClick()
        for (nome in listOf("Onde treinar", "Progresso", "Poções", "Defesa", "Mobs")) {
            regra.onNodeWithText(nome).performClick()
            regra.waitForIdle()
        }
    }
}

/** Abre o balão como no celular: um serviço de verdade colocando a janela por cima. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BalaoComoServicoTest {
    @Test fun servicoProgresso() = abrirNoServico(io.github.upalab.rucoylab.balao.Ferramenta.PROGRESSO)
    @Test fun servicoOndeTreinar() = abrirNoServico(io.github.upalab.rucoylab.balao.Ferramenta.ONDE_TREINAR)
    @Test fun servicoPocoes() = abrirNoServico(io.github.upalab.rucoylab.balao.Ferramenta.POCOES)
    @Test fun servicoDefesa() = abrirNoServico(io.github.upalab.rucoylab.balao.Ferramenta.DEFESA)
    @Test fun servicoMobs() = abrirNoServico(io.github.upalab.rucoylab.balao.Ferramenta.MOBS)

    private fun abrirNoServico(f: io.github.upalab.rucoylab.balao.Ferramenta) {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        app.getSharedPreferences("progresso", Context.MODE_PRIVATE).edit()
            .putString("statAtual", "72").putString("statMeta", "90").putInt("ritmo", 1).commit()
        app.getSharedPreferences("tela_pocoes", Context.MODE_PRIVATE).edit()
            .putString("statAtual", "72").putString("statMeta", "90").commit()
        org.robolectric.shadows.ShadowLog.stream = System.out
        org.robolectric.shadows.ShadowSettings.setCanDrawOverlays(true)
        val controle = org.robolectric.Robolectric.buildService(io.github.upalab.rucoylab.balao.ServicoBalao::class.java).create()
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        val campo = io.github.upalab.rucoylab.balao.ServicoBalao::class.java.getDeclaredField("vista").apply { isAccessible = true }
        val raiz = campo.get(controle.get()) as? androidx.compose.ui.platform.ComposeView ?: error("o balão não apareceu")
        controle.get().mostrar(abertoNoInicio = true, ferramenta = f)
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        raiz.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.AT_MOST),
            android.view.View.MeasureSpec.makeMeasureSpec(2000, android.view.View.MeasureSpec.AT_MOST),
        )
        raiz.layout(0, 0, raiz.measuredWidth, raiz.measuredHeight)
        check(raiz.isAttachedToWindow && raiz.childCount > 0) { "a janela do balão não montou" }
        check(raiz.measuredHeight > 300) { "o painel não abriu (altura ${raiz.measuredHeight})" }
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        controle.destroy()
    }
}
