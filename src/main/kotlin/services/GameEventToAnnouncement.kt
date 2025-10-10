package nl.vanalphenict.services

import kotlin.time.Clock
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.GameEventMessage

interface GameEventToAnnouncement {
    fun listenTo(): Set<String>
    fun interpret(statMessage: GameEventMessage, currentTimeStamp: Instant = Clock.System.now()): Set<Announcement>
}