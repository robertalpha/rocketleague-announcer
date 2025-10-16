package nl.vanalphenict.model

data class RLAMetaData(
    var prevailingAnnouncement: Announcement? = null,
    val announcements: MutableSet<Announcement> = HashSet(),
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
    WIN("Win");

    fun eq(other: String) : Boolean {
        return eventName == other
    }
}

enum class GameEvents(val eventName: String) {
    START_ROUND("Function GameEvent_Soccar_TA.Active.StartRound");

    fun eq(other: String) : Boolean {
        return eventName == other
    }
}