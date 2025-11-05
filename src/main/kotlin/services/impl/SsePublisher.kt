package nl.vanalphenict.services.impl

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.max
import kotlinx.coroutines.runBlocking
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.KillMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.model.Team
import nl.vanalphenict.services.EventHandler
import nl.vanalphenict.web.SSE_EVENT_TYPE
import nl.vanalphenict.web.triggerUpdateSSE
import nl.vanalphenict.web.view.actionListItemHtml
import nl.vanalphenict.web.view.emptyTeam
import nl.vanalphenict.web.view.scoreBoardHtml
import nl.vanalphenict.web.view.teamsInfoHtml
import nl.vanalphenict.web.view.timeRemainingHtml

class SsePublisher() : EventHandler {

    private val log = KotlinLogging.logger {}

    private val games: MutableMap<String, Game> = HashMap()

    private fun getGame(matchGUID: String) = games[matchGUID] ?: Game()

    override fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {
        log.trace { "SSE HANDLER handeling: ${msg.event.eventName}" }
        runBlocking {
            triggerUpdateSSE(SSE_EVENT_TYPE.NEW_ACTION, actionListItemHtml(msg, metaData))
        }

        synchronized(this) {
            val oldGame = getGame(msg.matchGUID)
            val team =
                if (msg.event == StatEvents.GOAL)
                    msg.player.team.copy(score = msg.player.team.score + 1)
                else msg.player.team

            val newGame =
                if (msg is KillMessage) {
                    if (team.homeTeam) Game(home = team, away = msg.victim.team)
                    else Game(away = team, home = msg.victim.team)
                } else if (team.homeTeam) oldGame.copy(home = team) else oldGame.copy(away = team)
            newGame.homeScore = max(newGame.home.score, oldGame.homeScore)
            newGame.awayScore = max(newGame.away.score, oldGame.awayScore)
            updateTeams(msg.matchGUID, newGame)
        }
    }

    override fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {
        if (msg.gameEvent == GameEvents.TEAMS_CREATED) {
            runBlocking { triggerUpdateSSE(SSE_EVENT_TYPE.SCORE_BOARD, scoreBoardHtml()) }
        }
        if (msg.teams.size == 2) {
            synchronized(this) {
                val game =
                    if (msg.teams[0].homeTeam) Game(msg.teams[0], msg.teams[1])
                    else Game(msg.teams[1], msg.teams[0])
                game.homeScore = game.home.score
                game.awayScore = game.away.score
                updateTeams(msg.matchGUID, game)
            }
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

    private fun updateTeams(matchGUID: String, game: Game) {
        runBlocking { triggerUpdateSSE(SSE_EVENT_TYPE.TEAMS, teamsInfoHtml(game.home, game.away)) }
        games[matchGUID] = game
    }

    data class Game(
        val home: Team = emptyTeam(true),
        val away: Team = emptyTeam(false),
        var homeScore: Int = 0,
        var awayScore: Int = 0,
    )
}
