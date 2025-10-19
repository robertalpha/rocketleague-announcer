package nl.vanalphenict.model

import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Color
import kotlin.String
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val log = KotlinLogging.logger {  }

fun parseTeam(src : JsonTeam) : Team = Team(src,null)

fun parsePlayer(src : JsonPlayer) : Player {
    val team = src.team?.let {Team(it, src) }
    return team?.players?.first() ?: throw RuntimeException ("Unable to parse player")
}

fun parseGameEventMessage(src : JsonGameEventMessage) : GameEventMessage? {
    val event = GameEvents.entries.find{ it.eq(src.gameEvent)}
    if (event == null) {
        log.info { "Event \"${src.gameEvent}\" not supported." }
        return null
    }
    return GameEventMessage(
        src.matchGUID,
        event,
        src.teams?.map { parseTeam(it) } ?:emptyList())
}

fun parseGameTimeMessage(src : JsonGameTimeMessage) : GameTimeMessage {
    return GameTimeMessage(
        matchGUID = src.matchGUID,
        remaining = src.remaining.seconds,
        overtime = src.overtime
    )
}

fun parseStatMessage(src : JsonStatMessage) : StatMessage? {
    val event = StatEvents.entries.find{ it.eq(src.event)}
    if (event == null) {
        log.info { "Event \"${src.event}\" not supported." }
        return null
    }
    return if (event == StatEvents.DEMOLISH)
        KillMessage(
            matchGUID = src.matchGUID,
            event = event,
            player =parsePlayer(src.player),
            victim = parsePlayer(src.victim!!)
        )
    else StatMessage(
            matchGUID = src.matchGUID,
            event = event,
            player =parsePlayer(src.player)
        )
}







data class GameTimeMessage(
    val matchGUID: String,
    val remaining: Duration,
    val overtime: Boolean
)

data class GameEventMessage(
    val matchGUID: String,
    val gameEvent: GameEvents,
    val teams: List<Team> = ArrayList()
)

open class StatMessage(
    open val matchGUID: String,
    open val event: StatEvents,
    open val player: Player,
)

data class KillMessage(
        override val matchGUID: String,
        override val event: StatEvents,
        override val player: Player,
        val victim: Player
) : StatMessage(matchGUID, event, player)

class Player (src: JsonPlayer, val team:Team) {
    val id: String = src.botSaveId()
    val name: String = src.name
    val bot: Boolean = src.isBot()
}

class Team (src:JsonTeam, srcPlayer:JsonPlayer?) {
    val homeTeam: Boolean = src.homeTeam ?: false
    val score: Int = src.score ?: 0
    val primaryColor: Color = src.primaryColor?.let { toColor(it) } ?: Color.BLACK
    val secondaryColor: Color = src.secondaryColor?.let {toColor(it)} ?: Color.WHITE
    val name: String = if ((src.name != null) && (src.name!= "Team")) src.name
            else if (primaryColor == BLUE) "Team Blue"
            else if (primaryColor == ORANGE) "Team Orange"
            else "Unknown"
    val players: List<Player> = (sequenceOf(srcPlayer) +
                (src.players?.asSequence() ?: emptySequence()))
            .filterNotNull()
            .map { Player(it,this) }
            .toList()

}

fun toColor(src:JsonColor):Color = Color(src.R, src.G, src.B)

val ORANGE = Color(194, 100, 24)
val BLUE = Color(24, 115, 255)