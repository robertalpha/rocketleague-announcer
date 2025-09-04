package nl.vanalphenict.util

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
data class TestMessage(
    @Contextual
    val timestamp: String,
    val topic: String,
    val message: RLMessage,
)

@Serializable
@JsonIgnoreUnknownKeys
data class RLMessage(
    val gameEvent: String? = null,
    val player: Player? = null,
    val tickerEvent: TickerEvent? = null
)

@Serializable
@JsonIgnoreUnknownKeys
data class Player(
    val name: String
)

@Serializable
@JsonIgnoreUnknownKeys
data class TickerEvent(
    val event: String
)