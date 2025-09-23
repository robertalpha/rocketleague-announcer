package nl.vanalphenict.messaging

import kotlinx.serialization.json.Json
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.LogMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.EventHandler
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence


class MessagingClient(
    eventHandler: EventHandler,
    serverAddress: String
) {
    private val TOPIC_ROOT = "rl2mqtt"
    private val TOPIC_STAT = "$TOPIC_ROOT/stat"
    private val TOPIC_TICKER = "$TOPIC_ROOT/ticker"
    private val TOPIC_GAME_EVENT = "$TOPIC_ROOT/gameevent"
    private val TOPIC_GAME_TIME = "$TOPIC_ROOT/gametime"
    private val TOPIC_LOG = "$TOPIC_ROOT/log"
    private val QOS = 1
    private var scrubber: EventScrubber = EventScrubber(eventHandler)
    private var client: MqttClient


    init {
        val clientId = "rouncerdouncer"

        client = MqttClient(serverAddress, clientId, MemoryPersistence())
        val options = MqttConnectOptions()

        val username = System.getenv("BROKER_USERNAME")
        val password = System.getenv("BROKER_PASSWORD")

        username?.let {  options.userName = username }
        password?.let {  options.password = password.toCharArray() }
        options.isCleanSession = true

        client.connect(options)

        fun logUnexpectedMessage(message: MqttMessage, topic: String) {
            println("unexpected message on non subscribed topic:")
            println("topic: $topic")
            println("qos: " + message.qos)
            println("message content: " + String(message.payload))
        }

        client.setCallback(object : MqttCallback {
            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                try {
                    when (topic) {
                        TOPIC_STAT ->
                            scrubber.processStat(decode<StatMessage>(message.payload))
                        TOPIC_TICKER ->
                            scrubber.processStat(decode<StatMessage>(message.payload))
                        TOPIC_GAME_TIME ->
                            scrubber.processGameTime(decode<GameTimeMessage>(message.payload))
                        TOPIC_GAME_EVENT ->
                            scrubber.processGameEvent(decode<GameEventMessage>(message.payload))
                        TOPIC_LOG ->
                            scrubber.processLog(decode<LogMessage>(message.payload))
                        else -> logUnexpectedMessage(message, topic)
                    }
                } catch (e: Exception) {
                    println("could not parse message: $e")
                    e.printStackTrace()
                }
            }

            override fun connectionLost(cause: Throwable) {
                println("connectionLost: " + cause.message)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
//                println("deliveryComplete: " + token.isComplete)
            }
        })

        client.subscribe(TOPIC_STAT, QOS)
        client.subscribe(TOPIC_GAME_EVENT, QOS)
        client.subscribe(TOPIC_GAME_TIME, QOS)
        client.subscribe(TOPIC_TICKER, QOS)
        client.subscribe(TOPIC_LOG, QOS)
    }

    inline fun <reified T> decode(bytes: ByteArray): T {
        val string = String(bytes)
        println("received message: $string")
        return Json.decodeFromString<T>(string)
    }

    fun send(topic: String, body: String) {
        val message = MqttMessage(body.toByteArray())
        message.setQos(QOS)
        client.publish(topic, message)
    }


}