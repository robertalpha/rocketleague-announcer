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
        return id.hashCode()
    }
}


@Serializable
data class Team(
    val clubId: Int,
    val homeTeam: Boolean,
    val index:Int,
    val name:String,
    val num:Int,
    val score: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
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
        var result = clubId
        result = 31 * result + index
        result = 31 * result + (players?.hashCode() ?: 0)
        return result
    }
}

@Serializable
data class Club(
    val accentColor: Color,
    val tag: String,
    val name: String,
    val id : Int,
    val primaryColor: Color) {

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
    val B: Int,
    val G: Int
)