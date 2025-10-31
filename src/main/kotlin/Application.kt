package nl.vanalphenict

import com.janoz.discord.SampleService
import com.janoz.discord.VoiceFactory
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
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.AnnouncementHandler
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.services.GameTimeTrackerService
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

fun Application.module(brokerAddress: String = "tcp://localhost:1883") {
    val voiceContext = VoiceFactory.createVoiceContext(System.getenv("TOKEN"))
    val sampleService = voiceContext.sampleService
    val discordService = voiceContext.discordService

    sampleService.readSamplesZip(javaClass.getResourceAsStream("/samples/FPS.zip"))

    val configs: MutableList<SampleMapper> = ArrayList()

    configs.add(
        SampleMapper.constructSampleMapper(
            javaClass.getResourceAsStream("/samples/FPS.mapping.json")!!
        )
    )

    System.getenv("SAMPLE_DIR")?.let { sampleDir -> sampleService.readSamples(sampleDir) }

    System.getenv("SAMPLE_MAPPING_DIR")?.let { sampleMappingDir ->
        File(sampleMappingDir)
            .walkTopDown()
            .filter { it.name.endsWith("mapping.json") }
            .forEach { sampleMapping ->
                configs.add(SampleMapper.constructSampleMapper(FileInputStream(sampleMapping)))
            }
    }

    val voiceChannel = discordService.getVoiceChannel(System.getenv("VOICE_CHANNEL_ID")!!.toLong())
    val timeService = TimeServiceImpl()

    val samplePlayer = SamplePlayer(discordService, voiceChannel)

    moduleWithDependencies(samplePlayer, configs, brokerAddress, timeService, sampleService)
}

fun Application.moduleWithDependencies(
    samplePlayer: SamplePlayer,
    configs: MutableList<SampleMapper>,
    brokerAddress: String,
    timeService: TimeService,
    sampleService: SampleService,
) {

    val statRepository = StatRepository()
    val gameEventRepository = GameEventRepository()
    val eventPersister = EventPersister(statRepository, gameEventRepository, timeService)
    val gameTimeTrackerService = GameTimeTrackerService()
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
                WitnessScore(statRepository),
                WitnessSave(statRepository),
            ),
            listOf(MatchStart(gameEventRepository)),
        )
    val eventHandler =
        EventHandler.Builder(announcementHandler)
            .add(eventPersister)
            .add(SsePublisher(timeService))
            .build()
    try {
        MessagingClient(
            eventHandler,
            System.getenv("BROKER_ADDRESS") ?: brokerAddress,
            timeService,
            gameTimeTrackerService,
        )
    } catch (ex: Exception) {
        log.error(ex) { "could not connect to broker" }
        throw ex
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
