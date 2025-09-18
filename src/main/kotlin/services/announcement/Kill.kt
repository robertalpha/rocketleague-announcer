package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class Kill: StatToAnnouncment  {

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        return if (Events.DEMOLISH.eq(statMessage.event))
            return if (statMessage.player.team?.homeTeam == true)
                setOf(Announcement.KILL)
            else setOf(Announcement.KILLED)
        else emptySet()
    }
}