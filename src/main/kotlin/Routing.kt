package nl.vanalphenict

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import nl.vanalphenict.messaging.MessagingClient


@Serializable
@JsonIgnoreUnknownKeys
data class MessageLine(val topic: String, val timestamp: String)


fun Application.configureRouting(client: MessagingClient) {
    routing {
        get("/") {
            call.respondText("Hello world!")
        }
        post("/") {
            val msg = call.receive<String>()
            val line = Json.decodeFromString<MessageLine>(msg)
            client.send(line.topic, msg)
            call.respond(HttpStatusCode.OK)
        }
    }
}
