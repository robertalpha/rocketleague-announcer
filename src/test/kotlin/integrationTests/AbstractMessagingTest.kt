package integrationTests

import java.net.URL
import kotlin.time.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.JsonEnvelope
import nl.vanalphenict.model.JsonUpdateStateData
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.images.builder.Transferable

abstract class AbstractMessagingTest {

    protected val json = Json { ignoreUnknownKeys = true }

    companion object {
        @JvmField
        val mosquitto =
            GenericContainer("eclipse-mosquitto:2.0.21")
                .withExposedPorts(1883, 9001)
                .waitingFor(HostPortWaitStrategy().forPorts(1883))
                .withCopyToContainer(
                    Transferable.of("listener 1883\nallow_anonymous true\n"),
                    "/mosquitto/config/mosquitto.conf",
                )
                .withReuse(true)
        val mqttClient: MqttClient

        init {

            mosquitto.start()

            val mappedPort = mosquitto.getMappedPort(1883)
            mqttClient =
                MqttClient("tcp://localhost:$mappedPort", "UNIT_TEST_CLIENT", MemoryPersistence())
            mqttClient.connect()
        }

        fun send(topic: String, message: String) {
            mqttClient.publish(topic, message.toByteArray(), 1, false)
        }
    }

    protected fun readMessagesFromResource(testFile: String): List<Message> {
        return readMessages(javaClass.getClassLoader().getResource(testFile)!!)
    }

    protected fun readMessages(resource: URL): List<Message> {
        return resource
            .readText()
            .lines()
            .filter { it.isNotBlank() }
            .map { json.decodeFromString<Message>(it) }
    }

    @Serializable
    protected data class Message(
        val timestamp: Instant,
        val topic: String,
        val message: JsonEnvelope,
    ) {
        @OptIn(ExperimentalSerializationApi::class)
        fun prettify(): String {
            val prettyJson = Json { // this returns the JsonBuilder
                prettyPrint = true
                encodeDefaults = true
                prettyPrintIndent = " "
            }
            return "Timestamp: $timestamp\nTopic: $topic\nEvent: ${message.event}\n" +
                prettyJson.encodeToString(
                    prettyJson.decodeFromString<JsonUpdateStateData>(message.data)
                )
        }
    }
}
