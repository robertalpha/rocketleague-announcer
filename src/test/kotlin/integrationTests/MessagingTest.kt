package integrationTests

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import nl.vanalphenict.module
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class MessagingTest : AbstractMessagingTest() {


    @Test
    fun testLines() = testApplication {

        val testFile = "RL_log_20250903.txt"


        val messages = parseMessagesFromResource(testFile)

        application {
            val mappedPort = mosquitto.getMappedPort(1883)

            //FIXME put val broker = "tcp://127.0.0.1:mappedPort" in system env

            //FIXME mock voice during integrationtest
            module()
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

    private fun parseMessagesFromResource(testFile: String): List<String> {
        val stream = javaClass.getClassLoader().getResourceAsStream(testFile)

        val lines = String(stream.readAllBytes()).lines()
        val messages = lines.filter { it.isNotBlank() }.sortedBy {
            try {
                Json.decodeFromString(MessageLine.serializer(), it).timestamp
            } catch (e: Exception) {
                println("could nor parse MessageLine: $it$")
                throw e
            }
        }
        return messages
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class MessageLine(val topic: String, val timestamp: String)

}