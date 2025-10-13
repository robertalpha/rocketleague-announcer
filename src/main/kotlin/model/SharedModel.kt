package nl.vanalphenict.model

import kotlinx.serialization.Serializable
import java.util.Collections

@Serializable
data class Player(
    val id: String,
    val name: String,
    val score: Int? = null,
    val team: Team? = null,
    val club: Club? = null) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (score != other.score) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    fun isSame(other: Player?): Boolean {
        if (other == null) return false
        return other.botSaveId() == botSaveId()
    }

    fun isBot(): Boolean {
        return id.equals("Unknown|0|0")
    }

    fun botSaveId(): String {
        if (isBot())
            return "bot|" + name + "|0"
        else
            return id
    }
}


@Serializable
data class Team(
    val clubId: Int? = null,
    val homeTeam: Boolean? = null,
    val index:Int? = null,
    val name:String? = null,
    val num:Int? = null,
    val score: Int? = null,
    val primaryColor: Color? = null,
    val secondaryColor: Color? = null,
    val players: List<Player>? = Collections.emptyList()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Team

        if (clubId != other.clubId) return false
        if (index != other.index) return false
        if (score != other.score) return false
        if (players != other.players) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clubId?:0
        result = 31 * result + (index?:0)
        result = 31 * result + (players?.hashCode() ?: 0)
        return result
    }
}

@Serializable
data class Club(
    val id: Int,
    val name: String,
    val tag: String,
    val accentColor: Color,
    val primaryColor: Color
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Club

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}

@Serializable
data class Color(
    val R: Int,
    val G: Int,
    val B: Int
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