package nl.vanalphenict

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.services.EventHandler
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.httpMethod
import io.ktor.server.webjars.Webjars
import nl.vanalphenict.services.EventRepository
import nl.vanalphenict.services.impl.EventPersister

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(mqttPort: Int = 1883) {

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

    val repository = EventRepository()
    val eventPersister = EventPersister(repository)
    val eventHandler = EventHandler.Builder(eventPersister).build()
    val client = MessagingClient(eventHandler, mqttPort)

    configureRouting(client)
}
