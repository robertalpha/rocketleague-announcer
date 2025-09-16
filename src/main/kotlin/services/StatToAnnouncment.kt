package nl.vanalphenict.services

import kotlin.time.Clock
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement

interface StatToAnnouncment {

    fun interpret(statMessage: nl.vanalphenict.model.StatMessage, currentTimeStamp: Instant = Clock.System.now()): Announcement
}