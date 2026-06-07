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
import java.awt.Color
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.time.Duration.Companion.milliseconds
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.Team
import nl.vanalphenict.model.TeamSide
import nl.vanalphenict.model.getDefaultTeam
import nl.vanalphenict.repository.GameStateRepository
import nl.vanalphenict.services.SamplePlayer
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.page.homepage
import nl.vanalphenict.web.page.rosterpage
import nl.vanalphenict.web.page.scoreboardpage
import nl.vanalphenict.web.page.soundboardpage
import nl.vanalphenict.web.page.teamrosterpage
import nl.vanalphenict.web.page.tickerpage
import nl.vanalphenict.web.view.themeHtml

fun Application.configureRouting(
    themeService: ThemeService,
    sampleService: SampleService,
    samplePlayer: SamplePlayer,
    gameStateRepository: GameStateRepository,
) {

    val team = Team(teamNum = 0, name = "Je Moeder", tag = "JM")
    team.primaryColor =
        Color(Integer.parseInt("26", 16), Integer.parseInt("26", 16), Integer.parseInt("26", 16))
    team.secondaryColor =
        Color(Integer.parseInt("00", 16), Integer.parseInt("82", 16), Integer.parseInt("00", 16))

    team.players.add(
        Player(
            name = "Janoz",
            avatar =
                "https://avatars.fastly.steamstatic.com/c40994356218a539c42d6a1d147086351512c285_full.jpg",
            id = "Steam|1234567890123456|0",
            shortcut = 1,
            teamNum = 0,
            bot = false,
            team = team,
            score = 1999,
        )
    )

    team.players.add(
        Player(
            name = "Robert",
            avatar =
                "https://avatars.fastly.steamstatic.com/040340ff726945a83f3ed7046e948ab7f99e6d8f_full.jpg",
            id = "Steam|1234567890123456|1",
            shortcut = 2,
            teamNum = 0,
            bot = false,
            team = team,
            score = 12,
        )
    )

    team.players.add(
        Player(
            name = "JdeP",
            avatar =
                "https://avatars.fastly.steamstatic.com/5495be562c01700138d6dc48830358342a03d759_full.jpg",
            id = "Steam|1234567890123456|2",
            shortcut = 3,
            teamNum = 0,
            bot = false,
            team = team,
            score = 567,
        )
    )

    team.players.add(
        Player(
            name = "Someone",
            id = "Steam|1234567890123456|2",
            shortcut = 4,
            teamNum = 0,
            bot = false,
            team = team,
            score = 367,
        )
    )

    routing {
        // Homepage
        get("/") { call.respondHtml { homepage(themeService, sampleService) } }
        get("/roster") { call.respondHtml { rosterpage(themeService, gameStateRepository) } }

        // parts
        get("/parts-scoreboard") { call.respondHtml { scoreboardpage() } }
        get("/parts-ticker") { call.respondHtml { tickerpage() } }
        get("/parts-ticker-rev") { call.respondHtml { tickerpage(reversed = true) } }
        get("/parts-soundboard") { call.respondHtml { soundboardpage(sampleService) } }

        get("/parts-team-home") {
            call.respondHtml { teamrosterpage(getDefaultTeam(TeamSide.HOME.teamNum)) }
        }
        get("/parts-team-away") {
            call.respondHtml { teamrosterpage(getDefaultTeam(TeamSide.AWAY.teamNum)) }
        }

        get("/parts-team-away") {
            call.respondHtml { teamrosterpage(getDefaultTeam(TeamSide.AWAY.teamNum)) }
        }

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
                val ids = it.substringAfter("=")
                val decoded = URLDecoder.decode(ids, StandardCharsets.UTF_8)
                if (decoded.contains(',')) {
                    samplePlayer.playSemiRandom(decoded.split(','))
                } else {
                    samplePlayer.play(decoded)
                }
            }
            call.respond(HttpStatusCode.Accepted)
        }

        // Static resources
        staticResources("/web", "web")
        get("favicon.ico") {
            try {
                call.respondBytes {
                    this::class.java.getResourceAsStream("/favicon.ico")?.readAllBytes()!!
                }
            } catch (_: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
