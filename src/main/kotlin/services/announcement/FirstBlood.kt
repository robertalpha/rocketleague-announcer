package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatEvents
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant
import nl.vanalphenict.repository.StatRepository.Companion.filterType

class FirstBlood(private val repository: StatRepository) : StatToAnnouncment  {

    override fun listenTo() = setOf(StatEvents.DEMOLISH)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        if (!StatEvents.DEMOLISH.eq(statMessage.event)) return emptySet()

        if(repository.getStatHistory(statMessage.matchGUID)
            .filterType(StatEvents.DEMOLISH)
            .isEmpty()) {
            return setOf(Announcement.FIRST_BLOOD)
        }

        return emptySet()
    }
}