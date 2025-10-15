package nl.vanalphenict.web.page

import io.github.allangomes.kotlinwind.css.kw
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.html.DIV
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.role
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import nl.vanalphenict.services.Theme
import nl.vanalphenict.services.ThemeService
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerUpdateSSE

fun Application.themeRoutes(themeService: ThemeService) {
    routing {
        // select a theme
        post("/themes") {
            val themeId = call.receive<String>().also {
                val id = it.substringAfter("=")
                themeService.selectTheme(id.toInt())
            }

            val htmlText = createHTML().div {
                renderThemes(themeService.themes, themeService.selectedTheme)
            }
            triggerUpdateSSE(SSE_EVENT_TYPE.SWITCH_THEME, htmlText)
            call.respond(HttpStatusCode.OK)
        }

        // read themes
        get("/themes") {
            call.respondHtml {
                body {
                    div {
                        renderThemes(themeService.themes, themeService.selectedTheme)
                    }
                }
            }
        }
    }


}

fun DIV.renderThemes(themes: List<Theme>, selectedTheme: Theme) {

    style = kw.inline { flex }
    role = "group"

    id = "themes-div"
    attributes["hx-ext"] = "sse"
    attributes["sse-connect"] = "/sse"
    attributes["sse-swap"] = "switch_theme"

    val baseCss = setOf(
        "h-10",
        "px-5",
        "text-indigo-100",
        "transition-colors",
        "duration-150",
        "focus:shadow-outline",
        "hover:bg-pink-700"
    )

    themes.sortedBy { theme -> theme.title }.forEachIndexed { index, theme ->
        button {
            disabled = selectedTheme == theme
            classes = (
                    setOfNotNull(
                            "rounded-l-lg".takeIf { index == 0 },
                            "rounded-r-lg".takeIf { index == themes.size - 1 },
                            "bg-pink-700".takeIf { selectedTheme == theme },
                            "bg-indigo-700".takeIf { selectedTheme != theme }
                        ) + baseCss
                    )

            value = theme.title
            attributes["hx-post"] = "/themes"
            attributes["hx-vals"] = "{\"id\":\"${theme.id}\"}"
            +theme.title
        }
    }
}

