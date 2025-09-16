package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class FirstBlood(private val repository: StatRepository) : StatToAnnouncment  {

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Announcement {
        if (!Events.DEMOLISH.eq(statMessage.event)) return Announcement.NOTHING

        if (repository.getStatHistory(statMessage.matchGUID)
            .count {(_,message) -> Events.DEMOLISH.eq(message.event)} > 0) {
            return Announcement.NOTHING
        }
        return Announcement.FIRST_BLOOD
    }
}