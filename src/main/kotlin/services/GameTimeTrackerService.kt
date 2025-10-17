package nl.vanalphenict.services

import nl.vanalphenict.model.GameTimeMessage
import kotlin.time.Duration.Companion.seconds

class GameTimeTrackerService {

    private val gameTimes = HashMap<String, GameTimeMessage>()

    fun storeGameTime(message: GameTimeMessage) {
        gameTimes[message.matchGUID] = message
    }

    fun getGameTime(matchGUID: String): GameTimeMessage =
        gameTimes[matchGUID] ?: GameTimeMessage(matchGUID,300.seconds,false)
}