package nl.vanalphenict.services.enrichers

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment


class Retaliation(private val statRepository: StatRepository) : StatToAnnouncment {

    private val TIME_WINDOW = 15000L
    private val DEMOLISH = "Demolish"

    override fun interpret(msg: StatMessage): Announcement {
        if (msg.event != DEMOLISH) return Announcement.NOTHING

        val now = System.currentTimeMillis()
        if (statRepository.getStatHistory(msg.matchGUID)
            .filter { (timestamp, _) -> timestamp > now - TIME_WINDOW }
            .filter { (_, message) -> message.event == DEMOLISH }
            .filter { (_, message) -> message.player.isSame(msg.victim) }
            .count { (_, message) -> msg.player.isSame(message.victim) } > 0)
            return Announcement.RETALIATION
        else
            return Announcement.NOTHING
    }
}
