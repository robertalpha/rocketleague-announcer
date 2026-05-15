package nl.vanalphenict

import com.janoz.discord.SampleService
import com.janoz.discord.VoiceContext
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.httpMethod
import io.ktor.server.sse.SSE
import io.ktor.server.webjars.Webjars
import java.io.File
import java.io.FileInputStream
import kotlinx.serialization.json.Json
import nl.vanalphenict.messaging.MQTTClient
import nl.vanalphenict.messaging.MessageInterpreter
import nl.vanalphenict.messaging.SocketClient
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.repository.GameStateRepository
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.AnnouncementHandler
import nl.vanalphenict.services.GameEventHandler
import nl.vanalphenict.services.SampleMapper
import nl.vanalphenict.services.SamplePlayer
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.services.announcement.AsIs
import nl.vanalphenict.services.announcement.DemolitionChain
import nl.vanalphenict.services.announcement.Extermination
import nl.vanalphenict.services.announcement.FirstBlood
import nl.vanalphenict.services.announcement.Goal
import nl.vanalphenict.services.announcement.KickOffKill
import nl.vanalphenict.services.announcement.Kill
import nl.vanalphenict.services.announcement.KilledByBot
import nl.vanalphenict.services.announcement.MatchStart
import nl.vanalphenict.services.announcement.MutualDestruction
import nl.vanalphenict.services.announcement.Retaliation
import nl.vanalphenict.services.announcement.Revenge
import nl.vanalphenict.services.announcement.WinLoss
import nl.vanalphenict.services.announcement.WitnessSave
import nl.vanalphenict.services.announcement.WitnessScore
import nl.vanalphenict.services.impl.EventPersister
import nl.vanalphenict.services.impl.SsePublisher
import nl.vanalphenict.utility.TimeService
import nl.vanalphenict.utility.TimeServiceImpl
import nl.vanalphenict.web.configureRouting
import nl.vanalphenict.web.configureSSE

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val log = KotlinLogging.logger {}

fun Application.module() {
    val voiceContext =
        System.getenv("DISCORD_BOT_TOKEN")?.let { VoiceContext.builder().token(it).build() }
            ?: run {
                log.warn { "DISCORD_BOT_TOKEN not provided, using dummy discord implementation!" }
                VoiceContext.builder().asMock().build()
            }
    val sampleService = voiceContext.sampleService
    val discordService = voiceContext.discordService

    val brokerAddress = System.getenv("BROKER_ADDRESS") ?: "tcp://localhost:1883"
    val rocketLeagueAddress = System.getenv("ROCKET_LEAGUE_ADDRESS")

    val configs: MutableList<SampleMapper> = ArrayList()

    // Add default samples
    sampleService.readSamplesZip(javaClass.getResourceAsStream("/samples/FPS.zip"))
    configs.add(
        SampleMapper.constructSampleMapper(
            javaClass.getResourceAsStream("/samples/FPS.mapping.json")!!
        )
    )

    // Add custom samples
    System.getenv("SAMPLE_DIR")?.let { sampleDir -> sampleService.readSamples(sampleDir) }
    System.getenv("SAMPLE_MAPPING_DIR")?.let { sampleMappingDir ->
        File(sampleMappingDir)
            .walkTopDown()
            .filter { it.name.endsWith("mapping.json") }
            .forEach { sampleMapping ->
                configs.add(SampleMapper.constructSampleMapper(FileInputStream(sampleMapping)))
            }
    }

    val voiceChannel =
        System.getenv("DISCORD_VOICE_CHANNEL_ID")?.let {
            discordService.getVoiceChannel(it.toLong())
        }
            ?: run {
                log.warn {
                    "DISCORD_VOICE_CHANNEL_ID not provided. Voice functionality will be limited."
                }
                null
            }
    moduleWithDependencies(
        samplePlayer = SamplePlayer(discordService, voiceChannel),
        configs = configs,
        brokerAddress = brokerAddress,
        rocketLeagueAddress = rocketLeagueAddress,
        timeService = TimeServiceImpl(),
        sampleService = sampleService,
    )
}

fun Application.moduleWithDependencies(
    samplePlayer: SamplePlayer,
    configs: MutableList<SampleMapper>,
    brokerAddress: String,
    rocketLeagueAddress: String? = null,
    timeService: TimeService,
    sampleService: SampleService,
    msgProcessed: ((msg: String) -> Unit) = {},
    gameStateRepository: GameStateRepository = GameStateRepository(),
) {

    val statRepository = StatRepository()
    val gameEventRepository = GameEventRepository()
    val eventPersister = EventPersister(statRepository, gameEventRepository, timeService)
    val announcementHandler =
        AnnouncementHandler(
            samplePlayer,
            configs.last(),
            listOf(
                AsIs(),
                DemolitionChain(statRepository),
                Extermination(statRepository),
                FirstBlood(statRepository),
                Goal(),
                KickOffKill(gameEventRepository),
                Kill(),
                KilledByBot(),
                MutualDestruction(),
                Retaliation(),
                Revenge(),
                WinLoss(),
                WitnessScore(statRepository),
                WitnessSave(statRepository),
            ),
            listOf(MatchStart(gameEventRepository)),
        )
    val interpreter =
        MessageInterpreter(
            eventHandler =
                GameEventHandler.Builder(announcementHandler)
                    .add(eventPersister)
                    .add(SsePublisher(gameStateRepository))
                    .build(),
            gameStateRepository = gameStateRepository,
        )

    if (rocketLeagueAddress != null) {
        log.info { "Connecting to Rocket League at $rocketLeagueAddress" }
        SocketClient(interpreter, rocketLeagueAddress)
    } else {
        log.info { "Connecting to MQTT broker at $brokerAddress" }
        MQTTClient(interpreter, brokerAddress, timeService, msgProcessed)
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }
    install(Webjars) { path = "assets" }

    install(SSE)

    if (log.isTraceEnabled()) {
        install(CallLogging) {
            format { call ->
                val status = call.response.status()
                val httpMethod = call.request.httpMethod.value
                val userAgent = call.request.headers["User-Agent"]
                "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
            }
        }
    }

    val themeService = ThemeService(configs, announcementHandler)
    configureRouting(themeService, sampleService, samplePlayer)
    configureSSE()
}
