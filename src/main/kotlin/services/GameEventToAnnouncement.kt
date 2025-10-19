package nl.vanalphenict.services

import kotlin.time.Clock
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage
import nl.vanalphenict.model.GameEvents

interface GameEventToAnnouncement {
    fun listenTo(): Set<GameEvents>

    fun interpret(
        statMessage: GameEventMessage,
        currentTimeStamp: Instant = Clock.System.now(),
    ): Set<Announcement>
}
