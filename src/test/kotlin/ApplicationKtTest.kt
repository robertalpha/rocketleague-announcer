package nl.vanalphenict

import integrationTests.AbstractMessagingTest
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest : AbstractMessagingTest() {
    @Test
    fun testRoot() = testApplication {
        application {
            val mappedPort = mosquitto.getMappedPort(1883)
            module(brokerAddress = "tcp://localhost:$mappedPort", mocked = true)
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}