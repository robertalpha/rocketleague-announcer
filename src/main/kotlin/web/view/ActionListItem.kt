package nl.vanalphenict.web.view

import kotlin.time.Duration
import kotlinx.html.HtmlBlockTag
import kotlinx.html.LI
import kotlinx.html.b
import kotlinx.html.br
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.img
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.visit
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.utility.ColorUtils.Companion.toHexString
import nl.vanalphenict.utility.TimeUtils.Companion.toGameString

fun HtmlBlockTag.actionListItem(
    actionItem: StatRepository.StatMessageRecord,
    timeLeft: Duration,
    overtime: Boolean,
) {
    li {
        // no icons are associated with these:
        val hidden =
            actionItem.message.event.eventName in
                setOf(
                    "BoostPickups",
                    "SmallBoostsCollected",
                    "BoostUsed",
                    "Dodges",
                    "AerialHit",
                    "BigBoostsCollected",
                    "InfectedPlayersDefeated",
                    "TimePlayed",
                    "FastestGoal",
                    "DistanceDrivenMeters",
                    "DistanceFlown",
                    "DoubleGrapple",
                    "MaxDodgeStreak",
                )

        classes = if (hidden) setOf("hidden") else setOf("actionItem")

        img {
            src = "web/icons/${actionItem.message.event.eventName}.webp"
            height = "50px"
            width = "50px"
        }

        div {
            classes = setOf("accent")
            style =
                "background: linear-gradient(0deg, ${actionItem.message.player.team.secondaryColor.darker().toHexString()} 0%, ${actionItem.message.player.team.secondaryColor.brighter().toHexString()} 100%);"
            div {
                classes = setOf("team")
                style =
                    "background: linear-gradient(0deg, ${actionItem.message.player.team.primaryColor.darker().toHexString()} 0%, ${actionItem.message.player.team.primaryColor.brighter().toHexString()} 100%);"
                span { +actionItem.message.player.team.tag }
            }
        }
        div {
            classes = setOf("info")
            span {
                classes = setOf("time")
                if (overtime) {
                    span { +"+" }
                }
                +timeLeft.toGameString
            }
            span {
                classes = setOf("name")
                +actionItem.message.player.name
            }
            br
            span {
                classes = setOf("info")
                if (actionItem.message is KillMessage) {
                    +"Demolished "
                    b { +actionItem.message.victim.name }
                } else {
                    +actionItem.message.event.eventName
                }
            }
            br
            span {
                classes = setOf("announcements")
                actionItem.metatada.prevailingAnnouncement?.let {
                    b {
                        + it.name
                    }
                    +" / "
                }
                + actionItem.metatada.announcements
                    .filter { it != actionItem.metatada.prevailingAnnouncement }
                    .joinToString(" / ") { it.name }
            }
        }
    }
}

fun HtmlBlockTag.li(e: LI.() -> Unit) =
    LI(initialAttributes = mutableMapOf(), consumer = consumer).visit(e)
