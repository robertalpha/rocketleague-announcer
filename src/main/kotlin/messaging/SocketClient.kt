package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.network.selector.SelectorManager
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SocketClient(val interpreter: MessageInterpreter, rocketLeagueAddress: String) {
    private val log = KotlinLogging.logger {}
    internal val json = Json { ignoreUnknownKeys = true }

    init {
        val parts = rocketLeagueAddress.split(":")
        val host = parts[0]
        val port = parts.getOrNull(1)?.toInt() ?: 49124
        CoroutineScope(Dispatchers.IO).launch { start(host, port) }
    }

    // Reusable HTTP Client configured for WebSockets
    private val client = HttpClient(CIO) { install(WebSockets) }

    suspend fun start(host: String, port: Int) {
        SelectorManager(Dispatchers.IO).use { selectorManager ->
            log.info { "Connecting to Rocket League..." }
            while (true) {
                try {
                    client.webSocket(host = host, port = port, path = "/") {
                        log.info { "Connected to Rocket League via WebSocket" }
                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val jsonString = frame.readText()
                                interpreter.interpret(jsonString)
                            }
                        }
                    }
                } catch (e: Exception) {
                    log.error { "Connection error occurred: ${e.message}" }
                    log.debug(e) { "Stacktrace" }
                }

                delay(5.seconds)
                log.info { "Reconnecting to Rocket League..." }
            }
        }
    }
}
