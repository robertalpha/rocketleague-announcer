package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.time.Clock
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.JsonGameEventMessage
import nl.vanalphenict.model.JsonGameTimeMessage
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.JsonStatMessage
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.services.GameTimeTrackerService
import nl.vanalphenict.utility.TimeService
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MessagingClient(
    eventHandler: EventHandler,
    serverAddress: String,
    timeService: TimeService,
    gameTimeTrackerService: GameTimeTrackerService,
) {
    private val TOPIC_ROOT = "rl2mqtt"
    private val TOPIC_STAT = "$TOPIC_ROOT/stat"
    private val TOPIC_TICKER = "$TOPIC_ROOT/ticker"
    private val TOPIC_GAME_EVENT = "$TOPIC_ROOT/gameevent"
    private val TOPIC_GAME_TIME = "$TOPIC_ROOT/gametime"
    private val TOPIC_LOG = "$TOPIC_ROOT/log"
    private val QOS = 1
    private var scrubber: EventScrubber =
        EventScrubber(
            eventHandler = eventHandler,
            gameTimeTrackerService = gameTimeTrackerService,
            timeService = timeService,
        )
    private var client: MqttClient
    private val log = KotlinLogging.logger {}

    init {
        val clientId = "rla_announcer_" + Base64.encode(Random.nextBytes(3))

        client = MqttClient(serverAddress, clientId, MemoryPersistence())
        val options = MqttConnectOptions()

        val username = System.getenv("BROKER_USERNAME")
        val password = System.getenv("BROKER_PASSWORD")

        username?.let { options.userName = username }
        password?.let { options.password = password.toCharArray() }
        options.isCleanSession = true

        options.isAutomaticReconnect = true

        client.connect(options)

        fun logUnexpectedMessage(message: MqttMessage, topic: String) {
            log.warn {
                """
                unexpected message on non subscribed topic:")
                topic: $topic
                qos: ${message.qos}
                message content: ${String(message.payload)}
            """
                    .trimIndent()
            }
        }

        client.setCallback(
            object : MqttCallback {
                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    try {
                        when (topic) {
                            TOPIC_STAT -> {
                                val stat = decode<JsonStatMessage>(message.payload)
                                logStatMessage(stat)
                                scrubber.processStat(stat)
                            }
                            TOPIC_TICKER ->
                                scrubber.processStat(decode<JsonStatMessage>(message.payload))
                            TOPIC_GAME_TIME ->
                                scrubber.processGameTime(
                                    decode<JsonGameTimeMessage>(message.payload)
                                )
                            TOPIC_GAME_EVENT -> {
                                val msg = decode<JsonGameEventMessage>(message.payload)
                                logGameEvent(msg)
                                scrubber.processGameEvent(msg)
                            }
                            TOPIC_LOG ->
                                scrubber.processLog(decode<JsonLogMessage>(message.payload))
                            else -> logUnexpectedMessage(message, topic)
                        }
                    } catch (e: Exception) {
                        log.error { "could not parse message: $e" }
                        e.printStackTrace()
                    }
                }

                private fun logStatMessage(stat: JsonStatMessage) {
                    log.trace { "${Clock.System.now()} - $stat" }
                }

                private fun logGameEvent(gameEvent: JsonGameEventMessage) {
                    log.trace { "${Clock.System.now()} - $gameEvent" }
                }

                override fun connectionLost(cause: Throwable) {
                    log.trace { "connectionLost: " + cause.message }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    log.trace { "deliveryComplete: " + token.isComplete }
                }
            }
        )

        client.subscribe(TOPIC_STAT, QOS)
        client.subscribe(TOPIC_GAME_EVENT, QOS)
        client.subscribe(TOPIC_GAME_TIME, QOS)
        client.subscribe(TOPIC_TICKER, QOS)
        client.subscribe(TOPIC_LOG, QOS)
    }

    inline fun <reified T> decode(bytes: ByteArray): T {
        val string = String(bytes)
        return Json.decodeFromString<T>(string)
    }

    fun send(topic: String, body: String) {
        val message = MqttMessage(body.toByteArray())
        message.qos = QOS
        client.publish(topic, message)
    }
}
