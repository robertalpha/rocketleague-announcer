package integrationTests

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlinx.serialization.json.Json
import nl.vanalphenict.module
import nl.vanalphenict.util.TestMessage
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class MessagingTest : AbstractMessagingTest() {

    @Test
    fun testLines() = testApplication {

        val testFile = "RL_Log.txt"


        val messages = parseMessagesFromResource(testFile)

        application {
            val mappedPort = mosquitto.getMappedPort(1883)
            module(mappedPort)
        }
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

       messages.forEach {
           client.post("/") {
               contentType(ContentType.Application.Json)
               setBody(it)
           }
       }
    }


    @Test
    fun testNewLines() = testApplication {

        val testFile = "RL_log_20250903.txt"


        val messages = parseMessagesFromResource(testFile)

        application {
            val mappedPort = mosquitto.getMappedPort(1883)
            module(mappedPort)
        }
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

       messages.forEach {
           client.post("/") {
               contentType(ContentType.Application.Json)
               setBody(it)
           }
       }
    }

    private fun parseMessagesFromResource(testFile: String): List<TestMessage> {
        val stream = javaClass.getClassLoader().getResourceAsStream(testFile)

        val lines = String(stream.readAllBytes()).lines()
        val messages = lines.filter { it.isNotBlank() }.map {
            try {
                Json.decodeFromString(TestMessage.serializer(), it)
            } catch (e: Exception) {
                println(it)
                println(it)
                throw e
            }
        }.sortedBy { it.timestamp }
        return messages
    }

}