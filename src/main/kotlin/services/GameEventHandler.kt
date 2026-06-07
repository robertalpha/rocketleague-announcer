package nl.vanalphenict.services

import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameTimeMessage
import nl.vanalphenict.model.RLAMetaData
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.impl.EventDistributor

interface GameEventHandler {
    /**
     * Notification that the game state might have been updated based on an UpdateState message. The
     * frequency of this message is determined by the update rate and the amount of connected
     * players
     */
    fun handleTick(matchGuid: String) {}

    /**
     * Notification that a stat message has been received. A Stat message is an event that happened
     * to a player. Every event should only trigger a single handleStatMessage call.
     *
     * For the type of events that can happen to a player see @see nl.vanalphenict.model.StatEvents
     */
    fun handleStatMessage(msg: StatMessage, metaData: RLAMetaData) {}

    /**
     * Notification that a game event has been received. A game event is an event that happened to
     * the game. Every event should only trigger a single handleGameEvent call.
     *
     * For the type of events that can happen to a player see @see nl.vanalphenict.model.GameEvents
     */
    fun handleGameEvent(msg: GameEventMessage, metaData: RLAMetaData) {}

    /**
     * Notification everytime the gameclock changes.
     *
     * Be aware, when the time is up, the game only ends when the ball touches te ground. Since the
     * time doesn't change, no handleGameTime will be triggered.
     */
    fun handleGameTime(msg: GameTimeMessage) {}

    class Builder(private var handler: GameEventHandler) {

        fun add(handler: GameEventHandler): Builder {
            this.handler = EventDistributor(this.handler, handler)
            return this
        }

        fun build(): GameEventHandler {
            return this.handler
        }
    }
}
