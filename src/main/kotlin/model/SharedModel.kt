package nl.vanalphenict.model

data class RLAMetaData(
    var prevailingAnnouncement: Announcement? = null,
    val announcements: MutableSet<Announcement> = HashSet(),
)

enum class StatEvents(val eventName: String, val showInUI: Boolean = true) {
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

    // meta stat events, not used in action view
    BOOSTUSED("BoostUsed", showInUI = false),
    DODGES("Dodges", showInUI = false),
    AERIALHIT("AerialHit", showInUI = false),
    BOOSTPICKUPS("BoostPickups", showInUI = false),
    SMALLBOOSTSCOLLECTED("SmallBoostsCollected", showInUI = false),
    BIGBOOSTSCOLLECTED("BigBoostsCollected", showInUI = false),
    INFECTEDPLAYERSDEFEATED("InfectedPlayersDefeated", showInUI = false),
    TIMEPLAYED("TimePlayed", showInUI = false),
    FASTESTGOAL("FastestGoal", showInUI = false),
    DISTANCEDRIVENMETERS("DistanceDrivenMeters", showInUI = false),
    DISTANCEFLOWN("DistanceFlown", showInUI = false),
    DOUBLEGRAPPLE("DoubleGrapple", showInUI = false),
    MAXDODGESTREAK("MaxDodgeStreak", showInUI = false),
    ;

    fun eq(other: String) : Boolean {
        return eventName == other
    }
}

enum class GameEvents(val eventName: String) {
    START_ROUND("Function GameEvent_Soccar_TA.Active.StartRound"),
    TEAMS_CREATED("Function TAGame.GameEvent_Soccar_TA.OnAllTeamsCreated"),
    PLAYER_ADDED("Function TAGame.GameEvent_TA.EventPlayerAdded"),
    PLAYER_REMOVED("Function TAGame.GameEvent_TA.EventPlayerRemoved"),
    MATCH_ENDED("Function TAGame.GameEvent_Soccar_TA.EventMatchEnded");

    fun eq(other: String) : Boolean {
        return eventName == other
    }
}