/*
 * UpaLab — Ferramentas para Rucoy Online (projeto de fã, sem vínculo com os desenvolvedores do jogo).
 * Copyright (C) 2026 UpaLab — GPL-3.0 (ver LICENSE).
 *
 * Código próprio do UpaLab. Balão flutuante: só MOSTRA as calculadoras por cima de outros apps.
 * Não toca no jogo, não lê a tela e não automatiza nada.
 */
package io.github.upalab.rucoylab.balao

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import io.github.upalab.rucoylab.R

class ServicoBalao : Service(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner, OnBackPressedDispatcherOwner {

    private val registro = LifecycleRegistry(this)
    private val estadoSalvo = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle get() = registro
    override val savedStateRegistry: SavedStateRegistry get() = estadoSalvo.savedStateRegistry

    // Peças que o app normal ganha de graça da tela (Activity) e que as listas de escolha (dropdown)
    // exigem. Sem elas, abrir uma lista no balão derrubava o app.
    private val guardaModelos = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = guardaModelos
    override val onBackPressedDispatcher = OnBackPressedDispatcher { }

    private lateinit var janelas: WindowManager
    private lateinit var params: WindowManager.LayoutParams
    private var vista: ComposeView? = null
    private var xDaBolinha = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        estadoSalvo.performRestore(null)
        registro.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        iniciarEmPrimeiroPlano()

        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        janelas = getSystemService(WINDOW_SERVICE) as WindowManager
        val lado = (64 * resources.displayMetrics.density).toInt()  // a bolinha: 56 dp + 4 dp de cada lado
        params = WindowManager.LayoutParams(
            lado,
            lado,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            FLAGS_FECHADO,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = resources.displayMetrics.heightPixels / 4
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        }
        val v = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@ServicoBalao)
            setViewTreeSavedStateRegistryOwner(this@ServicoBalao)
            setViewTreeViewModelStoreOwner(this@ServicoBalao)
            setViewTreeOnBackPressedDispatcherOwner(this@ServicoBalao)
        }
        vista = v
        mostrar()
        janelas.addView(v, params)
        registro.handleLifecycleEvent(Lifecycle.Event.ON_START)
        registro.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    /** Monta o conteúdo do balão. Os testes usam [abertoNoInicio] e [ferramenta] pra abrir direto numa ferramenta. */
    internal fun mostrar(abertoNoInicio: Boolean = false, ferramenta: Ferramenta? = null) {
        vista?.setContent {
            CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides this@ServicoBalao) {
                key(abertoNoInicio, ferramenta) { ConteudoBalao(
                    mover = { dx, dy -> mover(dx, dy) },
                    abrirOuFechar = { aberto, largura -> ajustarJanela(aberto, largura) },
                    encerrar = { stopSelf() },
                    abertoNoInicio = abertoNoInicio,
                    ferramentaNoInicio = ferramenta,
                ) }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACAO_PARAR) stopSelf()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        registro.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        guardaModelos.clear()
        vista?.let { runCatching { janelas.removeView(it) } }
        vista = null
        super.onDestroy()
    }

    private fun mover(dx: Float, dy: Float) {
        val v = vista ?: return
        val tela = resources.displayMetrics
        params.x = (params.x + dx.toInt()).coerceIn(0, (tela.widthPixels - v.width).coerceAtLeast(0))
        params.y = (params.y + dy.toInt()).coerceIn(0, (tela.heightPixels - v.height).coerceAtLeast(0))
        janelas.updateViewLayout(v, params)
    }

    /**
     * Fechado: só o balão, e a janela não pega o teclado (o jogo continua recebendo tudo).
     * Aberto: o painel na largura do tamanho escolhido (P, M ou G); aceita digitar, e toques fora dele vão pro jogo.
     */
    private fun ajustarJanela(aberto: Boolean, fracaoLargura: Float) {
        val v = vista ?: return
        val largura = resources.displayMetrics.widthPixels
        if (aberto) {
            if (params.flags == FLAGS_FECHADO) xDaBolinha = params.x
            params.width = (largura * fracaoLargura).toInt()
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.x = (largura - params.width) / 2
            params.flags = FLAGS_ABERTO
        } else {
            // Janela do tamanho exato da bolinha (56 dp + 4 dp de cada lado), no lugar onde ela estava.
            val lado = (64 * resources.displayMetrics.density).toInt()
            params.width = lado
            params.height = lado
            params.x = xDaBolinha.coerceIn(0, (largura - lado).coerceAtLeast(0))
            params.flags = FLAGS_FECHADO
        }
        janelas.updateViewLayout(v, params)
    }

    private fun iniciarEmPrimeiroPlano() {
        val gerente = getSystemService(NotificationManager::class.java)
        gerente.createNotificationChannel(
            NotificationChannel(CANAL, "Balão flutuante", NotificationManager.IMPORTANCE_MIN),
        )
        val parar = PendingIntent.getService(
            this, 0, Intent(this, ServicoBalao::class.java).setAction(ACAO_PARAR),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val aviso = Notification.Builder(this, CANAL)
            .setSmallIcon(R.drawable.ic_icone_frente)
            .setContentTitle("Balão do UpaLab ativo")
            .setContentText("Toque em Fechar para tirar o balão da tela.")
            .addAction(Notification.Action.Builder(null, "Fechar", parar).build())
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 34) startForeground(1, aviso, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        else startForeground(1, aviso)
    }

    companion object {
        private const val CANAL = "balao"
        private const val ACAO_PARAR = "io.github.upalab.rucoylab.PARAR_BALAO"

        private const val FLAGS_FECHADO = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        private const val FLAGS_ABERTO = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

        fun podeMostrar(c: Context) = Settings.canDrawOverlays(c)
        fun abrir(c: Context) { c.startForegroundService(Intent(c, ServicoBalao::class.java)) }
        fun fechar(c: Context) { c.stopService(Intent(c, ServicoBalao::class.java)) }
    }
}
