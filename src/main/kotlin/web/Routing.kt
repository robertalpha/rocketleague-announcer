package nl.vanalphenict.web

import com.janoz.discord.SampleService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtmlTemplate
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import nl.vanalphenict.messaging.MessagingClient
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.page.Root

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MessageLine(val topic: String, val message: String)

fun Application.configureRouting(
    client: MessagingClient,
    themeService: ThemeService,
    sampleService: SampleService,
) {
    routing {
        get("/") { call.respondHtmlTemplate(Root.LayoutTemplate(themeService, sampleService)) {} }

        sse("/heartbeat") {
            heartbeat {
                period = 3000.milliseconds
                event = ServerSentEvent("heartbeat")
            }
        }

        staticResources("/web", "web")

        post("/") {
            val msg = call.receive<String>()
            val line = Json.decodeFromString<MessageLine>(msg)
            client.send(line.topic, line.message)
            call.respond(HttpStatusCode.OK)
        }
    }
}
