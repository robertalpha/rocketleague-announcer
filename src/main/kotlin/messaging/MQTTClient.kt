package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import nl.vanalphenict.utility.TimeService
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MQTTClient(
    interpreter: MessageInterpreter,
    serverAddress: String,
    val timeService: TimeService,
    msgProcessed: ((msg: String) -> Unit) = {},
) {
    private val TOPIC_ROOT = "rlapi2mqtt"
    private val TOPIC_WILDCARD = "$TOPIC_ROOT/#"
    private val QOS = 1

    private var client: MqttClient
    private val log = KotlinLogging.logger {}
    private val messagesCache: MutableMap<Int, Instant> = HashMap()

    init {
        val clientId = "rla_announcer_" + Base64.encode(Random.nextBytes(3))

        client = MqttClient(serverAddress, clientId, MemoryPersistence())
        val options = MqttConnectOptions()
        System.getenv("BROKER_USERNAME")?.let {
            options.userName = System.getenv("BROKER_USERNAME")
        }
        System.getenv("BROKER_PASSWORD")?.let {
            options.password = System.getenv("BROKER_PASSWORD").toCharArray()
        }
        options.isCleanSession = true
        options.isAutomaticReconnect = true

        try {
            client.connect(options)

            client.setCallback(
                object : MqttCallback {
                    @Throws(Exception::class)
                    override fun messageArrived(topic: String, message: MqttMessage) {
                        try {
                            val payload = String(message.payload)
                            val key = msgHash(topic, payload)
                            messagesCache.computeIfAbsent(key) {
                                interpreter.interpret(payload)
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
        } catch (ex: Exception) {
            log.error(ex) { "Unable to connect to MQTT broker: $serverAddress" }
            throw ex
        }
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
            // During a match never more than one goal scored per 500 milliseconds
            // TODO: fix https://github.com/robertalpha/rocketleague-announcer/issues/42
            topic == "rlapi2mqtt/goalscored" -> topic.hashCode()
            else -> payload.hashCode()
        }
}
