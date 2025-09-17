package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import nl.vanalphenict.utility.TimeUtils.Companion.bothHappenWithin

class Retaliation(private val statRepository: StatRepository) : StatToAnnouncment {

    override fun interpret( statMessage: StatMessage, currentTimeStamp: Instant): Announcement {
        if (!Events.DEMOLISH.eq(statMessage.event)) return Announcement.NOTHING

        if (statRepository.getStatHistory(statMessage.matchGUID)
            .filter { (timestamp, _) -> timestamp.bothHappenWithin(currentTimeStamp,15.seconds) }
            .filter { (_, message) -> Events.DEMOLISH.eq(message.event) }
            .filter { (_, message) -> message.player.isSame(statMessage.victim) }
            .count { (_, message) -> statMessage.player.isSame(message.victim) } > 0)
            return Announcement.RETALIATION
        else
            return Announcement.NOTHING
    }
}
