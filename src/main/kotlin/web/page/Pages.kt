package nl.vanalphenict.web.page

import com.janoz.discord.SampleService
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.styleLink
import kotlinx.html.unsafe
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.view.scoreBoard
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
                soundboard(sampleService)
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

fun HTML.rosterpage(themeService: ThemeService, sampleService: SampleService) {
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
                +"away"
            }

            div {
                classes = setOf("veryOptional")
                +"Very optional"
            }

            div {
                classes = setOf("optional")
                +"home"
            }
        }

        div { id = "fader" }

        scoreBoard()

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
    }
}

private fun HtmlBlockTag.soundboard(sampleService: SampleService) = div {
    sampleService.packs.forEach { pack ->
        h2 { +pack.name }
        pack.samples
            .sortedBy { it.name }
            .forEach { sample ->
                button {
                    attributes["hx-put"] = "/play"
                    attributes["hx-vals"] = "{\"sample\":\"${sample.id}\"}"
                    attributes["hx-swap"] = "none"

                    span { +sample.name }
                }
                +" "
            }
    }
}

fun HTML.scoreboard() {
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

fun HTML.ticker(
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
        script(type = ScriptType.textJavaScript) {
            unsafe {
                raw(
                    "const fadeDelay = ${delay.inWholeMilliseconds};" +
                        "const fadeDuration = ${duration.inWholeMilliseconds};"
                )
            }
        }
        script(src = "web/script/ticker-fader.js") {}
    }
}

fun HTML.soundboard(sampleService: SampleService) {
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

        soundboard(sampleService)

        script(src = "assets/htmx.org/dist/htmx.min.js") {}
        script(src = "assets/htmx-ext-json-enc/2.0.2/dist/json-enc.min.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.min.js") {}
    }
}
