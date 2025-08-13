package com.mrunk.wearhr

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HrStreamService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var ws: WsClient? = null
    private var health: Health? = null

    override fun onCreate() {
        super.onCreate()
        createNotifChannel()
        startForeground(1, notif("Connecting…"))
        val url = Prefs.getUrl(this)
        val jwt = Prefs.getJwt(this)
        ws = WsClient(
            url, jwt,
            onOpen = {
                updateNotif("Streaming HR…")
                scope.launch {
                    health = Health(this@HrStreamService) { bpm -> ws?.sendHr(bpm) }
                    try { health?.start() } catch (_: Throwable) { updateNotif("HR start failed") }
                }
            },
            onClose = { _, _ -> updateNotif("Disconnected") },
            onError = { updateNotif("WS error: ${'$'}{it.message}") }
        )
        ws?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.launch { runCatching { health?.stop() } }
        ws?.close()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationChannel("hr", "HR Streaming", NotificationManager.IMPORTANCE_LOW))
        }
    }

    private fun notif(text: String): Notification = NotificationCompat.Builder(this, "hr")
        .setSmallIcon(R.drawable.ic_heart)
        .setContentTitle("Wear HR")
        .setContentText(text)
        .setOngoing(true)
        .build()

    private fun updateNotif(text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1, notif(text))
    }
}