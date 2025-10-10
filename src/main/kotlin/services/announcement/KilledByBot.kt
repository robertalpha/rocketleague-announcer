package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class KilledByBot : StatToAnnouncment {
    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        return if (
            StatEvents.DEMOLISH.eq(statMessage.event) &&
            statMessage.victim?.team?.homeTeam==true &&
            statMessage.player.isBot())
            setOf(Announcement.KILLED_BY_BOT)
        else emptySet()
    }
}