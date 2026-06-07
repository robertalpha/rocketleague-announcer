package nl.vanalphenict.web.page

import com.janoz.discord.SampleService
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.html.HTML
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.script
import kotlinx.html.styleLink
import kotlinx.html.unsafe
import nl.vanalphenict.model.Team
import nl.vanalphenict.model.TeamSide
import nl.vanalphenict.model.getDefaultTeam
import nl.vanalphenict.repository.GameStateRepository
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.view.scoreBoard
import nl.vanalphenict.web.view.soundBoard
import nl.vanalphenict.web.view.teamOfPlayers
import nl.vanalphenict.web.view.themeSelector
import nl.vanalphenict.web.view.ticker

fun HTML.homepage(themeService: ThemeService, sampleService: SampleService) {
    head {
        styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
        styleLink(href = "https://fonts.googleapis.com", type = "text/css", rel = "preconnect")
        styleLink(
            href = "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
            rel = "stylesheet",
        )
    }
    body {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"
        h1 { +"Rocket League Announcer" }

        themeSelector(themeService)

        div {
            classes = setOf("content")
            div {
                classes = setOf("optional")
                soundBoard(sampleService)
                div { classes = setOf("spacer") }
            }

            ticker()

            div {
                classes = setOf("veryOptional")
                img {
                    src = "web/style/rl-logo-ds.png"
                    alt = "Rocket League logo"
                    width = "500px"
                }
            }
        }

        div { id = "fader" }

        scoreBoard()

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
    }
}

fun HTML.rosterpage(themeService: ThemeService, gameStateRepository: GameStateRepository) {
    head {
        styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
        styleLink(href = "https://fonts.googleapis.com", type = "text/css", rel = "preconnect")
        styleLink(
            href = "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
            rel = "stylesheet",
        )
    }
    body {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"
        h1 { +"Rocket League Announcer" }

        themeSelector(themeService)

        div {
            classes = setOf("content")
            div {
                classes = setOf("optional")
                teamOfPlayers(getDefaultTeam(TeamSide.AWAY))
            }

            div {
                classes = setOf("veryOptional")
                ticker()
            }

            div {
                classes = setOf("optional")
                teamOfPlayers(getDefaultTeam(TeamSide.HOME))
            }
        }

        div { id = "fader" }

        scoreBoard()

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
    }
}

fun HTML.scoreboardpage() {
    head {
        styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
        styleLink(href = "https://fonts.googleapis.com", type = "text/css", rel = "preconnect")
        styleLink(
            href = "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
            rel = "stylesheet",
        )
    }
    body {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"

        scoreBoard()

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
    }
}

fun HTML.tickerpage(
    reversed: Boolean = false,
    delay: Duration = 5.seconds,
    duration: Duration = 2.seconds,
) {
    head {
        styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
        styleLink(href = "https://fonts.googleapis.com", type = "text/css", rel = "preconnect")
        styleLink(
            href = "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
            rel = "stylesheet",
        )
    }
    body {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"

        ticker(reversed)

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
        if (delay > 0.seconds) {
            script(type = ScriptType.textJavaScript) {
                unsafe {
                    raw(
                        "const fadeDelay = ${delay.inWholeMilliseconds};" +
                            "const fadeDuration = ${duration.inWholeMilliseconds};"
                    )
                }
            }
        }
        script(src = "web/script/ticker-fader.js") {}
    }
}

fun HTML.soundboardpage(sampleService: SampleService) {
    head {
        styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
        styleLink(href = "https://fonts.googleapis.com", type = "text/css", rel = "preconnect")
        styleLink(
            href = "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
            rel = "stylesheet",
        )
    }
    body {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"

        soundBoard(sampleService)

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
    }
}

fun HTML.teamrosterpage(team: Team) {
    head {
        styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
        styleLink(href = "https://fonts.googleapis.com", type = "text/css", rel = "preconnect")
        styleLink(
            href = "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
            rel = "stylesheet",
        )
    }
    body {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"

        teamOfPlayers(team)

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
    }
}
