package nl.vanalphenict.web.page

import io.ktor.server.html.Template
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.option
import kotlinx.html.script
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.styleLink
import kotlinx.html.ul
import nl.vanalphenict.services.Theme
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.view.scoreBoard

class Root {

    class LayoutTemplate(val themeService: ThemeService) : Template<HTML> {

        override fun HTML.apply() {
            head {
                styleLink(href = "web/style/style.css", rel = "stylesheet", type = "text/css")
                styleLink(
                    href = "https://fonts.googleapis.com",
                    type = "text/css",
                    rel = "preconnect",
                )
                styleLink(
                    href =
                        "https://fonts.googleapis.com/css2?family=Oxanium:wght@200..800&display=swap",
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
                        button { span { +"Aap" } }
                        button { span { +"Noot" } }
                        button { span { +"Mies" } }
                    }
                    ul {
                        id = "actionlist"
                        attributes["sse-swap"] = "new_action"
                        attributes["hx-swap"] = "afterbegin"
                        attributes["hx-target"] = "#actionlist"
                    }
                    div {
                        classes = setOf("veryOptional")
                        +"WHAT A WIDE SCREEN!!!!"
                    }
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

fun themeHtml(themes: List<Theme>, selectedTheme: Theme) =
    createHTML().body { themeElement(themes, selectedTheme) }

fun HtmlBlockTag.themeElement(themes: List<Theme>, selectedTheme: Theme) {
    if (themes.size < 4) themeButtons(themes, selectedTheme) else themeSelect(themes, selectedTheme)
}

fun HtmlBlockTag.themeButtons(themes: List<Theme>, selectedTheme: Theme) {
    div {
        attributes["hx-swap"] = "outerHTML"
        attributes["sse-swap"] = SSE_EVENT_TYPE.SWITCH_THEME.asString()
        id = "announcerButtons"
        themes.forEach { theme ->
            button {
                classes = if (selectedTheme == theme) setOf("selected") else emptySet()
                attributes["hx-post"] = "/themes"
                attributes["hx-vals"] = "{\"announcerSelect\":\"${theme.id}\"}"
                attributes["hx-swap"] = "none"

                span { +theme.title }
            }
        }
    }
}

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
