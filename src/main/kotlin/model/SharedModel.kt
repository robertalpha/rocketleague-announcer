package nl.vanalphenict.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(val id: String, val name: String, val score: Int? = null, val team: Team? = null, val club: Club? = null)

@Serializable
data class Club(
    val accentColor: TeamColor,
    val tag: String,
    val name: String,
    val id : Int,
    val primaryColor: TeamColor
)