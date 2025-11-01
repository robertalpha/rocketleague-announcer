package integrationTests

import com.janoz.discord.VoiceFactory
import com.janoz.discord.domain.Guild
import com.janoz.discord.domain.VoiceChannel
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.assertions.nondeterministic.fibonacci
import io.kotest.common.KotestInternal
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import io.ktor.server.testing.testApplication
import io.ktor.sse.ServerSentEvent
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import nl.vanalphenict.moduleWithDependencies
import nl.vanalphenict.services.SampleMapper
import nl.vanalphenict.services.SamplePlayer
import nl.vanalphenict.utility.TimeServiceMock
import nl.vanalphenict.web.SSE_EVENT_TYPE
import org.testcontainers.junit.jupiter.Testcontainers

@OptIn(DelicateCoroutinesApi::class)
@Testcontainers
class MessagingTest : AbstractMessagingTest() {

    private val log = KotlinLogging.logger {}

    @OptIn(ExperimentalAtomicApi::class, KotestInternal::class)
    @Test
    fun testLines() = testApplication {
        val testFile = "RL_log_20250903.txt"

        val timeServiceMock = TimeServiceMock()

        val messages = parseMessagesFromResource(testFile)

        val semaphore = AtomicInt(0)

        application {
            val mappedPort = mosquitto.getMappedPort(1883)
            val voiceContext = VoiceFactory.createVoiceContextMock()
            val configsList = mutableListOf(SampleMapper("123", "123", emptyMap()))
            val voiceChannel =
                VoiceChannel.builder().guild(Guild.builder().id(1L).build()).id(2L).build()
            moduleWithDependencies(
                samplePlayer = SamplePlayer(voiceContext.discordService, voiceChannel),
                configs = configsList,
                brokerAddress = "tcp://localhost:$mappedPort",
                timeServiceMock,
                sampleService = voiceContext.sampleService,
                { println("${semaphore.addAndFetch(-1)} \t ${it}") },
            )
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
                        log.trace {
                            """
                                Event from server:
                                $event
                            """
                                .trimIndent()
                        }
                        sseData.add(event)
                    }
                }
            }
        }

        // wait for application to start and sse to conenct
        delay(1000)

        println("sending messages")
        messages.forEach {
            val mockTime = kotlin.time.Instant.parse(it.timestamp)
            semaphore.addAndFetch(1)
            timeServiceMock.setTime(mockTime)
            send(it.topic, it.message)
            eventually(
                config =
                    eventuallyConfig {
                        duration = 1.seconds
                        intervalFn = 5.milliseconds.fibonacci()
                    }
            ) {
                semaphore.load() shouldBe 0
            }
        }

        eventually(10.seconds) {
            val actionEvents =
                sseData.filter { it.event.equals(SSE_EVENT_TYPE.NEW_ACTION.asString()) }
            actionEvents.size shouldBe 48
            actionEvents.count { it.data?.contains("icons/Demolish.webp") ?: false } shouldBe 4
            actionEvents.count { it.data?.contains("icons/Goal.webp") ?: false } shouldBe 5
            actionEvents.count { it.data?.contains("icons/Win.webp") ?: false } shouldBe 3
        }
    }

    private fun parseMessagesFromResource(testFile: String): List<MessageLine> {
        val stream = javaClass.getClassLoader().getResourceAsStream(testFile)!!

        val lines = String(stream.readAllBytes()).lines()
        return lines
            .filter { it.isNotBlank() }
            .map {
                try {
                    Json.decodeFromString(MessageLine.serializer(), it)
                } catch (e: Exception) {
                    log.error { "could nor parse MessageLine: ${it}$" }
                    throw e
                }
            }
            .sortedBy { it.timestamp }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class MessageLine(val topic: String, val timestamp: String, val message: String)
}
