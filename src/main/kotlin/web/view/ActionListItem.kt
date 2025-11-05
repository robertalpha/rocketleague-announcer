package nl.vanalphenict.web.view

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
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.utility.ColorUtils.Companion.toHexString
import nl.vanalphenict.utility.TimeUtils.Companion.toGameString

fun HtmlBlockTag.actionListItem(message: StatMessage, metaData: RLAMetaData) {
    li {
        // no icons are associated with these:
        val hidden =
            message.event.eventName in
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
            src = "web/icons/${message.event.eventName}.webp"
            height = "50px"
            width = "50px"
        }

        div {
            classes = setOf("accent")
            style =
                "background: linear-gradient(0deg, ${message.player.team.secondaryColor.darker().toHexString()} 0%, ${message.player.team.secondaryColor.brighter().toHexString()} 100%);"
            div {
                classes = setOf("team")
                style =
                    "background: linear-gradient(0deg, ${message.player.team.primaryColor.darker().toHexString()} 0%, ${message.player.team.primaryColor.brighter().toHexString()} 100%);"
                span { +message.player.team.tag }
            }
        }
        div {
            classes = setOf("info")
            span {
                classes = setOf("time")
                if (metaData.overtime) {
                    span { +"+" }
                }
                +metaData.remaining.toGameString
            }
            span {
                classes = setOf("name")
                +message.player.name
            }
            br
            span {
                classes = setOf("info")
                if (message is KillMessage) {
                    +"Demolished "
                    b { +message.victim.name }
                } else {
                    +message.event.eventName
                }
            }
            br
            span {
                classes = setOf("announcements")
                metaData.prevailingAnnouncement?.let {
                    b { +it.name }
                    +" / "
                }
                +metaData.announcements
                    .filter { it != metaData.prevailingAnnouncement }
                    .joinToString(" / ") { it.name }
            }
        }
    }
}

fun HtmlBlockTag.li(e: LI.() -> Unit) =
    LI(initialAttributes = mutableMapOf(), consumer = consumer).visit(e)
