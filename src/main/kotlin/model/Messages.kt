package nl.vanalphenict.model

import io.ktor.util.toUpperCasePreservingASCIIRules
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

// ── Envelope ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class JsonEnvelope(
    @SerialName("Event") val event: String,
    @SerialName("Data") val data: String,
)

// ── Shared sub-objects ──────────────────────────────────────────────────────

interface JsonPlayer {
    val name: String
    val shortcut: Int
    val teamNum: Int
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonPlayerRef(
    @SerialName("Name") override val name: String,
    @SerialName("Shortcut") override val shortcut: Int = 0,
    @SerialName("TeamNum") override val teamNum: Int = 0,
) : JsonPlayer

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonPlayerFull(
    @SerialName("Name") override val name: String,
    @SerialName("PrimaryId") val primaryId: String = "",
    @SerialName("Shortcut") override val shortcut: Int,
    @SerialName("TeamNum") override val teamNum: Int,
    @SerialName("Score") val score: Int = 0,
    @SerialName("Goals") val goals: Int = 0,
    @SerialName("Shots") val shots: Int = 0,
    @SerialName("Assists") val assists: Int = 0,
    @SerialName("Saves") val saves: Int = 0,
    @SerialName("Touches") val touches: Int = 0,
    @SerialName("CarTouches") val carTouches: Int = 0,
    @SerialName("Demos") val demos: Int = 0,
    @SerialName("bHasCar") val hasCar: Boolean = false,
    @SerialName("Speed") val speed: Double = 0.0,
    @SerialName("Boost") val boost: Int = 0,
    @SerialName("bBoosting") val boosting: Boolean = false,
    @SerialName("bOnGround") val onGround: Boolean = false,
    @SerialName("bOnWall") val onWall: Boolean = false,
    @SerialName("bPowersliding") val powersliding: Boolean = false,
    @SerialName("bDemolished") val demolished: Boolean = false,
    @SerialName("bSupersonic") val supersonic: Boolean = false,
    @SerialName("Attacker") val attacker: JsonPlayerRef? = null,
) : JsonPlayer {
    fun isBot(): Boolean = primaryId == "" || primaryId == "Unknown|0|0"

    fun botSaveId(): String = if (isBot()) "bot|$name|0" else primaryId
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonTeam(
    @SerialName("Name") val name: String = "",
    @SerialName("TeamNum") val teamNum: Int = 0,
    @SerialName("Score") val score: Int = 0,
    @SerialName("ColorPrimary") val colorPrimary: String = "",
    @SerialName("ColorSecondary") val colorSecondary: String = "",
) {
    inline val tag: String
        get() = name.toTag()
}

@Serializable
data class JsonVector(
    @SerialName("X") val x: Double = 0.0,
    @SerialName("Y") val y: Double = 0.0,
    @SerialName("Z") val z: Double = 0.0,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonBallState(
    @SerialName("Speed") val speed: Double = 0.0,
    @SerialName("TeamNum") val teamNum: Int = 255,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonGameState(
    @SerialName("Teams") val teams: List<JsonTeam> = emptyList(),
    @SerialName("TimeSeconds") val timeSeconds: Int = 0,
    @SerialName("bOvertime") val overtime: Boolean = false,
    @SerialName("Frame") val frame: Int = 0,
    @SerialName("Elapsed") val elapsed: Double = 0.0,
    @SerialName("Ball") val ball: JsonBallState = JsonBallState(),
    @SerialName("bReplay") val replay: Boolean = false,
    @SerialName("bHasWinner") val hasWinner: Boolean = false,
    @SerialName("Winner") val winner: String = "",
    @SerialName("Arena") val arena: String = "",
    @SerialName("bHasTarget") val hasTarget: Boolean = false,
    @SerialName("Target") val target: JsonPlayerRef? = null,
)

@Serializable
data class JsonBallLastTouch(
    @SerialName("Player") val player: JsonPlayerRef,
    @SerialName("Speed") val speed: Double = 0.0,
)

// ── Event Data payloads ─────────────────────────────────────────────────────

// UpdateState
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonUpdateStateData(
    @SerialName("MatchGuid") val matchGuid: String = "",
    @SerialName("Players") val players: List<JsonPlayerFull> = emptyList(),
    @SerialName("Game") val game: JsonGameState = JsonGameState(),
)

// StatfeedEvent
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonStatfeedEventData(
    @SerialName("MatchGuid") val matchGuid: String = "",
    @SerialName("EventName") val eventName: String = "",
    @SerialName("Type") val type: String = "",
    @SerialName("MainTarget") val mainTarget: JsonPlayerRef = JsonPlayerRef(""),
    @SerialName("SecondaryTarget") val secondaryTarget: JsonPlayerRef? = null,
)

// GoalScored
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonGoalScoredData(
    @SerialName("MatchGuid") val matchGuid: String = "",
    @SerialName("GoalSpeed") val goalSpeed: Double = 0.0,
    @SerialName("GoalTime") val goalTime: Double = 0.0,
    @SerialName("ImpactLocation") val impactLocation: JsonVector = JsonVector(),
    @SerialName("Scorer") val scorer: JsonPlayerRef = JsonPlayerRef(""),
    @SerialName("Assister") val assister: JsonPlayerRef? = null,
    @SerialName("BallLastTouch") val ballLastTouch: JsonBallLastTouch? = null,
)

// ClockUpdatedSeconds
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonClockUpdatedSecondsData(
    @SerialName("MatchGuid") val matchGuid: String = "",
    @SerialName("TimeSeconds") val timeSeconds: Int = 0,
    @SerialName("bOvertime") val overtime: Boolean = false,
)

// MatchEnded
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonMatchEndedData(
    @SerialName("MatchGuid") val matchGuid: String = "",
    @SerialName("WinnerTeamNum") val winnerTeamNum: Int = 0,
)

// BallHit
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonBallHitData(
    @SerialName("MatchGuid") val matchGuid: String = "",
    @SerialName("Players") val players: List<JsonPlayerRef> = emptyList(),
    @SerialName("Ball") val ball: JsonBallHitBall = JsonBallHitBall(),
)

@Serializable
data class JsonBallHitBall(
    @SerialName("PreHitSpeed") val preHitSpeed: Double = 0.0,
    @SerialName("PostHitSpeed") val postHitSpeed: Double = 0.0,
    @SerialName("Location") val location: JsonVector = JsonVector(),
)

// CrossbarHit
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonCrossbarHitData(
    @SerialName("MatchGuid") val matchGuid: String = "",
    @SerialName("BallLocation") val ballLocation: JsonVector = JsonVector(),
    @SerialName("BallSpeed") val ballSpeed: Double = 0.0,
    @SerialName("ImpactForce") val impactForce: Double = 0.0,
    @SerialName("BallLastTouch") val ballLastTouch: JsonBallLastTouch? = null,
)

// Simple events that only carry a MatchGuid
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class JsonMatchGuidData(@SerialName("MatchGuid") val matchGuid: String = "")

// ── Log message (kept for backwards compat if still used) ───────────────────

@Serializable data class JsonLogMessage(val log: String, val user: String, val userId: String)

fun String.toTag() =
    if (this.contains(" ")) {
        val parts = this.split(" ")
        parts
            .filter { it.isNotBlank() }
            .joinToString("") { it.left(1).toUpperCasePreservingASCIIRules() }
    } else {
        val noLower = this.filter { !it.isLowerCase() }
        if (noLower.length > 1) noLower.left(3) else this.left(3).toUpperCasePreservingASCIIRules()
    }

fun String.left(length: Int) = substring(0, minOf(this.length, length))
