package nl.vanalphenict.web.view

import kotlinx.html.HEAD
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import nl.vanalphenict.model.JsonTeam
import nl.vanalphenict.model.Team
import nl.vanalphenict.utility.ColorUtils.Companion.toHexString
import nl.vanalphenict.utility.TimeUtils.Companion.toGameString
import nl.vanalphenict.web.SSE_EVENT_TYPE
import kotlin.time.Duration

fun teamColorStyleHtml(homeTeam: Team = emptyTeam, awayTeam: Team = emptyTeam) =
    createHTML().head { teamColorStyle(homeTeam, awayTeam) }

fun HEAD.teamColorStyle(
        homeTeam: Team = emptyTeam,
        awayTeam: Team = emptyTeam) = style {
    attributes["hx-swap"] = "outerHTML"
    attributes["sse-swap"] = SSE_EVENT_TYPE.TEAM_COLORS.asString()

    + """
    div.scoreboard div.left {
        background: linear-gradient(0deg, 
        ${homeTeam.secondaryColor.darker().toHexString()} 0%, 
        ${homeTeam.secondaryColor.brighter().toHexString()} 100%);
    }
    
    div.scoreboard div.left div.team {
        background: linear-gradient(0deg, 
        ${homeTeam.primaryColor.darker().toHexString()} 0%, 
        ${homeTeam.primaryColor.brighter().toHexString()} 100%);
    }
    
    div.scoreboard div.right {
        background: linear-gradient(0deg, 
        ${awayTeam.secondaryColor.darker().toHexString()} 0%, 
        ${awayTeam.secondaryColor.brighter().toHexString()} 100%);
    }
    
    div.scoreboard div.right div.team {
        background: linear-gradient(0deg, 
        ${awayTeam.primaryColor.darker().toHexString()} 0%, 
        ${awayTeam.primaryColor.brighter().toHexString()} 100%);
    }
    """
}

fun teamNameHtml(team: Team = emptyTeam, event: SSE_EVENT_TYPE) =
    createHTML().body { teamName(team, event) }

fun HtmlBlockTag.teamName(team: Team = emptyTeam, event: SSE_EVENT_TYPE) = div {
    attributes["hx-swap"] = "outerHTML"
    attributes["sse-swap"] = event.asString()
    classes = setOf("team")
    span {
        classes = setOf("name")
        +team.name
    }
    span {
        classes = setOf("tag")
        +team.tag
    }
}


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



fun HtmlBlockTag.scoreBoard() {
    div {
        classes = setOf("scoreboard")
        div {
            classes = setOf("center")
            +"VS"
            timeRemaining(null)
        }

        div {
            classes = setOf("left")

            teamName( event =  SSE_EVENT_TYPE.HOME_NAME)

            div {
                classes = setOf("score")
                div {
                    span {
                        attributes["sse-swap"] = SSE_EVENT_TYPE.HOME_SCORE.asString()
                        + "-"
                    }
                }
            }
        }

        div {
            classes = setOf("right")

            teamName( event = SSE_EVENT_TYPE.AWAY_NAME)

            div {
                classes = setOf("score")
                div {
                    span {
                        attributes["sse-swap"] = SSE_EVENT_TYPE.AWAY_SCORE.asString()
                        + "-"
                    }
                }
            }
        }
    }
}



val emptyTeam = Team (
    JsonTeam(
        clubId = -1,
        score = 0),null)

