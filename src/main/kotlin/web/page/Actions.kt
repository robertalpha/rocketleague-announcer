package nl.vanalphenict.web.page

import kotlinx.html.DIV
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.ul

fun DIV.renderActions() {

    classes =
        setOf(
            "w-full",
            "h-128",
            "overflow-auto",
            "md:overflow-scroll",
            "mt-4",
            "text-center",
            "bg-white",
            "border",
            "border-gray-200",
            "rounded-lg",
            "shadow-sm",
            "sm:p-4",
            "dark:bg-gray-800",
            "dark:border-gray-700",
        )
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
