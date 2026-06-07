package nl.vanalphenict.web.view

import kotlin.collections.forEach
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.dd
import kotlinx.html.dl
import kotlinx.html.dt
import kotlinx.html.img
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.ul
import nl.vanalphenict.model.Platform
import nl.vanalphenict.model.Team
import nl.vanalphenict.model.TeamSide
import nl.vanalphenict.web.SSE_EVENT_TYPE

fun playersHtml(team: Team) = createHTML().body { renderPlayers(team) }

fun HtmlBlockTag.teamOfPlayers(team: Team) = ul {
    val side = TeamSide.get(team.teamNum)
    classes = setOf(side.sideName)
    attributes["hx-swap"] = "innerHTML"
    attributes["sse-swap"] =
        if (side.equals(TeamSide.HOME)) SSE_EVENT_TYPE.PLAYERS_HOME.toString()
        else if (side.equals(TeamSide.AWAY)) SSE_EVENT_TYPE.PLAYERS_AWAY.toString() else ""
    renderPlayers(team)
}

fun HtmlBlockTag.renderPlayers(team: Team) {
    team.players
        .sortedBy { it.score * -1 }
        .forEach { player ->
            li {
                classes = setOf("player")
                img {
                    if (player.bot) {
                        src = "web/icons/bot.png"
                    } else if (player.avatar != null) {
                        src = player.avatar!!
                    } else {
                        src = "web/icons/user.png"
                        classes = setOf(Platform.getByPlayerId(player.id).className)
                    }
                }
                span {
                    classes = setOf("score")
                    +player.score.toString()
                }

                span {
                    classes = setOf("name")
                    +player.name
                }
            }
            li {
                classes = setOf("playerstats")
                dl {
                    dt { +"Go:" }
                    dd { +"${player.goals}" }
                    dt { +"Sh:" }
                    dd { +"${player.shots}" }
                    dt { +"As:" }
                    dd { +"${player.assists}" }
                    dt { +"Sa:" }
                    dd { +"${player.saves}" }
                    dt { +"De:" }
                    dd { +"${player.demos}" }
                }
            }
        }
}
