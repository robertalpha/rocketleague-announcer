package nl.vanalphenict.services.announcement

import nl.vanalphenict.model.Announcement
import nl.vanalphenict.model.Events
import nl.vanalphenict.model.StatMessage
import nl.vanalphenict.repository.StatRepository
import nl.vanalphenict.services.StatToAnnouncment
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class Witness(val statRepository: StatRepository): StatToAnnouncment {

    private val witnessWindow = 3.seconds

    override fun interpret(statMessage: StatMessage,currentTimeStamp: Instant): Set<Announcement> {
        if (!Events.GOAL.eq(statMessage.event) || !statMessage.player.team!!.homeTeam) return emptySet()

        if (statRepository.getStatHistory(statMessage.matchGUID)
                .filter { (instant, _) -> currentTimeStamp.minus(instant) < witnessWindow }
                .filter { (_, message) -> Events.DEMOLISH.eq(message.event) }
                .count { (_, message) -> message.player.team?.homeTeam == false } > 0) {
            return setOf(Announcement.WITNESS)
        } else return emptySet()
    }
}