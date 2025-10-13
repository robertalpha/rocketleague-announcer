package nl.vanalphenict.web.page

import kotlinx.html.DIV
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.ul


fun DIV.renderActions() {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse"

        ul {
            classes = setOf("max-w-md","divide-y","divide-gray-200","dark:divide-gray-700")
            id = "actionListItem-list"

            attributes["hx-get"] = "/actions"
            attributes["hx-trigger"] = "load,sse:new_action"

            p(classes = "htmx-indicator") { +"Loading..." }
        }
    }
