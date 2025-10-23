package nl.vanalphenict.web.view

import kotlinx.html.HtmlBlockTag
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.style
import nl.vanalphenict.model.Team
import nl.vanalphenict.utility.ColorUtils.Companion.toHexString
import nl.vanalphenict.utility.TimeUtils.Companion.toGameString
import kotlin.time.Duration

fun HtmlBlockTag.scoreBoard(
    homeTeam: Team,
    awayTeam: Team,
    timeLeft: Duration,
    overtime: Boolean,
) {
    div {
        classes = setOf("scoreboard")
        div {
            classes = setOf("center")
            +"VS"
            div {
                classes = if (overtime) setOf("overtime") else setOf()
                + timeLeft.toGameString
            }
        }

        div {
            classes = setOf("left")
            style = "background: linear-gradient(0deg, ${homeTeam.secondaryColor.darker().toHexString()} 0%, ${homeTeam.secondaryColor.brighter().toHexString()} 100%);"
            div {
                classes = setOf("team")
                style = "background: linear-gradient(0deg, ${homeTeam.primaryColor.darker().toHexString()} 0%, ${homeTeam.primaryColor.brighter().toHexString()} 100%);"
                span {
                    classes = setOf("name")
                    +homeTeam.name
                }
                span {
                    classes = setOf("tag")
                    +homeTeam.tag
                }
            }
            div {
                classes = setOf("score")
                div {
                    span {
                        +homeTeam.score.toString()
                    }
                }
            }
        }

        div {
            classes = setOf("right")
            style = "background: linear-gradient(0deg, ${awayTeam.secondaryColor.darker().toHexString()} 0%, ${awayTeam.secondaryColor.brighter().toHexString()} 100%);"
            div {
                classes = setOf("team")
                style = "background: linear-gradient(0deg, ${awayTeam.primaryColor.darker().toHexString()} 0%, ${awayTeam.primaryColor.brighter().toHexString()} 100%);"
                span {
                    classes = setOf("name")
                    +awayTeam.name
                }
                span {
                    classes = setOf("tag")
                    +awayTeam.tag
                }
            }
            div {
                classes = setOf("score")
                div {
                    span {
                        +awayTeam.score.toString()
                    }
                }
            }
        }
    }
}
