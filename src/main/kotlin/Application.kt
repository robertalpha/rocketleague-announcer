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
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.AnnouncementHandler
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.services.EventRepository
import nl.vanalphenict.services.SampleMapper
import nl.vanalphenict.services.announcement.DemolitionChain
import nl.vanalphenict.services.announcement.FirstBlood
import nl.vanalphenict.services.announcement.Kill
import nl.vanalphenict.services.announcement.KilledByBot
import nl.vanalphenict.services.announcement.OwnGoal
import nl.vanalphenict.services.announcement.Retaliation
import nl.vanalphenict.services.announcement.Witness
import nl.vanalphenict.services.impl.EventPersister
import services.announcement.Extermination
import services.announcement.MutualDestruction

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(
        brokerProtocol: String = "tcp",
        brokerAddress: String = "127.0.0.1",
        brokerPort: Int = 1883
    ) {

    val brokerProtocolEnv = "ssl"
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

    val sampleMapper = SampleMapper()

    voice.readSamples(System.getenv("SAMPLE_DIR"))
    sampleMapper.readSampleMapping(FileInputStream(System.getenv("SAMPLE_TEMPLATE")))


    val repository = EventRepository()
    val statRepository = StatRepository()
    val eventPersister = EventPersister(repository, statRepository)
    val announcementHandler = AnnouncementHandler(
        voice,
        listOf(
            DemolitionChain(statRepository),
            Extermination(statRepository),
            MutualDestruction(statRepository),
            Witness(statRepository),
            FirstBlood(statRepository),
            KilledByBot(),
            OwnGoal(),
            Retaliation(),
            Kill()),
        sampleMapper)
    val eventHandler = EventHandler.Builder(announcementHandler).add(eventPersister).build()
    val client = try {
        MessagingClient(eventHandler, brokerUrl)
    } catch (ex: Exception) {
        println("could not connect to broker")
        throw ex
    }

    configureRouting(client)
}
