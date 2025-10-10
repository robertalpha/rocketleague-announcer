package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class Kill: StatToAnnouncment  {

    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
            return if (statMessage.player.team?.homeTeam == true)
                setOf(Announcement.KILL)
            else setOf(Announcement.KILLED)
    }
}