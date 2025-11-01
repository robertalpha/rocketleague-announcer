package nl.vanalphenict.web.view

import kotlin.collections.forEach
import kotlin.collections.set
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import nl.vanalphenict.services.Theme
import nl.vanalphenict.web.SSE_EVENT_TYPE

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
