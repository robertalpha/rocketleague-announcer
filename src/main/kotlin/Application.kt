package nl.vanalphenict

import com.janoz.discord.Voice
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.httpMethod
import io.ktor.server.webjars.Webjars
import java.io.FileInputStream
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.repository.GameEventRepository
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.AnnouncementHandler
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.services.SampleMapper
import nl.vanalphenict.services.announcement.AsIs
import nl.vanalphenict.services.announcement.DemolitionChain
import nl.vanalphenict.services.announcement.FirstBlood
import nl.vanalphenict.services.announcement.KickOffKill
import nl.vanalphenict.services.announcement.Kill
import nl.vanalphenict.services.announcement.KilledByBot
import nl.vanalphenict.services.announcement.Retaliation
import nl.vanalphenict.services.announcement.Revenge
import nl.vanalphenict.services.announcement.WitnessSave
import nl.vanalphenict.services.announcement.WitnessScore
import nl.vanalphenict.services.impl.EventPersister
import services.announcement.Extermination
import services.announcement.MutualDestruction
import java.nio.file.Files
import kotlin.io.path.Path
import nl.vanalphenict.page.themeRoutes
import nl.vanalphenict.services.Theme
import nl.vanalphenict.services.ThemeService

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(
        brokerProtocol: String = "tcp",
        brokerAddress: String = "127.0.0.1",
        brokerPort: Int = 1883
    ) {

    val brokerProtocolEnv = System.getenv("BROKER_PROTOCOL")
    val brokerAddressEnv = System.getenv("BROKER_ADDRESS")
    val brokerPortEnv = System.getenv("BROKER_PORT")
    val brokerUrl = "${brokerProtocolEnv?:brokerProtocol}://${brokerAddressEnv?:brokerAddress}:${brokerPortEnv?:brokerPort}"


    install(ContentNegotiation) {
        json()
    }
    install(Webjars) {
        path = "assets"
    }

    install(CallLogging) {
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }

    val voice = Voice(System.getenv("TOKEN"))
    voice.sampleService.readSamplesZip(javaClass.getResourceAsStream("/samples/FPS.zip"))

    val configs: MutableList<SampleMapper> = ArrayList()

    configs.add(SampleMapper.constructSampleMapper(
        javaClass.getResourceAsStream("/samples/FPS.mapping.json")!!))

    System.getenv("SAMPLE_DIR")?.let { sampleDir ->
        voice.sampleService.readSamples(sampleDir)
    }

    System.getenv("SAMPLE_MAPPING_DIR")?.let { sampleMappingDir ->
        Files.list(Path(sampleMappingDir))
            .filter { it.endsWith("mapping.json") }
            .forEach { sampleMapping ->
        configs.add(
            SampleMapper.constructSampleMapper(
                FileInputStream(
                    sampleMapping.toFile())))
    }}

    val statRepository = StatRepository()
    val gameEventRepository = GameEventRepository()
    val eventPersister = EventPersister(statRepository, gameEventRepository)
    val announcementHandler = AnnouncementHandler(
        voice.discordService,
        listOf(
            AsIs(),
            DemolitionChain(statRepository),
            Extermination(statRepository),
            FirstBlood(statRepository),
            KickOffKill(gameEventRepository),
            Kill(),
            KilledByBot(),
            MutualDestruction(statRepository),
            Retaliation(),
            Revenge(),
            WitnessScore(statRepository),
            WitnessSave(statRepository),
        ),
        configs.last()) // For now use environment when available, otherwise default
    val eventHandler = EventHandler.Builder(announcementHandler).add(eventPersister).build()
    val client = try {
        MessagingClient(eventHandler, brokerUrl)
    } catch (ex: Exception) {
        println("could not connect to broker")
        throw ex
    }

    val themes = listOf(Theme("1","FPS"),Theme("2","Unreal"),Theme("3","DukeNukem"))
    val themeService = ThemeService(themes)
    configureRouting(client,themeService)
    themeRoutes(themeService)

}
