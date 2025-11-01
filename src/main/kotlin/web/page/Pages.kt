package nl.vanalphenict.web.page

import com.janoz.discord.SampleService
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.styleLink
import kotlinx.html.ul
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.view.scoreBoard
import nl.vanalphenict.web.view.themeElement

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

        div {
            classes = setOf("select")
            +"Announcer theme: "
            themeElement(themeService.themes, themeService.selectedTheme)
        }

        div {
            classes = setOf("content")
            div {
                classes = setOf("optional")
                sampleService.samples.forEach { sample ->
                    button {
                        attributes["hx-put"] = "/play"
                        attributes["hx-vals"] = "{\"sample\":\"${sample.id}\"}"
                        attributes["hx-swap"] = "none"

                        span { +sample.name }
                    }
                    +" "
                }
                div { classes = setOf("spacer") }
            }
            ul {
                id = "actionlist"
                attributes["sse-swap"] = "new_action"
                attributes["hx-swap"] = "afterbegin"
                attributes["hx-target"] = "#actionlist"
            }
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

        script(src = "assets/htmx.org/dist/htmx.js") {}
        script(src = "assets/htmx.org/dist/ext/json-enc.js") {}
        script(src = "assets/htmx-ext-sse/dist/sse.js") {}
    }
}
