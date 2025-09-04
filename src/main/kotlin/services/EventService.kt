package nl.vanalphenict.services

import org.eclipse.paho.client.mqttv3.MqttMessage

class EventService {
    fun processMessage(message: MqttMessage) {
        println("process message: ${String(message.payload)}")
    }
}