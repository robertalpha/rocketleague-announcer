package nl.vanalphenict.model

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration

private val log = KotlinLogging.logger {}

data class RLAMetaData(
    val matchGuid: String,
    var prevailingAnnouncement: Announcement? = null,
    val announcements: MutableSet<Announcement> = HashSet(),
    val overtime: Boolean,
    val remaining: Duration,
)

enum class StatEvents(val eventName: String) {
    AERIAL_GOAL("AerialGoal"),
    ASSIST("Assist"),
    BACKWARDS_GOAL("BackwardsGoal"),
    BICYCLE_HIT("BicycleHit"),
    BICYCLE_GOAL("BicycleGoal"),
    BREAKOUT_DAMAGE("BreakoutDamage"),
    BREAKOUT_DAMAGE_LARGE("BreakoutDamageLarge"),
    CENTER("Center"),
    CLEAR("Clear"),
    DEMOLITION("Demolition"),
    DEMOLISH("Demolish"),
    EPIC_SAVE("EpicSave"),
    FIRST_TOUCH("FirstTouch"),
    GOAL("Goal"),
    HATTRICK("HatTrick"),
    HIGH_FIVE("HighFive"),
    HOOPS_SWISH_GOAL("HoopsSwishGoal"),
    LONG_GOAL("LongGoal"),
    LOW_FIVE("LowFive"),
    MVP("MVP"),
    OWN_GOAL("OwnGoal"),
    OVERTIME_GOAL("OvertimeGoal"),
    POOL_SHOT("PoolShot"),
    PLAYMAKER("Playmaker"),
    TURTLE_GOAL("TurtleGoal"),
    SAVIOR("Savior"),
    SAVE("Save"),
    SHOT("Shot"),
    WIN("Win"),
    BOOST_USED("BoostUsed"),
    DODGES("Dodges"),
    AERIAL_HIT("AerialHit"),
    BOOST_PICKUPS("BoostPickups"),
    SMALL_BOOSTS_COLLECTED("SmallBoostsCollected"),
    BIG_BOOSTS_COLLECTED("BigBoostsCollected"),
    INFECTED_PLAYERS_DEFEATED("InfectedPlayersDefeated"),
    TIME_PLAYED("TimePlayed"),
    FASTEST_GOAL("FastestGoal"),
    DISTANCE_DRIVEN_METERS("DistanceDrivenMeters"),
    DISTANCE_FLOWN("DistanceFlown"),
    DOUBLE_GRAPPLE("DoubleGrapple"),
    MAX_DODGE_STREAK("MaxDodgeStreak"),
    FLIP_RESET("FlipReset");

    fun eq(other: String): Boolean {
        return eventName == other
    }

    companion object {
        fun of(event: String): StatEvents? {
            return StatEvents.entries.find { it.eq(event) }
        }
    }
}

enum class GameEvents(val eventName: String) {
    BALL_HIT("BallHit"),
    COUNTDOWN_BEGIN("CountdownBegin"),
    CROSSBAR_HIT("CrossbarHit"),
    GOAL_SCORED("GoalScored"),
    GOAL_REPLAY_WILL_END("ReplayWillEnd"), // actual eventname differs from api doc
    MATCH_CREATED("MatchCreated"),
    MATCH_INITIALIZED("MatchInitialized"),
    MATCH_DESTROYED("MatchDestroyed"),
    MATCH_ENDED("MatchEnded"),
    MATCH_PAUSED("MatchPaused"),
    MATCH_UNPAUSED("MatchUnpaused"),
    PODIUM_START("PodiumStart"),
    REPLAY_CREATED("ReplayCreated"),
    REPLAY_PLAYBACK_END("ReplayPlaybackEnd"),
    REPLAY_PLAYBACK_START("ReplayPlaybackStart"),
    ROUND_STARTED("RoundStarted");

    fun eq(other: String): Boolean {
        return eventName == other
    }

    companion object {
        fun of(event: String): GameEvents? {
            return entries.find { it.eq(event) }
        }
    }
}

enum class Platform(val className: String) {
    PLAYSTATION("ps"),
    XBOX("xbox"),
    NINTENDO("nintendo"),
    STEAM("steam"),
    EPIC("epic"),
    UNKNOWN("unknown"),
    BOT("bot");

    companion object {
        fun getByPlayerId(playerId: String): Platform {
            return when (playerId.substringBefore('|')) {
                "PS4" -> PLAYSTATION
                "XboxOne" -> XBOX
                "Switch" -> NINTENDO
                "Steam" -> STEAM
                "Epic" -> EPIC
                "Unknown" -> BOT
                else -> {
                    log.error { "Unknown platform for: $playerId" }
                    UNKNOWN
                }
            }
        }
    }
}

enum class TeamSide(val teamNum: Int, val sideName: String) {
    HOME(0, "home"),
    AWAY(1, "away"),
    OTHER(255, "other");

    companion object {
        fun get(teamNum: Int) = entries.firstOrNull { it.teamNum == teamNum } ?: OTHER
    }
}
