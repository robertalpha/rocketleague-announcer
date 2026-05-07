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
    fun getPlayer(matchGuid: String, shortcut: Int, teamNum: Int): Player {
        return players.get(Key(matchGuid, shortcut))!!.also {
            fixPlayerTeam(matchGuid, it, teamNum)
        }
    }

    /**
     * Get player.
     *
     * If a player is not yet known, a player, team and/or match are created based on the data
     * provided.
     */
    fun getPlayer(matchGuid: String, player: JsonPlayer): Player {
        return players
            .computeIfAbsent(Key(matchGuid, player.shortcut)) {
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
            .also { fixPlayerTeam(matchGuid, it, player.teamNum) }
    }

    private fun fixPlayerTeam(matchGuid: String, player: Player, actualTeam: Int) {
        if (player.teamNum == actualTeam) return
        val wrongTeam = player.teamNum
        player.teamNum = actualTeam
        player.team = getTeam(matchGuid, actualTeam)
        getTeam(matchGuid, wrongTeam).players.remove(player)
        getTeam(matchGuid, actualTeam).players.add(player)
        log.error { "Team mismatch for ${player.name}: $wrongTeam != $actualTeam" }
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

    private fun processPlayer(matchGuid: String, player: JsonPlayerFull) =
        getPlayer(matchGuid, player).apply {
            id = player.botSaveId()
            bot = player.isBot()
            score = player.score
            goals = player.goals
            shots = player.shots
            assists = player.assists
            saves = player.saves
            touches = player.touches
            carTouches = player.carTouches
            demos = player.demos
            hasCar = player.hasCar
            speed = player.speed
            boost = player.boost
            boosting = player.boosting
            onGround = player.onGround
            onWall = player.onWall
            powersliding = player.powersliding
            demolished = player.demolished
            supersonic = player.supersonic
            attacker = player.attacker?.let { getPlayer(matchGuid, it) }
        }

    private fun processTeam(matchGuid: String, team: JsonTeam) =
        getTeam(matchGuid, team.teamNum).apply {
            name = team.name
            score = team.score
            primaryColor = team.colorPrimary.hexToColor()
            secondaryColor = team.colorSecondary.hexToColor()
        }

    private fun processGame(matchGuid: String, game: JsonGameState) =
        getGame(matchGuid).apply {
            replay = game.replay
            overtime = game.overtime
            remaining = game.timeSeconds.seconds
        }
}
