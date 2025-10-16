package integrationTests

import com.janoz.discord.VoiceFactory
import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import io.ktor.sse.ServerSentEvent
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import nl.vanalphenict.moduleWithDependencies
import nl.vanalphenict.services.SampleMapper
import nl.vanalphenict.utility.TimeServiceMock
import org.testcontainers.junit.jupiter.Testcontainers

@OptIn(DelicateCoroutinesApi::class)
@Testcontainers
class MessagingTest : AbstractMessagingTest() {


    @Test
    fun testLines() = testApplication {

        val testFile = "RL_log_20250903.txt"

        val timeServiceMock = TimeServiceMock()

        val messages = parseMessagesFromResource(testFile)

        application {
            val mappedPort = mosquitto.getMappedPort(1883)
            val voiceContext = VoiceFactory.createVoiceContextMock()
            val discordService = voiceContext.discordService
            val configsList = mutableListOf(SampleMapper("123", "123", emptyMap()))
            val voiceChannel = VoiceChannel.builder().guild(Guild.builder().id(1L).build()).id(2L).build()
            moduleWithDependencies(
                discordService = discordService,
                voiceChannel = voiceChannel,
                configs = configsList,
                brokerAddress = "tcp://localhost:$mappedPort",
                timeServiceMock
            )
        }
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val sseClient = createClient {
            install(SSE) {
                showCommentEvents()
                showRetryEvents()
            }
        }
        val sseData = mutableListOf<ServerSentEvent>()
        GlobalScope.async {
            withContext(Dispatchers.IO) {
                sseClient.sse(path = "/sse") {
                        incoming.collect { event ->
                            println("Event from server:")
                            println(event)
                            sseData.add(event)
                        }
                }
            }
        }

       messages.forEach {
           val mockTime = kotlin.time.Instant.parse(it.timestamp)
           timeServiceMock.setTime(mockTime)
           client.post("/") {
               contentType(ContentType.Application.Json)
               setBody(TestMessage(topic =  it.topic, message= it.message))
           }
       }
        eventually(10.seconds) {
            sseData.size shouldBe 33
            sseData.count { it.data?.contains("Goal.webp") ?: false } shouldBe 6
            sseData.count { it.data?.contains("Win.webp") ?: false } shouldBe 3
        }
    }

    @Serializable
    data class TestMessage(val topic: String, val message: String)

    private fun parseMessagesFromResource(testFile: String): List<MessageLine> {
        val stream = javaClass.getClassLoader().getResourceAsStream(testFile)

        val lines = String(stream.readAllBytes()).lines()
        return  lines.filter { it.isNotBlank() }.map {
            try {
                Json.decodeFromString(MessageLine.serializer(), it)
            } catch (e: Exception) {
                println("could nor parse MessageLine: ${it}$")
                throw e
            }
        }.sortedBy { it.timestamp }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class MessageLine(val topic: String, val timestamp: String, val message: String)

}