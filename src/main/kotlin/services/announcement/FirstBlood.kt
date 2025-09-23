package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant
import nl.vanalphenict.repository.StatRepository.Companion.filterType

class FirstBlood(private val repository: StatRepository) : StatToAnnouncment  {

    override fun listenTo() = setOf(Events.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        if (!Events.DEMOLISH.eq(statMessage.event)) return emptySet()

        if(repository.getStatHistory(statMessage.matchGUID)
            .filterType(Events.DEMOLISH)
            .isEmpty()) {
            return setOf(Announcement.FIRST_BLOOD)
        }

        return emptySet()
    }
}