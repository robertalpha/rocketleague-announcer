package nl.vanalphenict.web.page

import io.ktor.server.html.Placeholder
import io.ktor.server.html.PlaceholderList
import io.ktor.server.html.Template
import io.ktor.server.html.TemplatePlaceholder
import io.ktor.server.html.each
import io.ktor.server.html.insert
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.UL
import kotlinx.html.article
import kotlinx.html.b
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.styleLink
import nl.vanalphenict.services.ThemeService

class Root {

    class LayoutTemplate(val themeService: ThemeService) : Template<HTML> {

        override fun HTML.apply() {
            head {
                styleLink(href="web/style/style.css", rel="stylesheet", type="text/css")
                styleLink(href="https://fonts.googleapis.com", type="text/css", rel="preconnect")
                styleLink(href="https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap", rel="stylesheet")

            }
            body {
                div {
                    h1 {
                        + "Rocket League Announcer"
                    }

                    p {
                        + "Theme:"
                    }
                    div {
                        attributes["hx-ext"] = "sse"
                        attributes["sse-connect"] = "/sse"
                        attributes["sse-swap"] = "switch_theme"
                        div { renderThemes(themeService.themes, themeService.selectedTheme) }
                    }

                    div { renderActions() }
                }

                val htmx = { e: String -> "assets/htmx.org/dist/$e" }
                script(src = htmx("htmx.js")) {}
                script(src = htmx("ext/json-enc.js")) {}
                script(src = "assets/htmx-ext-sse/dist/sse.js") {}
            }
        }
    }
}
