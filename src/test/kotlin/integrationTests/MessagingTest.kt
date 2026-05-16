package integrationTests

import com.janoz.discord.VoiceContext
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
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.sse.ServerSentEvent
import java.io.File
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import nl.vanalphenict.model.JsonEnvelope
import nl.vanalphenict.model.JsonUpdateStateData
import nl.vanalphenict.moduleWithDependencies
import nl.vanalphenict.repository.GameStateRepository
import nl.vanalphenict.services.SampleMapper
import nl.vanalphenict.services.SamplePlayer
import nl.vanalphenict.utility.TimeServiceMock
import nl.vanalphenict.web.SSE_EVENT_TYPE
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Integration tests for the messaging system.
 *
 * These tests spin up a Mosquitto MQTT broker via Testcontainers, start a Ktor test application,
 * and replay game logs to verify that the system correctly interprets messages and publishes
 * corresponding events via SSE.
 */
@OptIn(ExperimentalAtomicApi::class, DelicateCoroutinesApi::class)
@Testcontainers
class MessagingTest : AbstractMessagingTest() {

    private val log = KotlinLogging.logger {}
    private val timeServiceMock = TimeServiceMock()
    private var gameStateRepository = GameStateRepository()
    private val semaphore = AtomicInt(0)
    private val sseData = mutableListOf<ServerSentEvent>()

    @BeforeTest
    fun setup() {
        gameStateRepository = GameStateRepository()
        sseData.clear()
        semaphore.store(0)
    }

    /**
     * Tests a full match replay from a log file. This test ensures that all major game events
     * (goals, saves, etc.) are correctly recognized and announced via SSE.
     */
    @Test
    fun `should correctly process and announce events for match 4366BADC95F480E3`() =
        testApplication {
            setupIntegrationApplication()

            coroutineScope {
                // Start collecting SSE events in the background
                val sseJob = async { collectSseEvents() }

                // wait for application to start and sse to connect
                delay(1000)

                runAndValidateGame("4366BADC95F480E3")

                sseJob.cancel()
            }
        }

    /** Configures the test application with necessary dependencies and mocks. */
    private fun ApplicationTestBuilder.setupIntegrationApplication() {
        application {
            val mappedPort = mosquitto.getMappedPort(1883)
            val voiceContext = VoiceContext.builder().asMock().build()
            val configsList = mutableListOf(SampleMapper("123", "123", emptyMap()))
            val voiceChannel =
                VoiceChannel.builder().guild(Guild.builder().id(1L).build()).id(2L).build()

            moduleWithDependencies(
                samplePlayer = SamplePlayer(voiceContext.discordService, voiceChannel),
                configs = configsList,
                brokerAddress = "tcp://localhost:$mappedPort",
                timeService = timeServiceMock,
                sampleService = voiceContext.sampleService,
                msgProcessed = { semaphore.addAndFetch(-1) },
                gameStateRepository = gameStateRepository,
            )
        }
    }

    /** Connects to the SSE endpoint and collects events into [sseData]. */
    private suspend fun ApplicationTestBuilder.collectSseEvents() {
        withContext(Dispatchers.IO) {
            val client = createClient {
                install(SSE) {
                    showCommentEvents()
                    showRetryEvents()
                }
            }
            client.sse(path = "/sse") {
                incoming.collect { event ->
                    log.trace { "Received SSE event: ${event.event}" }
                    sseData.add(event)
                }
            }
        }
    }

    @OptIn(KotestInternal::class)
    private suspend fun runAndValidateGame(matchId: String) {
        log.info { "Starting replay for match: $matchId" }

        // send messages
        readMessagesFromResource("testmatches/${matchId}.txt").forEach { message ->
            semaphore.addAndFetch(1)
            timeServiceMock.setTime(message.timestamp)
            send(message.topic, json.encodeToString(message.message))

            // Wait for the message to be fully processed by the MQTT client before moving to next
            eventually(
                config =
                    eventuallyConfig {
                        duration = 1.seconds
                        intervalFn = 1_000.nanoseconds.fibonacci()
                    }
            ) {
                semaphore.load() shouldBe 0
            }
        }

        // Verify game results based on final state in repository and captured SSE events
        val expected = gameStateRepository.getGameResult(matchId)
        val actual =
            GameResult(
                goals = sseData.count("Goal"),
                assists = sseData.count("Assist"),
                shots = sseData.count("Shot"),
                saves = sseData.count("Save") + sseData.count("EpicSave"),
                demos = sseData.count("Demolish"),
            )

        log.info { "Validating results for $matchId. Expected: $expected, Actual: $actual" }
        actual shouldBe expected
        sseData.count("MVP") shouldBe 1
    }

    /**
     * Anonymizes player identifiers in the provided match data to obfuscate sensitive information.
     *
     * This method processes messages from a test data file. It extract players and replaces each
     * player's primary identifier with a randomized obfuscated value, while preserving the
     * identifier's structure and integrity.
     */
    @Test
    @Ignore
    fun anonymizeMatch() {
        val matchGuid = "___UUID____"

        val newGuid = md5(matchGuid).substring(16)
        val input = "testmatches/${matchGuid}.txt"
        val output = "testmatches/${newGuid}.txt"

        val nameIt = names.asSequence().shuffled().iterator()

        val replacements: MutableMap<String, String> = HashMap()

        replacements[matchGuid] = newGuid

        val messages = readMessages(File(input).toURI().toURL())
        messages.forEach {
            if (it.topic.equals("rlapi2mqtt/updatestate")) {
                json
                    .decodeFromString<JsonUpdateStateData>(it.message.data)
                    .players
                    .filter { !it.isBot() }
                    .forEach {
                        if (!replacements.containsKey(it.primaryId)) {
                            val parts = it.primaryId.split("|")
                            val random = (1..2048).random().toString(16)
                            replacements[it.primaryId] = parts[0] + "|" + random + "|" + parts[2]
                            replacements[it.name] = nameIt.next()
                        }
                    }
            }
        }
        File(output).printWriter().use { out ->
            messages.forEach {
                var data = it.message.data
                replacements.forEach { (key, value) -> data = data.replace(key, value) }
                out.println(
                    json.encodeToString(
                        Message(it.timestamp, it.topic, JsonEnvelope(it.message.event, data))
                    )
                )
            }
        }
    }

    private val names =
        setOf(
            "Jasper",
            "Yasmine",
            "Milo",
            "Fay",
            "Bram",
            "Elin",
            "Xavier",
            "Luna",
            "Sven",
            "Amira",
            "Twan",
            "Noé",
            "Stijn",
            "Zara",
            "Hugo",
            "Liva",
            "Otis",
            "Romy",
            "Boris",
            "Isa",
        )

    private fun md5(input: String): String {
        val md = java.security.MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02X".format(it) }
    }

    data class GameResult(
        var goals: Int = 0,
        var assists: Int = 0,
        var shots: Int = 0,
        var saves: Int = 0,
        var demos: Int = 0,
    )

    companion object {
        fun GameStateRepository.getGameResult(matchGuid: String): GameResult {
            val expected = GameResult()
            getGame(matchGuid).teams.forEach {
                it.players.forEach { player ->
                    expected.goals += player.goals
                    expected.assists += player.assists
                    expected.shots += player.shots
                    expected.saves += player.saves
                    expected.demos += player.demos
                }
            }
            return expected
        }

        fun List<ServerSentEvent>.count(event: String): Int {
            return this.filter { it.event.equals(SSE_EVENT_TYPE.NEW_ACTION.asString()) }
                .count { it.data?.contains("icons/${event}.webp") ?: false }
        }
    }
}
