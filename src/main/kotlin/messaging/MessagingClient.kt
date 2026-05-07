package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import nl.vanalphenict.repository.GameStateRepository
import nl.vanalphenict.services.GameEventHandler
import nl.vanalphenict.utility.TimeService
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MessagingClient(
    eventHandler: GameEventHandler,
    serverAddress: String,
    val timeService: TimeService,
    gameStateRepository: GameStateRepository,
    msgProcessed: ((msg: String) -> Unit) = {},
) {
    private val TOPIC_ROOT = "rlapi2mqtt"
    private val TOPIC_WILDCARD = "$TOPIC_ROOT/#"
    private val QOS = 1

    private val interpreter =
        MessageInterpreter(eventHandler = eventHandler, gameStateRepository = gameStateRepository)
    private var client: MqttClient
    private val log = KotlinLogging.logger {}
    private val messagesCache: MutableMap<Int, Instant> = HashMap()
    internal val json = Json { ignoreUnknownKeys = true }

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
                        val key = msgHash(topic, payload)
                        messagesCache.computeIfAbsent(key) {
                            val envelope = json.parseToJsonElement(payload).jsonObject
                            val eventName = envelope["Event"]?.jsonPrimitive?.content
                            val dataElement = envelope["Data"]
                            val dataStr =
                                when {
                                    dataElement is JsonPrimitive && dataElement.isString ->
                                        dataElement.content
                                    dataElement != null -> dataElement.toString()
                                    else -> "{}"
                                }
                            if (eventName != null) {
                                log.trace { "${timeService.now()} - [$eventName] $dataStr" }
                                interpreter.interpret(eventName, dataStr)
                            } else {
                                log.error { "Event name not found in message: $payload" }
                            }
                            timeService.now()
                        }
                        clearCache()
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

    private fun clearCache() {
        messagesCache.entries.removeIf { it.value.plus(500.milliseconds) < timeService.now() }
    }

    /**
     * Messages might be sent multiple times so we need a hash to detect duplicates. Some messages
     * have specific hash calculations because equivalent messages might differ slightly because of
     * rounding and timing issues.
     *
     * For example, the same goal message might contain slightly different impact locations
     */
    private fun msgHash(topic: String, payload: String) =
        when {
            // Never more than one goal scored per 500 milliseconds
            topic == "rlapi2mqtt/goalscored" -> topic.hashCode()
            else -> payload.hashCode()
        }
}
