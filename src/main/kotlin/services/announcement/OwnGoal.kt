package nl.vanalphenict.services.announcement

import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment

class OwnGoal: StatToAnnouncment   {
    override fun listenTo() = setOf(Events.OWN_GOAL)

    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Set<Announcement> {
        return setOf(Announcement.OWN_GOAL)
    }
}