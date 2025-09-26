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

enum class Events(val eventName: String) {
    DEMOLITION("Demolition"),
    DEMOLISH("Demolish"),
    OWN_GOAL("OwnGoal"),
    GOAL("Goal"),
    AERIAL_GOAL("AerialGoal"),
    BACKWARD_GOAL("BackwardsGoal"),
    BICYCLE_GOAL("BicycleGoal"),
    LONG_GOAL("LongGoal"),
    TURTLE_GOAL("TurtleGoal"),
    POOL_SHOT("PoolShot"),
    HATTRICK("HatTrick"),
    SAVE("Save"),
    EPIC_SAVE("EpicSave");

    fun eq(other: String) : Boolean {
        return eventName == other
    }
}