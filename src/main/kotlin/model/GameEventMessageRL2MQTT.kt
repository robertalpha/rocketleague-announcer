package nl.vanalphenict.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class GameEventMessageRoot(
    val timestamp: String,
    val message: GameEventMessage)

@Serializable
data class GameEventMessage(val gameEvent: String, val teams: List<GameEventTeam>)

@Serializable
data class GameEventTeam(val players: List<Player>,
                         val clubId: Int? = null,
                         val homeTeam: Boolean? = null,
                         val name: String? = null,
                         val num: Int? = null,
                         val index: Int? = null,
                         val score: Int? = null,
                         val primaryColor: TeamColor? = null,
                         val secondaryColor: TeamColor? = null,
    )

