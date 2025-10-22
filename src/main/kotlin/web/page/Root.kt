package nl.vanalphenict.web.page

import io.ktor.server.html.Template
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.select
import kotlinx.html.styleLink
import kotlinx.html.ul
import nl.vanalphenict.services.ThemeService

class Root {

    class LayoutTemplate(val themeService: ThemeService) : Template<HTML> {

        override fun HTML.apply() {
            head {
                styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
                styleLink(href = "https://fonts.googleapis.com", type = "text/css", rel = "preconnect")
                styleLink(
                    href = "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
                    rel = "stylesheet"
                )

            }
            body {
                h1 {
                    +"Rocket League Announcer"
                }

                div {
                    classes = setOf("select")
                        +"Announcer theme: "
                    select {
                        attributes["hx-post"] = "/themes"
                        attributes["hx-vals"] = "{\"id\": \"#announcerSelect.value\"}"
                        attributes["hx-swap"] = "outerHTML"
                        attributes["hx-trigger"] = "change"
                        id = "announcerSelect"
                        themeService.themes.forEach { theme ->
                            option {
                                value = theme.id.toString()
                                selected = themeService.selectedTheme == theme
                                +theme.title
                            }
                        }
                    }
                }



                p {
                    +"Theme:"
                }



                div {
                    attributes["hx-ext"] = "sse"
                    attributes["sse-connect"] = "/sse"
                    attributes["sse-swap"] = "switch_theme"
                    div { renderThemes(themeService.themes, themeService.selectedTheme) }
                }

                ul {
                    id = "actionlist"

                    attributes["hx-ext"] = "sse"
                    attributes["sse-connect"] = "/sse"
                    attributes["sse-swap"] = "new_action"
                    attributes["hx-swap"] = "afterbegin"
                    attributes["hx-target"] = "#actionlist"
                }

                val htmx = { e: String -> "assets/htmx.org/dist/$e" }
                script(src = htmx("htmx.js")) {}
                script(src = htmx("ext/json-enc.js")) {}
                script(src = "assets/htmx-ext-sse/dist/sse.js") {}
            }
        }
    }
}
