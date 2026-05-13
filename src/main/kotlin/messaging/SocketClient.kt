package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import utility.JsonSplitter.Companion.toJsonSequence

class SocketClient(val interpreter: MessageInterpreter, rocketLeagueAddress: String) {
    private val log = KotlinLogging.logger {}
    internal val json = Json { ignoreUnknownKeys = true }

    init {
        val parts = rocketLeagueAddress.split(":")
        val host = parts[0]
        val port = parts.getOrNull(1)?.toInt() ?: 49123
        CoroutineScope(Dispatchers.IO).launch { start(host, port) }
    }

    suspend fun start(host: String, port: Int) {
        SelectorManager(Dispatchers.IO).use { selectorManager ->
            log.info { "Connecting..." }
            while (true) {
                try {
                    aSocket(selectorManager).tcp().connect(host, port).use {
                        log.info { "Connected to server" }
                        it.openReadChannel().toJsonSequence().forEach { interpreter.interpret(it) }
                    }
                } catch (e: Exception) {
                    log.error { "Connection error occurred: ${e.message}" }
                    log.debug(e) { "Stacktrace" }
                }
                delay(5.seconds)
                log.info { "Reconnecting..." }
            }
        }
    }
}
