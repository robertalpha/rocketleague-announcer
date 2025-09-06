package nl.vanalphenict.services

import java.time.ZonedDateTime
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.GameEventMessageRoot
import nl.vanalphenict.model.TickerMessageRoot
import org.eclipse.paho.client.mqttv3.MqttMessage

class EventService(val handler: GameEventHandler) {
    fun processTicker(message: MqttMessage) {
        parseMessageRoot<TickerMessageRoot>(message.payload)?.let {
            val ts = parseTimestamp(it.timestamp)
            handler.handleTicker(ts, it)
        }
    }

    fun processGameEvent(message: MqttMessage) {
        parseMessageRoot<GameEventMessageRoot>(message.payload)?.let {
            val ts = parseTimestamp(it.timestamp)
            handler.handleGameEvent(ts, it)
        }
    }

    fun processGameTime(message: MqttMessage) {
        // TODO
        println("NOT YET IMPLEMENTED: process GameTime: ${String(message.payload)}")
    }

    fun processStat(message: MqttMessage) {
        // TODO
        println("NOT YET IMPLEMENTED: process Stat: ${String(message.payload)}")
    }

    private inline fun <reified T> parseMessageRoot(payload: ByteArray): T? {
        return try {
            val output = Json.decodeFromString<T>(String(payload))
            output
        } catch (e: Exception) {
            println("could not parse TickerMessage: ${String(payload)}, error: $e")
            null
        }
    }

    private fun parseTimestamp(input: String) = ZonedDateTime.parse(input)
}

