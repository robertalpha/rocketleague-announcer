package integrationTests

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy
import org.testcontainers.images.builder.Transferable

abstract class AbstractMessagingTest {

    companion object {
        @JvmField
        val mosquitto =
            GenericContainer("eclipse-mosquitto:2.0.20")
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
            mqttClient.publish(topic, message.toByteArray(), 1, true)
        }
    }
}
