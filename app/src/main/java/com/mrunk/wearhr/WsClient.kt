package com.mrunk.wearhr

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class WsClient(
    private val url: String,
    private val jwt: String,
    private val onOpen: () -> Unit,
    private val onClose: (Int, String) -> Unit,
    private val onError: (Throwable) -> Unit
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var ws: WebSocket? = null

    fun connect() {
        val req = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $jwt")
            .build()
        ws = client.newWebSocket(req, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) { onOpen() }
            override fun onMessage(webSocket: WebSocket, text: String) { /* ignore */ }
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) { /* ignore */ }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) { onClose(code, reason) }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) { onError(t) }
        })
    }

    fun sendHr(bpm: Int) {
        val payload = json.encodeToString(HrEvent(bpm = bpm))
        ws?.send(payload)
    }

    fun close() { ws?.close(1000, "bye") }
}

@Serializable
data class HrEvent(
    val type: String = "hr",
    val bpm: Int,
    val ts: String = java.time.Instant.now().toString(),
    val source: String = "wearos-healthservices",
    val sessionId: String? = null
)