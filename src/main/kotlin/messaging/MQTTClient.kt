package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.encoding.Base64
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import nl.vanalphenict.utility.TimeService
import nl.vanalphenict.utility.TimeWindowedEventFilter
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

data class MqttMessageData(val topic: String, val payload: String)

class MQTTClient(
    interpreter: MessageInterpreter,
    brokerAddress: String,
    timeService: TimeService,
    msgProcessed: ((msg: String) -> Unit) = {},
) {
    private val log = KotlinLogging.logger {}

    private val TOPIC_ROOT = "rlapi2mqtt"
    private val TOPIC_WILDCARD = "$TOPIC_ROOT/#"
    private val QOS = 1

    private var client: MqttClient

    init {
        val clientId = "rla_announcer_" + Base64.encode(Random.nextBytes(3))

        client = MqttClient(brokerAddress, clientId, MemoryPersistence())
        val options = MqttConnectOptions()
        System.getenv("BROKER_USERNAME")?.let {
            options.userName = System.getenv("BROKER_USERNAME")
        }
        System.getenv("BROKER_PASSWORD")?.let {
            options.password = System.getenv("BROKER_PASSWORD").toCharArray()
        }
        options.isCleanSession = true
        options.isAutomaticReconnect = true

        val windowedEventFilter =
            TimeWindowedEventFilter<MqttMessageData, Int>(
                ::msgHash,
                { s -> interpreter.interpret(s.payload) },
                1.seconds,
                timeService,
            )

        try {
            client.connect(options)

            client.setCallback(
                object : MqttCallback {
                    @Throws(Exception::class)
                    override fun messageArrived(topic: String, message: MqttMessage) {
                        try {
                            val payload = String(message.payload)
                            windowedEventFilter.process(MqttMessageData(topic, payload))
                        } catch (e: Exception) {
                            log.error { "Could not parse message: $e" }
                            log.debug(e) { "Stacktrace: " }
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
            log.error { "Unable to connect to MQTT broker: $brokerAddress" }
            log.debug(ex) { "Stacktrace: " }
            throw ex
        }
    }

    /**
     * Messages might be sent multiple times so we need a hash to detect duplicates. Some messages
     * have specific hash calculations because equivalent messages might differ slightly because of
     * rounding and timing issues.
     *
     * For example, the same goal message might contain slightly different impact locations
     */
    private fun msgHash(msg: MqttMessageData) =
        when {

            // During a match never more than one goal scored per 500 milliseconds
            msg.topic == "rlapi2mqtt/goalscored" ->
                (msg.topic + getGuidFromMessage(msg.payload)).hashCode()

            else -> msg.payload.hashCode()
        }

    val matchGuidRegexp = """MatchGuid\\":\\"([A-F0-9]+)\\"""".toRegex()

    private fun getGuidFromMessage(message: String): String =
        matchGuidRegexp.find(message)?.groupValues[1] ?: ""
}
