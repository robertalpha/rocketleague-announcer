package nl.vanalphenict

import com.janoz.discord.Voice
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.services.EventHandler
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.httpMethod
import io.ktor.server.webjars.Webjars
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.AnnouncementHandler
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
import java.io.FileInputStream

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

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
//    voice.readSamples(System.getenv("SAMPLES"))

    val sampleMapper = SampleMapper()
//    sampleMapper.readSampleMapping({}.javaClass.getResourceAsStream("/mapping.json"))

    voice.readSamples("./testsamples/duke")
    sampleMapper.readSampleMapping(FileInputStream("./testsamples/duke/duke.json"))

    val repository = EventRepository()
    val statRepository = StatRepository()
    val eventPersister = EventPersister(repository, statRepository)
    val announcementHandler = AnnouncementHandler(
        voice,
        listOf(
            DemolitionChain(statRepository),
            Witness(statRepository),
            FirstBlood(statRepository),
            KilledByBot(),
            OwnGoal(),
            Retaliation(),
            Kill()),
        sampleMapper)
    val eventHandler = EventHandler.Builder(announcementHandler).add(eventPersister).build()
    val client = MessagingClient(eventHandler, System.getenv("BROKER_ADDRESS"))

    configureRouting(client)
}
