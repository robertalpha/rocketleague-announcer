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
import nl.vanalphenict.services.announcement.DemolitionChain
import nl.vanalphenict.services.announcement.FirstBlood
import nl.vanalphenict.services.announcement.KilledByBot
import nl.vanalphenict.services.announcement.OwnGoal
import nl.vanalphenict.services.announcement.Retaliation
import nl.vanalphenict.services.impl.EventPersister

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(
        brokerProtocol: String = "tcp",
        brokerAddress: String = "127.0.0.1",
        brokerPort: Int = 1883
    ) {

    val token = System.getenv("TOKEN")
    val sampleDir = System.getenv("SAMPLES")

    val brokerProtocolEnv = System.getenv("BROKER_PROTOCOL")
    val brokerAddressEnv = System.getenv("BROKER_ADDRESS")
    val brokerPortEnv = System.getenv("BROKER_ADDRESS")
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


    val voice = null // Voice(token) # fixme
    //  voice.readSamples(sampleDir)

    val repository = EventRepository()
    val statRepository = StatRepository()
    val eventPersister = EventPersister(repository, statRepository)
    val announcementHandler = AnnouncementHandler(voice,listOf(
        DemolitionChain(statRepository),
        FirstBlood(statRepository),
        KilledByBot(),
        OwnGoal(),
        Retaliation()))
    val eventHandler = EventHandler.Builder(announcementHandler).add(eventPersister).build()
    val client = MessagingClient(eventHandler, brokerUrl)

    configureRouting(client)
}
