package nl.vanalphenict.web.view

import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import nl.vanalphenict.model.JsonTeam
import nl.vanalphenict.model.Team
import nl.vanalphenict.utility.ColorUtils.Companion.toHexString
import nl.vanalphenict.utility.TimeUtils.Companion.toGameString
import nl.vanalphenict.web.SSE_EVENT_TYPE
import kotlin.time.Duration

fun timeRemainingHtml(remaining: Duration?, overtime: Boolean = false) =
    createHTML().body { timeRemaining(remaining, overtime) }

fun HtmlBlockTag.timeRemaining(remaining: Duration?, overtime: Boolean = false) = div {
    div {
        attributes["hx-swap"] = "outerHTML"
        attributes["sse-swap"] = SSE_EVENT_TYPE.GAME_TIME.asString()
        classes = if (overtime) setOf("overtime") else emptySet()
        + (remaining?.toGameString ?: "--:--")
    }
}



fun HtmlBlockTag.scoreBoard(homeTeam: Team = emptyTeam(true), awayTeam: Team = emptyTeam(false)) = div {
    div {
        classes = setOf("scoreboard")
        div {
            classes = setOf("center")
            +"VS"
            timeRemaining(null)
        }
        renderTeamInfo(homeTeam)
        renderTeamInfo(awayTeam)
    }
}

fun teamInfoHtml(team: Team) =
    createHTML().body { renderTeamInfo(team) }

fun HtmlBlockTag.renderTeamInfo(team: Team) = div {
    attributes["hx-swap"] = "outerHTML"
    attributes["sse-swap"] = (if (team.homeTeam) SSE_EVENT_TYPE.HOME_TEAM else SSE_EVENT_TYPE.AWAY_TEAM).asString()
    style = """
                background: linear-gradient(0deg, 
        ${team.secondaryColor.darker().toHexString()} 0%, 
        ${team.secondaryColor.brighter().toHexString()} 100%);

    """.trimIndent()

    classes = if (team.homeTeam) setOf("left") else setOf("right")
    div {
        classes = setOf("team")
        style = """
                    background: linear-gradient(0deg, 
        ${team.primaryColor.darker().toHexString()} 0%, 
        ${team.primaryColor.brighter().toHexString()} 100%);
        """.trimIndent()

        span {
            classes = setOf("name")
            +team.name
        }
        span {
            classes = setOf("tag")
            +team.tag
        }
    }

    div {
        classes = setOf("score")
        div {
            span {
                + if (team.score == -1) "-" else team.score.toString()
            }
        }
    }
}


fun emptyTeam(homeTeam: Boolean) = Team (
    JsonTeam(
        homeTeam = homeTeam,
        clubId = -1,
        score = -1),null)

