package nl.vanalphenict.model

import java.awt.Color
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import nl.vanalphenict.utility.ColorUtils

// ── Internal messages ───────────────────────────────────────────────────────
data class GameTimeMessage(val matchGuid: String, val remaining: Duration, val overtime: Boolean)

// ── GameEventMessages ───────────────────────────────────────────────────────
open class GameEventMessage(open val matchGuid: String, open val gameEvent: GameEvents)

data class GoalEventMessage(
    override val matchGuid: String,
    override val gameEvent: GameEvents,
    val goalSpead: Double,
    val goalTime: Double,
    val scorer: Player,
    val assister: Player?,
    val lastToucher: Player?,
    val touchSpeed: Double?,
) : GameEventMessage(matchGuid, gameEvent)

data class BallHitEventMessage(
    override val matchGuid: String,
    override val gameEvent: GameEvents,
    val playerss: List<Player>,
) : GameEventMessage(matchGuid, gameEvent)

// TODO maybe more specialized events

// ── StatMessages─────────────────────────────────────────────────────────────
open class StatMessage(
    open val matchGuid: String,
    open val event: StatEvents,
    open val player: Player,
)

data class KillMessage(
    override val matchGuid: String,
    override val event: StatEvents,
    override val player: Player,
    val victim: Player,
) : StatMessage(matchGuid, event, player)

// ── Game state ──────────────────────────────────────────────────────────────
data class Player(
    val name: String,
    var id: String,
    val shortcut: Int,
    val teamNum: Int,
    var bot: Boolean,
    var score: Int = -1,
    var goals: Int = -1,
    var shots: Int = -1,
    var assists: Int = -1,
    var saves: Int = -1,
    var touches: Int = -1,
    var carTouches: Int = -1,
    var demos: Int = -1,
    var hasCar: Boolean = false,
    var speed: Double = 0.0,
    var boost: Int = 0,
    var boosting: Boolean = false,
    var onGround: Boolean = false,
    var onWall: Boolean = false,
    var powersliding: Boolean = false,
    var demolished: Boolean = false,
    var supersonic: Boolean = false,
    var attacker: Player? = null,
    val team: Team,
    var contributor: Boolean = false,
)

data class Team(
    var name: String,
    val teamNum: Int = -1,
    var score: Int = -1,
    var primaryColor: Color = ColorUtils.GREY,
    var secondaryColor: Color = ColorUtils.DARK_GREY,
    val players: MutableList<Player> = ArrayList(),
    var hasContributors: Boolean = false,
)

fun getDefaultTeam(teamNum: Int) =
    Team(
        teamNum = teamNum,
        name = if (teamNum == 0) "BLUE" else "ORANGE",
        primaryColor = if (teamNum == 0) ColorUtils.BLUE else ColorUtils.ORANGE,
        secondaryColor = ColorUtils.DARK_GREY,
    )

data class Game(
    val matchGuid: String,
    var replay: Boolean = false,
    var paused: Boolean = false,
    var overtime: Boolean = false,
    var remaining: Duration = 0.seconds,
    val teams: MutableList<Team> = ArrayList(),
)
