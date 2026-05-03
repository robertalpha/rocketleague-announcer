package nl.vanalphenict.model

import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Color
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val log = KotlinLogging.logger {}

// ── Parse: GameEvent (simple events that map to GameEvents enum) ────────────

fun parseGameEventMessage(event: String, matchGuid: String, teams: List<JsonTeam> = emptyList()): GameEventMessage? {
    val gameEvent = GameEvents.entries.find { it.eq(event) }
    if (gameEvent == null) {
        log.info { "Event \"$event\" not supported." }
        return null
    }
    return GameEventMessage(matchGuid, gameEvent, teams.map { parseTeam(it) })
}

// ── Parse: ClockUpdatedSeconds → GameTimeMessage ────────────────────────────

fun parseClockUpdatedSeconds(src: JsonClockUpdatedSecondsData): GameTimeMessage {
    return GameTimeMessage(
        matchGUID = src.matchGuid,
        remaining = src.timeSeconds.seconds,
        overtime = src.overtime,
    )
}

// ── Parse: StatfeedEvent → StatMessage / KillMessage ────────────────────────

fun parseStatfeedEvent(src: JsonStatfeedEventData): StatMessage? {
    val event = StatEvents.entries.find { it.eq(src.eventName) }
    if (event == null) {
        log.info { "Event \"${src.eventName}\" not supported." }
        return null
    }
    val playerTeam = teamForNum(src.mainTarget.teamNum)
    val player = parsePlayerRef(src.mainTarget, playerTeam)
    return if (event == StatEvents.DEMOLISH && src.secondaryTarget != null) {
        val victimTeam = teamForNum(src.secondaryTarget.teamNum)
        KillMessage(
            matchGUID = src.matchGuid,
            event = event,
            player = player,
            victim = parsePlayerRef(src.secondaryTarget, victimTeam),
        )
    } else {
        StatMessage(matchGUID = src.matchGuid, event = event, player = player)
    }
}

// ── Parse helpers ───────────────────────────────────────────────────────────

fun parsePlayerRef(src: JsonPlayerRef, team: Team): Player {
    val id = "ref|${src.name}|${src.teamNum}"
    return Player(id = id, name = src.name, bot = false, team = team)
}

fun parsePlayerFull(src: JsonPlayerFull, team: Team): Player {
    return Player(id = src.botSaveId(), name = src.name, bot = src.isBot(), team = team)
}

fun parseTeam(src: JsonTeam): Team {
    val primaryColor = hexToColor(src.colorPrimary)
    val secondaryColor = hexToColor(src.colorSecondary)
    return Team(
        teamNum = src.teamNum,
        score = src.score,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        name = src.name.ifEmpty {
            when (src.teamNum) {
                0 -> "TEAM BLUE"
                1 -> "TEAM ORANGE"
                else -> "Opponent"
            }
        },
        tag = when (src.teamNum) {
            0 -> "BLUE"
            1 -> "ORNG"
            else -> "TAG"
        },
    )
}

fun teamForNum(teamNum: Int): Team {
    return Team(
        teamNum = teamNum,
        name = when (teamNum) {
            0 -> "TEAM BLUE"
            1 -> "TEAM ORANGE"
            else -> "Opponent"
        },
        primaryColor = when (teamNum) {
            0 -> BLUE
            1 -> ORANGE
            else -> GREY
        },
        secondaryColor = when (teamNum) {
            0 -> BLUE
            1 -> ORANGE
            else -> DARK_GREY
        },
        tag = when (teamNum) {
            0 -> "BLUE"
            1 -> "ORNG"
            else -> "TAG"
        },
    )
}

// ── Domain classes ──────────────────────────────────────────────────────────

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
    val teamNum: Int = -1,
    val score: Int = -1,
    val primaryColor: Color = GREY,
    val secondaryColor: Color = DARK_GREY,
    val name: String = "-",
    val tag: String = "-",
    val players: List<Player> = emptyList(),
) {
    val homeTeam: Boolean get() = teamNum == 0
}

fun hexToColor(hex: String): Color {
    if (hex.isBlank() || hex.length < 6) return GREY
    return try {
        Color(
            hex.substring(0, 2).toInt(16),
            hex.substring(2, 4).toInt(16),
            hex.substring(4, 6).toInt(16),
        )
    } catch (_: Exception) {
        GREY
    }
}

val ORANGE = Color(194, 100, 24)
val BLUE = Color(24, 115, 255)
val GREY = Color(128, 128, 128)
val DARK_GREY = Color(229, 229, 229)
