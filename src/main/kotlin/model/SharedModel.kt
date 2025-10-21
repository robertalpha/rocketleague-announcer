package nl.vanalphenict.model

import kotlin.time.Duration

data class RLAMetaData(
    val matchGUID: String,
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
    MAX_DODGE_STREAK("MaxDodgeStreak");

    fun eq(other: String): Boolean {
        return eventName == other
    }
}

enum class GameEvents(val eventName: String) {
    START_ROUND("Function GameEvent_Soccar_TA.Active.StartRound"),
    TEAMS_CREATED("Function TAGame.GameEvent_Soccar_TA.OnAllTeamsCreated"),
    PLAYER_ADDED("Function TAGame.GameEvent_TA.EventPlayerAdded"),
    PLAYER_REMOVED("Function TAGame.GameEvent_TA.EventPlayerRemoved"),
    MATCH_ENDED("Function TAGame.GameEvent_Soccar_TA.EventMatchEnded");

    fun eq(other: String): Boolean {
        return eventName == other
    }
}
