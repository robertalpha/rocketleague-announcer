package nl.vanalphenict.messaging

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.GoalEventMessage
import nl.vanalphenict.model.JsonClockUpdatedSecondsData
import nl.vanalphenict.model.JsonGoalScoredData
import nl.vanalphenict.model.JsonMatchGuidData
import nl.vanalphenict.model.JsonStatfeedEventData
import nl.vanalphenict.model.JsonUpdateStateData
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.GameStateRepository
import nl.vanalphenict.services.GameEventHandler

class MessageInterpreter(
    private val eventHandler: GameEventHandler,
    private val gameStateRepository: GameStateRepository,
) {

    internal val json = Json { ignoreUnknownKeys = true }
    private val log = KotlinLogging.logger {}

    // Game events that carry only a MatchGuid and map to GameEvents enum
    private val gameEventNames =
        setOf(
            GameEvents.COUNTDOWN_BEGIN.eventName,
            GameEvents.GOAL_REPLAY_END.eventName,
            GameEvents.GOAL_REPLAY_WILL_END.eventName,
            GameEvents.GOAL_REPLAY_START.eventName,
            GameEvents.GOAL_REPLAY_WILL_END.eventName,
            GameEvents.MATCH_CREATED.eventName,
            GameEvents.MATCH_INITIALIZED.eventName,
            GameEvents.MATCH_DESTROYED.eventName,
            GameEvents.MATCH_ENDED.eventName,
            GameEvents.MATCH_PAUSED.eventName,
            GameEvents.MATCH_UNPAUSED.eventName,
            GameEvents.PODIUM_START.eventName,
            GameEvents.REPLAY_CREATED.eventName,
            GameEvents.ROUND_STARTED.eventName,
        )

    fun interpret(event: String, data: String) {
        when (event) {
            // Tick
            "UpdateState" -> {
                gameStateRepository.processUpdateState(
                    json.decodeFromString<JsonUpdateStateData>(data)
                )
            }

            // Events
            GameEvents.BALL_HIT.eventName -> {
                // TODO: Parse ballhit
            }
            "ClockUpdatedSeconds" -> {
                parseClockUpdatedSeconds(data).let {
                    gameStateRepository.processClockUpdatedSeconds(it)
                    eventHandler.handleGameTime(it)
                }
            }
            GameEvents.CROSSBAR_HIT.eventName -> {
                // TODO: Parse CrossbarHit
            }
            GameEvents.GOAL_SCORED.eventName -> {
                parseGoalScored(data)?.let {
                    gameStateRepository.processGoalScored(it)
                    eventHandler.handleGameEvent(it, gameStateRepository.getMetadata(it.matchGuid))
                }
            }
            in gameEventNames -> {
                parseOtherGameEvent(event, data)?.let {
                    eventHandler.handleGameEvent(it, gameStateRepository.getMetadata(it.matchGuid))
                }
            }
            "StatfeedEvent" -> {
                parseStatfeedEvent(data)?.let {
                    eventHandler.handleStatMessage(
                        it,
                        gameStateRepository.getMetadata(it.matchGuid),
                    )
                }
            }
            else -> {
                log.error { "Unhandled event: $event -> ($data)" }
            }
        }
    }

    fun parseStatfeedEvent(dataStr: String): StatMessage? {
        val src = json.decodeFromString<JsonStatfeedEventData>(dataStr)
        val event = StatEvents.entries.find { it.eq(src.eventName) }
        if (event == null) {
            log.info { "Event \"${src.eventName}\" not supported." }
            return null
        }
        val player =
            gameStateRepository.getPlayer(
                src.matchGuid,
                src.mainTarget.shortcut,
                src.mainTarget.teamNum,
            )
        return if (event == StatEvents.DEMOLISH && src.secondaryTarget != null) {
            KillMessage(
                matchGuid = src.matchGuid,
                event = event,
                player = player,
                victim =
                    gameStateRepository.getPlayer(
                        src.matchGuid,
                        src.secondaryTarget.shortcut,
                        src.secondaryTarget.teamNum,
                    ),
            )
        } else {
            StatMessage(matchGuid = src.matchGuid, event = event, player = player)
        }
    }

    fun parseClockUpdatedSeconds(dataStr: String): GameTimeMessage =
        json.decodeFromString<JsonClockUpdatedSecondsData>(dataStr).let {
            GameTimeMessage(
                matchGuid = it.matchGuid,
                remaining = it.timeSeconds.seconds,
                overtime = it.overtime,
            )
        }

    fun parseGoalScored(dataStr: String): GoalEventMessage? =
        json
            .decodeFromString<JsonGoalScoredData>(dataStr)
            .takeIf { it.scorer.name != "" } // Ignore illegal goal message during replay
            ?.let { data ->
                GoalEventMessage(
                    matchGuid = data.matchGuid,
                    gameEvent = GameEvents.GOAL_SCORED,
                    goalSpead = data.goalSpeed,
                    goalTime = data.goalTime,
                    scorer = gameStateRepository.getPlayer(data.matchGuid, data.scorer),
                    assister =
                        data.assister?.let { gameStateRepository.getPlayer(data.matchGuid, it) },
                    lastToucher =
                        data.ballLastTouch?.player?.let {
                            gameStateRepository.getPlayer(data.matchGuid, it)
                        },
                    touchSpeed = data.ballLastTouch?.speed,
                )
            }

    fun parseOtherGameEvent(gameEvent: String, dataStr: String): GameEventMessage? {
        val data = json.decodeFromString<JsonMatchGuidData>(dataStr)
        val event = GameEvents.entries.find { it.eq(gameEvent) }
        return if (event != null) GameEventMessage(matchGuid = data.matchGuid, gameEvent = event)
        else null
    }
}
