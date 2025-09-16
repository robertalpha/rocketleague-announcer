package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Instant

class OwnGoal: StatToAnnouncment   {
    override fun interpret(statMessage: StatMessage, currentTimeStamp: Instant): Announcement {
        if (Events.OWN_GOAL.eq(statMessage.event)) return Announcement.HUMILIATION
        return Announcement.NOTHING
    }
}