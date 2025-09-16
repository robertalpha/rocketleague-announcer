package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class KilledByBot : StatToAnnouncment {
    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Announcement {
        if (
            Events.DEMOLISH.eq(statMessage.event) &&
            statMessage.victim?.team?.homeTeam==true &&
            statMessage.player.isBot())
            return Announcement.HUMILIATION
        return Announcement.NOTHING
    }
}