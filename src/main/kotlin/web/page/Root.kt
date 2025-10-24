package nl.vanalphenict.web.page

import io.ktor.server.html.Template
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.option
import kotlinx.html.script
import kotlinx.html.select
import kotlinx.html.stream.createHTML
import kotlinx.html.styleLink
import kotlinx.html.ul
import nl.vanalphenict.services.Theme
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.view.scoreBoard
import nl.vanalphenict.web.view.teamColorStyle

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
                teamColorStyle()
            }
            body {
                attributes["hx-ext"] = "sse"
                attributes["sse-connect"] = "/sse"
                h1 {
                    +"Rocket League Announcer"
                }

                div {
                    classes = setOf("select")
                        +"Announcer theme: "
                    themeSelect(themeService.themes, themeService.selectedTheme)
                }

                ul {
                    id = "actionlist"
                    attributes["sse-swap"] = "new_action"
                    attributes["hx-swap"] = "afterbegin"
                    attributes["hx-target"] = "#actionlist"
                }

                div { id = "fader" }

                scoreBoard()


                val htmx = { e: String -> "assets/htmx.org/dist/$e" }
                script(src = htmx("htmx.js")) {}
                script(src = htmx("ext/json-enc.js")) {}
                script(src = "assets/htmx-ext-sse/dist/sse.js") {}
            }
        }
    }
}

fun themeSelectHtml(themes: List<Theme>, selectedTheme: Theme) =
    createHTML().body { themeSelect(themes, selectedTheme) }

fun HtmlBlockTag.themeSelect(themes: List<Theme>, selectedTheme: Theme) {
    select {
        attributes["hx-post"] = "/themes"
        attributes["hx-trigger"] = "change"
        attributes["hx-swap"] = "outerHTML"
        attributes["sse-swap"] = SSE_EVENT_TYPE.SWITCH_THEME.asString()
        id = "announcerSelect"
        name = "announcerSelect"

        themes.forEach { theme ->
            option {
                value = theme.id.toString()
                selected = (selectedTheme == theme)
                +theme.title
            }
        }
    }
}

