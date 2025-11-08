package nl.vanalphenict.services.announcement

import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment

class WinLoss : StatToAnnouncment {
    override fun listenTo(): Set<StatEvents> = setOf(StatEvents.MVP)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> =
        if (statMessage.player.team.homeTeam) {
            setOf(Announcement.VICTORY)
        } else {
            setOf(Announcement.DEFEAT)
        }
}
