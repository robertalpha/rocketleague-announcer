package nl.vanalphenict.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class TickerMessageRoot(val timestamp: String, val message: TickerMessage)

@Serializable
data class TickerMessage(val event: String, val player: Player, val victim: Player? = null)


@Serializable
data class Team(
    val clubId: Int, val homeTeam: Boolean, val index:Int, val name:String,val num:Int,
    val score: Int, val primaryColor: TeamColor, val secondaryColor: TeamColor
)

@Serializable
data class TeamColor(val R: Int, val B: Int, val G: Int)