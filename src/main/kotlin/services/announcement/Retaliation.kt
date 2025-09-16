package nl.vanalphenict.services.announcement

import kotlin.time.Duration.Companion.seconds
import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import nl.vanalphenict.utility.TimeUtils.Companion.isOlderThan

class Retaliation(private val statRepository: StatRepository) : StatToAnnouncment {

    private val DEMOLISH = "Demolish"

    override fun interpret(statMessage: StatMessage): Announcement {
        if (statMessage.event != DEMOLISH) return Announcement.NOTHING

        if (statRepository.getStatHistory(statMessage.matchGUID)
            .filter { (timestamp, _) -> timestamp.isOlderThan(15.seconds) }
            .filter { (_, message) -> message.event == DEMOLISH }
            .filter { (_, message) -> message.player.isSame(statMessage.victim) }
            .count { (_, message) -> statMessage.player.isSame(message.victim) } > 0)
            return Announcement.RETALIATION
        else
            return Announcement.NOTHING
    }
}
