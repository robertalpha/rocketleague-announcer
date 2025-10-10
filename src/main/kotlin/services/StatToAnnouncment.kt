package nl.vanalphenict.services

import kotlin.time.Clock
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents

interface StatToAnnouncment {
    fun listenTo(): Set<StatEvents>
    fun interpret(statMessage: nl.vanalphenict.model.StatMessage, currentTimeStamp: Instant = Clock.System.now()): Set<Announcement>
}