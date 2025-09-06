package nl.vanalphenict

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.services.EventService
import io.ktor.server.plugins.contentnegotiation.*
import nl.vanalphenict.services.GameEventHandler
import nl.vanalphenict.services.EventRepository

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(mqttPort: Int = 1883) {

    install(ContentNegotiation) {
        json()
    }

    val repository = EventRepository()
    val gameEventHandler = GameEventHandler(repository)
    val eventService = EventService(handler = gameEventHandler)
    val client = MessagingClient(eventService, mqttPort)

    configureRouting(client)
}
