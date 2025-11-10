package nl.vanalphenict.model

import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Color
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlin.collections.get
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val log = KotlinLogging.logger {}

fun parseTeam(src: JsonTeam): Team = parseTeam(src, null)

fun parsePlayer(src: JsonPlayer): Player {
    val team = src.team?.let { parseTeam(it, src) }
    return team?.players?.first() ?: throw RuntimeException("Unable to parse player")
}

fun parseGameEventMessage(src: JsonGameEventMessage): GameEventMessage? {
    val event = GameEvents.entries.find { it.eq(src.gameEvent) }
    if (event == null) {
        log.info { "Event \"${src.gameEvent}\" not supported." }
        return null
    }
    return GameEventMessage(src.matchGUID, event, src.teams?.map { parseTeam(it) } ?: emptyList())
}

fun parseGameTimeMessage(src: JsonGameTimeMessage): GameTimeMessage {
    return GameTimeMessage(
        matchGUID = src.matchGUID,
        remaining = src.remaining.seconds,
        overtime = src.overtime,
    )
}

fun parseStatMessage(src: JsonStatMessage): StatMessage? {
    val event = StatEvents.entries.find { it.eq(src.event) }
    if (event == null) {
        log.info { "Event \"${src.event}\" not supported." }
        return null
    }
    return if (event == StatEvents.DEMOLISH)
        KillMessage(
            matchGUID = src.matchGUID,
            event = event,
            player = parsePlayer(src.player),
            victim = parsePlayer(src.victim!!),
        )
    else StatMessage(matchGUID = src.matchGUID, event = event, player = parsePlayer(src.player))
}

fun parsePlayer(src: JsonPlayer, team: Team) =
    Player(id = src.botSaveId(), name = src.name, bot = src.isBot(), team = team)

fun parseTeam(src: JsonTeam, srcPlayer: JsonPlayer?): Team {
    val primaryColor = src.primaryColor?.let { toColor(it) } ?: GREY
    val secondaryColor: Color = src.secondaryColor?.let { toColor(it) } ?: DARK_GREY
    val players: MutableList<Player> = ArrayList()
    val result =
        Team(
            homeTeam = src.homeTeam ?: false,
            score = src.score ?: 0,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            name =
                CLUB_MAP[src.clubId]?.name
                    ?: if (src.name != null && src.name != "") src.name
                    else
                        when (primaryColor) {
                            BLUE -> "TEAM BLUE"
                            ORANGE -> "TEAM ORANGE"
                            else -> "Opponent" //no default color and no club. Must be an opponent.
                        },
            tag =
                CLUB_MAP[src.clubId]?.tag
                    ?: when (primaryColor) {
                        BLUE -> "BLUE"
                        ORANGE -> "ORNG"
                        else -> "TAG"
                    },
            players = players,
        )
    (sequenceOf(srcPlayer) + (src.players.asSequence()))
        .filterNotNull()
        .map { parsePlayer(it, result) }
        .forEach { players.add(it) }
    return result
}

data class GameTimeMessage(val matchGUID: String, val remaining: Duration, val overtime: Boolean)

data class GameEventMessage(
    val matchGUID: String,
    val gameEvent: GameEvents,
    val teams: List<Team> = ArrayList(),
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
    val victim: Player,
) : StatMessage(matchGUID, event, player)

data class Player(val id: String, val name: String, val bot: Boolean, val team: Team)

data class Team(
    val homeTeam: Boolean,
    val score: Int = -1,
    val primaryColor: Color = GREY,
    val secondaryColor: Color = DARK_GREY,
    val name: String = "-",
    val tag: String = "-",
    val players: List<Player> = emptyList(),
)

fun toColor(src: JsonColor): Color = Color(src.R, src.G, src.B)

val ORANGE = Color(194, 100, 24)
val BLUE = Color(24, 115, 255)
val GREY = Color(128, 128, 128)
val DARK_GREY = Color(229, 229, 229)
