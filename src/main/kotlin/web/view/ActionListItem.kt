package nl.vanalphenict.web.view

import kotlin.time.Duration
import kotlin.time.Instant
import kotlinx.html.HtmlBlockTag
import kotlinx.html.LI
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.visit
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.utility.TimeUtils.Companion.toGameString

fun HtmlBlockTag.actionListItem(
    actionItem: Pair<Instant, StatMessage>,
    timeLeft: Duration,
    overtime: Boolean,
) {
    li {
        classes = setOf("py-3", "sm:py-4")

        div {
            classes = setOf("flex", "items-center", "space-x-4", "rtl:space-x-reverse>")
            div {
                classes = setOf("shrink-0")
                img {
                    classes = setOf("w-8", "h-8", "rounded-full")
                    src = "web/icons/${actionItem.second.event.eventName}.webp"
                }
            }
            div {
                classes = setOf("flex-1", "min-w-0")
                val msg = actionItem.second
                p {
                    classes =
                        setOf(
                            "text-sm",
                            "font-medium",
                            "text-gray-900",
                            "truncate",
                            "dark:text-white",
                        )
                    msg.player.team.let {
                        span {
                            style =
                                "color: rgb(${it.primaryColor.red},${it.primaryColor.green},${it.primaryColor.blue});"
                            +"[${it.name}] "
                        }
                    }

                    +msg.player.name
                }
                p {
                    classes = setOf("text-sm", "text-gray-500", "truncate", "dark:text-gray-400")
                    +msg.event.eventName
                }
                if (msg is KillMessage) {
                    p {
                        classes = setOf("text-sm", "text-gray-500", "truncate", "dark:text-red-400")
                        +msg.victim.name
                    }
                }
            }
            div {
                classes =
                    setOf(
                        "inline-flex",
                        "items-center",
                        "text-base",
                        "font-semibold",
                        "text-gray-900",
                        "dark:text-white",
                    )

                span {
                    style = "color: RED;"
                    +if (overtime) {
                        "+"
                    } else {
                        ""
                    }
                }

                +timeLeft.toGameString
            }
        }
    }
}

fun HtmlBlockTag.li(e: LI.() -> Unit) =
    LI(initialAttributes = mutableMapOf(), consumer = consumer).visit(e)
