package nl.vanalphenict.services.impl

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.getDefaultTeam
import nl.vanalphenict.repository.GameStateRepository
import nl.vanalphenict.services.GameEventHandler
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerUpdateSSE
import nl.vanalphenict.web.view.actionListItemHtml
import nl.vanalphenict.web.view.scoreBoardHtml
import nl.vanalphenict.web.view.teamsInfoHtml
import nl.vanalphenict.web.view.timeRemainingHtml

class SsePublisher(val gameStateRepository: GameStateRepository) : GameEventHandler {

    private val log = KotlinLogging.logger {}

    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        log.trace { "SSE HANDLER handeling: ${msg.event.eventName}" }
        runBlocking {
            triggerUpdateSSE(SSE_EVENT_TYPE.NEW_ACTION, actionListItemHtml(msg, metaData))
        }
    }

    override fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {
        if (msg.gameEvent == GameEvents.MATCH_CREATED) {
            runBlocking { triggerUpdateSSE(SSE_EVENT_TYPE.SCORE_BOARD, scoreBoardHtml()) }
        }
        if (msg.gameEvent in setOf(GameEvents.ROUND_STARTED, GameEvents.GOAL_REPLAY_START)) {
            updateTeams(msg.matchGuid)
        }
    }

    override fun handleGameTime(msg: GameTimeMessage) {
        runBlocking {
            triggerUpdateSSE(
                SSE_EVENT_TYPE.GAME_TIME,
                timeRemainingHtml(msg.remaining, msg.overtime),
            )
        }
    }

    private fun updateTeams(matchGuid: String) {
        val game = gameStateRepository.getGame(matchGuid)
        val homeTeam = game.teams.filter { it.teamNum == 0 }.getOrElse(0) { getDefaultTeam(0) }
        val awayTeam = game.teams.filter { it.teamNum == 1 }.getOrElse(0) { getDefaultTeam(1) }
        runBlocking { triggerUpdateSSE(SSE_EVENT_TYPE.TEAMS, teamsInfoHtml(homeTeam, awayTeam)) }
    }
}
