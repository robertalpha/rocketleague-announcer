package nl.vanalphenict.web.page

import kotlinx.html.DIV
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.ul

fun DIV.renderActions() {
    ul {
        classes = setOf("max-w-md", "divide-y", "divide-gray-200", "dark:divide-gray-700")
        id = "actionlist"

        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"
        attributes["sse-swap"] = "new_action"
        attributes["hx-swap"] = "afterbegin"
        attributes["hx-target"] = "#actionlist"

        div {
            attributes["hx-get"] = "/actions"
            attributes["hx-trigger"] = "load"

            classes = setOf("htmx-indicator", "text-gray-500")
            +"Loading..."
        }
    }
}
