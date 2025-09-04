package nl.vanalphenict.messaging

import nl.vanalphenict.services.EventService
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence


class MessagingClient(
    private val eventService: EventService,
    port: Int,
) {

    private var client: MqttClient

    private val qos = 0 // no persistence needed, messages can get lost

    init {
        val broker = "tcp://127.0.0.1:$port"
        val clientId = "rl-announcer_client"

        client = MqttClient(broker, clientId, MemoryPersistence())
        val options = MqttConnectOptions()

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

                when (topic) {
                    // might split this up later:
                    "rl2mqtt/test" -> eventService.processMessage(message)
                    "rl2mqtt/ticker" -> eventService.processMessage(message)
                    "rl2mqtt/gameevent" -> eventService.processMessage(message)
                    else -> logUnexpectedMessage(message, topic)
                }

            }

            override fun connectionLost(cause: Throwable) {
                println("connectionLost: " + cause.message)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                println("deliveryComplete: " + token.isComplete)
            }
        })

        client.subscribe("rl2mqtt/test", qos)
        client.subscribe("rl2mqtt/gameevent", qos)
        client.subscribe("rl2mqtt/ticker", qos)

    }

    fun send(topic: String, body: String) {
        val message = MqttMessage(body.toByteArray())
        message.setQos(qos)
        client.publish(topic, message)
    }

}