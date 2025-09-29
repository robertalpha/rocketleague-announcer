package nl.vanalphenict.page

import io.github.allangomes.kotlinwind.css.kw
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receive
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
import kotlinx.html.style
import nl.vanalphenict.services.Theme
import nl.vanalphenict.services.ThemeService

fun Application.themeRoutes(themeService: ThemeService) {
    routing {
        // select a theme
        post("/themes") {
            val themeId = call.receive<String>().also {
                val id = it.substringAfter("=")
                themeService.selectTheme(id)
            }

            call.respondHtml {
                body {
                    div {
                        renderThemes(themeService.themes,themeService.selectedTheme.id)
                    }
                }
            }

            // TODO: handle sse
//            triggerSSE(id)
        }

        // read themes
        get("/themes") {
            call.respondHtml {
                body {
                    div {
                        renderThemes(themeService.themes, themeService.selectedTheme.id)
                    }
                }
            }
        }
    }


}

 fun DIV.renderThemes(themes: List<Theme>, selectedTheme: String) {

     style = kw.inline { flex }
     role = "group"

     id = "themes-div"

    val baseCss = setOf(
        "h-10",
        "px-5",
        "text-indigo-100",
        "transition-colors",
        "duration-150",
        "focus:shadow-outline",
        "hover:bg-pink-500"
    )

    themes.sortedBy { theme -> theme.title }.forEachIndexed { index, theme ->
        button {
            classes = (when (index) {
                0 -> baseCss + "rounded-l-lg"
                themes.size - 1 -> baseCss + "rounded-r-lg"
                else -> baseCss
            } + (if (selectedTheme==theme.id) "bg-pink-700" else "bg-indigo-700")
            ).filter { it.isNotBlank() }.toSet()

            value = theme.id
            attributes["hx-post"] = "/themes"
            attributes["hx-vals"] = "{\"id\":\"${theme.id}\"}"
            attributes["hx-swap"] = "outerHTML"
            attributes["hx-target"] ="#themes-div"
            +theme.title
        }
    }
}

