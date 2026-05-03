package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.time.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import nl.vanalphenict.model.JsonClockUpdatedSecondsData
import nl.vanalphenict.model.JsonLogMessage
import nl.vanalphenict.model.JsonMatchGuidData
import nl.vanalphenict.model.JsonStatfeedEventData
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
    msgProcessed: ((msg: String) -> Unit) = {},
) {
    private val TOPIC_ROOT = "rlapi2mqtt"
    private val TOPIC_WILDCARD = "$TOPIC_ROOT/#"
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

    internal val json = Json { ignoreUnknownKeys = true }

    // Game events that carry only a MatchGuid and map to GameEvents enum
    private val GAME_EVENT_NAMES = setOf(
        "RoundStarted", "MatchCreated", "MatchInitialized", "MatchDestroyed",
        "MatchEnded", "MatchPaused", "MatchUnpaused", "CountdownBegin",
        "GoalReplayStart", "GoalReplayWillEnd", "GoalReplayEnd",
        "PodiumStart", "ReplayCreated",
    )

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

        client.setCallback(
            object : MqttCallback {
                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    try {
                        val payload = String(message.payload)
                        val envelope = json.parseToJsonElement(payload).jsonObject
                        val eventName = envelope["Event"]?.jsonPrimitive?.content ?: return
                        val dataElement = envelope["Data"]
                        val dataStr = when {
                            dataElement is JsonPrimitive && dataElement.isString -> dataElement.content
                            dataElement != null -> dataElement.toString()
                            else -> "{}"
                        }

                        log.trace { "${Clock.System.now()} - [$eventName] $dataStr" }

                        when {
                            eventName == "StatfeedEvent" -> {
                                val msg = json.decodeFromString<JsonStatfeedEventData>(dataStr)
                                scrubber.processStatfeedEvent(msg)
                            }
                            eventName == "ClockUpdatedSeconds" -> {
                                val msg = json.decodeFromString<JsonClockUpdatedSecondsData>(dataStr)
                                scrubber.processClockUpdatedSeconds(msg)
                            }
                            eventName in GAME_EVENT_NAMES -> {
                                val msg = json.decodeFromString<JsonMatchGuidData>(dataStr)
                                scrubber.processGameEvent(eventName, msg)
                            }
                            topic == TOPIC_LOG -> {
                                scrubber.processLog(json.decodeFromString<JsonLogMessage>(payload))
                            }
                            else -> {
                                log.trace { "Unhandled event: $eventName" }
                            }
                        }
                    } catch (e: Exception) {
                        log.error(e) { "could not parse message: $e" }
                        e.printStackTrace()
                    }
                    msgProcessed(message.toString())
                }

                override fun connectionLost(cause: Throwable) {
                    log.trace(cause) { "connectionLost: " + cause.message }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    log.trace { "deliveryComplete: " + token.isComplete }
                }
            }
        )

        client.subscribe(TOPIC_WILDCARD, QOS)
    }

    internal inline fun <reified T> decode(bytes: ByteArray): T {
        val string = String(bytes)
        return json.decodeFromString<T>(string)
    }
}
