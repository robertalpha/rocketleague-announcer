package nl.vanalphenict.web.view

import kotlin.time.Instant
import kotlinx.html.BODY
import kotlinx.html.HtmlBlockTag
import kotlinx.html.LI
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.visit
import nl.vanalphenict.model.StatMessage

fun HtmlBlockTag.actionListItem(actionItem: Pair<Instant, StatMessage>) {
    li {
        classes=setOf("py-3","sm:py-4")

        div {
            classes = setOf("flex", "items-center", "space-x-4", "rtl:space-x-reverse>")
            div {
                classes = setOf("shrink-0")
                img {
                    classes = setOf("w-8", "h-8", "rounded-full")
                    src = "web/icons/${actionItem.second.event}.webp"
                }
            }
            div {
                classes = setOf("flex-1", "min-w-0")
                p {

                    classes = setOf("text-sm", "font-medium", "text-gray-900", "truncate", "dark:text-white")
                    actionItem.second.player.team?.let {
                        span {
                            style = if (it.primaryColor != null) {
                                "color: rgb(${it.primaryColor.R},${it.primaryColor.G},${it.primaryColor.B});"
                            } else ""
                            +"[${it.name}] "
                        }
                    }

                    +actionItem.second.player.name
                }
                p {
                    classes = setOf("text-sm", "text-gray-500", "truncate", "dark:text-gray-400")
                    +actionItem.second.event
                }
                actionItem.second.victim?.let {
                    p {
                        classes = setOf("text-sm", "text-gray-500", "truncate", "dark:text-red-400")
                        +actionItem.second.victim!!.name
                    }
                }
            }
            div {
                classes = setOf(
                    "inline-flex",
                    "items-center",
                    "text-base",
                    "font-semibold",
                    "text-gray-900",
                    "dark:text-white"
                )
                +actionItem.first.toString().drop(11).take(12)
            }
        }
    }
}
fun HtmlBlockTag.li(e: LI.() -> Unit) = LI(
    initialAttributes = mutableMapOf(),
    consumer = consumer
).visit(e)