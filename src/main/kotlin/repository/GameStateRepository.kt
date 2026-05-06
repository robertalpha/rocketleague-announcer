package nl.vanalphenict.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.Collections
import kotlin.time.Duration.Companion.seconds
import nl.vanalphenict.model.Game
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.GoalEventMessage
import nl.vanalphenict.model.JsonGameState
import nl.vanalphenict.model.JsonPlayer
import nl.vanalphenict.model.JsonPlayerFull
import nl.vanalphenict.model.JsonTeam
import nl.vanalphenict.model.JsonUpdateStateData
import nl.vanalphenict.model.Player
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.Team
import nl.vanalphenict.model.getDefaultTeam
import nl.vanalphenict.utility.ColorUtils.Companion.hexToColor

/**
 * Repository responsible for tracking and supplying the current state of games. This state consists
 * of the state of the game, the teams and the players.
 *
 * TODO: validate synchrnization sollution
 */
class GameStateRepository {
    private val log = KotlinLogging.logger {}

    private val games = Collections.synchronizedMap(HashMap<String, Game>())
    private val players = Collections.synchronizedMap(HashMap<Key, Player>())
    private val teams = Collections.synchronizedMap(HashMap<Key, Team>())

    fun getGame(matchGuid: String): Game {
        return games.computeIfAbsent(matchGuid) {
            log.info { "Creating new game: ${matchGuid}" }
            Game(matchGuid = matchGuid)
        }
    }

    fun getTeam(matchGuid: String, teamNum: Int): Team {
        return teams.computeIfAbsent(Key(matchGuid, teamNum)) {
            getDefaultTeam(teamNum).also {
                log.info { "Creating new team: ${it.name} (${it.teamNum})" }
                getGame(matchGuid).teams.add(it)
            }
        }
    }

    /** Find the player by match and player id */
    fun getPlayer(matchGuid: String, shortcut: Int): Player {
        return players.get(Key(matchGuid, shortcut))!! // TODO at this point they should be known
    }

    /**
     * Get player.
     *
     * If a player is not yet known, a player, team and/or match are created based on the data
     * provided.
     */
    fun getPlayer(matchGuid: String, player: JsonPlayer): Player {
        return players.computeIfAbsent(Key(matchGuid, player.shortcut)) {
            log.info {
                "Creating new player: ${player.name} (shortcut ${player.shortcut}, team ${player.teamNum})"
            }
            val player =
                Player(
                    name = player.name,
                    id = "-1",
                    shortcut = player.shortcut,
                    teamNum = player.teamNum,
                    bot = false,
                    team = getTeam(matchGuid, player.teamNum),
                )
            player.team.players.add(player)
            player
        }
    }

    fun getMetadata(matchGuid: String): RLAMetaData {
        val game = getGame(matchGuid)
        return RLAMetaData(
            matchGuid = matchGuid,
            overtime = game.overtime,
            remaining = game.remaining,
        )
    }

    fun hasHomeTeam(matchGuid: String) = getGame(matchGuid).teams.count { it.hasContributors } == 1

    /** @throws NoSuchElementException if no home team is found */
    fun homeTeam(matchGuid: String) = getGame(matchGuid).teams.first { it.hasContributors }

    private data class Key(val matchGuid: String, val id: Int)

    // ── Interpreting Clock Update ────────────────────────────────────────────────
    fun processClockUpdatedSeconds(gameTime: GameTimeMessage) {
        val game = getGame(gameTime.matchGuid)
        game.overtime = gameTime.overtime
        game.remaining = gameTime.remaining
    }

    // ── Interpreting Goal Update ─────────────────────────────────────────────────
    fun processGoalScored(goal: GoalEventMessage) {
        goal.scorer.team.score += 1
    }

    // ── Interpreting State Update ────────────────────────────────────────────────
    fun processUpdateState(updateState: JsonUpdateStateData) {
        processGame(updateState.matchGuid, updateState.game)
        updateState.game.teams.forEach { processTeam(updateState.matchGuid, it) }
        updateState.players.forEach { processPlayer(updateState.matchGuid, it) }
        updateState.game.target?.let {
            val player = getPlayer(updateState.matchGuid, it)
            player.contributor = true
            getTeam(updateState.matchGuid, player.teamNum).hasContributors = true
        }
    }

    private fun processPlayer(matchGuid: String, jsonPlayer: JsonPlayerFull) =
        getPlayer(matchGuid, jsonPlayer).apply {
            id = jsonPlayer.botSaveId()
            bot = jsonPlayer.isBot()
            score = jsonPlayer.score
            goals = jsonPlayer.goals
            shots = jsonPlayer.shots
            assists = jsonPlayer.assists
            saves = jsonPlayer.saves
            touches = jsonPlayer.touches
            carTouches = jsonPlayer.carTouches
            demos = jsonPlayer.demos
            hasCar = jsonPlayer.hasCar
            speed = jsonPlayer.speed
            boost = jsonPlayer.boost
            boosting = jsonPlayer.boosting
            onGround = jsonPlayer.onGround
            onWall = jsonPlayer.onWall
            powersliding = jsonPlayer.powersliding
            demolished = jsonPlayer.demolished
            supersonic = jsonPlayer.supersonic
            attacker = jsonPlayer.attacker?.let { getPlayer(matchGuid, it) }
        }

    private fun processTeam(matchGuid: String, team1: JsonTeam) =
        getTeam(matchGuid, team1.teamNum).apply {
            name = team1.name
            score = team1.score
            primaryColor = team1.colorPrimary.hexToColor()
            secondaryColor = team1.colorSecondary.hexToColor()
        }

    private fun processGame(matchGuid: String, jsonGame: JsonGameState) =
        getGame(matchGuid).apply {
            replay = jsonGame.replay
            overtime = jsonGame.overtime
            remaining = jsonGame.timeSeconds.seconds
        }
}
