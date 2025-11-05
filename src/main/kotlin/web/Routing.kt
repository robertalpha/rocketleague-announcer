package nl.vanalphenict.web

import com.janoz.discord.SampleService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.time.Duration.Companion.milliseconds
import nl.vanalphenict.services.SamplePlayer
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.page.homepage
import nl.vanalphenict.web.view.themeHtml

fun Application.configureRouting(
    themeService: ThemeService,
    sampleService: SampleService,
    samplePlayer: SamplePlayer,
) {
    routing {
        // Homepage
        get("/") { call.respondHtml { homepage(themeService, sampleService) } }

        // Heartbeat
        sse("/heartbeat") {
            heartbeat {
                period = 3000.milliseconds
                event = ServerSentEvent("heartbeat")
            }
        }

        // Themeswitching
        post("/themes") {
            call.receive<String>().also {
                val id = it.substringAfter("=")
                themeService.selectTheme(id.toInt())
            }

            val htmlText = themeHtml(themeService.themes, themeService.selectedTheme)
            triggerUpdateSSE(SSE_EVENT_TYPE.SWITCH_THEME, htmlText)
            call.respond(HttpStatusCode.OK, message = htmlText)
        }

        // Soundboard action
        put(path = "/play") {
            call.receive<String>().also {
                val id = it.substringAfter("=")
                samplePlayer.play(URLDecoder.decode(id, StandardCharsets.UTF_8))
            }
            call.respond(HttpStatusCode.Accepted)
        }

        // Static resources
        staticResources("/web", "web")
        get("favicon.ico") {
            try {
                call.respondBytes { this::class.java.getResourceAsStream("/favicon.ico")?.readAllBytes()!! }
            } catch (_: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
