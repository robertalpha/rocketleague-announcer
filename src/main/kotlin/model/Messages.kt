package nl.vanalphenict.model

import java.util.Collections
import kotlinx.serialization.Serializable

val CLUB_MAP = HashMap<Int, JsonClub>()

@Serializable
data class JsonGameEventMessage(
    val matchGUID: String,
    val gameEvent: String,
    val teams: List<JsonTeam>? = emptyList(),
)

@Serializable
data class JsonGameTimeMessage(val matchGUID: String, val remaining: Int, val overtime: Boolean)

@Serializable
data class JsonStatMessage(
    val matchGUID: String,
    val event: String,
    val player: JsonPlayer,
    val victim: JsonPlayer? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonStatMessage

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

@Serializable data class JsonLogMessage(val log: String, val user: String, val userId: String)

@Serializable
data class JsonPlayer(
    val id: String,
    val name: String,
    val score: Int? = null,
    val team: JsonTeam? = null,
    val club: JsonClub? = null,
) {
    init {
        club?.let { CLUB_MAP[it.id] = it }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonPlayer

        if (score != other.score) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    fun isBot(): Boolean {
        return id == "Unknown|0|0"
    }

    fun botSaveId(): String {
        return if (isBot()) "bot|$name|0" else id
    }
}

@Serializable
data class JsonTeam(
    val clubId: Int? = null,
    val homeTeam: Boolean? = null,
    val index: Int? = null,
    val name: String? = null,
    val num: Int? = null,
    val score: Int? = null,
    val primaryColor: JsonColor? = null,
    val secondaryColor: JsonColor? = null,
    val players: List<JsonPlayer>? = Collections.emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonTeam

        if (clubId != other.clubId) return false
        if (index != other.index) return false
        if (score != other.score) return false
        if (players != other.players) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clubId ?: 0
        result = 31 * result + (index ?: 0)
        result = 31 * result + (players?.hashCode() ?: 0)
        return result
    }
}

@Serializable
data class JsonClub(
    val id: Int,
    val name: String,
    val tag: String,
    val accentColor: JsonColor,
    val primaryColor: JsonColor,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonClub

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}

@Serializable data class JsonColor(val R: Int, val G: Int, val B: Int)
