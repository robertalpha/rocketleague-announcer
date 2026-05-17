package nl.vanalphenict.web.view

import kotlin.collections.forEach
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.dl
import kotlinx.html.dt
import kotlinx.html.img
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.ul
import nl.vanalphenict.model.Platform
import nl.vanalphenict.model.Player
import nl.vanalphenict.web.SSE_EVENT_TYPE

fun playersHtml(players: List<Player>) = createHTML().body { renderPlayers(players) }

fun HtmlBlockTag.teamOfPlayers(players: List<Player>, side: Int) = ul {
    classes = setOf(if (side == 0) "home" else "away")
    attributes["hx-swap"] = "innerHTML"
    attributes["sse-swap"] =
        if (side == 0) SSE_EVENT_TYPE.PLAYERS_HOME.toString()
        else SSE_EVENT_TYPE.PLAYERS_AWAY.toString()
    renderPlayers(players)
}

fun HtmlBlockTag.renderPlayers(players: List<Player>) {
    players.forEach { player ->
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
                dl { +"${player.goals}" }
                dt { +"Sh:" }
                dl { +"${player.shots}" }
                dt { +"As:" }
                dl { +"${player.assists}" }
                dt { +"Sa:" }
                dl { +"${player.saves}" }
                dt { +"To:" }
                dl { +"${player.touches}" }
                dt { +"Bu:" }
                dl { +"${player.carTouches}" }
                dt { +"De:" }
                dl { +"${player.demos}" }
            }
        }
    }
}
