package nl.vanalphenict.services.impl

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kotlinx.html.body
import kotlinx.html.stream.createHTML
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.Team
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.utility.TimeService
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerUpdateSSE
import nl.vanalphenict.web.view.actionListItem
import nl.vanalphenict.web.view.emptyTeam
import nl.vanalphenict.web.view.scoreBoardHtml
import nl.vanalphenict.web.view.teamInfoHtml
import nl.vanalphenict.web.view.timeRemainingHtml

class SsePublisher(val timeService: TimeService) : EventHandler {

    private val log = KotlinLogging.logger {}

    private val games: MutableMap<String, Game> = HashMap()

    private fun getGame(matchGUID: String) = games[matchGUID] ?: Game()

    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        log.trace { "SSE HANDLER handeling: ${msg.event.eventName}" }
        val actionItem = StatRepository.StatMessageRecord(timeService.now(), msg, metaData)
        val htmlText =
            createHTML().body { actionListItem(actionItem, metaData.remaining, metaData.overtime) }
        runBlocking { triggerUpdateSSE(SSE_EVENT_TYPE.NEW_ACTION, htmlText) }

        val oldGame = getGame(msg.matchGUID)
        val team = msg.player.team

        val newGame =
            if (msg is KillMessage) {
                if (team.homeTeam) Game(home = team, away = msg.victim.team)
                else Game(away = team, home = msg.victim.team)
            } else if (team.homeTeam) oldGame.copy(home = team) else oldGame.copy(away = team)
        updateTeams(msg.matchGUID, newGame)
    }

    override fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {
        if (msg.gameEvent == GameEvents.TEAMS_CREATED) {
            runBlocking { triggerUpdateSSE(SSE_EVENT_TYPE.SCORE_BOARD, scoreBoardHtml()) }
        }
        if (msg.teams.size != 2) return
        if (msg.teams[0].homeTeam) updateTeams(msg.matchGUID, Game(msg.teams[0], msg.teams[1]))
        else updateTeams(msg.matchGUID, Game(msg.teams[1], msg.teams[0]))
    }

    override fun handleGameTime(msg: GameTimeMessage) {
        val htmlText = timeRemainingHtml(msg.remaining, msg.overtime)
        runBlocking { triggerUpdateSSE(SSE_EVENT_TYPE.GAME_TIME, htmlText) }
    }

    private fun updateTeams(matchGUID: String, game: Game) {
        runBlocking {
            triggerUpdateSSE(SSE_EVENT_TYPE.HOME_TEAM, teamInfoHtml(game.home))
            triggerUpdateSSE(SSE_EVENT_TYPE.AWAY_TEAM, teamInfoHtml(game.away))
        }
        games[matchGUID] = game
    }

    data class Game(val home: Team = emptyTeam(true), val away: Team = emptyTeam(false))
}
