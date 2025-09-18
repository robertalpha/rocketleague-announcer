package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class FirstBlood(private val repository: StatRepository) : StatToAnnouncment  {

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        return if (Events.DEMOLISH.eq(statMessage.event)  &&
                repository.getStatHistory(statMessage.matchGUID)
                    .count {(_,message) -> Events.DEMOLISH.eq(message.event)} == 0)
            return setOf(Announcement.FIRST_BLOOD)
        else emptySet()
    }
}