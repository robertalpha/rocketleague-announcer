package nl.vanalphenict.model

import kotlinx.serialization.Serializable

@Serializable
data class GameEventMessage(
    val matchGUID: String,
    val gameEvent: String,
    val teams: List<Team>? = emptyList()
)

@Serializable
data class GameTimeMessage(
    val matchGUID: String,
    val remaining: Int,
    val overtime: Boolean)

@Serializable
data class StatMessage(
    val matchGUID: String,
    val event: String,
    val player: Player,
    val victim: Player? = null,
    val icon : String = getIcon()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatMessage

        if (event != other.event) return false
        if (matchGUID != other.matchGUID) return false
        if (player != other.player) return false
        if (victim != other.victim) return false

        return true
    }

    override fun hashCode(): Int {
        var result = event.hashCode()
        result = 31 * result + matchGUID.hashCode()
        result = 31 * result + player.hashCode()
        result = 31 * result + (victim?.hashCode() ?: 0)
        return result
    }
}

fun getIcon(): String {
    return listOf("AerialGoal.webp",
        "Assist.webp",
        "BackwardsGoal.webp",
        "BicycleGoal.webp",
        "BicycleHit.webp",
        "BreakoutDamage.webp",
        "BreakoutDamageLarge.webp",
        "Center.webp",
        "Clear.webp",
        "Demolish.webp",
        "Demolition.webp",
        "EpicSave.webp",
        "FirstTouch.webp",
        "Goal.webp",
        "HatTrick.webp",
        "HighFive.webp",
        "HoopsSwishGoal.webp",
        "LongGoal.webp",
        "LowFive.webp",
        "MVP.webp",
        "OvertimeGoal.webp",
        "Playmaker.webp",
        "PoolShot.webp",
        "Save.webp",
        "Savior.webp",
        "Shot.webp",
        "TurtleGoal.webp",
        "Win.webp"
    ).random()
}

@Serializable
data class LogMessage(
    val log: String,
    val user: String,
    val userId: String
)