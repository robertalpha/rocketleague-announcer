package nl.vanalphenict

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.page.Root
import nl.vanalphenict.services.ThemeService


@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MessageLine(val topic: String, val timestamp: String)


fun Application.configureRouting(client: MessagingClient, themeService: ThemeService) {
    routing {
        get("/") {
            call.respondHtmlTemplate(Root.LayoutTemplate(themeService)) {
                header {
                    +"RocketLeage Announcer"
                }
                content {
                    articleTitle {
                        +"Hello from Ktor!"
                    }
                    list {
                        item { +"One" }
                        item { +"Two" }
                    }
                }
            }
        }
        post("/") {
            val msg = call.receive<String>()
            val line = Json.decodeFromString<MessageLine>(msg)
            client.send(line.topic, msg)
            call.respond(HttpStatusCode.OK)
        }
    }
}
